package com.example.mdxj.util;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
public class Tool {


	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		} else if ((value instanceof String)
				&& (((String) value).trim().length() < 1)) {
			return true;
		} else if (value.getClass().isArray()) {
			if (0 == java.lang.reflect.Array.getLength(value)) {
				return true;
			}
		} else if (value instanceof List) {
			if (((List) value).isEmpty()) {
				return true;
			}
		}
		return false;
	}



	
}
