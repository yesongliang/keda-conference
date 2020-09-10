package com.kedacom.tz.sh.cometd;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.cometd.websocket.client.JettyWebSocketTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.kedacom.tz.sh.constant.ConferenceURL;
import com.kedacom.tz.sh.utils.IPUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CometDClient {

	private BayeuxClient localBayeuxClient;

	private String domain_id;// 5.0平台的用户域id

	/** 会议平台地址 **/
	private String ip;

	/** 会议平台端口 **/
	private Integer port;

	/** 登录成功返回的cookie **/
	private List<String> cookieList;

	/** 是否订阅 **/
//	private boolean isSubscribed = false;

	public CometDClient(String ip, Integer port, List<String> cookieList) {
		this.ip = ip;
		this.port = port;
		this.cookieList = cookieList;
	}

	/**
	 * 握手操作,连接cometD服务器
	 * 
	 * @param ip cometD服务器ip地址
	 * @throws Exception
	 */
	public void handShake() throws Exception {
		// 长轮询传输层
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		ClientTransport httptransport = new LongPollingTransport(null, httpClient);

		// WebSocket传输层
		WebSocketClient webSocketClient = new WebSocketClient();
		webSocketClient.start();

		// 当WebSocket传输层不可用时，CometD客户端会自动降级为长轮询传输层。
		JettyWebSocketTransport wsTransport = new JettyWebSocketTransport(null, null, webSocketClient);
		// 访问CometD服务器的URL
		String url = String.format(ConferenceURL.PUBLISH.getUrl(), this.ip, this.port);
		this.localBayeuxClient = new BayeuxClient(url, httptransport, wsTransport);

		/*
		 * 用户使用推送服务需要先登录推送服务器，登录复用平台业务API的登录逻辑，即先通过APP ID和APP KEY获取APP
		 * TOKEN，再使用用户名及密码登录。
		 * 登录成功后，HTTP服务器会在回复HTTP客户端登录成功的消息中带Cookie，CometD客户端需携带此Cookie，
		 * 调用handshake接口与CometD服务器进行握手， CometD服务器端通过校验此Cookie字段来完成认证。
		 */
		for (String cookie : cookieList) {
			String[] split = cookie.split("=");
			HttpCookie httpCookie = new HttpCookie(split[0], split[1]);
			this.localBayeuxClient.putCookie(httpCookie);// 将Login回复中携带的Cookie设置给bayeuxClient
		}

		// 握手
		this.localBayeuxClient.handshake(new ClientSessionChannel.MessageListener() {
			/*
			 * 由于握手过程是异步操作，要想确认握手是否成功，可以向handshake()接口传入回调，
			 * 在回调中获取服务器发给客户端的用户域ID(domain_id)信息
			 */
			@Override
			public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
				if (message.isSuccessful()) {
					log.info("cometD握手成功!");
					Map<String, Object> map = message.getExt();
					// 握手成功,获取domain_id
					domain_id = map.get("user_domain_moid").toString();
					// 订阅所有会议信息
					subScribe("/confs/**");
				} else {
					log.error("cometD握手失败!");
				}
			}
		});
	}

	/**
	 * 订阅消息
	 * 
	 * @param channel
	 */
	public void subScribe(String channel) {
		// 通道前要加入用户域信息
		String subchannel = "/userdomains/" + domain_id + channel;
		log.info("订阅通道：" + subchannel);
		// 订阅通道也是异步操作，需要传入两个回调，一个用来接收订阅通道的消息内容(msglistener)，一个用来判断订阅是否成功(sublistener)
		this.localBayeuxClient.getChannel(subchannel).subscribe(new MsgListener(this.domain_id, IPUtils.ipToLong(this.ip)), new ClientSessionChannel.MessageListener() {
			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				String strChannel = message.get("subscription").toString();// 获取订阅的通道
				if (message.isSuccessful()) {
//					isSubscribed = true;
					log.info("通道：" + strChannel + "订阅成功");
					long ipToLong = IPUtils.ipToLong(ip);
					Map<String, String> map = new HashMap<>();
					map.put("type", "subScribe");
					map.put("key", String.valueOf(ipToLong));
					MessageHandelrManager.getInstance().addMessage(ipToLong, map);
				} else {
					log.info("通道：" + strChannel + "订阅失败");
				}
			}
		});

	}

	/**
	 * 取消订阅
	 * 
	 * @param channel
	 */
	public void unSubScribe(String channel) {
		// 通道前要加入用户域信息
		String subchannel = "/userdomains/" + domain_id + channel;
		log.info("取消订阅通道：" + subchannel);
		this.localBayeuxClient.getChannel(subchannel).unsubscribe();
	}

	/**
	 * 断开连接
	 * 
	 */
	public void disconnect() {
		this.localBayeuxClient.disconnect();
	}

	/**
	 * 是否已连接
	 * 
	 * @return
	 */
	public boolean isConnected() {
		boolean connected = this.localBayeuxClient.isConnected();
		return connected;
	}
}
