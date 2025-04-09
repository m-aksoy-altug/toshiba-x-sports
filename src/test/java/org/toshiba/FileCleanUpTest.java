package org.toshiba;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FileCleanUpTest {
	
	private static Path tempDir;
	private static final DateTimeFormatter FILE_FORMAT = 
	        DateTimeFormatter.ofPattern("yy-MM-dd'T'HH-mm-ss");
    
    @BeforeAll
    static void setup() throws IOException {
        tempDir = Files.createTempDirectory("RecordingsTest");
    }
    
    @AfterAll
    static void cleanup() throws IOException {
    	FileCleanUp.shutdown();
        Files.walk(tempDir)
             .sorted(Comparator.reverseOrder())
             .forEach(path -> {
                 try { Files.deleteIfExists(path); } 
                 catch (IOException e) {}
             });
    }
    
    @Test
    void deleteOldFile() throws IOException, InterruptedException {        
    	 Path oldFile = createTestFile("25-03-01T12-00-00.mp4");
         Path newFile = createTestFile(LocalDateTime.now().format(FILE_FORMAT) + ".mp4");
         Path invalidFormat = createTestFile("invalid.mp4");
         Path wrongExtension = createTestFile(LocalDateTime.now().format(FILE_FORMAT) + ".txt");
         Path futureFile = createTestFile(LocalDateTime.now().plusDays(1).
        		 format(DateTimeFormatter.ofPattern("yy-MM-dd'T'HH-mm-ss")) + ".mp4");
         
        FileCleanUp.mpFourAsync(tempDir,0,100, TimeUnit.MILLISECONDS);
        
        long start = System.currentTimeMillis();
        while (Files.exists(oldFile) && System.currentTimeMillis() - start < 2000) {
            Thread.sleep(100);
        }	
        assertFalse(Files.exists(oldFile), "Delete old file with correct format");
        assertTrue(Files.exists(newFile), "Keep new file with correct format");
        assertTrue(Files.exists(invalidFormat), "Keep invalid format file");
        assertTrue(Files.exists(wrongExtension), "Keep wrong extension file ");
        assertTrue(Files.exists(futureFile), "Keep future-date file");
     }
    
    private Path createTestFile(String filename) throws IOException {
        Path file = tempDir.resolve(filename);
        Files.createFile(file);
        return file;
    }
    
    
}
