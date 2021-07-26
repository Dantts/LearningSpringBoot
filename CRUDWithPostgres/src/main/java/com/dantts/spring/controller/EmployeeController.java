package com.dantts.spring.controller;

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

import com.dantts.spring.model.Employee;
import com.dantts.spring.repository.EmployeeRepository;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
	
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	//Get employees
	@GetMapping()
	public ResponseEntity<List<Employee>> getEmployees(){
		List<Employee> employees = employeeRepository.findAll();
		
		return ResponseEntity.status(HttpStatus.OK).body(employees);
	}
	
	
	//Get employee by id
	@GetMapping("/{id}")
	public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
		Optional<Employee> employees = employeeRepository.findById(id);
		
		if(employees.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(employees.get());
	}
	
	//Post a employee
	@PostMapping()
	public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
		Employee createdEmployee = employeeRepository.save(employee);
		
		return ResponseEntity.status(HttpStatus.OK).body(createdEmployee);
	}
	
	//Update a employee
	@PutMapping("/{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails){
		Optional<Employee> currentEmployee = employeeRepository.findById(id);
		
		if(currentEmployee.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		currentEmployee.get().setName(employeeDetails.getName());
		currentEmployee.get().setLastName(employeeDetails.getLastName());
		currentEmployee.get().setEmail(employeeDetails.getEmail());
		
		Employee updatedEmployee = employeeRepository.save(currentEmployee.get());
		
		return ResponseEntity.status(HttpStatus.OK).body(updatedEmployee);
	}
	
	//Delete a employee
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
		Optional<Employee> employeeDeleted = employeeRepository.findById(id);
		
		if(employeeDeleted.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
				
		employeeRepository.deleteById(id);
		
		
		return ResponseEntity.status(HttpStatus.OK).build();		
	}
}




