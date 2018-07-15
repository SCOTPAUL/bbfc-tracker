package uk.co.paulcowie.bbfctracker.twitter

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TweetRepository: CrudRepository<BbfcTweet, Long> {
    fun findByRating(rating: String): List<BbfcTweet>

    fun findFirstByOrderByCreatedAtDesc(): BbfcTweet?

    fun findByReasons_ReasonContaining(substr: String): List<BbfcTweet>
}
