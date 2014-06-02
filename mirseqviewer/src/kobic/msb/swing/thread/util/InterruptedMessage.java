package kobic.msb.swing.thread.util;

public class InterruptedMessage {
	public void interruptedMessage( Thread currentThread ) {
        if( currentThread.isInterrupted() ) {
        	return;
        }
	}
}
