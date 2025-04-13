package com.abikkk.jpa_relationships.controller;

import com.abikkk.jpa_relationships.config.ModelMapperConfig;
import com.abikkk.jpa_relationships.dto.StudentDTO;
import com.abikkk.jpa_relationships.dto.StudentResponseDTO;
import com.abikkk.jpa_relationships.model.Contact;
import com.abikkk.jpa_relationships.model.Department;
import com.abikkk.jpa_relationships.model.Project;
import com.abikkk.jpa_relationships.model.Student;
import com.abikkk.jpa_relationships.service.StudentService;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public StudentDTO saveStudent(@RequestBody Student student) {
        Student savedStudent = studentService.saveStudent(student);
        return modelMapper.map(student,StudentDTO.class);
    }

    @PostMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<StudentDTO> saveAllStudents(@RequestBody List<Student> students) {
        List<Student> savedStudents = studentService.saveAllStudents(students);
        return savedStudents.stream()
                .map(student -> modelMapper.map(student,StudentDTO.class))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public StudentDTO updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return modelMapper.map(studentService.updateStudent(id,student),StudentDTO.class);
    }

    @PostMapping("/{id}/contacts")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Student> addContact(@PathVariable Long id, @RequestBody Contact contact) {
        Student updatedStudent = studentService.addContactByStudentId(id, contact);
        return ResponseEntity.ok(updatedStudent);
    }

    @PostMapping("/{id}/projects")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Student> addProject(@PathVariable Long id, @RequestBody Project project) {
        Student updatedStudent = studentService.addProjectByStudentId(id, project);
        return ResponseEntity.ok(updatedStudent);
    }

    @PostMapping("/{id}/department")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Student> addDepartment(@PathVariable Long id, @RequestBody Department department) {
        Student updatedStudent = studentService.addDepartmentByStudentId(id, department);
        return ResponseEntity.ok(updatedStudent);
    }

    //pagination
    @GetMapping("/page/{pageNo}/{pageSize}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Page<Student> getStudents(@PathVariable int pageNo,
                                     @PathVariable int pageSize) {
        return studentService.getStudentsByPage(pageNo, pageSize);
    }

    //pagination using criteria query
    @GetMapping("/pagecriteria/{pageNumber}/{pageSize}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<Student>> getStudentsPaged(@PathVariable int pageNumber, @PathVariable int pageSize) {
        return new ResponseEntity<>(studentService.getStudentsByPageCriteria(pageNumber, pageSize), HttpStatus.OK);
    }

    //pagination and sorting and search filtering in single api.
    @GetMapping("/pageandsort")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<StudentResponseDTO> getStudentsPagedSorted(@RequestParam(defaultValue = "1") int pageNumber,
                                                                     @RequestParam(defaultValue = "10") int pageSize,
                                                                     @RequestParam(defaultValue = "null") String sortBy,
                                                                     @RequestParam(defaultValue = "null") String sortOrder,
                                                                     @RequestParam String search
    ) {

        List<Student> students = studentService.getStudentsByPageCriteria(pageNumber, pageSize, sortBy, sortOrder, search);

        List<StudentDTO> studentDto = students.stream().map(
                student -> modelMapper.map(student, StudentDTO.class)
        ).collect(Collectors.toList());

        long totalStudents = studentService.getTotalStudentsCount(search);
        int totalPages = studentService.getTotalPages(pageSize,search);

        StudentResponseDTO responseDTO = new StudentResponseDTO();
        responseDTO.setStudents(studentDto);
        responseDTO.setTotalStudents(totalStudents);
        responseDTO.setTotalPages(totalPages);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);

    }
    //Java Streams
    @GetMapping("/details/{name}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<Student>> getStudentDetails(@PathVariable String name){
        return new ResponseEntity<>(studentService.getStudentByName(name),HttpStatus.OK);
    }



}