package org.toshiba;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileCleanUp {

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yy-MM-dd'T'HH-mm-ss");

	// 25-04-08T17-19-46.mp4
	public static void mpFourAsync(Path directory, long initialDelay, long period, TimeUnit unit) {
		scheduler.scheduleAtFixedRate(() -> {
			mpFour(directory);
		}, initialDelay, period, unit);
	}

	private static void mpFour(Path directory) {
		try {
			Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					System.out.println("Visited file: " + file.toAbsolutePath());
					try {
						String filename = file.getFileName().toString();
						if (filename.endsWith(".mp4") && filename.length() == 21) {
							LocalDateTime fileDate = parseFileDate(filename);
							if (fileDate != null && isOlderThan7Days(fileDate)) {
								deleteFileSafely(file);
							}
						}
					} catch (Exception e) {
						System.err.printf("Error processing file %s: %s%n", file, e.getMessage());
					}
					return FileVisitResult.CONTINUE;
				}
	
				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					System.err.println("Failed to visit file: " + file + " due to " + exc.getMessage());
					return FileVisitResult.CONTINUE;
				}
			});
		}catch(IOException e){
			System.err.printf("Failed to traverse directory %s: %s%n", directory, e.getMessage());
		}
    }

	private static LocalDateTime parseFileDate(String filename) {
		try {
			String datePart = filename.substring(0, filename.lastIndexOf('.'));
			return LocalDateTime.parse(datePart, FILE_DATE_FORMAT);
		} catch (DateTimeParseException e) {
			System.err.printf("Invalid date format in filename %s%n", filename);
			return null;
		} catch (StringIndexOutOfBoundsException e) {
			System.err.printf("Malformed filename %s%n", filename);
			return null;
		}
	}

	private static boolean isOlderThan7Days(LocalDateTime fileDate) {
		return fileDate.isBefore(LocalDateTime.now().minusDays(7));
	}

	private static void deleteFileSafely(Path file) {
		try {
			Files.deleteIfExists(file);
			System.out.printf("Deleted old file: %s%n", file);
		} catch (NoSuchFileException e) {
			System.err.printf("File already deleted: %s%n", file);
		} catch (DirectoryNotEmptyException e) {
			System.err.printf("Cannot delete non-empty directory: %s%n", file);
		} catch (IOException e) {
			System.err.printf("Failed to delete file %s: %s%n", file, e.getMessage());
		} catch (SecurityException e) {
			System.err.printf("Permission denied for file %s: %s%n", file, e.getMessage());
		}
	}

	public static void shutdown() {
		scheduler.shutdown();
		try {
			if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			scheduler.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}
