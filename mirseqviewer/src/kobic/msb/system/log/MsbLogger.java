package kobic.msb.system.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class MsbLogger {
	public static String getPrintStackTraceInfoString(Exception e) {
		String returnStackTraceString = e.toString();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream( out );
		e.printStackTrace( printStream );
		returnStackTraceString = printStream.toString();

		printStream.close();

		try {
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return returnStackTraceString;
	}
}
