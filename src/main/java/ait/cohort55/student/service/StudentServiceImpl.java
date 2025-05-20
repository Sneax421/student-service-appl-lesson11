package ait.cohort55.student.service;

import ait.cohort55.student.dao.StudentRepository;
import ait.cohort55.student.dto.ScoreDto;
import ait.cohort55.student.dto.StudentAddDto;
import ait.cohort55.student.dto.StudentDto;
import ait.cohort55.student.dto.StudentUpdateDto;
import ait.cohort55.student.dto.exceptions.StudentNotFoundException;
import ait.cohort55.student.model.Student;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;




@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;


    @Override
    public Boolean addStudent(StudentAddDto studentAddDto) {
        if(studentRepository.existsById(studentAddDto.getId())) {
            return false;
        }
//        Student student = new Student(studentAddDto.getId(), studentAddDto.getName(), studentAddDto.getPassword());
        Student student = modelMapper.map(studentAddDto, Student.class);
        studentRepository.save(student);
        return true;
    }

    @Override
    public StudentDto findStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        return modelMapper.map(student, StudentDto.class);
    }

    @Override
    public StudentDto removeStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        studentRepository.deleteById(id);
        return modelMapper.map(student, StudentDto.class);
    }

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
        return modelMapper.map(student, StudentAddDto.class);
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
        return studentRepository.findByNameIgnoreCase(name)
//                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
                .map(s -> modelMapper.map(s, StudentDto.class))
                .toList();
    }

    @Override
    public Long getStudentsQuantityByName(Set<String> names) {
       Set<String> lowerCase = names.stream()
               .map(String::toLowerCase)
               .collect(Collectors.toSet());

       return studentRepository.countByNameInIgnoreCase(lowerCase);
    }

    @Override
    public List<StudentDto> findStudentsByExamNameMinScore(String exam, Integer minScore) {
        return studentRepository.findByExamNameAndMinScore(exam, minScore).stream()
//                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
                .map(s -> modelMapper.map(s, StudentDto.class))
                .toList();
    }

}
