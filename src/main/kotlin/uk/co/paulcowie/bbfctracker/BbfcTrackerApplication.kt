package uk.co.paulcowie.bbfctracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class BbfcTrackerApplication

fun main(args: Array<String>) {
    runApplication<BbfcTrackerApplication>(*args)
}
