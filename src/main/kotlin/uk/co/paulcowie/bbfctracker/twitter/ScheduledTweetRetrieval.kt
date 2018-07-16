package uk.co.paulcowie.bbfctracker.twitter

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import twitter4j.Paging
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import uk.co.paulcowie.bbfctracker.films.Film
import uk.co.paulcowie.bbfctracker.films.FilmRepository
import javax.annotation.PostConstruct

@Component
class ScheduledTweetRetrieval {
    companion object {
        private val log = LoggerFactory.getLogger(ScheduledTweetRetrieval::class.java)
        private const val BBFC_TWITTER_ID = 82602839L
        private const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L
    }

    private lateinit var twitter: Twitter

    @Autowired
    private lateinit var repo: FilmRepository

    @Autowired
    private lateinit var credentials: TwitterCredentials

    @PostConstruct
    fun init(){
        val t = TwitterFactory.getSingleton()

        val accessToken = AccessToken(credentials.accessKey, credentials.accessSecret)
        t.setOAuthConsumer(credentials.consumerKey, credentials.consumerSecret)
        t.oAuthAccessToken = accessToken

        twitter = t
    }

    @Scheduled(fixedRate = ONE_DAY_MILLIS)
    fun getNewTweets(){
        val latestStoredTweet = repo.findFirstByOrderByCreatedAtDesc()
        val latestTweetId = latestStoredTweet?.id

        val parsed = mutableListOf<Film>()

        var page = 1

        val paging = if(latestTweetId != null){
            Paging(page, 3200, latestTweetId)
        }
        else {
            Paging(page, 3200)
        }

        var newParsedTweets: List<Film>

        try {
            do {
                paging.page = page++
                val tweets = twitter.getUserTimeline(BBFC_TWITTER_ID, paging)
                newParsedTweets = tweets.mapNotNull(TweetParser::parse)
                parsed.addAll(newParsedTweets)
            } while (newParsedTweets.isNotEmpty())
        }
        catch(e: TwitterException){
            log.error("Retrieving tweets failed", e)
        }

        parsed.forEach {
            log.info("$it")
        }

        repo.saveAll(parsed)

        log.info("Got ${parsed.size} tweets")
    }
}