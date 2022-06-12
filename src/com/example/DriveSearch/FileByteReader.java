package com.example.DriveSearch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FileByteReader {
	BufferedInputStream is;
	public FileByteReader(File f) throws FileNotFoundException {
		is=new BufferedInputStream(new FileInputStream(f));
	}
	public int getIntfromByteArray(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).getInt();
	}
	public long getLongfromByteArray(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).getLong();
	}
	public char getCharfromByteArray(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).getChar();
	}
	
	public byte[] readAmount(int size) throws IOException {
		byte[] out=new byte[size];
		for (int i = 0; i < size; i++) {
			out[i]=(byte) is.read();
		}
		return out;
	}
	public int readInt() throws IOException {
		return getIntfromByteArray(readAmount(Integer.BYTES));
	}
	public long readLong() throws IOException {
		return getLongfromByteArray(readAmount(Long.BYTES));
	}
	public char readChar() throws IOException {
		return getCharfromByteArray(readAmount(Character.BYTES));
	}
	public String readString() throws IOException {
		int len=readInt();
		char[] carr=new char[len];
		for (int i = 0; i < len; i++) {
			carr[i]=readChar();
		}
		return new String(carr);
	}
	public void close() throws IOException {
		is.close();
	}
	
}
