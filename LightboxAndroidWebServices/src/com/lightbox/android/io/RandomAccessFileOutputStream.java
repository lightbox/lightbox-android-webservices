/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/** 
 * RandomAccessFileOutputStream 
 * @author Nilesh Patel
 */
public class RandomAccessFileOutputStream extends OutputStream {
	RandomAccessFile mRandomAccessFile;
	
	public RandomAccessFileOutputStream(File file) throws FileNotFoundException {
		mRandomAccessFile = new RandomAccessFile(file, "rwd");
	}
	
	@Override
	public void write(int oneByte) throws IOException {
		mRandomAccessFile.write(oneByte);
	}

	@Override
	public void close() throws IOException {
		super.flush();
		super.close();
		mRandomAccessFile.close();
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
		mRandomAccessFile.write(buffer, offset, count);
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		mRandomAccessFile.write(buffer);
	}
	
}