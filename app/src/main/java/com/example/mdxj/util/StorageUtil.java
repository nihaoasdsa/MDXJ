package com.example.mdxj.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class StorageUtil {
	private static final int ERROR = -1;

	private static final String MYFOLDER = "/DWPC/";
	private static final String MYFOLDER_DB = "/DBDWPC/";

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final String PATH_PREFIX = "file://";

	public static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}
	
	public static String getOutputMediaPath() {
        if (!isExternalMemoryAvailable()) {
            return null;
        }

        String savePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + MYFOLDER;
        
        return savePath;
	}

	public static String getOutputDbPath() {
        if (!isExternalMemoryAvailable()) {
            return null;
        }

        String savePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + MYFOLDER_DB;
		File mediaStorageDir = new File(savePath);
		
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
        
        return savePath;
	}
	
	public static boolean isExistName(String key) {
		if (!isExternalMemoryAvailable()) {
			return false;
		}

		String savePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + MYFOLDER;
		File mediaStorageDir = new File(savePath);
		
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return false;
			}
		}
		
		File[] files = mediaStorageDir.listFiles();
		for(File f : files) {
			if (f.getName().contains(key)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static int mkdirs(String dirName) {
		if (!isExternalMemoryAvailable()) {
			return -1;
		}

		String savePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + MYFOLDER;
		File mediaStorageDir = new File(savePath);
		
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return -1;
			}
		}
		
		savePath += File.separator + dirName;
		mediaStorageDir = new File(savePath);

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return -1;
			} else {
				return 1;
			}
		}
		return 0;
	}
	
	public static File createFile(String srcFileName) {
		boolean result = true;
		
		File file = new File(srcFileName);  
	    if(file.exists()) {   
	        file.delete();
	    } 
	    
	    try {
			result = file.createNewFile();
			if (!result) {
				return null;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    return file;
	}
	
	public static boolean moveFile(String srcFileName, String destDirName) {
		File srcFile = new File(srcFileName);  
	    if(!srcFile.exists() || !srcFile.isFile())   
	        return false;
	      
	    return srcFile.renameTo(new File(destDirName)); 
	}
		
	public static void deleteFile(String srcFileName) {
		File srcFile = new File(srcFileName);  
	    if(!srcFile.exists() || (!srcFile.isFile() && !srcFile.isDirectory()))   
	        return;
	    
	    if (srcFile.isFile()) {
	    	 srcFile.delete();
	    	 return;
	    }

    	if(srcFile.isDirectory()){  
    	    File[] childFiles = srcFile.listFiles();  
    	    if (childFiles == null || childFiles.length == 0) {  
    	    	srcFile.delete();  
    	        return;  
    	    }  

    	    for (int i = 0; i < childFiles.length; i++) {  
    	        childFiles[i].delete();  
    	    }  
    	    srcFile.delete();  
    	}  
	}

	public static File getOutputMediaFile(int type) {
		if (!isExternalMemoryAvailable()) {
			return null;
		}

		String savePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + MYFOLDER;
		File mediaStorageDir = new File(savePath);

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		savePath += File.separator + "temp";
		mediaStorageDir = new File(savePath);

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	public static boolean isExternalMemoryAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	@SuppressWarnings("deprecation")
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();

		return availableBlocks * blockSize;
	}

	@SuppressWarnings("deprecation")
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();

		return totalBlocks * blockSize;
	}

	@SuppressWarnings("deprecation")
	public static long getAvailableExternalMemorySize() {
		if (isExternalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();

			return availableBlocks * blockSize;
		} else {
			return ERROR;
		}
	}

	@SuppressWarnings("deprecation")
	public static long getTotalExternalMemorySize() {
		if (isExternalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();

			return totalBlocks * blockSize;
		} else {
			return ERROR;
		}
	}

	public static Bitmap convertToBitmapNew(String defaultImagePath,int w, int h) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		BitmapFactory.decodeFile(defaultImagePath, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		float scaleWidth = 0.f, scaleHeight = 0.f;
				
		scaleWidth = ((float) w) / width;
		scaleHeight = ((float) h) / height;
		
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
				BitmapFactory.decodeFile(defaultImagePath, null));
		
		Bitmap bitmap = Bitmap.createBitmap(weak.get(), 0, 0, (int) width,
                (int) height, matrix, true);
		return bitmap;
	}
	
	public static Bitmap convertToBitmap(String defaultImagePath,int w, int h) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		BitmapFactory.decodeFile(defaultImagePath, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		float scaleWidth = 0.f, scaleHeight = 0.f;
		if (width > w || height > h) {
			scaleWidth = ((float) width) / w;
			scaleHeight = ((float) height) / h;
		}
		opts.inJustDecodeBounds = false;
		float scale = Math.max(scaleWidth, scaleHeight);
		opts.inSampleSize = (int) scale;
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
				BitmapFactory.decodeFile(defaultImagePath, opts));
		
		
		ExifInterface ei = null;
		try {
			ei = new ExifInterface(defaultImagePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

		switch(orientation) {
		    case ExifInterface.ORIENTATION_ROTATE_90:
		        return rotateImage(weak.get(), (float) 90);
		    case ExifInterface.ORIENTATION_ROTATE_180:
		        return rotateImage(weak.get(), 180);
		    // etc.
		}
		

		return rotateImage(weak.get(), 0);
	}
	
	public static Bitmap rotateImage(Bitmap source, float angle)
	{
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	public static String saveBitmap(String convertImagePath,Bitmap bitmap) {
		String pictureDir = "";
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();

			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			byte[] byteArray = baos.toByteArray();
			File file = new File(convertImagePath);
			file.delete();
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);
			pictureDir = file.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return pictureDir;
	}
    
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
      {
       if(null == bitmap || edgeLength <= 0)
       {
        return  null;
       }
                                                                                    
       Bitmap result = bitmap;
       int widthOrg = bitmap.getWidth();
       int heightOrg = bitmap.getHeight();
                                                                                    
       if(widthOrg > edgeLength && heightOrg > edgeLength)
       {
        int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
        int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
        int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
        Bitmap scaledBitmap;
                                                                                     
              try{
               scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
              }
              catch(Exception e){
               return null;
              }

           int xTopLeft = (scaledWidth - edgeLength) / 2;
           int yTopLeft = (scaledHeight - edgeLength) / 2;
                                                                                        
           try{
            result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
            scaledBitmap.recycle();
           }
           catch(Exception e){
            return null;
           }       
       }
                                                                                         
       return result;
      }
}
