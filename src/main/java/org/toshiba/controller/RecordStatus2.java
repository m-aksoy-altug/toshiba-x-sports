package org.toshiba.controller;

public class RecordStatus2 {
	private int battery;
	private int cap_mode;
	private int dzoom_step;
	private String memory;
	private int ntc;
	private int photo_size;
	private int rec_status;
	private int recording_timelapse;
	private int video_res;

	public int getMode() {
		return this.cap_mode;
	}

	public void setMode(int mode) {
		this.cap_mode = mode;
	}

	public int getDzoom_step() {
		return this.dzoom_step;
	}

	public void setDzoom_step(int dzoom_step2) {
		this.dzoom_step = dzoom_step2;
	}

	public int getRec_status() {
		return this.rec_status;
	}

	public void setRec_status(int rec_status2) {
		this.rec_status = rec_status2;
	}

	public int getBattery() {
		return this.battery;
	}

	public void setBattery(int battery2) {
		this.battery = battery2;
	}

	public String getMemory() {
		return this.memory;
	}

	public void setMemory(String memory2) {
		this.memory = memory2;
	}

	public int getVideo_res() {
		return this.video_res;
	}

	public void setVideo_res(int video_res2) {
		this.video_res = video_res2;
	}

	public int getPhoto_size() {
		return this.photo_size;
	}

	public void setPhoto_size(int photo_size2) {
		this.photo_size = photo_size2;
	}

	public int getNtc() {
		return this.ntc;
	}

	public void setNtc(int ntc2) {
		this.ntc = ntc2;
	}

	public int getRecording_timelapse() {
		return this.recording_timelapse;
	}

	public void setRecording_timelapse(int recording_timelapse2) {
		this.recording_timelapse = recording_timelapse2;
	}
}