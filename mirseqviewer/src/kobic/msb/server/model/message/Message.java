package kobic.msb.server.model.message;

public class Message {
	public static int ERROR_MESSAGE = 0;
	public static int WARN_MESSAGE = 1;
	public static int NORMAL_MESSAGE = 2;
	
	public Object obj;

	public int		messageCode;
	public String	message;
	
	public Message(int messageCode, String message, Object obj) {
		this.messageCode = messageCode;
		this.message = message;
		this.obj	= obj;
	}
	
	public int getCode() {
		return this.messageCode;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public Object getObject() {
		return this.obj;
	}
}
