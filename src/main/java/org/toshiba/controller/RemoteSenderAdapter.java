package org.toshiba.controller;

public interface RemoteSenderAdapter {
	void cancelAllCommand();

	void deleteFile();

	void enableDLNA(int i);

	void getCameraCapacity();

	void getCameraInfo();

	void getCameraStatus();

	void getLatestMedia(int i);

	void getRecordStatus();

	void resetStream();

	void setConShootMode(int i);

	void setDZoom(int i);

	void setPhotoRes(int i);

	void setPreviewMode(int i);

	void setStreamBitRate(int i);

	void setStreamEncodeConfig(int i, int i2, int i3, int i4);

	void setStreamGop(int i);

	void setStreamMode(int i);

	void setVideoRes(int i);

	void startCapture();

	void startConShoot();

	void startRecordVideo();

	void stopConShoot();

	void stopRecordVideo();
}
