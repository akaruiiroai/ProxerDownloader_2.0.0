package me.akaruiiroai.proxer.downloader;

public class NotAProxerLinkException extends Exception {

	private static final long serialVersionUID = -2082110351418512897L;

	public NotAProxerLinkException() {
		
	}
	
	public NotAProxerLinkException(String message) {
		super(message);
	}

}
