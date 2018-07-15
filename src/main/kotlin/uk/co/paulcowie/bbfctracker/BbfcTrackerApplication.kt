package uk.co.paulcowie.bbfctracker

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import uk.co.paulcowie.bbfctracker.twitter.BbfcTweet
import uk.co.paulcowie.bbfctracker.twitter.TweetParser
import uk.co.paulcowie.bbfctracker.twitter.TweetRepository
import java.util.*

@SpringBootApplication
@EnableScheduling
class BbfcTrackerApplication {

    companion object {
        private val log = LoggerFactory.getLogger(BbfcTrackerApplication::class.java)
    }

}

fun main(args: Array<String>) {
    runApplication<BbfcTrackerApplication>(*args)
}
