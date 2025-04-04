package org.toshiba;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.toshiba.controller.CameraController;
import org.toshiba.controller.CameraStatusReceiver;
import org.toshiba.controller.RemoteReceiverAdapter;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;

public class Main {
	// exec:java -Dexec.mainClass="org.toshiba.Main"
	public static void main(String[] args) throws InterruptedException, IOException {
//		RemoteReceiverAdapter receiver = new CameraStatusReceiver();
//		 CameraController cameraController = new CameraController(receiver);
//		 cameraController.getCameraInfo();
//	"rtsp://192.168.42.1/AmbaStreamTest";
		
		String response = new OkHttpClient().newCall(new Request.Builder()
		    .url(CameraDefine.GET_CAMERA_INFO)
		    .build()).execute().body().string();
		System.out.println("Camera status: " + response);
		
		String responseShoot = new OkHttpClient().newCall(new Request.Builder()
			    .url(CameraDefine.VIDEO_SETTING_2)
			    .build()).execute().body().string();
			System.out.println("Camera Shoot: " + responseShoot);
		
		String responseFrame = new OkHttpClient().newCall(new Request.Builder()
			    .url(CameraDefine.VIDEO_FRAME)
			    .build()).execute().body().string();
			System.out.println("Camera Frame: " + responseFrame);
			
			new ToshibaRecorder().start();
//		 String url= "rtsp://192.168.42.1/AmbaStreamTest";   
//			FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(url);
////			FFmpegFrameRecorder recorder=null;
//			AtomicReference<FFmpegFrameRecorder> recorderRef = new AtomicReference<>();
//			
//			
//			
//			/// Timeout Parameters (in microseconds)
//			grabber.setOption("timeout", "10000000");    // General timeout
//			grabber.setOption("stimeout", "10000000");   // Socket timeout
////			grabber.setOption("analyzeduration", "100000"); // Faster stream analysis
//
//			// RTSP-specific optimizations
////			grabber.setOption("rtsp_transport", "tcp"); // Force TCP transport
////			grabber.setOption("rtsp_flags", "prefer_tcp");
//			grabber.setOption("buffer_size", "425984"); // 512KB buffer // 425984
//			grabber.setOption("fflags", "nobuffer");    // Reduce latency
//			grabber.setOption("flags", "low_delay");    // Reduce buffering
//			grabber.setOption("rtsp_transport", "udp");
//			
//	        ExecutorService executor = Executors.newSingleThreadExecutor();
//	        
//	        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                System.out.println("\nShutdown signal received. Cleaning up...");
//                cleanup(grabber, recorderRef, executor);
//            }));
//	        
//	        Future<Boolean> future = executor.submit(() -> {
//		        try {
//	        	     
//		        	System.out.println("Attempting to connect to RTSP stream...");
//		        	grabber.start();
//		            System.out.println("Connected successfully!");
//		             return true;
//		        } catch (Exception e) {
//		        	 System.err.println("Error: Unable to connect to RTSP stream!");
//		            e.printStackTrace();
//		            return false;
//		        }
//	        });
//	        startRecording2(executor, future,grabber,recorderRef);
//	        startRecording(executor, future,grabber);
	    }
	
