package ru.nmedvedev.demo.service

import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.nmedvedev.demo.repository.DatabaseRecord
import ru.nmedvedev.demo.repository.Repository
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

private const val DATA_COUNT = 1_000_000
private const val FILL_BATCH = 1_000
private const val PRINT_FETCHED_EVERY = 10_000
private const val ENDPOINT = "/v2/data"

private val log = KotlinLogging.logger { }

@Component
class Demo(
    private val repository: Repository,
    private val webClient: WebClient
) : CommandLineRunner {


    override fun run(vararg args: String?) {
        repository.count()
            .flatMap {
                if (it != DATA_COUNT.toLong()) {
                    repository.deleteAll().then(fillData(DATA_COUNT))
                } else {
                    Mono.empty()
                }
            }
            .thenMany(Flux.just(
                runFetching(1),
                runFetching(2),
                runFetching(3),
                runFetching(4),
                runFetching(5),
                runFetching(6),
                runFetching(7),
                runFetching(8)
            ).flatMap { it }
            )
            .subscribe()
    }

    private fun fillData(dataCount: Int): Mono<Void> {
        val counter = AtomicInteger(0)
        return Flux.generate<DatabaseRecord> { sink ->
            if (counter.get() >= DATA_COUNT) {
                sink.complete()
            } else {
                sink.next(DatabaseRecord(data = getRandomData()))
            }
        }
            .doFirst { log.info { "Start data inserting" } }
            .buffer(FILL_BATCH)
            .flatMap {
                repository.saveAll(it)
                    .count()
                    .doOnNext { count -> log.info { "Inserted ${counter.addAndGet(count.toInt())} of $DATA_COUNT documents" } }
            }
            .doOnComplete { log.info { "Finished data inserting" } }
            .then()
    }

    fun runFetching(index: Int): Mono<Void> {
        val counter = AtomicInteger(0)
        return webClient.getData()
            .doFirst { log.info { "#$index Start data fetching" } }
            .doOnNext {
                val fetched = counter.incrementAndGet()
                if (fetched % PRINT_FETCHED_EVERY == 0) {
                    log.info { "#$index Fetched $fetched of $DATA_COUNT records" }
                }
            }
            .doOnComplete { log.info { "#$index Finished data fetching" } }
            .then()
    }

}

private fun WebClient.getData(): Flux<DatabaseRecord> =
    this.get()
        .uri("http://localhost:8080${ENDPOINT}")
        .accept(MediaType.APPLICATION_NDJSON)
        .retrieve()
        .bodyToFlux()

private fun getRandomData(): Map<String, String> =
    (1..Random.nextInt(3, 10))
        .associate { UUID.randomUUID().toString() to UUID.randomUUID().toString() }
