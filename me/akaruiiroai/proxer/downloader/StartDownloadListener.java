package me.akaruiiroai.proxer.downloader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class StartDownloadListener implements ActionListener {

	String vidUrl = "";
	String url = "";
	String path = "";
	String name = "";
	int episode = 0;
	NextListener nl = new NextListener();

	static Runnable main = new Runnable() {

		@Override
		public void run() {

			if (Frame.urlValid) {
				Saver s = new Saver();
				Frame.disableButtons();
				s.save(Frame.videolink, Frame.path, Frame.titel, Frame.episode);
			}
		}
	};
	public static Thread t = new Thread(main);

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		if (t.isAlive()) {
			try {
				Saver.aborted = true;
				Saver.rbc.close();
				Saver.fos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			t = new Thread(main);
			Frame.btnDownload.setText("Runterladen");
			t.stop();
		} else {
			Saver.aborted = false;
			t = new Thread(main);
			t.start();
		}
	}

	public void startDownload() {
		//System.out.println("Download triggered");
		t = new Thread(main);
		t.start();

	}

}
