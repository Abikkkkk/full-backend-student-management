package com.abikkk.jpa_relationships.repository;

import com.abikkk.jpa_relationships.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {

}
