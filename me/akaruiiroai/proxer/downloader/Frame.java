package me.akaruiiroai.proxer.downloader;

import java.awt.Font;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;

import static java.nio.file.StandardCopyOption.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Frame extends JFrame {

	private static final long serialVersionUID = -4350678040360964072L;

	// Variablen
	public String url;
	public static String path;
	public String videoUrl;
	public boolean all;
	public static int epMax;
	public static String title = "";
	public static String prevTitle = "";
	public boolean multDownRunning = false;
	public static int fileSize = 0;
	public static boolean updateNameFailed = false;
	public boolean multDown = false, multDownPrev = false;
	public static boolean urlValid = false;
	public static String rawHTML;
	public static String iframeURL;
	public static boolean captchaSolve;
	public static boolean noepisode;
	public static String pdLocation;
	public static String proxyIP = null, proxyPort = null;
	public static boolean proxyEnabled = false;
	public static ImageIcon gIcon;

	// Anime Info
	public static String titel = "Not Set";
	public static int episode = 0;
	public static int episodenAnzahl = 0;
	public static String videolink = "";

	// Frame elements
	JFrame frame;
	static JTextField textField = new JTextField();
	static JTextField textField_1 = new JTextField();
	JLabel lblProxermeDownloaderBy = new JLabel("Proxer.me Downloader by AkaruiKage");
	JLabel lblEnterProxerUrl = new JLabel("Paste proxer URL here:");
	static JButton btnDownload = new JButton("Download");
	JLabel lblSaveTo = new JLabel("Save to:");
	static JProgressBar progressBar = new JProgressBar();
	JButton btnApply = new JButton("OK");
	public static JLabel lblName = new JLabel("Anime name will be displayed here");
	public static JLabel lblStatus = new JLabel("");
	static JCheckBox chckbxDownloadAllEpisodes = new JCheckBox("Download all episodes");
	JLabel lblNext = new JLabel("");
	JLabel lblPrev = new JLabel("");
	static JButton button = new JButton(">");
	static JButton button_1 = new JButton("<");
	static JButton proxySettings = new JButton("Proxy Settings");
	
	JButton chooseSaveDir = new JButton("Select");
	static JLabel prozent = new JLabel();

	// Runnables and Threads
	private Runnable updateR = new Runnable() {

		@Override
		public void run() {
			Getter g = new Getter();
			String lastUrl = "";
			while (true) {
				// Gathering the save path, the url and the all
				path = textField_1.getText();
				url = textField.getText();
				all = chckbxDownloadAllEpisodes.isSelected();
				//System.out.println(StartDownloadListener.t.isAlive());
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// check if the url has changed
				if (url.equals(lastUrl)) {

				} else {
					try {
						g.getHtmlInfo(url, proxyEnabled, proxyIP, proxyPort);

					} catch (NotAProxerLinkException e) {
						lblName.setText("Das ist keine Proxer URL!");
					}
					// Disable any further clicking of buttons
					button.setEnabled(false);
					button_1.setEnabled(false);
					btnDownload.setEnabled(false);

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					
					if (urlValid)
						lblName.setText(Frame.titel + " Episode " + Frame.episode + "/" + Frame.episodenAnzahl);
					// Reenable next, prev and download buttons
					if (!Frame.chckbxDownloadAllEpisodes.isSelected())
						reEnableNextPrevBNTS();
					btnDownload.setEnabled(true);
				}
				lastUrl = url;
			}

		}
	};
	public Thread update = new Thread(updateR);

	public static Runnable updateProgressBarR = new Runnable() {

		@Override
		public void run() {
			Getter g = new Getter();

			float currentSize = 0;

			try {
				fileSize = g.getFileSize(videolink);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					
				}
				File f = new File(Saver.filePath);
				currentSize = (int) f.length();
				Frame.progressBar.setMaximum(fileSize);
				Frame.progressBar.setValue((int) currentSize);
				Frame.prozent.setText(Frame.prozent(currentSize, fileSize) + "%");

				if (prozent(currentSize, fileSize) > 9) {
					prozent.setBounds(210, 231, 50, 20);
				} else if (prozent(currentSize, fileSize) <= 9) {
					prozent.setBounds(215, 231, 50, 20);
				}
			}
		}
	};
	public static Thread updateProgressBar = new Thread(updateProgressBarR);

	public Frame() {

	}

	public void create() {
		setTitle("Proxer.me Downloader");
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setEnabled(true);
		String pdClassLocation = Frame.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		System.out.println(pdClassLocation);
		System.out.println(pdClassLocation);
		if(pdClassLocation.contains("/src/java/executable/pd.jar")) pdLocation = pdClassLocation.substring(0, pdClassLocation.length()-26); else pdLocation = pdClassLocation;
		File settingsFile = new File(pdLocation, "settings.conf");
		if(!settingsFile.exists()) {
			try {
				FileOutputStream fos = new FileOutputStream(settingsFile);
				fos.write("//Proxy Settings\n".getBytes());
				fos.write("proxyEnabled: false\n".getBytes());
				fos.write("proxyIP: \n".getBytes());
				fos.write("proxyPort: \n".getBytes());
				fos.close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			proxyEnabled = false;
			proxyIP = null;
			proxyPort = null;
		} else {
			BufferedReader br = null;
			String rawSettings = "";
			try {
				br = new BufferedReader(new FileReader(settingsFile));
				StringBuilder sb = new StringBuilder();
				String currentLine;
				while((currentLine = br.readLine()) != null) {
					if(!currentLine.startsWith("//")) {
						sb.append(currentLine);
						sb.append(";");
					}
					
				}
				rawSettings = sb.toString();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if(br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
			
			System.out.println(rawSettings);
			String sett1 = rawSettings.replaceAll("\\s", "");
			System.out.println(sett1);
			ArrayList<String> settList1 = new ArrayList<>();
			for(String s : sett1.split(";")) {
				settList1.add(s);
			}
			ArrayList<String> settList2 = new ArrayList<>();
			for(String s : settList1) {
				settList2.add((s.replaceAll("\\S+\\:", "").replaceAll(";", "")));
			}
			if(settList2.get(0).equals("false")||settList2.get(0).equals("true")) {
				if(settList2.get(0).equals("false")) {
					proxyEnabled = false;
				} else {
					proxyEnabled = true;
				}
			}
			if(!settList2.get(1).isEmpty()) {
				proxyIP = settList2.get(1);
			}
			if(!settList2.get(2).isEmpty()) {
				proxyPort = settList2.get(2);
			}
			System.out.println("Proxy Enabled: " + proxyEnabled);
			System.out.println("Proxy IP: " + proxyIP);
			System.out.println("Proxy Port: " + proxyPort);
			
			
		}
		System.out.println(pdLocation);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {

			e1.printStackTrace();
		} catch (InstantiationException e1) {

			e1.printStackTrace();
		} catch (IllegalAccessException e1) {

			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {

			e1.printStackTrace();
		}
		setResizable(false);
		setBounds(100, 100, 440, 420);
		if (System.getProperty("os.name").contains("Windows")) {
			String appdata = System.getenv("APPDATA");
			String iconPath = appdata + "\\ProxerDownloader\\ic_launcher.png";
			File indexPath = new File(appdata + "\\ProxerDownloader");
	
			File icon = new File(iconPath);
			if (!icon.exists()) {
				String path = Frame.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				File pathToIcon;
				try {
					String decodedPath = URLDecoder.decode(path, "UTF-8");
					pathToIcon = new File(decodedPath, "ic_launcher.png");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					pathToIcon = new File(path, "ic_launcher.png");
				}
				if(pathToIcon != null) {
					try {
						Files.copy(pathToIcon.toPath(), icon.toPath(), REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
			ImageIcon imgicon = new ImageIcon(iconPath);
			gIcon = imgicon;
			setIconImage(imgicon.getImage());
		} else if (System.getProperty("os.name").equals("Linux")) {
			String iconPath = System.getProperty("user.home") + "/ProxerDownloader/ic_launcher.png";
			File indexPath = new File(System.getProperty("user.home") + "/ProxerDownloader");
			
			File icon = new File(iconPath);
			if (!icon.exists()) {
				String path = Frame.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				File pathToIcon;
				try {
					String decodedPath = URLDecoder.decode(path, "UTF-8");
					pathToIcon = new File(decodedPath, "ic_launcher.png");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					pathToIcon = new File(path, "ic_launcher.png");
				}
				if(pathToIcon != null) {
					try {
						Files.copy(pathToIcon.toPath(), icon.toPath(), REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			ImageIcon imgicon = new ImageIcon(iconPath);
			gIcon = imgicon;
			setIconImage(imgicon.getImage());
		}

		getContentPane().setLayout(null);
		lblProxermeDownloaderBy.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblProxermeDownloaderBy.setBounds(10, 11, 303, 19);
		getContentPane().add(lblProxermeDownloaderBy);
		textField = new JTextField();
		textField.setBounds(10, 63, 410, 20);
		getContentPane().add(textField);
		textField.setColumns(10);
		lblEnterProxerUrl.setBounds(10, 49, 190, 14);
		getContentPane().add(lblEnterProxerUrl);
		btnDownload.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnDownload.setBounds(123, 148, 190, 35);
		btnDownload.addActionListener(new StartDownloadListener());
		getContentPane().add(btnDownload);
		textField_1 = new JTextField();
		textField_1.setBounds(10, 350, 320, 20);
		getContentPane().add(textField_1);
		chooseSaveDir.setBounds(330, 350, 90, 19);
		chooseSaveDir.setFont(new Font("Tahoma", Font.PLAIN, 10));
		chooseSaveDir.addActionListener(new DirectorySelector());
		getContentPane().add(chooseSaveDir);
		textField_1.setColumns(10);
		lblSaveTo.setBounds(10, 335, 150, 14);
		getContentPane().add(lblSaveTo);
		prozent.setBounds(215, 231, 50, 20);
		getContentPane().add(prozent);
		progressBar.setMaximum(1024);
		progressBar.setValue(0);
		progressBar.setBounds(123, 194, 190, 14);
		getContentPane().add(progressBar);
		
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setFont(new Font("Arial", Font.PLAIN, 16));
		lblName.setBounds(10, 94, 414, 43);
		getContentPane().add(lblName);
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setBounds(123, 215, 190, 19);
		getContentPane().add(lblStatus);
		chckbxDownloadAllEpisodes.setFont(new Font("Ubuntu", Font.PLAIN, 12));
		chckbxDownloadAllEpisodes.setHorizontalAlignment(SwingConstants.CENTER);
		chckbxDownloadAllEpisodes.setBounds(123, 250, 190, 23);
		getContentPane().add(chckbxDownloadAllEpisodes);
		lblNext.setBounds(316, 160, 35, 14);
		getContentPane().add(lblNext);
		lblPrev.setBounds(85, 160, 35, 14);
		getContentPane().add(lblPrev);
		button.setBounds(352, 156, 45, 23);
		button.addActionListener(new NextListener());
		getContentPane().add(button);
		button_1.setBounds(34, 156, 45, 23);
		button_1.addActionListener(new PrevListener());
		getContentPane().add(button_1);
		proxySettings.setBounds(140, 290, 160, 20);
		proxySettings.addActionListener(new ProxySettingsListener());
		getContentPane().add(proxySettings);
		
		
		String home = System.getProperty("user.home");
		String[] s;
		StringBuilder sb = new StringBuilder();
		s = (home.split("\\\\"));
		for (int i = 0; i < s.length; i++) {
			sb.append(s[i] + "/");
		}
		textField_1.setText(sb.toString() + "Downloads/Proxer");
		setVisible(true);
		if(!update.isAlive()) {
			update.start();
		}
	}

	public void reEnableNextPrevBNTS() {
		if (episode > 1) {
			button_1.setEnabled(true);
		}
		if (episode < episodenAnzahl) {
			button.setEnabled(true);
		}
	}
	
	public static void disableButtons() {
		button.setEnabled(false);
		button_1.setEnabled(false);
	}

	public static int prozent(float value, float max) {
		return (int) ((value / max) * 100);
	}
	
	public void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
