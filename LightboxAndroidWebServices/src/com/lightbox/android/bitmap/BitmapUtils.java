/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.bitmap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.FloatMath;
import android.util.Log;

import com.lightbox.android.io.RandomAccessFileOutputStream;
import com.lightbox.android.utils.debug.DebugLog;

/** 
 * BitmapUtils 
 * @author Fabien Devos & Nilesh Patel
 */
public final class BitmapUtils {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "BitmapUtils";
	
	private static final int FULL_QUALITY = 100;
	public static final int HIGH_QUALITY = 90;
	
	public static final int MAX_PIXELS_LRG = 1024 * 1024 * 2; // 2 MegaPixels
	public static final int MAX_UPLOAD_SIZE = 720; // max width or height of 720 pixels
		
    //------------------------------------------------------
    // Private constructor for utility class
    private BitmapUtils() {
        throw new UnsupportedOperationException("Sorry, you cannot instantiate an utility class!");
    }
    //------------------------------------------------------
    
    /**
     * Read a bitmap from disk
     * @param absoluteFileName
     * @param config optional Bitmap.Config. If null, the default ARGB_8888 will be used.
     * @return
     */
    public static Bitmap readBitmapFromFile(String absoluteFileName, Bitmap.Config config) {
    	return readBitmapFromFile(absoluteFileName, config, 1);
    }
    
    /**
     * Read a bitmap from disk
     * @param absoluteFileName
     * @param config optional Bitmap.Config. If null, the default ARGB_8888 will be used.
     * @return
     */
    public static Bitmap readBitmapFromFile(String absoluteFileName, Bitmap.Config config, int sampleSize) {
		Options opts = new Options();
		opts.inPreferredConfig = config == null ? Config.ARGB_8888 : config;
		opts.inDither = true;
		opts.inSampleSize = sampleSize;
		try {
			return BitmapFactory.decodeFile(absoluteFileName, opts);
		} catch (OutOfMemoryError e1) {
			try {
				opts.inPreferredConfig = Config.RGB_565;
				return BitmapFactory.decodeFile(absoluteFileName, opts);
			} catch (OutOfMemoryError e2) {
				throw e2;
			}
		}
    }
    
