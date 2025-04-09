package org.toshiba;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ToshibaDownload {

	private static final int CONNECT_TIMEOUT = 15000;
	private static final int READ_TIMEOUT = 30000;
	private static final int THREAD_POOL_SIZE = 4;
	private static final String DOWNLOADS = "Downloads";
	private static final String PHOTOS = "photos";
	private static final String VIDEOS = "videos";

	public void start(String responseFiles) {
		List<String> fileNames = extractFileNames(responseFiles);
		System.out.println("Total number of photos/videos : " + fileNames.size());
		List<String> photoNames = fileNames.stream().filter(x -> !x.endsWith(".mp4")).collect(Collectors.toList());
		System.out.println("Total number of photos : " + photoNames.size());
		downloadAsync(photoNames,PHOTOS);
		List<String> videoNames = fileNames.stream().filter(x -> x.endsWith(".mp4")).collect(Collectors.toList());
		System.out.println("Total number of videos : " + videoNames.size());
		downloadAsync(videoNames, VIDEOS);
	}
    
	/* - Nice and easy for downloading photos.
	 * - Bottleneck for downloading mp4 video if it's larger than 1 GB.
	 * - curl -o /dev/null -w "%{speed_download}\n" http://192.168.42.1/DCIM/100MEDIA/VID00105.mp4
	*/	
	public void downloadAsync(List<String> urls, String folderName) {
		System.setProperty("http.maxConnections", String.valueOf(THREAD_POOL_SIZE));
		System.setProperty("http.keepAlive", "true");
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		for (String each : urls) {
			URL downloadUrl = createUrl(each);
			Path filePath = Paths.get(DOWNLOADS, folderName, each);
			executor.submit(() -> {
				try {
					if (folderName.equals(PHOTOS)) {
						download(downloadUrl, filePath);
					} else {
						Thread.sleep(500 * ThreadLocalRandom.current().nextInt(1, 5));
						downloadVideo(downloadUrl, filePath);
					}
				} catch (IOException e) {
					System.err.println("Failed to download " + downloadUrl + ": " + e.getMessage());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Downloads interrupted");
		}
	}
	
	
	private void downloadVideo(URL videoUrl, Path destination) throws IOException {
		HttpURLConnection connection = null;
		try {
			long fileSize = getContentLength(videoUrl);
			if(fileSize > 1024*1024*1024) { // skipping for more than 1 GB.
				System.out.printf("Skipping file  %s (%,.1f MB)%n", 
						destination.getFileName(), fileSize / (1024.0 * 1024.0));
				
			}
			int timeout = calculateOptimalTimeout(fileSize, destination);
			// 128KB balances throughput and memory usage
			byte[] buffer = new byte[128 * 1024]; 			 
			System.out.printf("Downloading %s (%,.1f MB) | Buffer: %dKB | Timeout: %d min%n", destination.getFileName(),
					fileSize / (1024.0 * 1024.0), buffer.length / 1024, timeout / 60_000);
			Files.createDirectories(destination.getParent());
			Path tempFile = destination.resolveSibling(destination.getFileName() + ".tmp");
			
	        connection = (HttpURLConnection) videoUrl.openConnection();
	        connection.setRequestMethod("GET");
	        connection.setConnectTimeout(5_000);  
	        connection.setReadTimeout(timeout);
	        connection.setDoOutput(false);
	        connection.setUseCaches(false);
	        connection.setRequestProperty("Accept-Encoding", "identity");
	        connection.setRequestProperty("Connection", "keep-alive");
	        connection.setRequestProperty("Cache-Control", "no-cache");
	        
			try (InputStream in = new BufferedInputStream(connection.getInputStream());
					OutputStream out = new BufferedOutputStream(Files.newOutputStream(tempFile))) {

				long totalRead = 0;
				int bytesRead;
				long lastUpdate = System.currentTimeMillis();

				long lastSpeedCheck = System.currentTimeMillis();
				long lastBytes = 0;
				long checkInterval = 5000; 

				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					totalRead += bytesRead;

					long currentTime = System.currentTimeMillis();
					if (currentTime - lastSpeedCheck > checkInterval) {
						double currentSpeed = (totalRead - lastBytes) / ((currentTime - lastSpeedCheck) / 1000.0)
								/ (1024.0 * 1024.0);
						System.out.printf("\nFile: %s, [Thread %d] Speed: %.1f MB/s | ", destination.getFileName(),Thread.currentThread().getId(),
								currentSpeed);

						lastSpeedCheck = currentTime;
						lastBytes = totalRead;
					}

					if (currentTime - lastUpdate > 20_000) {
						double percent = fileSize > 0 ? (totalRead * 100.0 / fileSize) : 0;
						System.out.printf("\rProgress:%s, %.1f%% (%,d/%,d KB)", destination.getFileName(), percent,
								totalRead / 1024, fileSize / 1024);
						lastUpdate = currentTime;
					}
				}

				Files.move(tempFile, destination, StandardCopyOption.REPLACE_EXISTING);
				double totalSpeed = (totalRead / (1024.0 * 1024.0))
						/ ((System.currentTimeMillis() - lastSpeedCheck) / 1000.0);
				System.out.printf("\n Completed: %s (%,.1f MB @ %.1f MB/s)%n", destination.getFileName(),
						totalRead / (1024.0 * 1024.0), totalSpeed);
			}
		} catch (SocketTimeoutException e) {
			throw new IOException("Download timed out after "
					+ (connection != null ? connection.getReadTimeout() / 1000 : "?") + " seconds", e);
		} finally {
			if (connection != null) {
				try {
					InputStream es = connection.getErrorStream();
					if (es != null)
						es.close();
				} catch (IOException ignored) {
				}
				connection.disconnect();
			}
		}
	}

	private long getContentLength(URL url) throws IOException {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.setConnectTimeout(10_000);
			return conn.getContentLengthLong();
		} finally {
			if (conn != null)
				conn.disconnect();
		}
	}
	
	/* - Works only for small size files 
	 * - For large files, MAX_TIMEOUT must be determined dynamically by the timeout calculation
	 * - like, return Math.max(BASE_TIMEOUT, timeout);
	*/
	private int calculateOptimalTimeout(long fileSize, Path destination) {
		final double MB_PER_SECOND_PER_THREAD = 25.0; // 25MB/s per thread
		final int BASE_TIMEOUT = 120_000; // 2 minutes minimum
		final int MAX_TIMEOUT = 600_000; // 10 minutes maximum
		if (fileSize <= 0)
			return BASE_TIMEOUT;
		double effectiveSpeed = MB_PER_SECOND_PER_THREAD * 0.7; // 30% overhead
		double sizeInMB = fileSize / (1024.0 * 1024.0);
		int timeout = (int) ((sizeInMB / effectiveSpeed) * 1000); // ms
		// Add 20% buffer for HTTP overhead
		timeout = (int) (timeout * 1.2);
		System.out.printf("%s | Size: %dMB | Thread-adjusted timeout: %.1f min%n", destination.getFileName(),
				(int) sizeInMB, timeout / 60000.0);
		return Math.max(BASE_TIMEOUT, Math.min(timeout, MAX_TIMEOUT));
	}

	private void download(URL imageUrl, Path destination) throws IOException {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) imageUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);

			int responseCode = connection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new IOException("HTTP " + responseCode);
			}

			Files.createDirectories(destination.getParent());

			try (InputStream in = connection.getInputStream()) {
				long fileSize = connection.getContentLengthLong();
				if (fileSize > 0) {
					System.out.printf("Downloading %s (%,d KB)%n", destination.getFileName(), fileSize / 1024);
				}

				Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Completed: " + destination);
			}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private URL createUrl(String FileName) {
		URL url = null;
		try {
			URI uri = new URI(CameraDefine.FILE_PATH_100MEDIA + FileName);
			url = uri.toURL();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	private List<String> extractFileNames(String html) {
		List<String> fileNames = new ArrayList<>();
		Document doc = Jsoup.parse(html);
		Elements links = doc.select("tr td a.link");
		for (Element link : links) {
			String fileName = link.attr("href");
			if (fileName != null && !fileName.isEmpty()) {
				fileNames.add(fileName);
			}
		}
		return fileNames;
	}
	
}
