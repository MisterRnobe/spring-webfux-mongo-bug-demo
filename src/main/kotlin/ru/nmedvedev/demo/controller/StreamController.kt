package ru.nmedvedev.demo.controller

import com.mongodb.reactivestreams.client.MongoClient
import org.bson.Document
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import ru.nmedvedev.demo.repository.DatabaseRecord
import ru.nmedvedev.demo.repository.Repository

@RestController
class StreamController(private val repository: Repository, private val client: MongoClient) {


    @GetMapping(value = ["/v1/data"], produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun streamDataFromSpringData(): Flux<DatabaseRecord> = repository.findAll()

    @GetMapping(value = ["/v2/data"], produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun streamDataFromDriver(): Flux<DatabaseRecord> {
        return client
            .getDatabase("data")
            .getCollection("test")
            .find()
            .toFlux()
            .map { doc ->
                DatabaseRecord(
                    doc.getObjectId("_id"),
                    doc.get("data", Document::class.java).mapValues { it.toString() }
                )
            }
    }

}