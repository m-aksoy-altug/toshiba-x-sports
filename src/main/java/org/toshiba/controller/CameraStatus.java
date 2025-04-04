package org.toshiba.controller;


public class CameraStatus {
	private String Available;
    private String Capacity;
    private int app_type;
    private int auto_shutdown_threshold;
    private int battery;
    private int beep_indicator;
    private int cardv_enable;
    private int continue_shooting;
    private int contrast;
    private int dzoom_step;
    private int flicker;
    private int led_indicator;
    private int meter_mode;
    private int mic_sensitivity;
    private int ntc;
    private int photo_amount;
    private int photo_quality;
    private int photo_selftimer;
    private int photo_size;
    private int rec_status;
    private int recording_timelapse;
    private int scene_mode;
    private int stream_type;
    private String time;
    private int video_amount;
    private int video_fov;
    private int video_framerate;
    private int video_res;
    private int video_rotation;
    private int white_balance;

    public int getVideo_amount() {
        return this.video_amount;
    }

    public void setVideo_amount(int video_amount2) {
        this.video_amount = video_amount2;
    }

    public int getPhoto_amount() {
        return this.photo_amount;
    }

    public void setPhoto_amount(int photo_amount2) {
        this.photo_amount = photo_amount2;
    }

    public int getStream_type() {
        return this.stream_type;
    }

    public void setStream_type(int stream_type2) {
        this.stream_type = stream_type2;
    }

    public int getNtc() {
        return this.ntc;
    }

    public void setNtc(int ntc2) {
        this.ntc = ntc2;
    }

    public int getApp_type() {
        return this.app_type;
    }

    public void setApp_type(int app_type2) {
        this.app_type = app_type2;
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

    public int getMeter_mode() {
        return this.meter_mode;
    }

    public void setMeter_mode(int meter_mode2) {
        this.meter_mode = meter_mode2;
    }

    public int getPhoto_quality() {
        return this.photo_quality;
    }

    public void setPhoto_quality(int photo_quality2) {
        this.photo_quality = photo_quality2;
    }

    public int getScene_mode() {
        return this.scene_mode;
    }

    public void setScene_mode(int scene_mode2) {
        this.scene_mode = scene_mode2;
    }

    public int getWhite_balance() {
        return this.white_balance;
    }

    public void setWhite_balance(int white_balance2) {
        this.white_balance = white_balance2;
    }

    public int getContrast() {
        return this.contrast;
    }

    public void setContrast(int contrast2) {
        this.contrast = contrast2;
    }

    public int getPhoto_selftimer() {
        return this.photo_selftimer;
    }

    public void setPhoto_selftimer(int photo_selftimer2) {
        this.photo_selftimer = photo_selftimer2;
    }

    public int getFlicker() {
        return this.flicker;
    }

    public void setFlicker(int flicker2) {
        this.flicker = flicker2;
    }

    public int getContinue_shooting() {
        return this.continue_shooting;
    }

    public void setContinue_shooting(int continue_shooting2) {
        this.continue_shooting = continue_shooting2;
    }

    public int getAuto_shutdown_threshold() {
        return this.auto_shutdown_threshold;
    }

    public void setAuto_shutdown_threshold(int auto_shutdown_threshold2) {
        this.auto_shutdown_threshold = auto_shutdown_threshold2;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time2) {
        this.time = time2;
    }

    public int getVideo_fov() {
        return this.video_fov;
    }

    public void setVideo_fov(int video_fov2) {
        this.video_fov = video_fov2;
    }

    public int getMic_sensitivity() {
        return this.mic_sensitivity;
    }

    public void setMic_sensitivity(int mic_sensitivity2) {
        this.mic_sensitivity = mic_sensitivity2;
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

    public int getLed_indicator() {
        return this.led_indicator;
    }

    public void setLed_indicator(int led_indicator2) {
        this.led_indicator = led_indicator2;
    }

    public int getBeep_indicator() {
        return this.beep_indicator;
    }

    public void setBeep_indicator(int beep_indicator2) {
        this.beep_indicator = beep_indicator2;
    }

    public int getVideo_rotation() {
        return this.video_rotation;
    }

    public void setVideo_rotation(int video_rotation2) {
        this.video_rotation = video_rotation2;
    }

    public int getCardv_enable() {
        return this.cardv_enable;
    }

    public void setCardv_enable(int cardv_enable2) {
        this.cardv_enable = cardv_enable2;
    }

    public int getVideo_framerate() {
        return this.video_framerate;
    }

    public void setVideo_framerate(int video_framerate2) {
        this.video_framerate = video_framerate2;
    }

    public String getCapacity() {
        return this.Capacity;
    }

    public void setCapacity(String capacity) {
        this.Capacity = capacity;
    }

    public String getAvailable() {
        if (this.Available == null) {
//            return EXTHeader.DEFAULT_VALUE;
        	return "Available is null";
        }
        return this.Available;
    }

    public void setAvailable(String available) {
        this.Available = available;
    }

    public int getRecording_timelapse() {
        return this.recording_timelapse;
    }

    public void setRecording_timelapse(int recording_timelapse2) {
        this.recording_timelapse = recording_timelapse2;
    }
}
