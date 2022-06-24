package it.uniroma3.travelblog.presentation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public class FileStorer {

	public static String uploadDirectory = System.getProperty("user.dir")+"/src/main/resources/static/images/";	
	
	private static String setupDirName(String owner) {
		return uploadDirectory+owner;
	}
	
	public static String store(MultipartFile file, String owner) {
		new File(setupDirName(owner)).mkdirs();
		Path fileNameAndPath  = Paths.get(setupDirName(owner), file.getOriginalFilename());
		System.out.println(fileNameAndPath);
		try {
			Files.write(fileNameAndPath, file.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileNameAndPath.getFileName().toString();
	}
	
	public static void removeImg(String owner, String name) {
		Path fileNameAndPath  = Paths.get(setupDirName(owner)+"/"+name);
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
		new File(setupDirName(owner)).delete();
	}
	
	public static void dirEmpty(String owner) {
		for(File file : new File(setupDirName(owner)).listFiles()) {
			if(file.isDirectory()) dirEmptyEndDelete(owner + "/" + file.getName()); 
			try {
				Files.delete(file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void dirEmptyEndDelete(String owner) {
		dirEmpty(owner);
		removeDirectory(owner);
	}
	
	public static void dirRename(String oldName, String newName) {
		new File(setupDirName(oldName)).renameTo(new File(setupDirName(newName)));
	}
		
	

	
}
