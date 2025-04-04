package org.toshiba;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	private static String url = "rtsp://192.168.42.1/AmbaStreamTest";
	private static String outputFile = "output.mp4";
	private static Path filePath = Paths.get("Recordings", outputFile);
	
	private FFmpegFrameGrabber grabber;
	private FFmpegFrameRecorder recorder;
	private ExecutorService executor;

	public void start() {
		this.grabber = new FFmpegFrameGrabber(url);
		this.recorder = null;
		this.executor = Executors.newSingleThreadExecutor();

		Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));

		Future<Boolean> future = executor.submit(() -> {
			try {

				System.out.println("Attempting to connect to RTSP stream...");
				grabber.start();
				System.out.println("Connected successfully!");
				return true;
			} catch (Exception e) {
				System.err.println("Error: Unable to connect to RTSP stream!");
				e.printStackTrace();
				return false;
			}
		});

		

		try {
			boolean connected = future.get(10, TimeUnit.SECONDS);
			if (connected) {

				System.out.printf(
						"ImageWidth %d, ImageHeight %d , AudioChannels %d ,FrameRate %s ,PixelFormat %d ,SampleRate %d \n",
						grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels(),
						grabber.getFrameRate(), grabber.getPixelFormat(), grabber.getSampleRate());

				recorder = new FFmpegFrameRecorder(filePath.toAbsolutePath().toString(), grabber.getImageWidth(),
						grabber.getImageHeight(), grabber.getAudioChannels());

				settingFFmpegFrameRecorder();
				validationCheck();

				FFmpegLogCallback.set();
				avutil.av_log_set_level(avutil.AV_LOG_DEBUG);

				int frameCount = 0;
				if (grabber.getFrameRate() > 0) {
					recorder.start();

					Frame frame;
					int timeoutCounter = 0;
					final int MAX_TIMEOUTS = 30; // ~5 sec at 30fps

					while ((frame = grabber.grab()) != null) {
						try {
							// Reset timeout counter on successful frame
							timeoutCounter = 0;

							if (frame.image != null || frame.samples != null) {
								System.out.printf("--- Frame Count %d ---%n", frameCount++);
								recorder.record(frame);
							}

						} catch (Exception e) {
							if (++timeoutCounter > MAX_TIMEOUTS) {
								throw new IOException("Stream timeout exceeded");
							}
							System.err.println("Frame error: " + e.getMessage());
						}
					}
				}

				recorder.stop();
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
			cleanup();
		}
	}

	private void settingFFmpegFrameRecorder() {
		recorder.setFormat("mp4");
		recorder.setVideoOption("movflags", "frag_keyframe+empty_moov+faststart");
		recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
		recorder.setFrameRate(grabber.getFrameRate());
		recorder.setVideoBitrate(2500000);
		recorder.setPixelFormat(avutil.AV_PIX_FMT_YUVJ420P);
		recorder.setVideoOption("color_range", "pc");

		// Audio settings
		recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
		recorder.setAudioBitrate(128000);
		recorder.setSampleRate(grabber.getSampleRate());
	}

	private void validationCheck() throws IOException {
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

}
