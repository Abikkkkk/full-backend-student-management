package com.abikkk.jpa_relationships.repository;

import com.abikkk.jpa_relationships.model.Contact;
import com.abikkk.jpa_relationships.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact,Long> {

}

