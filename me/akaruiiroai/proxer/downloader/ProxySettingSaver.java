package me.akaruiiroai.proxer.downloader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProxySettingSaver implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		File settingsFile = new File(Frame.pdLocation, "settings.conf");
		settingsFile.delete();
		try {
			FileOutputStream fos = new FileOutputStream(settingsFile);
			fos.write("//Proxy Settings\n".getBytes());
			fos.write(("proxyEnabled: " + ProxySettingsListener.setting.getEnabled() + "\n").getBytes());
			fos.write(("proxyIP: " + ProxySettingsListener.setting.getIP() + "\n").getBytes());
			fos.write(("proxyPort: " + ProxySettingsListener.setting.getPort() + "\n").getBytes());
			fos.close();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		ProxySettingsListener.f.dispatchEvent(new WindowEvent(ProxySettingsListener.f, WindowEvent.WINDOW_CLOSING));
		ProxySettingsListener.updatet.interrupt();
		restartApplication();
	}
	
	public void restartApplication()
	{
	  final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
	  final File currentJar = new File(Frame.class.getProtectionDomain().getCodeSource().getLocation().getPath());

	  /* is it a jar file? */
	  if(!currentJar.getName().endsWith(".jar")) {
		  return;
	  }
	  /* Build command: java -jar application.jar */
	  final ArrayList<String> command = new ArrayList<String>();
	  command.add(javaBin);
	  command.add("-jar");
	  command.add(currentJar.getPath());

	  final ProcessBuilder builder = new ProcessBuilder(command);
	  try {
		builder.start();
	} catch (IOException e) {
		e.printStackTrace();
	}
	  System.exit(0);
	}

}
