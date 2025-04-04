package org.toshiba.controller;

import org.toshiba.CameraDefine;

import com.toshiba.model.DeviceInfo;

public class CameraStatusReceiver implements RemoteReceiverAdapter {

	@Override
	public void onDealtResponse(boolean z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeleteFile(boolean z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnableDLNA(boolean z, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetCameraCapacity(boolean z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetCameraInfo(boolean z, DeviceInfo deviceInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetCameraStatus(boolean z, CameraStatus cameraStatus, WifiInfoType wifiInfoType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetLatestMedia(boolean z, int i, String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetRecordStatus(boolean z, RecordStatus2 recordStatus2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResetStream(boolean z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetConShootMode(boolean z, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetDZoom(boolean z, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetPhotoRes(boolean z, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetPreviewMode(boolean z, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetStreamBitRate(boolean z, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetStreamEncodeConfig(boolean z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetStreamGop(boolean z, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetStreamMode(boolean z, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetVideoRes(boolean z, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartCapture(boolean z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartConShoot(boolean z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartRecordVideo(boolean z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopConShoot(boolean z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopRecordVideo(boolean z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveData(int command, String response, int flags) {
		System.out.printf("[CMD %d] Received: %s\n", command, response != null ? response : "NULL");
	        switch(command) {
	            case CameraDefine.CMD_GET_CAMERA_INFO:
	                System.out.println("Camera info updated");
	                break;
	            case CameraDefine.CMD_GET_CAMERA_STATUS:
	                System.out.println("Status updated");
	                break;
	        }
	}

}
