package com.kedacom.tz.sh.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPUtils {

	private static String ipReg = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
	private static Pattern ipPattern = Pattern.compile(ipReg);

	/**
	 * ip地址转成long型数字 将IP地址转化成整数的方法如下：
	 * 
	 * 1、通过String的split方法按.分隔得到4个长度的数组
	 * 
	 * 2、通过左移位操作（<<）给每一段的数字加权，第一段的权为2的24次方，第二段的权为2的16次方，第三段的权为2的8次方，最后一段的权为1
	 * 
	 * @param strIp
	 * @return
	 */
	public static long ipToLong(String ipAddress) {
		long result = 0;

		String[] ipAddressInArray = ipAddress.split("\\.");

		for (int i = 3; i >= 0; i--) {

			long ip = Long.parseLong(ipAddressInArray[3 - i]);

			// left shifting 24,16,8,0 and bitwise OR

			// 1. 192 << 24
			// 1. 168 << 16
			// 1. 1 << 8
			// 1. 2 << 0
			result |= ip << (i * 8);

		}
		return result;

	}

	/**
	 * 将十进制整数形式转换成127.0.0.1形式的ip地址 将整数形式的IP地址转化成字符串的方法如下：
	 * 1、将整数值进行右移位操作（>>>），右移24位，右移时高位补0，得到的数字即为第一段IP。
	 * 2、通过与操作符（&）将整数值的高8位设为0，再右移16位，得到的数字即为第二段IP。
	 * 3、通过与操作符吧整数值的高16位设为0，再右移8位，得到的数字即为第三段IP。
	 * 
	 * 4、通过与操作符吧整数值的高24位设为0，得到的数字即为第四段IP。
	 * 
	 * @param longIp
	 * @return
	 */
	public static String longToIP(long longIp) {
		StringBuilder result = new StringBuilder(15);

		for (int i = 0; i < 4; i++) {

			result.insert(0, Long.toString(longIp & 0xff));

			if (i < 3) {
				result.insert(0, '.');
			}

			longIp = longIp >> 8;
		}
		return result.toString();

	}

	/**
	 * 校验ip
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isIp(String ip) {
		Matcher matcher = ipPattern.matcher(ip);
		boolean matches = matcher.matches();
		return matches;
	}

	public static void main(String[] args) {
		System.out.println(isIp("172.16.188.16"));
		System.out.println(ipToLong("172.16.188.16"));
		System.out.println(longToIP(2886777872L));
	}

}
