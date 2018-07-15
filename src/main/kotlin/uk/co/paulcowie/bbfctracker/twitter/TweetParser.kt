package uk.co.paulcowie.bbfctracker.twitter

import org.springframework.beans.factory.annotation.Autowired
import twitter4j.Status
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.regex.Pattern

class TweetParser(private val ratingReasonRepo: ReasonRepository) {
    companion object {
        private val PATTERN = Pattern.compile("^(.+) \\(([A-Z0-9]+)\\) (.+?)(?: https?:.+)?\$")
    }

    fun parse(status: Status): BbfcTweet? {
        if(status.inReplyToUserId != -1L || status.isRetweet){
            // Retweet/reply
            return null
        }


        val id = status.id
        val createdAt = status.createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

        val matches = PATTERN.matcher(status.text)

        if(!matches.matches()){
            return null
        }

        val filmName = matches.group(1)
        val rating = matches.group(2)

        val reasonStrings = matches.group(3)

        val reasons = reasonStrings
                .split(",")
                .map(String::trim)
                .map(::RatingReason)

        return BbfcTweet(id, createdAt, filmName, rating, reasons.toSet())
    }
}