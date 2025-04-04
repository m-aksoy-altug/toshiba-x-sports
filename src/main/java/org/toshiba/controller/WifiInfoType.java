package org.toshiba.controller;

public class WifiInfoType {
	private int wifi_mode;
	private String wifi_password;
	private int wifi_security;
	private String wifi_ssid;

	public int getWifi_mode() {
		return this.wifi_mode;
	}

	public void setWifi_mode(int wifi_mode2) {
		this.wifi_mode = wifi_mode2;
	}

	public String getWifi_ssid() {
		return this.wifi_ssid;
	}

	public void setWifi_ssid(String wifi_ssid2) {
		this.wifi_ssid = wifi_ssid2;
	}

	public int getWifi_security() {
		return this.wifi_security;
	}

	public void setWifi_security(int wifi_security2) {
		this.wifi_security = wifi_security2;
	}

	public String getWifi_password() {
		return this.wifi_password;
	}

	public void setWifi_password(String wifi_password2) {
		this.wifi_password = wifi_password2;
	}
}