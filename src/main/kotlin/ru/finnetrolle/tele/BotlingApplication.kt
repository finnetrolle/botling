package ru.finnetrolle.tele

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class BotlingApplication

fun main(args: Array<String>) {
    SpringApplication.run(BotlingApplication::class.java, *args)
}
