package ru.nmedvedev.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
class SpringWebfuxMongoBugDemoApplication {
    @Bean
    fun webClient(builder: WebClient.Builder): WebClient = builder.build()
}

fun main(args: Array<String>) {
    runApplication<SpringWebfuxMongoBugDemoApplication>(args = args)
}
