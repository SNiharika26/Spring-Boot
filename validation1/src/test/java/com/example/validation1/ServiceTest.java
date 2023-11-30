package com.example.validation1;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ServiceTest {
    @Mock
    private StudentRepo studentRepo;
    @InjectMocks
    private StudentService studentService;
    private List<Student> students;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        students = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        students.clear(); // Clear the list of students
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testGetAll() {
        // Create a list of students
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");
        List<Student> students = new ArrayList<>();
        int count = 1;
        while (bundle.containsKey("students" + count + ".rollNo")) {
            long rollNo = Long.parseLong(bundle.getString("student" + count + ".rollNo"));
            String name = bundle.getString("student" + count + ".name");
            String email = bundle.getString("student" + count + ".email");
            long age = Long.parseLong(bundle.getString("student" + count + ".age"));

            students.add(new Student(rollNo, name, email, age));
        }
        // Mock the behavior of studentService.getAll()
        when(studentRepo.findAll()).thenReturn(students);

        // Call the getAll() method of the studentController
        List<Student> result = studentService.getAll();

        // Verify the result
        assertEquals(students.size(), result.size());
        for (int i = 0; i < students.size(); i++) {
            assertEquals(students.get(i), result.get(i));
        }

        // Verify that studentService.getAll() was called once
        verify(studentRepo, times(1)).findAll();
    }
    @Test
    void testGetById() {

        // Load the student details from the resource bundle
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");

        //Retrieve the student details using the corresponding keys from the resource bundle
        long rollNo = Long.parseLong(bundle.getString("student1.rollNo"));
        ;
        String name = bundle.getString("student1.name");
        String email = bundle.getString("student1.email");
        long age = Long.parseLong(bundle.getString("student1.age"));

        //Create a student object
        Student student = new Student(rollNo, name, email, age);
        // Mock the behavior of studentService.getById()
        when(studentRepo.findById(rollNo)).thenReturn(Optional.of(student));

        // Call the getById() method of the studentController
        Student result = studentService.getById(rollNo);

        // Verify the response status code and body
        assertEquals(student,result);

        // Verify that studentService.getById() was called once with the correct parameter
        verify(studentRepo, times(1)).findById(rollNo);
    }
    @Test
    void testNonExistingId()
    {
        Long nonExistingRollNo=4L;
        when(studentRepo.findById(nonExistingRollNo)).thenReturn(Optional.empty());

        // Call the getById() method of the studentService and expect a ResourceNotFoundException
        assertThrows(CombinedExceptions.ResourceNotFoundException.class, () -> {
            studentService.getById(nonExistingRollNo);
        });

        // Verify that studentRepo.findById() was called once with the correct parameter
        verify(studentRepo, times(1)).findById(nonExistingRollNo);

    }
    @Test
    void testCreate()
    {
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");

        //Retrieve the student details using the corresponding keys from the resource bundle
        long rollNo = Long.parseLong(bundle.getString("student1.rollNo"));
        ;
        String name = bundle.getString("student1.name");
        String email = bundle.getString("student1.email");
        long age = Long.parseLong(bundle.getString("student1.age"));
        Student student = new Student(rollNo, name, email, age);
        when(studentRepo.save(student)).thenReturn(student);

        // Call the create() method of the studentService
        studentService.create(student);

        // Verify that studentRepo.save() was called once with the correct parameter
        verify(studentRepo, times(1)).save(student);
    }
    @Test
    void testUpdate() {

        ResourceBundle bundle = ResourceBundle.getBundle("bundle");
        Long rollNo = 1L;
        String name = bundle.getString("student1.name");
        String email = bundle.getString("student1.email");
        long age = Long.parseLong(bundle.getString("student1.age"));
        Student existingStudent = new Student(rollNo, name, email, age);
        Student updatedEmp = new Student(1L, "John", "john@example.com", 27L);

        when(studentRepo.findById(rollNo)).thenReturn(Optional.of(existingStudent));
        when(studentRepo.save(updatedEmp)).thenReturn(existingStudent);
        studentService.update(rollNo, updatedEmp);

//       // Student expectedResponse=new Student(rollNo,name,email,updatedEmp.getAge());
//        assertEquals(expectedResponse.getName(),updatedEmp.getName());
//        assertEquals(expectedResponse.getEmail(),updatedEmp.getEmail());
//        assertEquals(expectedResponse.getAge(),updatedEmp.getAge());

        assertEquals(updatedEmp.getName(), existingStudent.getName());
        assertEquals(updatedEmp.getEmail(), existingStudent.getEmail());
        assertEquals(updatedEmp.getAge(), existingStudent.getAge());
        verify(studentRepo, times(1)).findById(rollNo);
        verify(studentRepo, times(1)).save(existingStudent);
    }
//    }
    @Test
    void testUpdateNotExistingId()
    {
        Long rollNo=4L;
        Student updatedEmp = new Student(rollNo, "John", "john@example.com", 27L);

        when(studentRepo.findById(rollNo)).thenReturn(Optional.empty()); // Simulate non-existing ID

        // Perform the update operation
        assertThrows(ResponseStatusException.class, () -> {
            studentService.update(rollNo, updatedEmp);
        });
    }


    @Test
    void testDelete()
    {
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");
        long rollNo = Long.parseLong(bundle.getString("student1.rollNo"));
        String name = bundle.getString("student1.name");
        String email = bundle.getString("student1.email");
        long age = Long.parseLong(bundle.getString("student1.age"));
        Student existingStudent = new Student(rollNo, name, email, age);
        // Mock the behavior of studentRepo.existsById()
        when(studentRepo.existsById(existingStudent.getRollNo())).thenReturn(true);

        // Call the delete method
        studentService.delete(existingStudent.getRollNo());

        // Verify that studentRepo.deleteById() was called once with the correct roll number
        verify(studentRepo, times(1)).deleteById(existingStudent.getRollNo());
    }
    @Test
    void testFindByName()
    {
        List<Student> students = Arrays.asList(
                new Student(1L, "John", "john@example.com", 20L),
                new Student(2L, "John", "john.doe@example.com", 25L)
        );

        // Mock the behavior of studentRepo.findByName()
        when(studentRepo.findByName("John")).thenReturn(students);

        // Call the findByName method
        List<Student> result = studentService.findByName("John");

        // Verify that studentRepo.findByName() was called once with the correct name
        verify(studentRepo, times(1)).findByName("John");

        // Verify the result matches the expected students
        assertEquals(students, result);
    }
    @Test
    void testFindByNameNullNameThrowsException() {
        // Call the findByName method with a null name
        assertThrows(IllegalArgumentException.class, () -> {
            studentService.findByName(null);
        });

        // Verify that studentRepo.findByName() was not called
        verify(studentRepo, never()).findByName(anyString());
    }

    @Test
    void testFindByNameEmptyNameThrowsException() {
        // Call the findByName method with an empty name
        assertThrows(IllegalArgumentException.class, () -> {
            studentService.findByName("");
        });

        // Verify that studentRepo.findByName() was not called
        verify(studentRepo, never()).findByName(anyString());
    }

    @Test
    void testFindByGreaterThanEqual_ValidAge_Success() {
        // Create a list of students with ages greater than or equal to the specified age
        List<Student> students = Arrays.asList(
                new Student(1L, "John", "john@example.com", 20L),
                new Student(2L, "Jane", "jane@example.com", 25L)
        );

        // Mock the behavior of studentRepo.findByGreaterEqual()
        when(studentRepo.findByGreaterEqual(20L)).thenReturn(students);

        // Call the findByGreaterThanEqual method
        List<Student> result = studentService.findByGreaterThanEqual(20L);

        // Verify that studentRepo.findByGreaterEqual() was called once with the correct age
        verify(studentRepo, times(1)).findByGreaterEqual(20L);

        // Verify the result matches the expected students
        assertEquals(students, result);
    }
}
