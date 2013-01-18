package edu.uw.cs.utils.telnet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TelnetConnection {
	/**
	 * Carriage return followed by line feed.
	 */
	private static final String		CRLF	= "\r\n";
	private final BufferedReader	reader;
	private final Socket			socket;
	private final BufferedWriter	writer;
	
	public TelnetConnection(String host, int port) throws UnknownHostException,
			IOException {
		socket = new Socket(host, port);
		reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));
	}
	
	public void close() throws IOException {
		socket.close();
	}
	
	public String readLine() throws IOException {
		return reader.readLine();
	}
	
	public void writeLine(String line) throws IOException {
		final String l = line + CRLF;
		writer.write(l, 0, l.length());
		writer.flush();
	}
}
