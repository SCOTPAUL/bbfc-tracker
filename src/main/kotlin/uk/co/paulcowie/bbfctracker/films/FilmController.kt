package uk.co.paulcowie.bbfctracker.films

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.co.paulcowie.bbfctracker.twitter.BbfcTweet
import uk.co.paulcowie.bbfctracker.twitter.RatingReason
import uk.co.paulcowie.bbfctracker.twitter.ReasonRepository
import uk.co.paulcowie.bbfctracker.twitter.TweetRepository
import javax.persistence.EntityManager
import javax.persistence.Persistence
import javax.persistence.TypedQuery
import javax.persistence.criteria.Predicate

@RestController
@RequestMapping("/films")
class FilmController {

    @Autowired
    private lateinit var repo: TweetRepository

    @Autowired
    private lateinit var entityManager: EntityManager


    @RequestMapping("/rating")
    fun filmByType(@RequestParam(value = "q", required = true) rating: String): Iterable<BbfcTweet> {
        val formattedRating = rating.toUpperCase()

        return if(formattedRating.isNotBlank()) {
            repo.findByRating(formattedRating)
        }
        else {
            repo.findAll()
        }
    }

    @RequestMapping("/reasons-contain")
    fun filmByReasons(@RequestParam(value = "q", required = true) reason: String): Iterable<BbfcTweet> {
        val formattedReason = reason.toLowerCase()

        return if(formattedReason.isNotBlank()) {
            repo.findByReasons_ReasonContaining(formattedReason)
        }
        else {
            repo.findAll()
        }
    }

    @RequestMapping("/all")
    fun films(@RequestParam(value = "reason", required = false) reason: String?,
              @RequestParam(value = "rating", required = false) rating: String?,
              @RequestParam(value = "name", required = false) name: String?): List<BbfcTweet> {
        val formattedReason = reason?.toLowerCase()
        val formattedRating = rating?.toUpperCase()
        val formattedName = name?.toUpperCase()

        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(BbfcTweet::class.java)
        cq.distinct(true)
        val e = cq.from(BbfcTweet::class.java)
        cq.select(e)

        val preds = mutableListOf<Predicate>()

        if(!formattedReason.isNullOrBlank()){
            val join = e.join<BbfcTweet, RatingReason>("reasons")
            preds.add(cb.and(cb.like(join.get("reason"), "%$formattedReason%")))
        }
        if(!formattedRating.isNullOrBlank()){
            preds.add(cb.and(cb.equal(e.get<String>("rating"), formattedRating)))
        }
        if(!formattedName.isNullOrBlank()){
            preds.add(cb.and(cb.equal(e.get<String>("film"), formattedName)))
        }

        cq.where(*preds.toTypedArray())

        val tq = entityManager.createQuery(cq)

        return tq.resultList
    }

}
