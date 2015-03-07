package utils;

import java.io.UnsupportedEncodingException;

public class StringUtils {
	public static String toHexString(byte[] bytearray)
	{
		StringBuilder str = new StringBuilder();
		for (byte b : bytearray)
			str.append(String.format("%02X", b));
		return str.toString();
	}

	public static String fromHexString(String hex)
	{
		StringBuilder str = new StringBuilder();
		byte[] bytearray = hex.getBytes();
		for (int i = 0; i < hex.length(); i+=2)
			str.append((char) Integer.parseInt(hex.substring(i, i+2), 16));
		return str.toString();
	}
	
	public static void PrintBytes(String s)
	{
		try {
			byte[] bytes = s.getBytes("UTF8");
			System.out.println(toHexString(bytes));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
