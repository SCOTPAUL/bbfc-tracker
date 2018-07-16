package uk.co.paulcowie.bbfctracker.films

import org.springframework.data.repository.CrudRepository

interface FilmRepository: CrudRepository<Film, Long> {
    fun findByRating(rating: String): List<Film>

    fun findFirstByOrderByCreatedAtDesc(): Film?

    fun findByReasons_ReasonContaining(substr: String): List<Film>
}
