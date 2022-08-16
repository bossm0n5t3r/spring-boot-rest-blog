package me.bossm0n5t3r.blog.common.exception

import me.bossm0n5t3r.blog.common.configuration.LOGGER
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandlingAdvice {
    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    internal fun handleResourceNotFoundException(
        exception: ResourceNotFoundException
    ): ResponseEntity<Unit> {
        LOGGER.error(exception.message)
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(InvalidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    internal fun handleInvalidException(
        exception: InvalidException
    ): ResponseEntity<String> {
        LOGGER.error(exception.message)
        return ResponseEntity.badRequest()
            .body(exception.message)
    }
}
