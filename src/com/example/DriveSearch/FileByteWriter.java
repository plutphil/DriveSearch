package com.example.DriveSearch;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FileByteWriter {
	BufferedOutputStream os;
	public FileByteWriter(File f) throws FileNotFoundException{
			os=new BufferedOutputStream(new FileOutputStream(f));
	}
	public void writeInt(int i) throws IOException {
		os.write(ByteBuffer.allocate(Integer.BYTES).putInt(i).array());
	}
	public void writeChar(char c)  throws IOException{
		os.write(ByteBuffer.allocate(Character.BYTES).putChar(c).array());
		
	}
	public void writeCharArr(char[] arr)  throws IOException{
		for(char c:arr) {
			writeChar(c);
		}
		
	}
	public void writeString(String str)  throws IOException{
		writeInt(str.length());
		writeCharArr(str.toCharArray());
	}
	public void writeLong(long l)  throws IOException{
		os.write(ByteBuffer.allocate(Long.BYTES).putLong(l).array());
	}
	public void close() throws IOException {
		os.close();
	}
}
