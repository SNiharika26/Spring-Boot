package com.example.validation1;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class ControllerTest {
    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;
     @Mock
     private HttpServletRequest request;

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
    @Timeout(value = 2)
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
            count++;
        }
        // Mock the behavior of studentService.getAll()
        when(studentService.getAll()).thenReturn(students);

        // Call the getAll() method of the studentController
        List<Student> result = studentController.getAll();

        // Verify the result
        assertEquals(students, result);

        // Verify that studentService.getAll() was called once
        verify(studentService, times(1)).getAll();
    }

    @Test
    void testGetByIdExistingStudent() {
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
        when(studentService.getById(rollNo)).thenReturn(student);

        // Call the getById() method of the studentController
        ResponseEntity<Student> response = studentController.getById(rollNo);

        // Verify the response status code and body
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(student, response.getBody());

        // Verify that studentService.getById() was called once with the correct parameter
        verify(studentService, times(1)).getById(rollNo);
    }

    @Test
    void testGetByIdNonExistingStudent() {

        // Load the error message from the resource bundle
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");
        String errorMessage = bundle.getString("error.resourceNotFound");
        // Mock the behavior of studentService.getById() to throw a ResourceNotFoundException
        when(studentService.getById(1L)).thenThrow(new CombinedExceptions.ResourceNotFoundException(errorMessage));

        // Call the getById() method of the studentController and expect a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> studentController.getById(1L));

        // Verify that studentService.getById() was called once with the correct parameter
        verify(studentService, times(1)).getById(1L);
    }


    @Test
    void testUpdateStudent() {
        //Load the student details from the resource bundle
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");
        //ResourceBundle bundle1 = ResourceBundle.getBundle("errorMessage");

        // Retrieve the student properties from the resource bundle
        Long rollNo = Long.parseLong(bundle.getString("student1.rollNo"));
        String name = bundle.getString("student1.name");
        String email = bundle.getString("student1.email");
        Long age = Long.parseLong(bundle.getString("student1.age"));

        // Create a student object using the retrieved values
        Student student = new Student(rollNo, name, email, age);

        // Mock the behavior of studentService.update()
        doNothing().when(studentService).update(rollNo, student);

        // Call the update() method of the studentController
        ResponseEntity<Message> response = studentController.update(rollNo, student);
        String expectedMessage = bundle.getString("error.updated");
        // Verify the response status code and message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, Objects.requireNonNull(response.getBody()).getMessage());

        // Verify that studentService.update() was called once with the correct parameters
        verify(studentService, times(1)).update(rollNo, student);
    }


    @Test
    void testUpdateStudentResourceNotFound() {
        // Load the student details from the resource bundle
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");

        // Retrieve the student properties from the resource bundle
        Long rollNo = Long.parseLong(bundle.getString("student1.rollNo"));
        String name = bundle.getString("student1.name");
        String email = bundle.getString("student1.email");
        Long age = Long.parseLong(bundle.getString("student1.age"));

        // Create a student object using the retrieved values
        Student student = new Student(rollNo, name, email, age);

        // Mock the behavior of studentService.update() to throw ResourceNotFoundException
        doThrow(CombinedExceptions.ResourceNotFoundException.class).when(studentService).update(rollNo, student);

        // Call the update() method of the studentController and catch the thrown exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            studentController.update(rollNo, student);
        });
        String expectedMessageText = bundle.getString("error.studentNotFound");

        // Verify the expected message
        assertTrue(exception.getMessage().contains(expectedMessageText));
        // Verify that studentService.update() was called once with the correct parameters
        verify(studentService, times(1)).update(rollNo, student);

    }


    @Test
    void testDeleteStudent() {
        // Load the student details from the resource bundle
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");
        //ResourceBundle bundle1 = ResourceBundle.getBundle("errorMessage");
        Long rollNo = Long.parseLong(bundle.getString("student1.rollNo"));
        //Mock the behavior of student service.delete()
        doNothing().when(studentService).delete(rollNo);
        //Call the delete() method of the student controller
        ResponseEntity<Message> response = studentController.delete(rollNo);
        String msg = bundle.getString("error.delete");
        //Verify the response status code and message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(msg, Objects.requireNonNull(response.getBody()).getMessage());
        //Verify whether studentService.delete() was called once
        verify(studentService, times(1)).delete(rollNo);
    }
    @Test
    void testDeleteStudentNotExisting(){
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");
        Long rollNo = Long.parseLong(bundle.getString("student1.rollNo"));
        doThrow(CombinedExceptions.ResourceNotFoundException.class).when(studentService).delete(rollNo);
        String msg = bundle.getString("error.msg");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            studentController.delete(rollNo);
        });

        // Verify the response status code and message
        assertEquals(HttpStatus.NOT_FOUND,exception.getStatusCode());
        assertEquals(msg,exception.getReason());

        // Verify whether studentService.delete() was called once
        verify(studentService, times(1)).delete(rollNo);

    }
