package com.toshiba.model.mixIn;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class DeviceInfoMixin {
	@JsonProperty("Available")
	private String Available;

	@JsonProperty("Battery")
	private String battery;

	@JsonProperty("Capacity")
	private String Capacity;

	@JsonProperty("IPAddress")
	private String IPAddress;

	@JsonProperty("MACAddress")
	private String MACAddress;

	@JsonProperty("ModelName")
	private String modelName;

	@JsonProperty("Name")
	private String Name;

	@JsonProperty("SerialNumber")
	private String SerialNumber;

	@JsonProperty("Version")
	private String Version;
}
