package us.supercheng.utils;

import org.apache.commons.codec.binary.Base64;
import java.security.MessageDigest;

public class MD5Utils {

	/**
	 * 
	 * @Title: MD5Utils.java
	 * @Package us.supercheng.utils
	 * @Description: String MD5 -> Hash String
	 */
	public static String getMD5Str(String strValue) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		return Base64.encodeBase64String(md5.digest(strValue.getBytes()));
	}

	public static void main(String[] args) {
		try {
			String md5 = getMD5Str("hcheng");
			System.out.println(md5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
