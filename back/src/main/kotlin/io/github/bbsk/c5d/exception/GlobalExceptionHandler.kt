package io.github.bbsk.c5d.exception

import io.github.bbsk.c5d.model.common.dto.ErrorDetail
import io.github.bbsk.c5d.model.common.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import jakarta.validation.ConstraintViolationException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * Обработка кастомных исключений приложения ApiException (BadRequest, NotFound, Conflict, Unprocessable)
     */
    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<ErrorResponse> {
        log.warn("API Exception [{}]: {}", ex.code, ex.message)
        val body = ErrorResponse.of(
            code = ex.code,
            message = ex.message,
            details = ex.details
        )
        return ResponseEntity.status(ex.status).body(body)
    }

    /**
     * Ошибки валидации DTO (@Valid / @Validated) — 400 BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.allErrors.map { error ->
            val fieldName = if (error is FieldError) error.field else error.objectName
            val message = error.defaultMessage ?: "Некорректное значение"
            ErrorDetail(field = fieldName, message = message)
        }

        val firstMessage = details.firstOrNull()?.let { "Поле ${it.field}: ${it.message}" }
            ?: "Ошибка валидации входных данных"

        val body = ErrorResponse.of(
            code = "BAD_REQUEST",
            message = firstMessage,
            details = details
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    /**
     * Ошибки парсинга JSON (невалидный синтаксис, неподходящий тип данных, неверный enum) — 400 BAD_REQUEST
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val message = cleanJsonErrorMessage(ex.message ?: "Некорректный формат JSON или недопустимое значение поля")
        val body = ErrorResponse.of(
            code = "BAD_REQUEST",
            message = message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    /**
     * Ошибки валидации параметров методов и ограничений валидатора — 400 BAD_REQUEST
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val details = ex.constraintViolations.map { violation ->
            ErrorDetail(
                field = violation.propertyPath.toString(),
                message = violation.message
            )
        }
        val body = ErrorResponse.of(
            code = "BAD_REQUEST",
            message = "Нарушение ограничений валидации параметров",
            details = details
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    /**
     * Стандартные ResponseStatusException из Spring
     */
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<ErrorResponse> {
        val httpStatus = HttpStatus.resolve(ex.statusCode.value()) ?: HttpStatus.INTERNAL_SERVER_ERROR
        val errorCode = when (httpStatus) {
            HttpStatus.BAD_REQUEST -> "BAD_REQUEST"
            HttpStatus.UNAUTHORIZED -> "UNAUTHORIZED"
            HttpStatus.FORBIDDEN -> "FORBIDDEN"
            HttpStatus.NOT_FOUND -> "NOT_FOUND"
            HttpStatus.CONFLICT -> "CONFLICT"
            HttpStatus.UNPROCESSABLE_ENTITY -> "UNPROCESSABLE_ENTITY"
            else -> httpStatus.name
        }

        val message = ex.reason ?: httpStatus.reasonPhrase
        val body = ErrorResponse.of(
            code = errorCode,
            message = message
        )
        return ResponseEntity.status(httpStatus).body(body)
    }

    /**
     * Ошибки аутентификации Spring Security — 401 UNAUTHORIZED
     */
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse.of(
            code = "UNAUTHORIZED",
            message = ex.message ?: "Требуется аутентификация для доступа к ресурсу"
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body)
    }

    /**
     * Ошибки доступа Spring Security — 403 FORBIDDEN
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse.of(
            code = "FORBIDDEN",
            message = "Недостаточно прав для выполнения данной операции"
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body)
    }

    /**
     * Все остальные непредвиденные ошибки сервера — 500 INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Throwable::class)
    fun handleGeneralException(ex: Throwable): ResponseEntity<ErrorResponse> {
        log.error("Unhandled internal server error", ex)
        val body = ErrorResponse.of(
            code = "INTERNAL_SERVER_ERROR",
            message = "Внутренняя ошибка сервера: ${ex.localizedMessage ?: "неизвестная ошибка"}"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }

    private fun cleanJsonErrorMessage(raw: String): String {
        val rootCause = raw.substringBefore("; nested exception is")
        return when {
            rootCause.contains("Cannot deserialize value of type") -> {
                val fieldMatch = Regex("through reference chain: .*?\\[\"(.*?)\"\\]").find(raw)
                val fieldName = fieldMatch?.groupValues?.get(1)
                if (fieldName != null) "Поле $fieldName содержит значение некорректного типа" else "Некорректное значение одного из полей"
            }
            rootCause.contains("JSON parse error") -> "Синтаксическая ошибка в теле JSON запроса"
            else -> rootCause
        }
    }
}
