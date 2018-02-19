package me.akaruiiroai.proxer.downloader;

public class ProxySetting {
	
	boolean enabled;
	String ip, port;
	
	public ProxySetting() {
		enabled = false;
		ip = null;
		port = null;
	}
	
	public ProxySetting(boolean proxyEnabled, String ip, String port) {
		enabled = proxyEnabled;
		this.ip = ip;
		this.port = port;
	}
	
	public boolean getEnabled() {
		return enabled;
	}
	
	public String getIP() {
		return ip;
	}
	
	public String getPort() {
		return port;
	}
	
	public boolean reset() {
		enabled = false;
		ip = null;
		port = null;
		return true;
	}
	
	public boolean setEnabled(boolean enabled) {
		this.enabled = enabled;
		return true;
	}
	
	public boolean setIP(String ip) {
		this.ip = ip;
		return true;
	}
	
	public boolean setPort(String port) {
		this.port = port;
		return true;
	}
	
	public boolean setPort(int port) {
		this.port = port + "";
		return true;
	}

}
