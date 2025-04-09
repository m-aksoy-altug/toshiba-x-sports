package org.toshiba;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;

public class Main {
	// exec:java -Dexec.mainClass="org.toshiba.Main"
	public static void main(String[] args) throws InterruptedException, IOException {

		String response = new OkHttpClient()
				.newCall(new Request.Builder().url(CameraDefine.GET_CAMERA_INFO).build()).execute().body().string();
		System.out.println("Camera status: " + response);

		String responseShoot = new OkHttpClient()
				.newCall(new Request.Builder().url(CameraDefine.VIDEO_SETTING_2).build()).execute().body().string();
		System.out.println("Camera Shoot: " + responseShoot);

		String responseCamCapacity = new OkHttpClient()
				.newCall(new Request.Builder().url(CameraDefine.GET_CAMERA_CAPACITY).build()).execute().body().string();
		System.out.println("Cam Capacity" + responseCamCapacity);

		String responseBattery = new OkHttpClient()
				.newCall(new Request.Builder().url(CameraDefine.GET_BATTERY).build()).execute().body().string();
		System.out.println("Battery" + responseBattery);

		String responseBody = new OkHttpClient()
				.newCall(new Request.Builder().url(CameraDefine.FILE_PATH_100MEDIA).build()).execute().body().string();
		System.out.println("Files" + responseBody);
		
//		new ToshibaDownload().start(responseBody);
		new ToshibaRecorder().stream();
	}

}
