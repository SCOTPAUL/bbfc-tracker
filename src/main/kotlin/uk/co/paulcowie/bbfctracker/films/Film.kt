package uk.co.paulcowie.bbfctracker.films

import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToMany

@Entity
data class Film(@Id
                val id: Long,

                val createdAt: Instant,
                val name: String,
                val rating: String,

                @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
                val reasons: Set<RatingReason>)
