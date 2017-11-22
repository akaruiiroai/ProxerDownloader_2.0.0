package me.akaruiiroai.proxer.downloader;

public class NoProxerStreamFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoProxerStreamFoundException() {
	}

	public NoProxerStreamFoundException(String message) {
		super(message);
	}
	
}
