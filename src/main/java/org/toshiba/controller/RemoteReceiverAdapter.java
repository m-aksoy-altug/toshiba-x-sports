package org.toshiba.controller;

import com.toshiba.model.DeviceInfo;

public interface RemoteReceiverAdapter {
	 void onDealtResponse(boolean z);

	    void onDeleteFile(boolean z);

	    void onEnableDLNA(boolean z, int i);

	    void onGetCameraCapacity(boolean z);

	    void onGetCameraInfo(boolean z, DeviceInfo deviceInfo);

	    void onGetCameraStatus(boolean z, CameraStatus cameraStatus, WifiInfoType wifiInfoType);

	    void onGetLatestMedia(boolean z, int i, String str);

	    void onGetRecordStatus(boolean z, RecordStatus2 recordStatus2);

	    void onResetStream(boolean z);

	    void onSetConShootMode(boolean z, int i);

	    void onSetDZoom(boolean z, int i);

	    void onSetPhotoRes(boolean z, int i);

	    void onSetPreviewMode(boolean z, int i);

	    void onSetStreamBitRate(boolean z, int i);

	    void onSetStreamEncodeConfig(boolean z);

	    void onSetStreamGop(boolean z, int i);

	    void onSetStreamMode(boolean z, int i);

	    void onSetVideoRes(boolean z, int i);

	    void onStartCapture(boolean z);

	    void onStartConShoot(boolean z);

	    void onStartRecordVideo(boolean z);

	    void onStopConShoot(boolean z);

	    void onStopRecordVideo(boolean z);
	    
	    void onReceiveData(int command, String response, int flags);
	    
}