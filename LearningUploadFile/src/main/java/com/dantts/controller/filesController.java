package com.dantts.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dantts.models.FilesModel;
import com.dantts.repositories.FilesRepository;

@CrossOrigin
@RestController
@RequestMapping("/api/files")
public class filesController {
	
	@Autowired
	private FilesRepository filesRepository;
	
	@GetMapping
	public ResponseEntity<List<FilesModel>> getAllFiles(){
		List<FilesModel> files = filesRepository.findAll();
		
		return ResponseEntity.ok(files);
	
	}
	
	@GetMapping("/download/{id}/{fileName:.*}")
	public ResponseEntity<?> downloads(@PathVariable Long id, @PathVariable String fileName){
		Path path = Paths.get(System.getProperty("user.dir") + "/temp/uploads/" + fileName);
		
		Resource resource = null;
		
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		Optional<FilesModel> file = filesRepository.findById(id);
		
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.get().getName().toString() + "\"")
				.body(resource);
	}
	
	
	@PostMapping("/uploads")
	public ResponseEntity<?> multiUploads(@RequestParam("files") MultipartFile[] files) {
		List<Object> fileDownloadUrls = new ArrayList<>();
		Arrays.asList(files)
				.stream()
				.forEach(file -> fileDownloadUrls.add(uploads(file).getBody()));
		return ResponseEntity.ok(fileDownloadUrls);
	}
	
	public ResponseEntity<String> uploads(MultipartFile file) {
		FilesModel newFile = new FilesModel();
		
		UUID randomUUID = UUID.randomUUID(); 
	    String randomName = randomUUID.toString().replaceAll("-", "");
		String fileName = StringUtils.cleanPath(randomName + "-" +file.getOriginalFilename());
		
		Path diretory = Paths.get(System.getProperty("user.dir"), "/temp/uploads");
		Path path = diretory.resolve(fileName);
		
		try {
			Files.createDirectories(diretory);
			file.transferTo(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		newFile.setName(file.getOriginalFilename());
		newFile.setSystemName(fileName);
		newFile.setSize(file.getSize());
		
		FilesModel currentFiles = filesRepository.save(newFile);
		
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/files/download/")
				.path(currentFiles.getId().toString())
				.path("/")
				.path(fileName)
				.toUriString();
		
		
		currentFiles.setDownload(fileDownloadUri);
		
		
		filesRepository.save(currentFiles);
		
		return ResponseEntity.ok(fileDownloadUri);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> deleteFiles(@PathVariable Long id) {
		Optional<FilesModel> currentFile = filesRepository.findById(id);
		
		if(currentFile.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
		}
		
		Path diretory = Paths.get(System.getProperty("user.dir") + "/temp/uploads/" + currentFile.get().getSystemName());
		
		
		boolean fileDeleted = false;
		try {
			fileDeleted = Files.deleteIfExists(diretory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!fileDeleted) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		filesRepository.deleteById(id);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
}

