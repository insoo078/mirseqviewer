package kobic.msb.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MsbObjectInputStream extends ObjectInputStream{

	protected MsbObjectInputStream() throws IOException, SecurityException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public MsbObjectInputStream(FilterInputStream fis) throws IOException {
		super(fis);
	}
}