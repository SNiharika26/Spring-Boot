package com.example.validation1;
import org.springframework.data.jpa.repository.*;
import java.util.List;
public interface StudentRepo extends JpaRepository<Student,Long> {
    //Query
    List<Student> findByName(String name);

    //Query annotation
    @Query("SELECT s FROM Student s WHERE s.age>=:age")
    List<Student> findByGreaterThanEqual(Long age);

    @Query("SELECT s FROM Student s WHERE s.rollNo>=:rollNo")
    List<Student> findByGreaterEqual(Long rollNo);

    @Query("SELECT s FROM Student s WHERE s.age >= :age AND s.email = :email")
    List<Student> findByAgeAndEmail(Long age, String email);
}
