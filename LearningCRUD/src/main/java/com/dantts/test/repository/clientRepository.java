package com.dantts.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dantts.test.model.Client;

@Repository
public interface clientRepository extends JpaRepository<Client, Long>{

}
