package com.pucetec.students.services

import com.pucetec.students.dto.EnrollmentRequest
import com.pucetec.students.entities.Enrollment
import com.pucetec.students.entities.Professor
import com.pucetec.students.entities.Student
import com.pucetec.students.entities.Subject
import com.pucetec.students.exceptions.BlankNameException
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.exceptions.SubjectNotFound
import com.pucetec.students.repositories.EnrollmentRepository
import com.pucetec.students.repositories.StudentRepository
import com.pucetec.students.repositories.SubjectRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals


@ExtendWith(MockitoExtension::class)

class EnrollmentServiceTest {

    @Mock
    private lateinit var studentRepository: StudentRepository
    @Mock
    private lateinit var subjectRepository: SubjectRepository
    @Mock
    private lateinit var enrollmentRepository: EnrollmentRepository

    @InjectMocks
    private lateinit var enrollmentService: EnrollmentService

    private val student = Student(
        id = 1L,
        name = "test",
        email = "test@test.com"

    )
    private val professor = Professor(
        id = 1L,
        name = "test-professor",
        email = "test-professor@test.com"

    )
    private val subject = Subject(
        id = 1L,
        name = "test",
        code = "test",
        professor = professor

    )
    @Test
    fun `createEnrollment should return an Enrollment when all parameters are valid`() {

        val request: EnrollmentRequest = EnrollmentRequest (
            studentId = 1L,
            subjectId = 5L,

        )
        val savedEnrollment = Enrollment(
            id = 100L,
            status = "ACTIVE",
            student = student,
            subject = subject

        )

        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(student))

        `when`(subjectRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(subject))

        `when`(enrollmentRepository.save(any(Enrollment::class.java)))
            .thenReturn(savedEnrollment)

        val response = enrollmentService.createEnrollment(request)

        assertEquals(100L, response.id)
        assertEquals("ACTIVE", response.status)
        assertEquals(1L, response.subject.id)
        assertEquals(1L, response.student.id)


    }
    @Test
    fun `createEnrollment should throw StudentNotFoundException when student is not found`(){

        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())


        assertThrows(StudentNotFoundException::class.java) {
            enrollmentService.createEnrollment(EnrollmentRequest(1L,5L))
        }
    }
    @Test
    fun `createEnrollment should throw SubjectNotFound when subject is not found`(){

        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(student))

        `when`(subjectRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())
        assertThrows(SubjectNotFound::class.java) {
            enrollmentService.createEnrollment(EnrollmentRequest(1L,5L))
        }
    }
    @Test
    fun `getAllEnrollments should return a list of EnrollmentResponse`() {
        val enrollments = listOf(
            Enrollment(
                id = 100L,
                status = "ACTIVE",
                student = student,
                subject = subject
            )
        )

        `when`(enrollmentRepository.findAll())
            .thenReturn(enrollments)

        val response = enrollmentService.getAllEnrollments()

        assertEquals(1, response.size)
        assertEquals(100L, response[0].id)
    }

    @Test
    fun `getEnrollmentById should return an EnrollmentResponse`() {
        val enrollment = Enrollment(
            id = 100L,
            status = "ACTIVE",
            student = student,
            subject = subject
        )

        `when`(enrollmentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(enrollment))

        val response = enrollmentService.getEnrollmentById(100L)

        assertEquals(100L, response.id)
        assertEquals("ACTIVE", response.status)
    }

    @Test
    fun `getEnrollmentById should throw StudentNotFoundException when id does not exist`() {
        `when`(enrollmentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            enrollmentService.getEnrollmentById(100L)
        }
    }

    @Test
    fun `updateEnrollment should throw StudentNotFoundException when enrollment does not exist`() {
        val request = EnrollmentRequest(studentId = 1L, subjectId = 1L)

        `when`(enrollmentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            enrollmentService.updateEnrollment(100L, request)
        }
    }

    @Test
    fun `updateEnrollment should throw StudentNotFoundException when student does not exist`() {
        val request = EnrollmentRequest(studentId = 1L, subjectId = 1L)
        val enrollment = Enrollment(
            id = 100L,
            status = "ACTIVE",
            student = student,
            subject = subject
        )

        `when`(enrollmentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(enrollment))
        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            enrollmentService.updateEnrollment(100L, request)
        }
    }

    @Test
    fun `updateEnrollment should throw StudentNotFoundException when subject does not exist`() {
        val request = EnrollmentRequest(studentId = 1L, subjectId = 1L)
        val enrollment = Enrollment(
            id = 100L,
            status = "ACTIVE",
            student = student,
            subject = subject
        )

        `when`(enrollmentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(enrollment))
        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(student))
        `when`(subjectRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            enrollmentService.updateEnrollment(100L, request)
        }
    }

    @Test
    fun `updateEnrollment should return valid EnrollmentResponse when data is correct`() {
        val request = EnrollmentRequest(studentId = 1L, subjectId = 1L)
        val existingEnrollment = Enrollment(
            id = 100L,
            status = "ACTIVE",
            student = student,
            subject = subject
        )
        val updatedEnrollment = Enrollment(
            id = 100L,
            status = "ACTIVE",
            student = student,
            subject = subject
        )

        `when`(enrollmentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(existingEnrollment))
        `when`(studentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(student))
        `when`(subjectRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(subject))
        `when`(enrollmentRepository.save(any(Enrollment::class.java)))
            .thenReturn(updatedEnrollment)

        val response = enrollmentService.updateEnrollment(100L, request)

        assertEquals(100L, response.id)
        assertEquals("ACTIVE", response.status)
    }

    @Test
    fun `deleteEnrollment should throw StudentNotFoundException when id does not exist`() {
        `when`(enrollmentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            enrollmentService.deleteEnrollment(100L)
        }
    }

    @Test
    fun `deleteEnrollment should delete enrollment when id exists`() {
        val enrollment = Enrollment(
            id = 100L,
            status = "ACTIVE",
            student = student,
            subject = subject
        )

        `when`(enrollmentRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(enrollment))

        enrollmentService.deleteEnrollment(100L)
    }

}