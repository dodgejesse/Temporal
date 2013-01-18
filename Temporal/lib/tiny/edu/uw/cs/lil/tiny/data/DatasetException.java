package edu.uw.cs.lil.tiny.data;

public class DatasetException extends RuntimeException {
	private static final long	serialVersionUID	= 8340881530382355110L;
	private final String		filename;
	private final int			lineNumber;
	
	public DatasetException(Exception e) {
		super(e);
		this.lineNumber = -1;
		this.filename = null;
	}
	
	public DatasetException(Exception e, int lineNumber) {
		super(e);
		this.lineNumber = lineNumber;
		this.filename = null;
	}
	
	public DatasetException(Exception e, int lineNumber, String filename) {
		super(e);
		this.lineNumber = lineNumber;
		this.filename = filename;
	}
	
	public DatasetException(Exception e, String msg) {
		super(msg, e);
		this.lineNumber = -1;
		this.filename = null;
	}
	
	public DatasetException(String msg, int lineNumber) {
		super(msg);
		this.lineNumber = lineNumber;
		this.filename = null;
	}
	
	public DatasetException(String msg, int lineNumber, String filename) {
		super(msg);
		this.lineNumber = lineNumber;
		this.filename = filename;
	}
	
	@Override
	public String toString() {
		return filename + ":" + lineNumber + " :: " + super.toString();
	}
}