@Test
void testCreate_ValidCredentials() throws IOException {
    // Arrange
    Student student = new Student();
    String validAuthorizationHeader = generateAuthorizationHeaderFromFile("C:\\Users\\nsathish\\OneDrive - Hitachi Vantara\\Desktop\\user.txt");
    when(request.getHeader("Authorization")).thenReturn(validAuthorizationHeader);
    doNothing().when(studentService).create(student);

    // Act
    ResponseEntity<Message> response = studentController.create(student, request);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Success", response.getBody().getMessage());

    // Verify that the studentService.create method was called with the correct student object
    verify(studentService, times(1)).create(student);
}


     @Test
    void testCreateInvalidCredentials() throws IOException {
        // Arrange
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");
        // Retrieve the student properties from the resource bundle
        Long rollNo = Long.parseLong(bundle.getString("student1.rollNo"));
        String name = bundle.getString("student1.name");
        String email = bundle.getString("student1.email");
        Long age = Long.parseLong(bundle.getString("student1.age"));
        // create
        String invalidAuthorizationHeader = generateAuthorizationHeaderFromFile("C:\\Users\\nsathish\\OneDrive - Hitachi Vantara\\Desktop\\invalid.txt");
        Student student = new Student(rollNo, name, email, age);
        // Call the create method of the studentController and expect an exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                studentController.create(student, createMockRequest(invalidAuthorizationHeader)));
        String expectedErrorMessage = bundle.getString("error.unauthorized");
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals(expectedErrorMessage, exception.getReason());
    }
     @Test
     void create_InvalidAuthorizationHeader_ReturnsUnauthorizedResponse() {
         // Arrange
         Student student = new Student();
         when(request.getHeader("Authorization")).thenReturn(null);

         // Act
         ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                 () -> studentController.create(student, request));

         // Assert
         assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
         assertEquals("Unauthorized", exception.getReason());
     }
     // Helper method to generate the authorization header from a file
    private String generateAuthorizationHeaderFromFile(String filePath) throws IOException {
        try {
            List<String> credentialsLines = Files.readAllLines(Path.of(filePath));
            if (credentialsLines.isEmpty()) {
                throw new IllegalArgumentException("Credentials file is empty");
            }

            String credentials = credentialsLines.get(0);
            String[] credentialsArray = credentials.split(",");
            if (credentialsArray.length != 2) {
                throw new IllegalArgumentException("Invalid credentials format in the file");
            }

            String username = credentialsArray[0];
            String password = credentialsArray[1];

            String encodedCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
            return "Basic " + encodedCredentials;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading credentials file", e);
        }
    }

    // Helper method to create a mock HttpServletRequest with the provided Authorization header
    private HttpServletRequest createMockRequest(String authorizationHeader) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        return request;
    }

    @Test
    void testFindByName() {
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");

        // Retrieve the student properties from the resource bundle
        Long rollNo1 = Long.parseLong(bundle.getString("student1.rollNo"));
        String name1 = bundle.getString("student1.name");
        String email1 = bundle.getString("student1.email");
        Long age1 = Long.parseLong(bundle.getString("student1.age"));

        Long rollNo2 = Long.parseLong(bundle.getString("student2.rollNo"));
        String name2 = bundle.getString("student2.name");
        String email2 = bundle.getString("student2.email");
        Long age2 = Long.parseLong(bundle.getString("student2.age"));
        List<Student> students = new ArrayList<>();
        students.add(new Student(rollNo1, name1, email1, age1));
        students.add(new Student(rollNo2, name2, email2, age2));
        // Mock the behavior of studentService.findByName()
        when(studentService.findByName("John")).thenReturn(students);

        // Call the findByName() method of the studentController
        List<Student> result = studentController.findByName("John");

        // Verify the result
        assertEquals(students, result);

        // Verify that studentService.findByName() was called once with the correct parameter
        verify(studentService, times(1)).findByName("John");
    }

    @Test
    void testFindByGreaterThanEqual() {
        ResourceBundle bundle = ResourceBundle.getBundle("bundle");
        Long age = Long.parseLong(bundle.getString("student1.age"));
        List<Student> students = new ArrayList<>();
        students.add(new Student(
                Long.parseLong(bundle.getString("student1.rollNo")),
                bundle.getString("student1.name"),
                bundle.getString("student1.email"),
                age
        ));
        students.add(new Student(
                Long.parseLong(bundle.getString("student2.rollNo")),
                bundle.getString("student2.name"),
                bundle.getString("student2.email"),
                age + 1
        ));

        // Mock the behavior of studentService.findByGreaterThanEqual()
        when(studentService.findByGreaterThanEqual(age)).thenReturn(students);

        // Call the findByGreaterThanEqual() method of the studentController
        List<Student> result = studentController.findByGreaterThanEqual(age);

        // Verify the result
        assertEquals(students, result);

        // Verify that studentService.findByGreaterThanEqual() was called once with the correct parameter
        verify(studentService, times(1)).findByGreaterThanEqual(age);
    }
}
