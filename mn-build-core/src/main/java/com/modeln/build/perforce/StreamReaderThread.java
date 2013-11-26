package com.modeln.build.perforce;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.lang.StringBuffer;

public class StreamReaderThread extends Thread{
	
	InputStreamReader _in;
	StringBuffer _out;
	
	public StreamReaderThread(InputStream in, StringBuffer out) {
		_in=new InputStreamReader(in);
		_out=out;
	}
	
	public void run() {
		int ch;
		try {
				while ((ch=_in.read()) != -1) {
					_out.append((char)ch);					
				}
		} catch (Exception e) {
			_out.append("\n\tRead error: "+e.getMessage());
				
		}
	}
}
