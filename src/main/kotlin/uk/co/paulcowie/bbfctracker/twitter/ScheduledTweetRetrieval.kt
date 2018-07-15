package uk.co.paulcowie.bbfctracker.twitter

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import twitter4j.Paging
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import java.time.DayOfWeek
import java.time.Period
import java.util.*
import javax.annotation.PostConstruct

@Component
class ScheduledTweetRetrieval {
    companion object {
        private val log = LoggerFactory.getLogger(ScheduledTweetRetrieval::class.java)
    }

    private lateinit var twitter: Twitter

    @Autowired
    private lateinit var repo: TweetRepository

    @Autowired
    private lateinit var reasonRepo: ReasonRepository

    private lateinit var parser: TweetParser

    @Autowired
    private lateinit var credentials: TwitterCredentials

    @PostConstruct
    fun init(){
        val t = TwitterFactory.getSingleton()

        val accessToken = AccessToken(credentials.accessKey, credentials.accessSecret)
        t.setOAuthConsumer(credentials.consumerKey, credentials.consumerSecret)
        t.oAuthAccessToken = accessToken

        twitter = t

        parser = TweetParser(reasonRepo)
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    fun getNewTweets(){
        val latestStoredTweet = repo.findFirstByOrderByCreatedAtDesc()
        val latestTweetId = latestStoredTweet?.id

        val parsed = mutableListOf<BbfcTweet>()

        var page = 1

        val paging = if(latestTweetId != null){
            Paging(page, 3200, latestTweetId)
        }
        else {
            Paging(page, 3200)
        }

        var newParsedTweets: List<BbfcTweet>

        do {
            paging.page = page++
            val tweets = twitter.getUserTimeline(82602839, paging)
            newParsedTweets = tweets.mapNotNull(parser::parse)
            parsed.addAll(newParsedTweets)
        } while(newParsedTweets.isNotEmpty())

        parsed
            .forEach {
                log.info("$it")
            }

        repo.saveAll(parsed)

        log.info("Got ${parsed.size} tweets")
    }
}