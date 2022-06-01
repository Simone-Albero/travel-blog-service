package it.uniroma3.travelblog.presentation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

public class FileStorer {

	public static String uploadDirectory = System.getProperty("user.dir")+"/src/main/resources/static/images/";
	
	
	
	public static String store(MultipartFile file, String owner) {
		new File(uploadDirectory+owner).mkdir();
		Path fileNameAndPath  = Paths.get(uploadDirectory+owner, file.getOriginalFilename());
		try {
			Files.write(fileNameAndPath, file.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileNameAndPath.getFileName().toString();
	}
	
	public static void removeImg(String owner, String name) {
		Path fileNameAndPath  = Paths.get(uploadDirectory+owner+"/"+name);
		try {
			Files.delete(fileNameAndPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeImgAndDir(String owner, String name) {
		removeImg(owner, name);
		removeDirectory(owner);
	}	
	
	public static void removeImgs(String owner, String[] names) {
		for(String name : names) {
			removeImg(owner, name);
		}
	}
	
	public static void removeImgsAndDir(String owner, String[] names) {
		removeImgs(owner, names);
		removeDirectory(owner);
	}
	
	private static void removeDirectory(String owner) {
		new File(uploadDirectory+owner).delete();
	}
	
	public static void dirEmpty(String owner) {
		for(File file : new File(uploadDirectory+owner).listFiles()) {
			file.delete();
		}
	}
		
	

	
}
