package me.akaruiiroai.proxer.downloader;

import java.awt.Font;
import java.io.File;
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
	public static boolean proxyEnabled = false;
	public static String proxyIP;
	public static int proxyPort;
	public static boolean urlValid = false;
	public static String rawHTML;
	public static String iframeURL;
	public static boolean captchaSolve;
	public static boolean noepisode;

	// Anime Info
	public static String titel = "Not Set";
	public static int episode = 0;
	public static int episodenAnzahl = 0;
	public static String videolink = "";

	// Frame elements
	JFrame frame;
	static JTextField textField = new JTextField();
	static JTextField textField_1 = new JTextField();
	JLabel lblProxermeDownloaderBy = new JLabel("Proxer.me Downloader von AkaruiKage");
	JLabel lblEnterProxerUrl = new JLabel("Hier Proxer URL eingeben:");
	static JButton btnDownload = new JButton("Runterladen");
	JLabel lblSaveTo = new JLabel("Speichern in:");
	static JProgressBar progressBar = new JProgressBar();
	JButton btnApply = new JButton("Anwenden");
	public static JLabel lblName = new JLabel("Animename wird hier angezeigt");
	public static JLabel lblStatus = new JLabel("");
	static JCheckBox chckbxDownloadAllEpisodes = new JCheckBox("Alle Episoden runterladen");
	JLabel lblNext = new JLabel("");
	JLabel lblPrev = new JLabel("");
	static JButton button = new JButton(">");
	static JButton button_1 = new JButton("<");
	JLabel proxyInfo = new JLabel("Proxyeinstellungen(leer, falls nicht vorhanden)");
	static JTextField proxyipfield = new JTextField();
	static JTextField proxyportfield = new JTextField();
	JButton chooseSaveDir = new JButton("Auswählen");
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
					if (!Frame.proxyipfield.getText().equals("") || !Frame.proxyportfield.getText().equals("")) {
						Frame.proxyEnabled = true;
						Frame.proxyIP = Frame.proxyipfield.getText();
						Frame.proxyPort = Integer.parseInt(Frame.proxyportfield.getText());
						} else {
						Frame.proxyEnabled = false;
					}
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
		update.start();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setEnabled(true);
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
				FileDownloaderNEW fd = new FileDownloaderNEW();
				indexPath.mkdirs();
				try {
					fd.download("http://brecher.be/files/res/icon/ic_launcher.png", iconPath, false, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			ImageIcon imgicon = new ImageIcon(iconPath);
			setIconImage(imgicon.getImage());
		} else if (System.getProperty("os.name").equals("Linux")) {
			String iconPath = System.getProperty("user.home") + "/ProxerDownloader/ic_launcher.png";
			File indexPath = new File(System.getProperty("user.home") + "/ProxerDownloader");
			
			File icon = new File(iconPath);
			if (!icon.exists()) {
				FileDownloaderNEW fd = new FileDownloaderNEW();
				indexPath.mkdirs();
				try {
					fd.download("http://brecher.be/files/res/icon/ic_launcher.png", iconPath, true, false);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
			ImageIcon imgicon = new ImageIcon(iconPath);
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
		proxyInfo.setHorizontalAlignment(SwingConstants.CENTER);
		proxyInfo.setBounds(20, 280, 390, 20);
		getContentPane().add(proxyInfo);
		proxyipfield.setBounds(100, 300, 170, 20);
		getContentPane().add(proxyipfield);
		proxyportfield.setBounds(290, 300, 50, 20);
		getContentPane().add(proxyportfield);
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
		String home = System.getProperty("user.home");
		String[] s;
		StringBuilder sb = new StringBuilder();
		s = (home.split("\\\\"));
		for (int i = 0; i < s.length; i++) {
			sb.append(s[i] + "/");
		}
		textField_1.setText(sb.toString() + "Downloads/Proxer");
		setVisible(true);
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
}
