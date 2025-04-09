package org.toshiba;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;

public class ToshibaRecorder {

	private static final long SEGMENNT_DURATION_MILLIS = 15 * 60 * 1000; // 15 mins
	private FFmpegFrameGrabber grabber;
	private FFmpegFrameRecorder recorder;
	private ExecutorService executor;
	private int frameCount= 0;
	
	/* - Only WQVGA Stream is possible, however multiple recording options: 720P, WVGA, VGA, etc
	 * - ImageWidth 432, ImageHeight 240 , AudioChannels 2 ,FrameRat  29.97002997002997 ,PixelFormat 3 ,SampleRate 48000
	 */
	public void stream() {
		this.grabber = new FFmpegFrameGrabber(CameraDefine.RTSP_STREAM);
		this.recorder = null;
		this.executor = Executors.newSingleThreadExecutor();
		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			cleanup();
			FileCleanUp.shutdown();
		}));
		Future<Boolean> future = rtspStreamConnection();
		FileCleanUp.mpFourAsync(Paths.get(CameraDefine.RECORDINGS),1, 1, TimeUnit.HOURS);
		rtspStreamRecord(future);
	}

	private void rtspStreamRecord(Future<Boolean> future) {
		try {
			boolean connected = future.get(10, TimeUnit.SECONDS);
			if (connected) {
				printStreamDetails();
				Frame frame;
				Path filePath = createPath();

				long lastSegmentStartMillis = System.currentTimeMillis();
	            recorder = new FFmpegFrameRecorder(filePath.toAbsolutePath().toString(), grabber.getImageWidth(),
	                    grabber.getImageHeight(), grabber.getAudioChannels());
	            
				settingFFmpegFrameRecorder();
				sanityCheck();

				recorder.start();
	            while ((frame = grabber.grab()) != null) {
	                try {
	                    long now = System.currentTimeMillis();
	                    if (now - lastSegmentStartMillis >= SEGMENNT_DURATION_MILLIS) {
		                    try {
		                    	recorder.flush();
		                    	recorder.stop();
		                    } catch (Exception e) {
		                        throw new RuntimeException("Failed to stop recorder: " + e.getMessage());
		                    }
		                    
		                    try {
		                    	 grabber.restart(); 
		                    } catch (Exception e) {
		                        throw new RuntimeException("Error restarting grabber: " + e.getMessage());
		                    }
		                    
	                        filePath = createPath();
   	                        recorder = new FFmpegFrameRecorder(filePath.toAbsolutePath().toString(),
	                                grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
   	                        
	                        settingFFmpegFrameRecorder();
	                        try {
	                        	recorder.start();
	                        } catch (Exception e) {
		                        throw new RuntimeException("Failed to start recorder: " + e.getMessage());
		                    }
	                        lastSegmentStartMillis = now;
	                        frameCount= 0;
	                        System.out.println("Started new segment at: " + filePath.toAbsolutePath().toString());
	                    }

	                    if (frame.image != null || frame.samples != null) {
	                        System.out.printf("--- Frame Count %d ---%n", frameCount++);
	                        recorder.record(frame);
	                    }
	                    frame = null; 
	                } catch (Exception e) {
	                    System.err.println("Frame error: " + e.getMessage());
	                }
	            }
	            recorder.stop();
	            grabber.stop();
	        } else {
	            System.err.println("Failed to connect, exiting.");
	        }
	    } catch (TimeoutException e) {
	        System.err.println("Connection timed out!");
	        future.cancel(true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        cleanup();
	        FileCleanUp.shutdown();
	    }	
	}

	private Path createPath() throws IOException {
		Files.createDirectories(Paths.get(CameraDefine.RECORDINGS));
		return Paths.get(CameraDefine.RECORDINGS,  LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern(CameraDefine.DATE_TIME_FORMAT)) + ".mp4");
	}
	

	private Future<Boolean> rtspStreamConnection() {
		return executor.submit(() -> {
			try {
				System.out.println("Attempting to connect to RTSP stream...");
				settingFFmpegFrameGrabber();
				avutil.av_log_set_level(avutil.AV_LOG_DEBUG);  // Enable verbose debugging
				FFmpegLogCallback.set();	// Redirect logs to Java console
				grabber.start();
				System.out.println("Connected successfully!");
				return true;
			} catch (Exception e) {
				System.err.println("Error: Unable to connect to RTSP stream!");
				e.printStackTrace();
				return false;
			}
		});
	}
	private void  settingFFmpegFrameGrabber() {
		// https://ffmpeg.org/ffmpeg-formats.html#Options-1
		this.grabber.setOption("rtsp_transport", "udp"); // Force UDP mode
		this.grabber.setOption("stimeout", "5000000"); // 5-second timeout (microseconds)
		this.grabber.setOption("threads", "4"); // Multi-threaded decoding
		this.grabber.setOption("fflags", "nobuffer"); // Reduce latency
		this.grabber.setOption("flags", "low_delay"); // Critical for live streams
		this.grabber.setOption("tune", "zerolatency"); // Optimize for real-time
		
		// UDP-Specific Tweaks (from logs)
		this.grabber.setOption("buffer_size", "425984"); // Match UDP buffer size in logs
		this.grabber.setOption("max_delay", "500000"); // 500ms jitter buffer (from logs)
		// Disable packet reordering (helps with UDP drops)
		this.grabber.setOption("reorder_queue_size", "0"); 
		// Drop late packets to prevent lag
		this.grabber.setOption("drop_pkts_on_overflow", "1"); 

	}
	
	private void settingFFmpegFrameRecorder() {
		recorder.setFormat("mp4");
		recorder.setVideoOption("movflags", "frag_keyframe+empty_moov+faststart");  ; // for streaming and progressive playback
		recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);    // H.264 codec for video encoding.
		recorder.setFrameRate(grabber.getFrameRate());     // Sets the target frame rate for the output video.
		// 2.5 Mbps 720p@30fps or even 1080p@25fps with moderate detail. // these settings are not possible  
		// 600 kps for 432@30fps // possible stream
		recorder.setVideoBitrate(grabber.getImageWidth()<=480 ? 600_000 : (grabber.getImageWidth()<=720 ? 1_200_000 : 2_500_000)); 
		// recorder.setPixelFormat(avutil.AV_PIX_FMT_YUVJ420P); 
		recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P); //  YUV420P is standard and fully supported the pixel format.
		recorder.setVideoOption("color_range", "pc"); // "pc" stands for full range (0–255)
		recorder.setVideoOption("tune", "zerolatency"); // Optimizes H.264 encoding for zero-latency (low delay) scenarios.
		
		// Audio settings
		// grabber returns 2.
		recorder.setAudioChannels(grabber.getAudioChannels()); // : Sets the number of audio channels (e.g., mono=1, stereo=2).
		recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);  // Uses AAC codec for encoding audio.
		recorder.setAudioBitrate(128000);   // Sets audio bitrate to 128 kbps.
		// Sets the sample rate (e.g., 44100 Hz or 48000 Hz).
		recorder.setSampleRate(grabber.getSampleRate()); // Matches the source audio's sample rate to prevent distortion or resampling artifacts.
	}

	private void sanityCheck() throws IOException {
		if (grabber.getImageWidth() <= 0 || grabber.getImageHeight() <= 0) {
			throw new IOException("Invalid video dimensions");
		}
		if (grabber.getFrameRate() <= 0 || grabber.getFrameRate() > 120) {
			System.out.println("Adjusting invalid framerate to 30fps");
			grabber.setFrameRate(30); // Force sane default
		}
		if (grabber.getAudioChannels() <= 0) {
			System.out.println("No audio detected - disabling audio recording");
			recorder.setAudioChannels(0); // Disable audio
		}
	}

	private void cleanup() {
		try {
			if (recorder != null) {
				recorder.setVideoOption("flush_packets", "1");
				recorder.stop();
				recorder.release();
			}
			if (grabber != null) {
				grabber.stop();
				grabber.release();
			}
			if (executor != null) {
				executor.shutdownNow();
			}
			System.out.println("Cleanup complete.");
		} catch (Exception e) {
			System.err.println("Cleanup error: " + e.getMessage());
		}
	}
	
	private void printStreamDetails() {			
			System.out.println("Format: " + grabber.getFormat());
	        System.out.println("Image Width: " + grabber.getImageWidth());
	        System.out.println("Image Height: " + grabber.getImageHeight());
	        System.out.println("Audio Channels: " + grabber.getAudioChannels());
	        System.out.println("Pixel Format: " + grabber.getPixelFormat());
	        System.out.println("Video Codec: " + grabber.getVideoCodec());
	        System.out.println("Video Bitrate: " + grabber.getVideoBitrate());
	        System.out.println("Frame Rate: " + grabber.getFrameRate());
	        System.out.println("Sample Rate: " + grabber.getSampleRate());
	        System.out.println("Audio Codec: " + grabber.getAudioCodec());
	        System.out.println("Audio Bitrate: " + grabber.getAudioBitrate());
	        System.out.println("Has Video: " + grabber.hasVideo());
	        System.out.println("Has Audio: " + grabber.hasAudio());
	        System.out.println("Gamma: " + grabber.getGamma());
	        System.out.println("Video: " + grabber.getImageWidth() + "x" + grabber.getImageHeight() 
	            + " (" + grabber.getVideoCodecName() + ")");
	        System.out.println("Audio: " + grabber.getAudioChannels() + "ch " 
	            + grabber.getSampleRate() + "Hz (" + grabber.getAudioCodecName() + ")");
	        System.out.println("Framerate: " + grabber.getVideoFrameRate());
	        System.out.println("Options: " + grabber.getOptions());
	}
}
	/* -- logs when grabber connects with default settings 
		 Debug: [rtsp @ 0x7f2fbc003600] SDP:
		v=0
		o=- 83750000 1 IN IP4 192.168.42.1
		s=Ambarella streaming
		i=Ambarella streaming
		t=0 0
		a=tool:Ambarella streaming 2012.03.12
		a=type:broadcast
		a=control:*
		a=range:npt=0-
		a=x-qt-text-nam:Ambarella streaming
		a=x-qt-text-inf:Ambarella streaming
		m=video 0 RTP/AVP 96
		c=IN IP4 0.0.0.0
		b=AS:10000
		a=rtpmap:96 H264/90000
		a=fmtp:96 packetization-mode=1;profile-level-id=4D4015;sprop-parameter-sets=J01AFZpkDY/YD7IAAAfSAAHUwcgADDYABhqN3lxkAAYbAAMNRu8uFA==,KO48gA==
		a=control:track1
		m=audio 0 RTP/AVP 97
		b=AS:1000
		a=rtpmap:97 MPEG4-GENERIC/48000/2
		a=fmtp:97 streamtype=5;profile-level-id=1;mode=AAC-hbr;sizelength=13;indexlength=3;indexdeltalength=3;config=1190
		a=control:track2
	 */