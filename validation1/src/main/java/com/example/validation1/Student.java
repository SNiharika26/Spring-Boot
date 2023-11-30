package com.example.validation1;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name="student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long rollNo;
    @NotNull(message = "Name can't be null")
    @Size(min = 4,message = "name can't be empty")
    private String name;

    @Email
    @NotNull(message = "cant be null")
    @Size(min = 1,message = "email can't be empty")
    private String email;
    @NotNull(message = "age can't be null")
    @Min(value = 18,message = "age is required")
    @Max(value = 26,message = "age can't be above 25!")
    private Long age;

    public Student() {
    }

    public Student(Long rollNo, String name, String email, Long age) {
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Long getRollNo() {
        return rollNo;
    }

    public void setRollNo(Long rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }
}
