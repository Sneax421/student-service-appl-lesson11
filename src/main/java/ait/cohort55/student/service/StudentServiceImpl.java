package ait.cohort55.student.service;

import ait.cohort55.student.dao.StudentRepository;
import ait.cohort55.student.dto.ScoreDto;
import ait.cohort55.student.dto.StudentAddDto;
import ait.cohort55.student.dto.StudentDto;
import ait.cohort55.student.dto.StudentUpdateDto;
import ait.cohort55.student.dto.exceptions.StudentNotFoundException;
import ait.cohort55.student.model.Student;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


//@Component
//@AllArgsConstructor
@Service
@RequiredArgsConstructor // toljka dlja final polja
public class StudentServiceImpl implements StudentService {
//    @Autowired
    private final StudentRepository studentRepository; // delaem final

    // esli estj constructor to @Autowired ne nado pisatj, spring sam dobawit

//    public StudentServiceImpl(StudentRepository studentRepository) { // sawisimostj
//        this.studentRepository = studentRepository;
//    }

    @Override
    public Boolean addStudent(StudentAddDto studentAddDto) {
        if(studentRepository.existsById(studentAddDto.getId())) {
            return false;
        }
        Student student = new Student(studentAddDto.getId(), studentAddDto.getName(), studentAddDto.getPassword());
        studentRepository.save(student);
        return true;
    }

    @Override
    public StudentDto findStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        return new StudentDto(student.getId(), student.getName(), student.getScores());
    }

    @Override
    public StudentDto removeStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        studentRepository.deleteById(id);
        return new StudentDto(student.getId(), student.getName(), student.getScores());
    }

//    @Override
//    public StudentAddDto updateStudent(Long id, StudentUpdateDto studentUpdateDto) {
//        Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
//        student.setName(studentUpdateDto.getName());
//        student.setPassword(studentUpdateDto.getPassword());
//        studentRepository.save(student);
//        return new StudentAddDto(student.getId(), student.getName(), student.getPassword());
//    }
    @Override
    public StudentAddDto updateStudent(Long id, StudentUpdateDto studentUpdateDto) {
        Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        if (studentUpdateDto.getName() != null) {
            student.setName(studentUpdateDto.getName());
        }
        if (studentUpdateDto.getPassword() != null) {
            student.setPassword(studentUpdateDto.getPassword());
        }
        studentRepository.save(student);
        return new StudentAddDto(student.getId(), student.getName(), student.getPassword());
    }

    @Override
    public Boolean addScore(Long id, ScoreDto scoreDto) {
        Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        boolean res = student.addScore(scoreDto.getExamName(), scoreDto.getScore());
        studentRepository.save(student);
        return res;
    }

    @Override
    public List<StudentDto> findStudentsByName(String name) {
//        return StreamSupport.stream(studentRepository.findAll().spliterator(), false)
//                .filter(s -> name.equalsIgnoreCase(s.getName()))
//                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
//                .toList();
        return studentRepository.findByNameIgnoreCase(name)
                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
                .toList();
    }

    @Override
    public Long getStudentsQuantityByName(Set<String> names) {
//        return StreamSupport.stream(studentRepository.findAll().spliterator(), false)
//                .filter(s -> names.contains(s.getName()))
//                .count();
       Set<String> lowerCase = names.stream()
               .map(String::toLowerCase)
               .collect(Collectors.toSet());

       return studentRepository.countByNameInIgnoreCase(lowerCase);
    }

    @Override
    public List<StudentDto> findStudentsByExamNameMinScore(String exam, Integer minScore) {
//        return StreamSupport.stream(studentRepository.findAll().spliterator(), false)
//                .filter(s -> s.getScores().containsKey(exam) && s.getScores().get(exam) > minScore)
//                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
//                .toList();
        return studentRepository.findByExamNameAndMinScore(exam, minScore).stream()
                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
                .toList();
    }
}
