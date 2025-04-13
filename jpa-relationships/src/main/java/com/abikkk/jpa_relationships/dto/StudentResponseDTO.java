package com.abikkk.jpa_relationships.dto;

import com.abikkk.jpa_relationships.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
public class StudentResponseDTO {
    private List<StudentDTO> students;
    private long totalStudents;
    private int totalPages;
}
