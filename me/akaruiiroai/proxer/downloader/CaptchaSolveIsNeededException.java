package me.akaruiiroai.proxer.downloader;

public class CaptchaSolveIsNeededException extends Exception {

	private static final long serialVersionUID = -1832159831305166709L;

	public CaptchaSolveIsNeededException() {
		
	}
	
	public CaptchaSolveIsNeededException(String message) {
		super(message);
	}

}
