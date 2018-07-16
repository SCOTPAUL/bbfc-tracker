package uk.co.paulcowie.bbfctracker.films

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToMany

@Entity
data class RatingReason (
    @Id
    val reason: String) {

    @JsonIgnore
    @ManyToMany(mappedBy = "reasons")
    lateinit var films: Set<Film>
}
