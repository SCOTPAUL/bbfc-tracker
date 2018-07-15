package uk.co.paulcowie.bbfctracker.twitter

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class RatingReason (
    @Id
    val reason: String) {

    @JsonIgnore
    @ManyToMany(mappedBy = "reasons")
    lateinit var films: Set<BbfcTweet>
}
