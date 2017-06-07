package com.example.mdxj.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtils {
	public static String getCurrentYear(){
        long time =System.currentTimeMillis();
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        String str = format.format(new Date(time));
        return str;
	}
	
	public static String getCurrentDate(){
        long time =System.currentTimeMillis();
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str = format.format(new Date(time));
        return str;
	}
	
	public static String getCurrentTime(){
        long time =System.currentTimeMillis();
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(new Date(time));
        return str;
	}
}
