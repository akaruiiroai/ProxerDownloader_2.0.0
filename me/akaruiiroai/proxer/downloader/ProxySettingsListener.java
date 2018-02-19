package me.akaruiiroai.proxer.downloader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class ProxySettingsListener implements ActionListener{
	
	public static ProxySetting setting = new ProxySetting();
	
	static JCheckBox enableCHKBX = new JCheckBox("Proxy verwenden");
	static JTextField proxyIPField = new JTextField(Frame.proxyIP);
	static JTextField proxyPortField = new JTextField(Frame.proxyPort);
	JButton  ok = new JButton("Speichern");
	
	public static JFrame f;

	static Runnable update = new Runnable() {
		
		@Override
		public void run() {
			while(true) {
				if(enableCHKBX.isSelected()) {
					proxyIPField.setEnabled(true);
					proxyPortField.setEnabled(true);
				} else {
					proxyIPField.setEnabled(false);
					proxyPortField.setEnabled(false);
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				setting.setEnabled(enableCHKBX.isSelected());
				setting.setIP(proxyIPField.getText());
				setting.setPort(proxyPortField.getText());
			}
		}
	};
	
	static Thread updatet = new Thread(update);
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		f = new JFrame();
		f.setTitle("Proxy Einstellungen");
		f.setBounds(120, 120, 350, 200);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setIconImage(Frame.gIcon.getImage());
		f.getContentPane().setLayout(null);
		enableCHKBX.setBounds(20, 20, 130, 20);
		enableCHKBX.setSelected(Frame.proxyEnabled);
		f.getContentPane().add(enableCHKBX);
		proxyIPField.setBounds(20, 50, 130, 20);
		proxyIPField.setEnabled(enableCHKBX.isSelected());
		f.getContentPane().add(proxyIPField);
		proxyPortField.setBounds(154, 50, 50, 20);
		proxyPortField.setEnabled(enableCHKBX.isSelected());
		f.getContentPane().add(proxyPortField);
		ok.setBounds(20, 90, 80, 23);
		ok.addActionListener(new ProxySettingSaver());
		f.getContentPane().add(ok);
		f.setVisible(true);
		updatet.start();
	}

}
