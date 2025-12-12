package com.erling.utils.fileU;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
public class FileUtils {

    public static void saveFile(byte[] file, String path,String fileName) throws IOException {
        Path dirPath = Paths.get(path);
        if(!Files.exists(dirPath)){
            Files.createDirectories(dirPath);
        }
        Path filePath = dirPath.resolve(fileName);
        Files.write(filePath, file, StandardOpenOption.CREATE_NEW); // 写入文件
    }
    public static byte[] readFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }
    public static void deleteFile(String path) throws IOException {
        Path filePath = Paths.get(path);
        if(Files.exists(filePath)){
            Files.delete(filePath);
        }
    }
    public static boolean isImage(String path) {
        try {
            BufferedImage image = ImageIO.read(Paths.get(path).toFile());
            return image != null;
        } catch (Exception e) { // 捕获所有异常
            return false;
        }
    }
}