	private static void startRecording2(ExecutorService executor, Future<Boolean> future,FFmpegFrameGrabber grabber, AtomicReference<FFmpegFrameRecorder> recorderRef ) {
		String outputFile = "output.mp4";
		Path filePath= Paths.get("Recordings",outputFile);
		
		
		try {
        	boolean connected = future.get(10, TimeUnit.SECONDS); 
            if (connected) {
                
            	System.out.printf("ImageWidth %d, ImageHeight %d , AudioChannels %d ,FrameRate %s ,PixelFormat %d ,SampleRate %d \n", 
            			grabber.getImageWidth(), grabber.getImageHeight(),
            			grabber.getAudioChannels(),grabber.getFrameRate(),
            			grabber.getPixelFormat(),grabber.getSampleRate());
            	
            	recorderRef.set(new FFmpegFrameRecorder(filePath.toAbsolutePath().toString(), 
	            		grabber.getImageWidth(), grabber.getImageHeight(), 
	            		grabber.getAudioChannels()));
            	
            	
            	recorderRef.get().setFormat("mp4");
            	recorderRef.get().setVideoOption("movflags", "frag_keyframe+empty_moov+faststart");
            	recorderRef.get().setVideoCodec(avcodec.AV_CODEC_ID_H264);
            	recorderRef.get().setFrameRate(grabber.getFrameRate());
	            recorderRef.get().setVideoBitrate(2500000);
	            recorderRef.get().setPixelFormat(avutil.AV_PIX_FMT_YUVJ420P);
	            recorderRef.get().setVideoOption("color_range", "pc");
	            
	            // Audio settings
	            recorderRef.get().setAudioCodec(avcodec.AV_CODEC_ID_AAC);
	            recorderRef.get().setAudioBitrate(128000);
	            recorderRef.get().setSampleRate(grabber.getSampleRate());
	         
	         
	            if (grabber.getImageWidth() <= 0 || grabber.getImageHeight() <= 0) {
	                throw new IOException("Invalid video dimensions");
	            }
	            
	         // 2. Verify frame rate is sane
	            if (grabber.getFrameRate() <= 0 || grabber.getFrameRate() > 120) {
	                System.out.println("Adjusting invalid framerate to 30fps");
	                grabber.setFrameRate(30); // Force sane default
	            }
	            
	         // 3. Audio channel validation
	            if (grabber.getAudioChannels() <= 0) {
	                System.out.println("No audio detected - disabling audio recording");
	                recorderRef.get().setAudioChannels(0); // Disable audio
	            }
	            FFmpegLogCallback.set();
	            avutil.av_log_set_level(avutil.AV_LOG_DEBUG);
	            
	            
	            int frameCount = 0;
	         // Start recording only if grabber is truly active
	            if (grabber.getFrameRate() > 0) {
	          	
	                recorderRef.get().start();
	                
	                Frame frame;
	                int timeoutCounter = 0;
	                final int MAX_TIMEOUTS = 30; // ~5 sec at 30fps
	                
	                while ((frame = grabber.grab()) != null) {
	                    try {
	                        // Reset timeout counter on successful frame
	                        timeoutCounter = 0; 
	                 	 	 
	                        if (frame.image != null || frame.samples != null) {
	                        	System.out.printf("--- Frame Count %d ---%n", frameCount++);
	                        	recorderRef.get().record(frame);
	                        }
	                        
	                    } catch (Exception e) {
	                        if (++timeoutCounter > MAX_TIMEOUTS) {
	                            throw new IOException("Stream timeout exceeded");
	                        }
	                        System.err.println("Frame error: " + e.getMessage());
	                    }
	                }
	            }
	           
	            recorderRef.get().stop();
	            grabber.stop();
	            System.out.println("Recording saved to: " + outputFile);
            } else {
                System.err.println("Failed to connect, exiting.");
            }
        } catch (TimeoutException e) {
            System.err.println("Connection timed out!");
            future.cancel(true); // Interrupt the connection attempt
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	 try {
        		 if (recorderRef.get() != null) {
        			 recorderRef.get().setVideoOption("flush_packets", "1");
        			 recorderRef.get().stop();
        			 recorderRef.get().release();
                 }
                 if (grabber != null) {
                     grabber.stop();
                     grabber.release();
                 }
             	} catch (Exception e) {
             		System.err.println("Error during grabber cleanup: " + e.getMessage());
             	}
             executor.shutdownNow();
             System.out.println("Program exited.");
         }
    }
		
	
	
