package me.akaruiiroai.proxer.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Getter {

	String titel, inhalt;
	String rawHTML, iframeURL = "", streamHTML = "";
	int filesize = 0;
	String url = "";

	// Frame f = new Frame();

	public boolean getHtmlInfo(String url, boolean proxyEN, String proxyIP, int proxyPort) throws NotAProxerLinkException {
		final String furl = url;
		this.url = url;
		boolean ret = false;
		Runnable run = new Runnable() {
			@Override
			public void run() {
				StringBuilder html = new StringBuilder();
				try {

					if (proxyEN) {
						System.getProperties().put("http.proxySet", "true");
						System.getProperties().put("http.proxyHost", proxyIP);
						System.getProperties().put("http.proxyPort", proxyPort + "");
					}
					URLConnection connection = (new URL(furl)).openConnection();
					connection.setConnectTimeout(5000);
					connection.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
					connection.setReadTimeout(5000);
					connection.connect();
					InputStream in;
					in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));

					for (String line; (line = reader.readLine()) != null;) {
						html.append(line);
					}
					in.close();
					Frame.urlValid = true;
					Frame.rawHTML = html.toString();
					gatherInformation();
				} catch (IOException ioe) {
					Frame.urlValid = false;
					ioe.printStackTrace();
				} catch (CaptchaSolveIsNeededException e) {
					Frame.captchaSolve = true;
				} catch (NotAnEpisodeLink nael) {
					Frame.noepisode = true;
				}
			}
		};
		Thread runt = new Thread(run);
		if (url.contains("proxer"))
			runt.start();
		else
			throw new NotAProxerLinkException();
		return ret;
	}

	public void gatherInformation() throws CaptchaSolveIsNeededException, NotAnEpisodeLink {
		int titleb, titlee;
		if (url.contains("watch")) {
			String streamCode;
			titleb = Frame.rawHTML.indexOf("<title>");
			titlee = Frame.rawHTML.indexOf("</title>");
			titel = Frame.rawHTML.substring(titleb + 7, titlee);
			Frame.titel = titel.substring(0, titel.indexOf(" Episode"));
			String preEp = titel.substring(titel.indexOf("Episode") + 8);
			if (preEp.contains("GerSub")) {
				Frame.episode = Integer.parseInt(preEp.substring(0, preEp.indexOf("GerSub") - 1));
			} else if (preEp.contains("EngSub")) {
				Frame.episode = Integer.parseInt(preEp.substring(0, preEp.indexOf("EngSub") - 1));
			} else if (preEp.contains("EngDub")) {
				Frame.episode = Integer.parseInt(preEp.substring(0, preEp.indexOf("EngDub") - 1));
			} else if (preEp.contains("GerDub")) {
				Frame.episode = Integer.parseInt(preEp.substring(0, preEp.indexOf("GerDub") - 1));
			} else {
				return;
			}

			try {
				Frame.episodenAnzahl = Integer.parseInt(Frame.rawHTML.substring(Frame.rawHTML.indexOf("</span>/") + 8,
						Frame.rawHTML.indexOf("wLinkerText") - 13));
			} catch (NumberFormatException nfe) {
				Frame.episodenAnzahl = Integer.parseInt(Frame.rawHTML.substring(Frame.rawHTML.indexOf("</span>/") + 8,
						Frame.rawHTML.indexOf("wLinkerText") - 14));
			} catch (IndexOutOfBoundsException ioobe) {
				throw new CaptchaSolveIsNeededException();
			}
			streamCode = Frame.rawHTML.substring(Frame.rawHTML.indexOf("var streams") + 38,
					Frame.rawHTML.indexOf("var streams") + 50);
			iframeURL = "http://stream.proxer.me/embed-" + streamCode + "-728x504.html?utype=guest";
			Frame.iframeURL = iframeURL;

			Runnable run = new Runnable() {
				@Override
				public void run() {
					StringBuilder html = new StringBuilder();
					try {
						URLConnection connection = (new URL(Frame.iframeURL)).openConnection();
						connection.setConnectTimeout(5000);
						connection.setReadTimeout(5000);
						connection.connect();
						InputStream in;
						in = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));

						for (String line; (line = reader.readLine()) != null;) {
							html.append(line);
						}
						in.close();
						Frame.urlValid = true;
						streamHTML = html.toString();
					} catch (IOException ioe) {
						Frame.urlValid = false;
						ioe.printStackTrace();
					}
				}
			};
			Thread runt = new Thread(run);
			runt.start();
			while (runt.isAlive()) {
			}
			Frame.videolink = streamHTML.substring(streamHTML.indexOf("file:") + 7, streamHTML.indexOf("image:") - 9);
		} else {
			throw new NotAnEpisodeLink();
		}
	}

	public int getFileSize(String videoUrl) {

		final String videoURL = videoUrl;

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(videoURL);
					if (Frame.proxyEnabled) {
						System.getProperties().put("http.proxySet", "true");
						System.getProperties().put("http.proxyHost", Frame.proxyIP);
						System.getProperties().put("http.proxyPort", Frame.proxyPort + "");

					}
					URLConnection urlConnection = url.openConnection();
					urlConnection.connect();
					final String contentLengthStr = urlConnection.getHeaderField("content-length");
					Frame.fileSize = Integer.parseInt(contentLengthStr);
				} catch (IOException e) {
					filesize = Integer.MAX_VALUE;
				}
			}
		}).start();
		return filesize;
	}

	public static String getStringFromBoolean(boolean in) {
		if (in) {
			return "true";
		} else {
			return "false";
		}
	}

}
