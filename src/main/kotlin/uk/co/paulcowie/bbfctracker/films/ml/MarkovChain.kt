package uk.co.paulcowie.bbfctracker.films.ml

import uk.co.paulcowie.bbfctracker.films.Film

class MarkovChain(private var films: Iterable<Film>): Iterable<String> {
    override fun iterator(): Iterator<String> {
        return MarkovChainIterator(this)
    }

    private val transitionMatrix = mutableMapOf<String, MutableMap<String, Double>>()
    private val startWords = mutableSetOf<String>()

    init {
        makeChain()
    }

    fun update(films: Iterable<Film>){
        this.films = films
        transitionMatrix.clear()
        startWords.clear()
        makeChain()
    }

    private fun makeChain() {
        val transitionCount = mutableMapOf<String, MutableMap<String, Int>>()

        for(film in films){


            val filmName = film.name

            val split = filmName
                    .split("(?!^)\\b".toRegex())
                    .filterNot { it.isBlank() }
                    .map { it.trim() }

            split.firstOrNull()?.let { startWords.add(it) }

            split.forEachIndexed { index, s ->
                if(index != 0){
                    val sPrev = split[index - 1]

                    val prevCount = transitionCount.getOrPut(sPrev, { mutableMapOf() }).getOrDefault(s, 0)
                    transitionCount[sPrev]!![s] = prevCount + 1
                }
            }

            split.lastOrNull()?.let {
                val prevCount = transitionCount.getOrPut(it, { mutableMapOf() }).getOrDefault("", 0)
                transitionCount[it]!![""] = prevCount + 1
            }

        }

        for((sPrev, mapped) in transitionCount){
            val total = mapped.values.sum().toDouble()

            for((s, count) in mapped){
                transitionMatrix.getOrPut(sPrev, { mutableMapOf() })[s] = count / total
            }

        }

    }

    fun getFirst(): String {
        return startWords.shuffled().first()
    }

    fun getNext(element: String): String? {
        if(element == ""){
            return null
        }

        val possibilities = transitionMatrix[element] ?: return null

        if(possibilities.values.size == 1){
            return possibilities.keys.first()
        }

        val p = Math.random()

        var cumulativeProb = 0.0

        for((possibility, prob) in possibilities){
            cumulativeProb += prob
            if(p <= cumulativeProb){
                return possibility
            }
        }

        return possibilities.keys.last()
    }

}

class MarkovChainIterator(private val markovChain: MarkovChain): Iterator<String> {
    private var curr: String? = null

    override fun hasNext(): Boolean {
        return curr != ""
    }

    override fun next(): String {
        return if(curr == null){
            curr = markovChain.getFirst()
            curr!!
        }
        else {
            curr = markovChain.getNext(curr!!)
            curr!!
        }
    }

}