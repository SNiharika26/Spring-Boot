package com.example.validation1;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:4200",allowedHeaders = {"Authorization", "Content-Type"})
@RequestMapping("/student")
//@Configuration
public class StudentController {
    private static final String CREDENTIALS_FILE_PATH = "C:\\Users\\nsathish\\OneDrive - Hitachi Vantara\\Desktop\\user.txt";

    @Autowired
    StudentService studentService;


    @GetMapping("/")
    public List<Student> getAll() {
        return studentService.getAll();
    }

    @GetMapping("/id")
    public ResponseEntity<Student> getById(@RequestParam Long rollNo) {
        try {
            Student student = studentService.getById(rollNo);
            return ResponseEntity.ok(student);
        } catch (CombinedExceptions.ResourceNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found", ex);
        }
    }

    @PostMapping(value = "/add")
    public ResponseEntity<Message> create(@Valid @RequestBody Student student, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        /*when you send a request to a server, you include an Authorization header to prove that you have permission to access certain things.
        It's like showing your ID card to the server. The server checks the information in the Authorization header to determine if you're allowed to
        access the requested resources.*/
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        try {
            String credentials = new String(Base64.getDecoder().decode(authorizationHeader.substring(6)));
            String[] credentialsArray = credentials.split(":");
            String username = credentialsArray[0];
            String password = credentialsArray[1];
            boolean isValidCredentials = checkCredentials(username, password);

            if (!isValidCredentials) {
                throw new CombinedExceptions.UnAuthorizedException("Invalid credentials");
            }
            boolean isDuplicateStudent = checkForDuplicateStudent(student);

            if (isDuplicateStudent) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate student");
            }

            studentService.create(student);
            Message message = new Message();
            message.setTimestamp(LocalDateTime.now());
            message.setHttpStatusCode(HttpStatusCode.valueOf(HttpStatus.CREATED.value()));
            message.setStatus(HttpStatus.CREATED);
            message.setMessage("Success");
            return ResponseEntity.status(HttpStatus.CREATED).body(message);

        } catch (CombinedExceptions.UnAuthorizedException | IOException ex) {
            // Handle unauthorized exception and return 401 Unauthorized response
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized", ex);
        }

    }

    boolean checkCredentials(String username, String password) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(CREDENTIALS_FILE_PATH));

        for (String line : lines) {
            String[] credentials = line.split(",");
            String storedUsername = credentials[0];
            String storedPassword = credentials[1];
            if (username.equals(storedUsername) && password.equals(storedPassword)) {
                return true;
            }
        }

        return false;
    }
    boolean checkForDuplicateStudent(Student student) {
        List<Student> existingStudents = studentService.getAll();

        for (Student existingStudent : existingStudents) {
            if (existingStudent.getName().equals(student.getName())) {
                return true;
            }
            else if (existingStudent.getEmail().equals(student.getEmail())) {
                return true;
            }
        }

        return false;
    }


    @PutMapping("/update")
    public ResponseEntity<Message> update(@Valid @RequestParam Long rollNo, @RequestBody Student student) {
        try {
            studentService.update(rollNo, student);
            Message message = new Message();
            message.setTimestamp(LocalDateTime.now());
            message.setHttpStatusCode(HttpStatusCode.valueOf(HttpStatus.OK.value()));
            message.setStatus(HttpStatus.OK);
            message.setMessage("Updated");
            return ResponseEntity.ok(message);
        } catch (CombinedExceptions.ResourceNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found", ex);
        }

    }

    @DeleteMapping("/delete")
    public ResponseEntity<Message> delete(@Valid @RequestParam Long rollNo) {
        try {
            studentService.delete(rollNo);
            Message message = new Message();
            message.setTimestamp(LocalDateTime.now());
            message.setHttpStatusCode(HttpStatusCode.valueOf(HttpStatus.OK.value()));
            message.setStatus(HttpStatus.OK);
            message.setMessage("Delete");
            return ResponseEntity.ok(message);
        } catch (CombinedExceptions.ResourceNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Roll_no is not present", ex);
        }
    }
    //Query
    @GetMapping("/findByName")
    public List<Student> findByName(@RequestParam String name) {
        return studentService.findByName(name);
    }
    @GetMapping("/findGreater")
    public List<Student> findByGreaterThanEqual(@RequestParam Long age) {
        return studentService.findByGreaterThanEqual(age);
    }
}
