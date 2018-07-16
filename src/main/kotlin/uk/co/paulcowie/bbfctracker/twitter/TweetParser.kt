package uk.co.paulcowie.bbfctracker.twitter

import twitter4j.Status
import uk.co.paulcowie.bbfctracker.films.Film
import uk.co.paulcowie.bbfctracker.films.RatingReason
import java.util.regex.Pattern

object TweetParser {
    private val PATTERN = Pattern.compile("^(.+) \\(([A-Z0-9]+)\\) (.+?)(?: https?:.+)?\$")

    fun parse(status: Status): Film? {
        if(status.inReplyToUserId != -1L || status.isRetweet){
            // Retweet/reply
            return null
        }


        val id = status.id
        val createdAt = status.createdAt.toInstant()

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

        return Film(id, createdAt, filmName, rating, reasons.toSet())
    }
}