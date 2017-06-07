package com.example.mdxj.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import android.content.Context;

import com.example.mdxj.DwpcApplication;

public class DBCopyUtil {
	private static DBCopyUtil mInstance = null;

	public static DBCopyUtil getInstance() {
		if (mInstance == null) {
			mInstance = new DBCopyUtil();
		}
		return mInstance;
	}

	public void copyToPackage(String dbName, int dbResource) {

		File dir = null;
		File dirDatabase = null;
		Context cont = DwpcApplication.getInstance().getApplicationContext();
		File file = cont.getFilesDir();
		File parentFile = file.getParentFile();
		if (parentFile.exists()) {
			String path = StorageUtil.getOutputDbPath();
			dir = new File(path);
			if (!dir.exists())
			try {
				dir.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String path2 = StorageUtil.getOutputDbPath() + dbName + ".db";
			
			if (fileIsExists(path2)) {
				return;
			}
			dirDatabase = new File(path2);
			if (!dirDatabase.exists())
			try {
				dirDatabase.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		InputStream is = cont.getResources().openRawResource(dbResource);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(dirDatabase);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] buffer = new byte[8192];
		int count = 0;
		try {
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
