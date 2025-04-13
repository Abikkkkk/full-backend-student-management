package com.abikkk.jpa_relationships.service;

import com.abikkk.jpa_relationships.model.Contact;
import com.abikkk.jpa_relationships.model.Department;
import com.abikkk.jpa_relationships.model.Project;
import com.abikkk.jpa_relationships.model.Student;
import com.abikkk.jpa_relationships.repository.ContactRepository;
import com.abikkk.jpa_relationships.repository.DepartmentRepository;
import com.abikkk.jpa_relationships.repository.ProjectRepository;
import com.abikkk.jpa_relationships.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {
     @Autowired
     private StudentRepository studentRepository;

     @Autowired
     private DepartmentRepository departmentRepository;

     @Autowired
     private ContactRepository contactRepository;

     @Autowired
     private ProjectRepository projectRepository;

     @Autowired
     private EntityManager entityManager;

     public List<Student> getAllStudents() {
         return studentRepository.findAll();
     }

     public Student getStudentById(Long id) {
         return studentRepository.findById(id).orElse(null);
     }

     public Student saveStudent(Student student) {
         //when given id -> departmentEntity.
         if (student.getDepartment() != null && student.getDepartment().getId() != null) {
             Department department = departmentRepository.findById(student.getDepartment().getId()).orElseThrow(
                     () -> new RuntimeException("Department not found.")
             );
             student.setDepartment(department);
         }
         //when given id -> projectsEntity.
         if ( student.getProjects() != null && !student.getProjects().isEmpty()) {
             Set<Project> projects = student.getProjects().stream()
                     .map(p -> projectRepository.findById(p.getId())
                             .orElseThrow(() -> new RuntimeException("Project not found")))
                     .collect(Collectors.toSet());

             student.setProjects(projects);
         }
         return studentRepository.save(student);
     }

     public List<Student> saveAllStudents(List<Student> students){
         for(Student student : students){
             //when given id -> departmentEntity.
             if (student.getDepartment() != null && student.getDepartment().getId() != null) {
                 Department department = departmentRepository.findById(student.getDepartment().getId()).orElseThrow(
                         () -> new RuntimeException("Department not found.")
                 );
                 student.setDepartment(department);
             }
             //when given id -> projectsEntity.
             if ( student.getProjects() != null && !student.getProjects().isEmpty()) {
                 Set<Project> projects = student.getProjects().stream()
                         .map(p -> projectRepository.findById(p.getId())
                                 .orElseThrow(() -> new RuntimeException("Project not found")))
                         .collect(Collectors.toSet());

                 student.setProjects(projects);
             }
         }
         return studentRepository.saveAll(students);
     }

     public void deleteStudent(Long id) {
            studentRepository.deleteById(id);
     }

     public Student updateStudent(Long id, Student student) {
         //studentService.updateStudent(id, student);
         Student existingStudent = studentRepository.findById(id).orElseThrow(
                 ()-> new RuntimeException("Student with id " + id + " not found"));
         //student fields
         if(student.getName()!=null) {
             existingStudent.setName(student.getName());
         }
         if(student.getEmail()!=null) {
             existingStudent.setEmail(student.getEmail());
         }
         //department
         if(student.getDepartment()!=null) {
             existingStudent.setDepartment(student.getDepartment());
         }
         //project
         if(student.getProjects()!=null && !student.getProjects().isEmpty()) {
             existingStudent.getProjects().clear();
             existingStudent.getProjects().addAll(student.getProjects());
         }
         //contact
         if(student.getContacts()!=null && !student.getContacts().isEmpty()) {
             existingStudent.getContacts().clear();
             existingStudent.getContacts().addAll(student.getContacts());
         }
         return studentRepository.save(existingStudent);
     }

    public Student addContactByStudentId(Long studentId, Contact contact) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.getContacts().add(contact);
        return studentRepository.save(student);
    }

    public Student addProjectByStudentId(Long studentId, Project project) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.getProjects().add(project);
        return studentRepository.save(student);
    }

    public Student addDepartmentByStudentId(Long studentId, Department department) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setDepartment(department);
        return studentRepository.save(student);
    }

    //Pagination using Normal Approacj.
    public Page<Student> getStudentsByPage(int pageNo, int pageSize)
    {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        return studentRepository.findAll(pageable);
    }

    //Pagination using Criteria Query.
    //first method : for getting paged data
    public List<Student> getStudentsByPageCriteria(int pageNumber, int pageSize,
                                                   String sortBy, String sortOrder,
                                                   String search ){
        //Setting Up.
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
        Root<Student> root = criteriaQuery.from(Student.class);
        //i.e. SELECT * FROM DB
        criteriaQuery.select(root);
        //Sorting
        //Since sorting and paging is implemented in a single api we have to check if sort field is not null,
        //and then implement sorting (inside which the data will be sorted and after then executed using TypedQuery).
        if(sortBy != null && !sortBy.isEmpty()){
            Path<?> path = root.get(sortBy);
            if(sortOrder.equalsIgnoreCase("desc")){
                criteriaQuery.orderBy(criteriaBuilder.desc(path));
            }else{
                criteriaQuery.orderBy(criteriaBuilder.asc(path));
            }
        }
        //Search Filtering.
        if(search!= null && !search.isEmpty()){
            Predicate searchName = criteriaBuilder.like(root.get("name"), "%" + search.toLowerCase() + "%");
            Predicate searchEmail = criteriaBuilder.like(root.get("email"), "%" + search.toLowerCase() + "%");
            Predicate searchAll = criteriaBuilder.or(searchName,searchEmail);
            criteriaQuery.where(searchAll);
        }
        //After Setting Up [for Execution]
        //To execute the query with Type Safety
        TypedQuery<Student> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setMaxResults(pageSize);
        typedQuery.setFirstResult((pageNumber - 1) * pageSize);

        return typedQuery.getResultList();
    }
    //second method : for counting totalStudents. (for pagination metadata)
    public long getTotalStudentsCount(String search){
         CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
         CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
         Root<Student> root = criteriaQuery.from(Student.class);
         criteriaQuery.select(criteriaBuilder.count(root));
         // Apply search filter
         if (search != null && !search.isEmpty()) {
             Predicate searchName = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + search.toLowerCase() + "%");
             Predicate searchEmail = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + search.toLowerCase() + "%");
             Predicate searchAll = criteriaBuilder.or(searchName, searchEmail);
             criteriaQuery.where(searchAll);
         }
         return entityManager.createQuery(criteriaQuery).getSingleResult();
    }
    //third method : for getting totalPages. (for pagination metadata)
    public int getTotalPages(int pageSize, String search){
        long totalStudents = getTotalStudentsCount(search);
        return (int)(totalStudents / pageSize);
    }
    //Overloaded method for ONLY pagination.
    public List<Student> getStudentsByPageCriteria(int pageNumber, int pageSize){
         return getStudentsByPageCriteria(pageNumber,pageSize,null,null,null);
    }

    //Java Streams
    public List<Student> getStudentByName(String name){

         return studentRepository.findAll()
                 .stream()
                 .filter(
                 (student)->student.getName().equalsIgnoreCase(name)
                        )
                 .collect(Collectors.toList());
    }
}