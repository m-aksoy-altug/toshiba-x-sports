package org.toshiba;

public class CameraDefine {
	public static final String RECORDINGS= "Recordings";
	public static final String  DATE_TIME_FORMAT= "yy-MM-dd'T'HH-mm-ss";
	public static final String RTSP_STREAM = "rtsp://192.168.42.1/AmbaStreamTest";
	public static final String FILE_PATH_100MEDIA ="http://192.168.42.1/DCIM/100MEDIA/";
	public static final String GET_CAMERA_INFO = "http://192.168.42.1/setting/cgi-bin/fd_control_client?func=fd_get_camera_info";
	public static final String VIDEO_SETTING_2 = "http://192.168.42.1/setting/cgi-bin/fd_control_client?func=fd_get_video_setting_2";
	public static final String GET_CAMERA_CAPACITY = "http://192.168.42.1/setting/cgi-bin/fd_control_client?func=fd_get_camera_capacity";
	public static final String GET_BATTERY = "http://192.168.42.1/setting/cgi-bin/fd_control_client?func=fd_get_camera_battery";
}