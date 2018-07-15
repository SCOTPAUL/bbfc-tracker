package uk.co.paulcowie.bbfctracker.twitter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment

@Configuration
@PropertySource("classpath:keys.properties")
class Config {

    @Autowired
    private lateinit var env: Environment

    @Bean
    fun getCredentials(): TwitterCredentials {
        return TwitterCredentials(
                env.getProperty("twitter.accesstoken.key")!!,
                env.getProperty("twitter.accesstoken.secret")!!,
                env.getProperty("twitter.consumer.key")!!,
                env.getProperty("twitter.consumer.secret")!!)
    }

}

data class TwitterCredentials(val accessKey: String,
                              val accessSecret: String,
                              val consumerKey: String,
                              val consumerSecret: String)