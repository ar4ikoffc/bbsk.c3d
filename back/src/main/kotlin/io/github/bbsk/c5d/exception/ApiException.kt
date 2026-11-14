package io.github.bbsk.c5d.exception

import io.github.bbsk.c5d.model.common.dto.ErrorDetail
import org.springframework.http.HttpStatus

open class ApiException(
    val status: HttpStatus,
    val code: String,
    override val message: String,
    val details: List<ErrorDetail> = emptyList(),
    cause: Throwable? = null
) : RuntimeException(message, cause)

class BadRequestException(
    code: String = "BAD_REQUEST",
    message: String,
    details: List<ErrorDetail> = emptyList()
) : ApiException(HttpStatus.BAD_REQUEST, code, message, details)

class NotFoundException(
    code: String = "NOT_FOUND",
    message: String
) : ApiException(HttpStatus.NOT_FOUND, code, message)

class ConflictException(
    code: String = "CONFLICT",
    message: String
) : ApiException(HttpStatus.CONFLICT, code, message)

class UnprocessableEntityException(
    code: String = "UNPROCESSABLE_ENTITY",
    message: String,
    details: List<ErrorDetail> = emptyList()
) : ApiException(HttpStatus.UNPROCESSABLE_ENTITY, code, message, details)
