package me.akaruiiroai.proxer.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JOptionPane;

public class IconDownloader {

	
	
	Frame f = new Frame();

	public void download(String a1, String a2, boolean showUI, boolean exit) throws Exception {

		if (Frame.proxyEnabled) {
			System.getProperties().put("http.proxySet", "true");
			System.getProperties().put("http.proxyHost", Frame.proxyIP);
			System.getProperties().put("http.proxyPort", Frame.proxyPort + "");

		}

		String site = a1;
		String filename = a2;
		try {
			URL url = new URL(site);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			int filesize = connection.getContentLength();
			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
			FileOutputStream fos = new FileOutputStream(filename);
			BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
			byte[] data = new byte[1024];
			int i = 0;
			while ((i = in.read(data, 0, 1024)) >= 0) {
				bout.write(data, 0, i);
			}
			bout.close();
			in.close();
		} catch (Exception e) {
			JOptionPane.showConfirmDialog(null, e.getMessage(), "Error", -1);
			e.printStackTrace();
		}
	}
}
