package com.dantts.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dantts.models.FilesModel;


@Repository
public interface FilesRepository extends JpaRepository<FilesModel, Long>{

}
