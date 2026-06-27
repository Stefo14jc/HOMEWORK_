package com.pucetec.students.services

import com.pucetec.students.dto.StudentRequest
import com.pucetec.students.entities.Student
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.repositories.StudentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class StudentServiceTest {

    @Mock
    private lateinit var studentRepository: StudentRepository

    @InjectMocks
    private lateinit var studentService: StudentService

    @Test
    fun `createStudent should throw BlankNameException when name is blank`() {
        val request = StudentRequest(name = "", email = "test@test.com")

        assertThrows(BlankNameException::class.java) {
            studentService.createStudent(request)
        }
    }

    @Test
    fun `createStudent should return valid StudentResponse when name is not blank`() {
        val request = StudentRequest(
            name = "test",
            email = "test@test.com"
        )

        val savedStudent = Student(
            id = 1L,
            name = "test",
            email = "test@test.com"
        )

        `when`(studentRepository.save(any(Student::class.java)))
            .thenReturn(savedStudent)

        val response = studentService.createStudent(request)

        assertEquals(1L, response.id)
        assertEquals("test", response.name)
        assertEquals("test@test.com", response.email)
    }

    @Test
    fun `createStudent should return valid StudentResponse with empty email when email is null`() {
        val request = StudentRequest(
            name = "test",
            email = null
        )

        val savedStudent = Student(
            id = 1L,
            name = request.name,
            email = request.email
        )

        `when`(studentRepository.save(any(Student::class.java)))
            .thenReturn(savedStudent)

        val response = studentService.createStudent(request)

        assertEquals(1L, response.id)
        assertEquals("test", response.name)
        assertEquals(null, response.email)
    }

    @Test
    fun `getAllStudents should return a list of StudentResponse `() {
        val students = listOf(
            Student (
                id = 1L,
                name = "ana",
                email = "ana@puce.com"
            ),
            Student (
                id = 1L,
                name = "juan",
                email = "juan@puce.com"
            ),
            Student (
                id = 1L,
                name = "Mai",
                email = "mai@puce.com"
            )

        )

        `when`(studentRepository.findAll())
            .thenReturn(students)

        val response = studentService.getAllStudents()

        assertEquals(3, response.size)
        assertEquals("ana",students[0].name )

    }
    @Test
    fun `getStudentById should return a StudentResponse `() {
        val student = Student(
                id = 1L,
                name = "test",
                email = "test@test.com"
        )

        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(student))

        val response = studentService.getStudentById(1L)

        assertEquals(1L, response.id)

    }
    @Test
    fun `getStudentById should throw a StudentNotFoundException `() {

        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            studentService.getStudentById(1L)
        }
    }
    @Test
    fun `updateStudent should throw StudentNotFoundException when id does not exist`() {
        val request = StudentRequest(name = "stefano", email = "stefano@puce.com")

        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            studentService.updateStudent(1L, request)
        }
    }

    @Test
    fun `updateStudent should throw BlankNameException when name is blank`() {
        val request = StudentRequest(name = "", email = "stefano@puce.com")
        val student = Student(
            id = 1L,
            name = "test",
            email = "test@test.com"
        )

        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(student))

        assertThrows(BlankNameException::class.java) {
            studentService.updateStudent(1L, request)
        }
    }

    @Test
    fun `updateStudent should return valid StudentResponse when data is correct`() {
        val request = StudentRequest(name = "stefano", email = "stefano@puce.com")
        val existingStudent = Student(
            id = 1L,
            name = "test",
            email = "test@test.com"
        )
        val updatedStudent = Student(
            id = 1L,
            name = "stefano",
            email = "stefano@puce.com"
        )

        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(existingStudent))
        `when`(studentRepository.save(any(Student::class.java)))
            .thenReturn(updatedStudent)

        val response = studentService.updateStudent(1L, request)

        assertEquals(1L, response.id)
        assertEquals("stefano", response.name)
        assertEquals("stefano@puce.com", response.email)
    }

    @Test
    fun `deleteStudent should throw StudentNotFoundException when id does not exist`() {
        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            studentService.deleteStudent(1L)
        }
    }

    @Test
    fun `deleteStudent should delete student when id exists`() {
        val student = Student(
            id = 1L,
            name = "test",
            email = "test@test.com"
        )

        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(student))

        studentService.deleteStudent(1L)
    }

}