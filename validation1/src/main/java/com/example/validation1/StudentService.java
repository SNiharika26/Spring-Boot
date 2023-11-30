package com.example.validation1;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class StudentService {

    @Autowired
    StudentRepo studentRepo;

    public List<Student> getAll()
    {
        return studentRepo.findAll();
    }

    public Student getById(Long rollNo)
    {
        return studentRepo.findById(rollNo).orElseThrow(()->new CombinedExceptions.ResourceNotFoundException("user not found"));
    }
    public void create(Student student){
        studentRepo.save(student);
    }
    public void update(Long rollNo,Student student)
    {try {
        Student student1 = studentRepo.findById(rollNo)
                .orElseThrow(() -> new CombinedExceptions.ResourceNotFoundException("User not found"));

        // Perform the update logic
        student1.setName(student.getName());
        student1.setAge(student.getAge());
        studentRepo.save(student1);
    } catch (CombinedExceptions.ResourceNotFoundException ex) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", ex);
    }

    }
    public void delete(Long rollNo)
    {
        studentRepo.deleteById(rollNo);
    }

    //Query
    public List<Student> findByName(String name)
    {
        if ((name==null)||name.isEmpty())
        {
            throw new IllegalArgumentException("Name can't be null or empty");
        }
        return studentRepo.findByName(name);
    }
    public List<Student> findByGreaterThanEqual(Long age)
    {
        return studentRepo.findByGreaterEqual(age);
    }



}