    /**
     * Get the size of a bitmap from disk
     * @param absoluteFileName
     */
    public static BitmapSize getBitmapSizeFromFile(String absoluteFileName) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(absoluteFileName, opts);
		return new BitmapSize(opts.outWidth, opts.outHeight);
    }
    
	public static void writeBitmapInFile(String absoluteFileName, Bitmap bitmap) throws IOException {
		writeBitmapInFile(new File(absoluteFileName), bitmap);
	}
	
	public static void writeBitmapInFile(String absoluteFileName, Bitmap bitmap, CompressFormat compressFormat) throws IOException {
		writeBitmapInFile(new File(absoluteFileName), bitmap, compressFormat, null);
	}
	
	public static void writeBitmapInFile(File file, Bitmap bitmap) throws IOException {
		writeBitmapInFile(file, bitmap, CompressFormat.JPEG, null);
	}
	
	public static void writeBitmapInFile(String absoluteFileName, Bitmap bitmap, CompressFormat compressFormat, StringBuilder outMD5) throws IOException {
		writeBitmapInFile(new File(absoluteFileName), bitmap, compressFormat, outMD5);
	}
	
	public static void writeBitmapInFile(File file, Bitmap bitmap, CompressFormat compressFormat, StringBuilder outMD5) throws IOException {
		// Ensure that the directory exist
		file.getParentFile().mkdirs();
		
		OutputStream outputStream = null;
		try {
			if (outMD5 != null) {
				// We want a MD5: writing JPEG into a byte array
				outputStream = new ByteArrayOutputStream();				
			} else {
				// Directly write to file
				try {
					outputStream = new BufferedOutputStream(new RandomAccessFileOutputStream(file), 65536);
				} catch (OutOfMemoryError e) {
					outputStream = new BufferedOutputStream(new RandomAccessFileOutputStream(file));
				}
			}
			boolean success = bitmap.compress(compressFormat, FULL_QUALITY, outputStream);
			if ( ! success ) {
				throw new IOException(String.format("Unable to save bitmap as a %s file: %s", (compressFormat == CompressFormat.JPEG) ? "jpeg" : "png" , file.getAbsoluteFile().toString()));
			} else {
				if (outMD5 != null) {
					// Calculate MD5 and write the file to disk
					long time = System.currentTimeMillis();
					byte[] jpeg = ((ByteArrayOutputStream) outputStream).toByteArray();				
					if (outMD5 != null) {
						outMD5.append(getMD5String(jpeg));
					}
					DebugLog.d(TAG, "Time to calculate MD5: "+(System.currentTimeMillis()-time));
					
					time = System.currentTimeMillis();
					RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
					randomAccessFile.write(jpeg);
					randomAccessFile.close();
					DebugLog.d(TAG, "Time to write to file: "+(System.currentTimeMillis()-time));
				}
			}
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}
	
	public static boolean writeBitmapInFileWithMD5Check(String filePath, Bitmap bitmap, CompressFormat compressFormat) throws IOException {
		return writeBitmapInFileWithMD5Check(new File(filePath), bitmap, compressFormat);
	}
	
	public static boolean writeBitmapInFileWithMD5Check(String filePath, Bitmap bitmap, CompressFormat compressFormat, StringBuffer md5Out) throws IOException {
		return writeBitmapInFileWithMD5Check(new File(filePath), bitmap, compressFormat, md5Out);
	}
	
	public static boolean writeBitmapInFileWithMD5Check(File file, Bitmap bitmap, CompressFormat compressFormat) throws IOException {
		return writeBitmapInFileWithMD5Check(file, bitmap, compressFormat, null);
	}
		
	public static boolean writeBitmapInFileWithMD5Check(File file, Bitmap bitmap, CompressFormat compressFormat, StringBuffer md5Out) throws IOException {
		// Ensure that the directory exist
		file.getParentFile().mkdirs();
		
		RandomAccessFileMD5OutputStream randomAccessFileOutputStream = new RandomAccessFileMD5OutputStream(file);
		BufferedOutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(randomAccessFileOutputStream, 65536);
			boolean compressSuccess = bitmap.compress(compressFormat, FULL_QUALITY, outputStream);
			
			if (!compressSuccess) {
				return false;
			}
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
			
		String md5Data = randomAccessFileOutputStream.getMD5();
		DebugLog.d(TAG, "MD5 of data:"+md5Data);
		
		String md5File = getFileMD5(file);	
		DebugLog.d(TAG, "MD5 of file:"+md5File);
		
		if (md5Data.equals(md5File)) {
			if (md5Out != null) {
				md5Out.append(md5Data);
			}
			return true;
		} else {
			//TrackHelper.trackException(TAG, new Exception("MD5 comparison failed."));
			FileUtils.deleteQuietly(file);
			return false;
		}
	}
	
	private static String getFileMD5(File file) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(file);
				byte[] buffer = new byte[8192];
				int count;
				while ((count = fileInputStream.read(buffer)) != -1) {
					md5.update(buffer, 0, count);
				}
				
				return md5ToString(md5.digest()).toString();
			} finally {
				IOUtils.closeQuietly(fileInputStream);
			}			
		} catch (NoSuchAlgorithmException e) {
			Log.w(TAG, e);
		} catch (FileNotFoundException e) {
			Log.w(TAG, e);
		} catch (IOException e) {
			Log.w(TAG, e);
		}
		
		return "";
	}
	
	private static String md5ToString(byte[] md5Hash) {
		Formatter formatter = new Formatter();
        for (byte b : md5Hash) {
            formatter.format("%02x", b);
        }
	    return formatter.toString();
	}
	
	private static String getMD5String(byte[] data) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(data);
			byte[] md5Hash = md5.digest();
			Formatter formatter = new Formatter();
	        for (byte b : md5Hash) {
	            formatter.format("%02x", b);
	        }
		    return formatter.toString();
		} catch (NoSuchAlgorithmException e1) {
			Log.w(TAG, e1);
		}
		
		return "";
	}
	
	public static Bitmap createUserPhotoThumbnail(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		Bitmap scaledBitmap;
		//TODO get thumb size constant from somewhere, but where?
		if (width > height) {
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int)(BitmapSource.THM_SIZE_PX * ((float)width/height)), BitmapSource.THM_SIZE_PX, true);
		} else {
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, BitmapSource.THM_SIZE_PX, (int)(BitmapSource.THM_SIZE_PX * ((float)height/width)), true);
		}
		
		Bitmap thumb = Bitmap.createBitmap(scaledBitmap, (scaledBitmap.getWidth()-BitmapSource.THM_SIZE_PX)/2, (scaledBitmap.getHeight()-BitmapSource.THM_SIZE_PX)/2, BitmapSource.THM_SIZE_PX, BitmapSource.THM_SIZE_PX);
		
		scaledBitmap.recycle();
		
		return thumb;
	}
	
	public static Bitmap decodeFile(String pathName, int maxWidth, int maxHeight) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		int sampleSize = 1;
		options.inSampleSize = sampleSize;
		BitmapFactory.decodeFile(pathName, options);
		
		if (maxWidth < options.outWidth || maxHeight < options.outHeight) {
			int sampleSizeW = (int)FloatMath.ceil((float)options.outWidth / maxWidth);
			int sampleSizeH = (int)FloatMath.ceil((float)options.outHeight / maxHeight);
			sampleSize = Math.max(sampleSizeW, sampleSizeH);
			options.inSampleSize = sampleSize;
		}
		
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeFile(pathName, options);
	}
	
	public static BitmapSize getScaledSize(int originalWidth, int originalHeight, int numPixels) {
		float ratio = (float)originalWidth/originalHeight;
		
		int scaledHeight = (int)FloatMath.sqrt((float)numPixels/ratio);
		int scaledWidth = (int)(ratio * FloatMath.sqrt((float)numPixels/ratio));
				
		return new BitmapSize(scaledWidth, scaledHeight);
	}
	
	private static Paint sScalePaint = new Paint(Paint.DITHER_FLAG|Paint.FILTER_BITMAP_FLAG|Paint.ANTI_ALIAS_FLAG);
	/*
	 * This method scales with less artifacts than the default Bitmap.createScaledBitmap() method
	 */
	public static Bitmap createScaledBitmap(Bitmap src, int scaledWidth, int scaledHeight) {
		Bitmap scaledBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, src.getConfig());
		Canvas canvas = new Canvas(scaledBitmap);
		canvas.drawBitmap(src, new Rect(0, 0, src.getWidth(), src.getHeight()), new Rect(0, 0, scaledWidth, scaledHeight), sScalePaint);
		return scaledBitmap;
	}
	
	/******************************************************************************************
	 * RandomAccessFileMD5OutputStream 
	 */
	private static class RandomAccessFileMD5OutputStream extends RandomAccessFileOutputStream {
		MessageDigest mMD5 = null;
		byte[] digest = null;
		
		public RandomAccessFileMD5OutputStream(File file) throws FileNotFoundException {
			super(file);
			try {
				mMD5 = MessageDigest.getInstance("MD5");
				mMD5.reset();
			} catch (NoSuchAlgorithmException e) {
				Log.w(TAG, e);
			}	
		}
		
		@Override
		public void write(int oneByte) throws IOException {
			super.write(oneByte);
			mMD5.update(new Integer(oneByte).byteValue());
		}

		@Override
		public void close() throws IOException {
			super.close();
			if (digest == null) {
				digest = mMD5.digest();
			}
		}

		@Override
		public void write(byte[] buffer, int offset, int count) throws IOException {
			super.write(buffer, offset, count);
			mMD5.update(buffer, offset, count);
		}

		@Override
		public void write(byte[] buffer) throws IOException {
			super.write(buffer);
			mMD5.update(buffer);
		}
		
		public String getMD5() {
		    return md5ToString(digest).toString();
		}
	}
}
