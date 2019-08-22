package com.github.hollykunge.security.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密工具类
 */
public class MD5Util {

	public final static String MD5(byte[] btInput) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
//		try {
//			// 获得MD5摘要算法的 MessageDigest 对象
//			MessageDigest mdInst = MessageDigest.getInstance("MD5");
//			// 使用指定的字节更新摘要
//			mdInst.update(btInput);
//			// 获得密文
//			byte[] md = mdInst.digest();
//			// 把密文转换成十六进制的字符串形式
//			int j = md.length;
//			char str[] = new char[j * 2];
//			int k = 0;
//			for (int i = 0; i < j; i++) {
//				byte byte0 = md[i];
//				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
//				str[k++] = hexDigits[byte0 & 0xf];
//			}
//			return new String(str);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
		// 消息签名（摘要）
		MessageDigest md5 = null;
		try {
			// 参数代表的是算法名称
			md5 = MessageDigest.getInstance("MD5");
			byte[] result = md5.digest(btInput);

			StringBuilder sb = new StringBuilder(32);
			// 将结果转为16进制字符  0~9 A~F
			for (int i = 0; i < result.length; i++) {
				// 一个字节对应两个字符
				byte x = result[i];
				// 取得高位
				int h = 0x0f & (x >>> 4);
				// 取得低位
				int l = 0x0f & x;
				sb.append(hexDigits[h]).append(hexDigits[l]);
			}
			return sb.toString();

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}