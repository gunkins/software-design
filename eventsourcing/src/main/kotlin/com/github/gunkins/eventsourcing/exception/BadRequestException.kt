package com.github.gunkins.eventsourcing.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class BadRequestException(reason: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, reason)