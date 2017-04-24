package com.summerlab.chords.main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.ContentResolver;
import android.provider.Settings;

public class MD5 {

	public static String md5(ContentResolver contentResolver, String androidId) {

		String android_id = Settings.Secure.getString(contentResolver,
				androidId);

		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(android_id.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
		}
		return "";
	}

}