	private static void cleanup(FFmpegFrameGrabber g, AtomicReference<FFmpegFrameRecorder> r, ExecutorService e) {
        try {
            if (r.get() != null) {
                r.get().setVideoOption("flush_packets", "1");
                r.get().stop();
                r.get().release();
            }
            if (g != null) {
                g.stop();
                g.release();
            }
            if (e != null) {
                e.shutdownNow();
            }
            System.out.println("Cleanup complete.");
            System.out.flush(); // Force output
        } catch (Exception ex) {
            System.err.println("Cleanup error: " + ex.getMessage());
        }
    }
	
	
		/*
		 * Resolution/Bitrate Guide:
		 * - Resolution	Recommended Bitrate 
		 * - 720p (1280x720)	1.5 Mbps 
		 * - 1080p (1920x1080)	2.5 Mbps 
		 * - 4K (3840x2160)	8-12 Mbps
		*/
//	private static void startRecording(ExecutorService executor, Future<Boolean> future,FFmpegFrameGrabber grabber) {
//		String outputFile = "output.mp4";
//		Path filePath= Paths.get("Recordings",outputFile);
//		double ntscFramerate = 30000.0 / 1001.0; 
//		try {
//        	boolean connected = future.get(10, TimeUnit.SECONDS); 
//            if (connected) {
//                
//            	System.out.printf("ImageWidth %d, ImageHeight %d , AudioChannels %d ,FrameRate %s ,PixelFormat %d ,SampleRate %d \n", 
//            			grabber.getImageWidth(), grabber.getImageHeight(),
//            			grabber.getAudioChannels(),grabber.getFrameRate(),
//            			grabber.getPixelFormat(),grabber.getSampleRate());
//            	
//	            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(filePath.toAbsolutePath().toString(), 
//	            		grabber.getImageWidth(), grabber.getImageHeight(), 
//	            		grabber.getAudioChannels());
//
//	         // Video settings
//	            recorder.setFormat("mp4");
//	            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//	            recorder.setFrameRate(grabber.getFrameRate());
//	            recorder.setVideoBitrate(2500000);
//	            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUVJ420P);
//	            recorder.setVideoOption("color_range", "pc");
//	            
//	            // Audio settings
//	            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
//	            recorder.setAudioBitrate(128000);
//	            recorder.setSampleRate(grabber.getSampleRate());
//	         
//	            recorder.start();
//	            avutil.av_log_set_level(avutil.AV_LOG_INFO);
//	            int frameCount = 0;
//	            Frame frame;
//	            while ((frame = grabber.grab()) != null) {
//	            	 System.out.printf("--- Frame %d @ %s fs ---%n", frameCount++, frame.timestamp);
//	                try {
//	                    // Record video frame if present
//	                	if (frame.image != null && frame.image.length > 0) {
//	                		 ByteBuffer imageBuffer = (ByteBuffer)frame.image[0];
//	                    	 
//	                		// Print basic video info
//	                         System.out.printf("Video: %dx%d, %s, %d buffers, stride: %d%n",
//	                             frame.imageWidth,
//	                             frame.imageHeight,
//	                             frame.image[0].getClass().getSimpleName(),
//	                             frame.image.length,
//	                             frame.imageStride);
//	                         
//	                         // Sample first few bytes of YUV data
//	                         imageBuffer.rewind();
//	                         System.out.print("First 16 Y bytes: ");
//	                         for (int i = 0; i < 16 && imageBuffer.hasRemaining(); i++) {
//	                             System.out.printf("%02X ", imageBuffer.get() & 0xFF);
//	                         }
//	                         System.out.println();
//	                    }
//	                	// AUDIO DEBUG
//	                    if (frame.samples != null && frame.samples.length > 0) {
//	                        ShortBuffer samples = (ShortBuffer)frame.samples[0];
//	                        
//	                        System.out.printf("Audio: %d samples (%.1fms), %d channels%n",
//	                            samples.remaining(),
//	                            samples.remaining() * 1000.0 / grabber.getSampleRate(),
//	                            frame.samples.length);
//	                        
//	                        // Print first 8 samples (4 stereo pairs)
//	                        samples.rewind();
//	                        for (int i = 0; i < 8 && samples.hasRemaining(); i++) {
//	                            System.out.printf("%6d", samples.get());
//	                            if (i % 2 == 1) System.out.println(); // Newline after stereo pairs
//	                        }
//	                    }
//	                    recorder.record(frame);
////	                    // Record audio if present
////	                    if (frame.samples != null) {
////	                    	recorder.record(frame);
////	                    }
//	                } catch (FFmpegFrameRecorder.Exception e) {
//	                    System.err.println("Frame recording error: " + e.getMessage());
//	                    // Continue to next frame instead of breaking
//	                }
//	            }   
//
//	            recorder.stop();
//	            grabber.stop();
//	            System.out.println("Recording saved to: " + outputFile);
//            	
//            } else {
//                System.err.println("Failed to connect, exiting.");
//            }
//        } catch (TimeoutException e) {
//            System.err.println("Connection timed out!");
//            
//            future.cancel(true); // Interrupt the connection attempt
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//        	 try {
//                 grabber.stop();
//                 grabber.release();
//             	} catch (Exception e) {
//             		System.err.println("Error during grabber cleanup: " + e.getMessage());
//             	}
//             executor.shutdownNow();
//             System.out.println("Program exited.");
//         }
//    }
		
}
