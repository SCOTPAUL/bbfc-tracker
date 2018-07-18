package uk.co.paulcowie.bbfctracker.films

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.co.paulcowie.bbfctracker.films.ml.MarkovChain
import uk.co.paulcowie.bbfctracker.twitter.TweetRetrievalEvent
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import javax.persistence.EntityManager
import javax.persistence.criteria.Predicate

@RestController
@RequestMapping("/films")
class FilmController: ApplicationListener<TweetRetrievalEvent> {

    @Autowired
    private lateinit var repo: FilmRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var markovChain: MarkovChain

    @Bean
    fun markovChain(repo: FilmRepository): MarkovChain {
        return MarkovChain(repo.findAll())
    }

    override fun onApplicationEvent(event: TweetRetrievalEvent) {
        markovChain.update(repo.findAll())
    }

    @RequestMapping("/generate-random")
    fun generateFilmName(@RequestParam(value = "n", required = false) num: Int?): List<String> {
        val names = mutableListOf<String>()

        if(num != null){
            for(i in 0..num){
                names.add(markovChain.joinToString(" ").trim())
            }
        }
        else {
            names.add(markovChain.joinToString(" ").trim())
        }

        return names
    }


    @RequestMapping("/rating")
    fun filmByType(@RequestParam(value = "q", required = true) rating: String): Iterable<Film> {
        val formattedRating = rating.toUpperCase()

        return if(formattedRating.isNotBlank()) {
            repo.findByRating(formattedRating)
        }
        else {
            repo.findAll()
        }
    }

    @RequestMapping("/reasons-contain")
    fun filmByReasons(@RequestParam(value = "q", required = true) reason: String): Iterable<Film> {
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
              @RequestParam(value = "name", required = false) name: String?,

              @RequestParam(value = "before", required = false)
              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) before: LocalDate?,

              @RequestParam(value = "after", required = false)
              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) after: LocalDate?): List<Film> {
        val formattedReason = reason?.toLowerCase()
        val formattedRating = rating?.toUpperCase()
        val formattedName = name?.toUpperCase()

        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(Film::class.java)
        cq.distinct(true)
        val e = cq.from(Film::class.java)
        cq.select(e)

        val preds = mutableListOf<Predicate>()

        if(!formattedReason.isNullOrBlank()){
            val join = e.join<Film, RatingReason>("reasons")
            preds.add(cb.and(cb.like(join.get("reason"), "%$formattedReason%")))
        }
        if(!formattedRating.isNullOrBlank()){
            preds.add(cb.and(cb.equal(e.get<String>("rating"), formattedRating)))
        }
        if(!formattedName.isNullOrBlank()){
            preds.add(cb.and(cb.equal(e.get<String>("name"), formattedName)))
        }

        if(before != null && after != null && before.isBefore(after)){
            throw HttpMessageNotReadableException("Argument 'before' cannot be before 'after'")
        }

        before?.let {
            preds.add(cb.and(cb.lessThanOrEqualTo(e.get<Instant>("createdAt"),
                    it.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))))
        }
        after?.let {
            preds.add(cb.and(cb.greaterThanOrEqualTo(e.get<Instant>("createdAt"),
                    it.atStartOfDay().toInstant(ZoneOffset.UTC))))
        }

        cq.where(*preds.toTypedArray())

        val tq = entityManager.createQuery(cq)

        return tq.resultList
    }

}
