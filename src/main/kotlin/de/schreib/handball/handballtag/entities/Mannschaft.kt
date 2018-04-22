package de.schreib.handball.handballtag.entities

import de.schreib.handball.handballtag.repositories.SpielRepository
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.*


@Entity
data class Mannschaft(
        @Id
        @GeneratedValue
        val id: Long,
        val name: String,
        @ManyToOne
        @JoinColumn(name = "verein_id")
        val verein: Verein,
        @ManyToOne
        @JoinColumn(name = "tabelle_id")
        val tabelle: Tabelle,
        val torverhaeltnis: Pair<Int, Int>,
        val punkteverhaeltnis: Pair<Int, Int>,
        @ManyToOne
        @JoinColumn(name = "jugend_id")
        val jugend: Jugend,

        @Transient
        @Autowired
        private val spielRepository: SpielRepository

) {
    //@Transient
    //var allSpiel: List<Spiel> = spielRepository.findAllByMannschaft(this)

}