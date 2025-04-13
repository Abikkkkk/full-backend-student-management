package com.abikkk.jpa_relationships.service;

import com.abikkk.jpa_relationships.model.Department;
import com.abikkk.jpa_relationships.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    // Create operation
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    // Read operations
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }


    // Update operation
    public Department updateDepartment(Long id, Department departmentDetails) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        department.setName(departmentDetails.getName());
        department.setLocation(departmentDetails.getLocation());

        return departmentRepository.save(department);
    }

    // Delete operation
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
}
