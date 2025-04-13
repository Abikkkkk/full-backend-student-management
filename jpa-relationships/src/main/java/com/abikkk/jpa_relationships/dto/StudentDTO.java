package com.abikkk.jpa_relationships.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
public class StudentDTO {
    private Long id;
    private String name;
    private String email;
    private DepartmentDTO department;
    private List<ContactDTO> contacts;
    private Set<ProjectDTO> projects;
}
