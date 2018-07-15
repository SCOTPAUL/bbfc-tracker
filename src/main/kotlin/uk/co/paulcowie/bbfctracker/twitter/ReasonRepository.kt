package uk.co.paulcowie.bbfctracker.twitter

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ReasonRepository: CrudRepository<RatingReason, Long> {
    fun findByReason(reason: String): RatingReason?

    fun findByReasonContaining(substr: String): Set<RatingReason>
}
