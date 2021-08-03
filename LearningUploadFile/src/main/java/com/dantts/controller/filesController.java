package com.dantts.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/api/files")
public class filesController {
	
	@GetMapping("/{fileName:.*}")
	public ResponseEntity<?> downloads(@PathVariable String fileName){
		Path path = Paths.get(System.getProperty("user.dir") + "/temp/uploads/" + fileName);
		
		Resource resource = null;
		
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	@PostMapping("/uploads")
	public ResponseEntity<String> uploads(@RequestParam MultipartFile file) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		Path diretory = Paths.get(System.getProperty("user.dir"), "/temp/uploads");
		Path path = diretory.resolve(file.getOriginalFilename());
		
		try {
			Files.createDirectories(diretory);
			file.transferTo(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/files/")
				.path(fileName)
				.toUriString();
		
		return ResponseEntity.ok(fileDownloadUri);
	}
	
	@PostMapping("/multi-uploads")
	public ResponseEntity<?> multiUploads(@RequestParam("files") MultipartFile[] files) {
		List<Object> fileDownloadUrls = new ArrayList<>();
		Arrays.asList(files)
				.stream()
				.forEach(file -> fileDownloadUrls.add(uploads(file).getBody()));
		return ResponseEntity.ok(fileDownloadUrls);
	}
	
	
}

