package com.pucetec.students.services

import com.pucetec.students.dto.EnrollmentRequest
import com.pucetec.students.dto.EnrollmentResponse
import com.pucetec.students.entities.Enrollment
import com.pucetec.students.exceptions.StudentNotFoundException
import com.pucetec.students.exceptions.SubjectNotFound
import com.pucetec.students.mappers.toResponse
import com.pucetec.students.repositories.EnrollmentRepository
import com.pucetec.students.repositories.StudentRepository
import com.pucetec.students.repositories.SubjectRepository
import org.springframework.stereotype.Service

@Service
class EnrollmentService (
    private val studentRepository: StudentRepository,
    private val subjectRepository: SubjectRepository,
    private val enrollmentRepository: EnrollmentRepository
){
    fun createEnrollment(request: EnrollmentRequest): EnrollmentResponse {
        val student = studentRepository.findById(request.studentId).orElseThrow {
            StudentNotFoundException("Estudiante con id ${request.studentId} not found")
        }
        val subjectEntity = subjectRepository.findById(request.subjectId).orElseThrow {
            SubjectNotFound("Materia con id ${request.subjectId} not found")
        }

        val enrollment = Enrollment (
            status = "ACTIVE",
            student = student,
            subject = subjectEntity
        )
        return enrollmentRepository.save(enrollment).toResponse()
    }

    fun getAllEnrollments(): List<EnrollmentResponse> {
        return enrollmentRepository.findAll().map { it.toResponse() }
    }

    fun getEnrollmentById(id: Long): EnrollmentResponse {
        val enrollment = enrollmentRepository.findById(id).orElseThrow {
            StudentNotFoundException("Enrollment with id $id not found")
        }
        return enrollment.toResponse()
    }

    fun updateEnrollment(id: Long, request: EnrollmentRequest): EnrollmentResponse {
        val existingEnrollment = enrollmentRepository.findById(id).orElseThrow {
            StudentNotFoundException("Enrollment with id $id not found")
        }
        val student = studentRepository.findById(request.studentId).orElseThrow {
            StudentNotFoundException("Estudiante con id ${request.studentId} not found")
        }
        val subject= subjectRepository.findById(request.subjectId).orElseThrow {
            StudentNotFoundException("Materia con id ${request.subjectId} not found")
        }

        val updatedEnrollment = Enrollment(
            id = existingEnrollment.id,
            status = "ACTIVE",
            student = student,
            subject = subject
        )

        return enrollmentRepository.save(updatedEnrollment).toResponse()
    }

    fun deleteEnrollment(id: Long) {
        val existingEnrollment = enrollmentRepository.findById(id).orElseThrow {
            StudentNotFoundException("Enrollment with id $id not found")
        }
        enrollmentRepository.delete(existingEnrollment)
    }
}