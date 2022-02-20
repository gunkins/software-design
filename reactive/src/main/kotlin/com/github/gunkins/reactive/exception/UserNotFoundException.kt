package com.github.gunkins.reactive.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class UserNotFoundException(override val message: String) : ResponseStatusException(HttpStatus.NOT_FOUND, message)