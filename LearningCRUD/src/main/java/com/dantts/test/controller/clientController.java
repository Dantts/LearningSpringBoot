package com.dantts.test.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dantts.test.model.Client;
import com.dantts.test.repository.clientRepository;


@RestController
@RequestMapping("/clients")
public class clientController {
	
	@Autowired
	private clientRepository repository;
	
	
	@GetMapping()
	public ResponseEntity<List<Client>> getAllClients(){
		List<Client> clientsFound = repository.findAll();
		return ResponseEntity.status(HttpStatus.OK).body(clientsFound);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Optional<Client>> getClient(@PathVariable long id){
		Optional<Client> optionalClientFound = repository.findById(id);
		
		
		if(optionalClientFound.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(optionalClientFound);		
		}
		return ResponseEntity.status(HttpStatus.OK).body(optionalClientFound);

	}
	
	@PostMapping()
	public ResponseEntity<Client> postClient(@RequestBody Client client) {
		Client newClient = repository.save(client);
		return ResponseEntity.ok(newClient);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Client> putClient(@PathVariable long id, @RequestBody Client client){
		return repository.findById(id).map(data -> {
			data.setName(client.getName());
			Client updated = repository.save(data);
			return ResponseEntity.ok().body(updated);
		}).orElse(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteClient(@PathVariable long id){
		
		return repository.findById(id).map(data -> {
			repository.deleteById(id);
			return ResponseEntity.ok().body("Client Deleted!");
		}).orElse(ResponseEntity.badRequest().body("ERROR to delete this client."));
	}
	
}
