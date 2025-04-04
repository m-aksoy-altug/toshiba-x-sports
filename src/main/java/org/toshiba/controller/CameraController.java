package org.toshiba.controller;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.toshiba.CameraDefine;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.toshiba.model.DeviceInfo;
import com.toshiba.model.mixIn.CameraInfoResMixin;
import com.toshiba.model.mixIn.DeviceInfoMixin;
import com.toshiba.model.mixIn.ResCommonMixin;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CameraController implements RemoteSenderAdapter {
	
	private static final int THREAD_POOL_SIZE = 5;
		
    private final RemoteReceiverAdapter receiver;
    private final ExecutorService executor;
    private final OkHttpClient httpClient;
    private final XmlMapper xmlMapper;
    
    private static CameraStatus cameraStatus = new CameraStatus();
    private static DeviceInfo devInfo = new DeviceInfo();
    private static WifiInfoType wifiInfo = new WifiInfoType();
    
    public CameraController(RemoteReceiverAdapter receiver) {
        this.receiver = receiver;
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)    
            .readTimeout(5, TimeUnit.SECONDS)     
            .callTimeout(10, TimeUnit.SECONDS)    
            .build(); 
        this.xmlMapper = XmlMapper.builder()
        	    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        	    .build();
        
        configureXmlAliases();
    }
    
    private void configureXmlAliases() {
        xmlMapper.addMixIn(ResCommon.class, ResCommonMixin.class);
        xmlMapper.addMixIn(CameraInfoRes.class, CameraInfoResMixin.class);
        xmlMapper.addMixIn(DeviceInfo.class, DeviceInfoMixin.class);
    }
    
    private static void syncDeviceInfoValue() {
        if (cameraStatus.getCapacity() == null && devInfo.getCapacity() != null) {
            cameraStatus.setCapacity(devInfo.getCapacity());
        }
        if ((cameraStatus.getAvailable() == null ) ) { // || cameraStatus.getAvailable().equals(EXTHeader.DEFAULT_VALUE)) && devInfo.getAvailable() != null) {
            cameraStatus.setAvailable(devInfo.getAvailable());
        }
    }
    
    private interface ResponseParser {
        boolean parseResponse(String response) throws IOException;
    }
    
    private void executeCommand(int cmdType, String url, ResponseParser parser) {
        executor.execute(() -> {
            try {
                Request request = new Request.Builder().url(url).build();
                try (Response response = httpClient.newCall(request).execute()) {
                	System.out.println("response:"+ response.toString());
                	String responseBody = response.body().string();
                    System.out.println("ResponseBody:"+ responseBody);
                    boolean success = parser.parseResponse(responseBody);
                    
                    // Directly call receiver methods without Handler
                    receiver.onReceiveData(cmdType, responseBody, 0);
                    receiver.onDealtResponse(success);
                }
            } catch (IOException e) {
                receiver.onReceiveData(cmdType, null, 0);
                receiver.onDealtResponse(false);
            }
        });
    }
    
    @Override
	public void getCameraInfo() {
        executeCommand(CameraDefine.CMD_GET_CAMERA_INFO, 
                CameraDefine.GET_CAMERA_INFO,
                response -> {
                    CameraInfoRes res = xmlMapper.readValue(response, CameraInfoRes.class);
                    System.out.println("getCameraInfo()"+res.toString());
                    devInfo = res.getDevInfo();
                    syncDeviceInfoValue();
                    return true;
                });
    }
    
	@Override
	public void cancelAllCommand() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteFile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableDLNA(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getCameraCapacity() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void getCameraStatus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getLatestMedia(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getRecordStatus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetStream() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConShootMode(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDZoom(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPhotoRes(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPreviewMode(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStreamBitRate(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStreamEncodeConfig(int i, int i2, int i3, int i4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStreamGop(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStreamMode(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVideoRes(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startCapture() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startConShoot() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRecordVideo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopConShoot() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopRecordVideo() {
		// TODO Auto-generated method stub
		
	}

}
