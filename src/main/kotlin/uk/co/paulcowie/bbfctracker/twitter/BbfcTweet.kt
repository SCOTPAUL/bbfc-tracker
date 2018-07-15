package uk.co.paulcowie.bbfctracker.twitter

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class BbfcTweet(@Id val id: Long,
                     val createdAt: LocalDateTime,
                     val film: String,
                     val rating: String,

                     @ManyToMany(cascade = [(CascadeType.PERSIST), (CascadeType.MERGE)])
                     val reasons: Set<RatingReason>)
