package me.akaruiiroai.proxer.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Saver {
	URLConnection conn;
	OutputStream outStream;
	URL url;
	InputStream is;
	public static String filePath = "";
	public int episodeVar;
	Frame f = new Frame();
	NextListener nl = new NextListener();
	public static boolean aborted = false;
	private static String pattern = "\\?|\\:|<|>|/|\\\\";

	public boolean proxyEnabled;
	public String proxyIP;
	public int proxyPort;
	
	public static FileOutputStream fos;
	public static ReadableByteChannel rbc;

	@SuppressWarnings("deprecation")
	public void save(String address, String path, String name, int episode) {
		String folderName = name;
		if (!Frame.proxyipfield.getText().equals("")||!Frame.proxyportfield.getText().equals("")) {
			System.getProperties().put("http.proxySet", "true");
			System.getProperties().put("http.proxyHost", Frame.proxyipfield.getText());
			System.getProperties().put("http.proxyPort", Frame.proxyportfield.getText());
		}
		try {
			url = new URL(address);
			try {
				rbc = Channels.newChannel(url.openStream());
			} catch (IOException ioe) {
				Frame.lblStatus.setText("Unable to connect to the video page!");
			}
			File folder = new java.io.File(path + "/" + folderName);
			if (!folder.exists()) {
				if (folder.mkdirs()) {
					} else if (folderName.contains(":") || folderName.contains("?") || folderName.contains("\\")
						|| folderName.contains("/") || folderName.contains("<") || folderName.contains(">")) {
					folderName = folderName.replaceAll(pattern, "");
					folder = new File(path + "/" + folderName);
					if (folder.mkdirs()) {
						} else {
						Frame.lblStatus.setText("Irgendwas ist schief gelaufen!");
					}
				}
			}
			filePath = path + "/" + folderName + "/Episode " + episode + ".mp4";
			try {
				fos = new FileOutputStream(filePath);
			} catch (FileNotFoundException fnfe) {
				Frame.lblStatus.setText("Something went horrobly wrong here!");
				return;
			}
			Frame.lblStatus.setText("Runterladen..");
			Frame.btnDownload.setText("Abbruch");

			Frame.updateProgressBar = new Thread(Frame.updateProgressBarR);
			Frame.updateProgressBar.start();
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			Frame.btnDownload.setEnabled(true);
			if (aborted) {
				Frame.lblStatus.setText("Abgebrochen");
				Frame.prozent.setText("");
				Frame.prozent.setBounds(215, 231, 50, 20);
			} else {
				Frame.lblStatus.setText("Fertig!");
				Frame.prozent.setText("100%");
				Frame.prozent.setBounds(205, 231, 50, 20);
			}
			Frame.updateProgressBar.stop();
			Frame.progressBar.setValue(0);
			rbc.close();
			fos.close();
			fos = null;

			if ((Frame.chckbxDownloadAllEpisodes.isSelected() && Frame.episode != Frame.episodenAnzahl) && !aborted) {
				StartDownloadListener sdl = new StartDownloadListener();
				NextListener.incrementEpisode();
				Thread.sleep(500);
				while (!Frame.btnDownload.isEnabled()) {
					System.out.println("Waiting");
					Thread.sleep(100);
				}
				sdl.startDownload();
			} else {
				f.reEnableNextPrevBNTS();
			}

		} catch (Exception e) {
			Frame.btnDownload.setEnabled(true);
			e.printStackTrace();
		}

	}
}
