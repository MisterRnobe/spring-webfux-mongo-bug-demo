package ru.nmedvedev.demo.repository

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

@Document(collection = "test")
data class DatabaseRecord(
    @field:Id
    val id: ObjectId? = null,
    val data: Map<String, String>? = null
)

interface Repository : ReactiveMongoRepository<DatabaseRecord, ObjectId>



