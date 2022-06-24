package ru.nmedvedev.demo.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import ru.nmedvedev.demo.repository.DatabaseRecord
import ru.nmedvedev.demo.repository.Repository

@RestController
class StreamController(private val repository: Repository) {


    @GetMapping(value = ["/data"], produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun streamData(): Flux<DatabaseRecord> = repository.findAll()

}