package com.toshiba.model;

//import org.jupnp.model.message.header.EXTHeader;

public class DeviceInfo {
	private String Available;
    private String Battery;
    private String Capacity;
    private String IPAddress;
    private String MACAddress;
    private String ModelName;
    private String Name;
    private String SerialNumber;
    private String Version;

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getCapacity() {
        return this.Capacity;
    }

    public void setCapacity(String capacity) {
        this.Capacity = capacity;
    }

    public String getAvailable() {
        return this.Available;
    }

    public void setAvailable(String available) {
        this.Available = available;
    }

    public String getVersion() {
        if (this.Version != null) {
            return this.Version;
        }
//        return EXTHeader.DEFAULT_VALUE;
        return "Version is null";
    }

    public int getVersionInt() {
        if (this.Version != null) {
        	return 0;
//            return Integer.valueOf(this.Version.trim().replaceAll("\\D", EXTHeader.DEFAULT_VALUE)).intValue();
        }
        return 0;
    }

    public void setVersion(String version) {
        this.Version = version;
    }

    public String getModelName() {
        if (this.ModelName == null) {
//            return EXTHeader.DEFAULT_VALUE;
        	return "ModelName is null";
        }
        return this.ModelName;
    }

    public void setModelName(String modelName) {
        this.ModelName = modelName;
    }

    public String getSerialNumber() {
        return this.SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.SerialNumber = serialNumber;
    }

    public String getiPAddress() {
        return this.IPAddress;
    }

    public void setiPAddress(String iPAddress) {
        this.IPAddress = iPAddress;
    }

    public String getMACAddress() {
        return this.MACAddress;
    }

    public void setMACAddress(String mACAddress) {
        this.MACAddress = mACAddress;
    }

    public String toString() {
        return "RemoteDeviceAbout [name=" + this.Name + ", capacity=" + this.Capacity + ", available=" + this.Available + ", version=" + this.Version + ", modelName=" + this.ModelName + ", serialNumber=" + this.SerialNumber + ", iPAddress=" + this.IPAddress + ", MACAddress=" + this.MACAddress + "]";
    }

    public String getBattery() {
        return this.Battery;
    }

    public void setBattery(String battery) {
        this.Battery = battery;
    }
}
