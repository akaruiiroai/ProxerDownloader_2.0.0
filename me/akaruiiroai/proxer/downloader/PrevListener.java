package me.akaruiiroai.proxer.downloader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PrevListener implements ActionListener {

	private int episode;
	private String url;
	private Frame f = new Frame();
	@Override
	public void actionPerformed(ActionEvent e) {
		episode = Frame.episode;
		url = Frame.textField.getText();
		int i = url.indexOf("/" + episode + "/");
		System.out.println(episode);
		String newUrl;
		if(episode < 10) {
			newUrl = url.substring(0, i+1) + (episode-1) + url.substring(i+2, url.length());
		} else {
			newUrl = url.substring(0, i+1) + (episode-1) + url.substring(i+3, url.length());		
		}
		f.url = newUrl;
		Frame.textField.setText(newUrl);
	}

}
