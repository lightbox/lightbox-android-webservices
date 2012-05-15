/**
 * Copyright (c) 2012 Lightbox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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