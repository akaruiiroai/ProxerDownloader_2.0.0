package me.akaruiiroai.proxer.downloader;

public class NotAnEpisodeLink extends Exception {
	
	private static final long serialVersionUID = 1977849191411708160L;

	public NotAnEpisodeLink() {
		
	}
	
	public NotAnEpisodeLink(String message) {
		super(message);
	}
	
}
