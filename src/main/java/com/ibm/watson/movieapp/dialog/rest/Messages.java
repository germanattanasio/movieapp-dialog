package com.ibm.watson.movieapp.dialog.rest;

import java.io.UnsupportedEncodingException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;

public class Messages {
	private static final String BUNDLE_NAME = "locale/messages"; //$NON-NLS-1$
	private static Locale locale;
	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {

	}

	public static void setInfo(Locale local) {
		locale = local;
		RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}

	public static String getString(String key) {
		String val = "";
		try {
			// Require UTF-8 to render properties file properly
			val = new String(RESOURCE_BUNDLE.getString(key).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		catch (MissingResourceException e) {
			return '!' + key + '!';
		}
		return val;
	}
}
