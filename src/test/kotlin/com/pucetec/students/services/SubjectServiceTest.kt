package com.pucetec.students.services

import com.pucetec.students.dto.SubjectRequest
import com.pucetec.students.entities.Professor
import com.pucetec.students.entities.Subject
import com.pucetec.students.exceptions.ProfessorNotFound
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.repositories.ProfessorRepository
import com.pucetec.students.repositories.SubjectRepository
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
class SubjectServiceTest {

    @Mock
    private lateinit var subjectRepository: SubjectRepository

    @Mock
    private lateinit var professorRepository: ProfessorRepository

    @InjectMocks
    private lateinit var subjectService: SubjectService

    private val professor = Professor(
        id = 1L,
        name = "test-professor",
        email = "professor@test.com"
    )

    @Test
    fun `createSubject should throw ProfessorNotFound when professor does not exist`() {
        val request = SubjectRequest(name = "Arquitectura", code = "ARQ-101", professorId = 1L)

        `when`(professorRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(ProfessorNotFound::class.java) {
            subjectService.createSubject(request)
        }
    }

    @Test
    fun `createSubject should return valid SubjectResponse when all parameters are valid`() {
        val request = SubjectRequest(name = "Arquitectura", code = "ARQ-101", professorId = 1L)
        val savedSubject = Subject(
            id = 50L,
            name = "Arquitectura",
            code = "ARQ-101",
            professor = professor
        )

        `when`(professorRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(professor))
        `when`(subjectRepository.save(any(Subject::class.java)))
            .thenReturn(savedSubject)

        val response = subjectService.createSubject(request)

        assertEquals(50L, response.id)
        assertEquals("Arquitectura", response.name)
        assertEquals("ARQ-101", response.code)
    }

    @Test
    fun `getAllSubjects should return a list of SubjectResponse`() {
        val subjects = listOf(
            Subject(
                id = 50L,
                name = "Arquitectura",
                code = "ARQ-101",
                professor = professor
            )
        )

        `when`(subjectRepository.findAll())
            .thenReturn(subjects)

        val response = subjectService.getAllSubjects()

        assertEquals(1, response.size)
        assertEquals(50L, response[0].id)
    }

    @Test
    fun `getSubjectById should return a SubjectResponse`() {
        val subject = Subject(
            id = 50L,
            name = "Arquitectura",
            code = "ARQ-101",
            professor = professor
        )

        `when`(subjectRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(subject))

        val response = subjectService.getSubjectById(50L)

        assertEquals(50L, response.id)
        assertEquals("Arquitectura", response.name)
    }

    @Test
    fun `getSubjectById should throw StudentNotFoundException when id does not exist`() {
        `when`(subjectRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            subjectService.getSubjectById(50L)
        }
    }

    @Test
    fun `updateSubject should throw StudentNotFoundException when subject does not exist`() {
        val request = SubjectRequest(name = "Arquitectura II", code = "ARQ-102", professorId = 1L)

        `when`(subjectRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            subjectService.updateSubject(50L, request)
        }
    }

    @Test
    fun `updateSubject should throw ProfessorNotFound when professor does not exist`() {
        val request = SubjectRequest(name = "Arquitectura II", code = "ARQ-102", professorId = 1L)
        val subject = Subject(
            id = 50L,
            name = "Arquitectura",
            code = "ARQ-101",
            professor = professor
        )

        `when`(subjectRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(subject))
        `when`(professorRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(ProfessorNotFound::class.java) {
            subjectService.updateSubject(50L, request)
        }
    }

    @Test
    fun `updateSubject should return valid SubjectResponse when data is correct`() {
        val request = SubjectRequest(name = "Arquitectura II", code = "ARQ-102", professorId = 1L)
        val existingSubject = Subject(
            id = 50L,
            name = "Arquitectura",
            code = "ARQ-101",
            professor = professor
        )
        val updatedSubject = Subject(
            id = 50L,
            name = "Arquitectura II",
            code = "ARQ-102",
            professor = professor
        )

        `when`(subjectRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(existingSubject))
        `when`(professorRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(professor))
        `when`(subjectRepository.save(any(Subject::class.java)))
            .thenReturn(updatedSubject)

        val response = subjectService.updateSubject(50L, request)

        assertEquals(50L, response.id)
        assertEquals("Arquitectura II", response.name)
        assertEquals("ARQ-102", response.code)
    }

    @Test
    fun `deleteSubject should throw StudentNotFoundException when id does not exist`() {
        `when`(subjectRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.empty())

        assertThrows(StudentNotFoundException::class.java) {
            subjectService.deleteSubject(50L)
        }
    }

    @Test
    fun `deleteSubject should delete subject when id exists`() {
        val subject = Subject(
            id = 50L,
            name = "Arquitectura",
            code = "ARQ-101",
            professor = professor
        )

        `when`(subjectRepository.findById(any(Long::class.java)))
            .thenReturn(Optional.of(subject))

        subjectService.deleteSubject(50L)
    }
}