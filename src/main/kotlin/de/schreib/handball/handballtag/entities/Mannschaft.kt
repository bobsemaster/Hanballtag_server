package de.schreib.handball.handballtag.entities

import de.schreib.handball.handballtag.repositories.SpielRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.*


/**
 * Diese Klasse bildet eine Mannschaft ab und speichert den Namen der Mannschaft den Zugehörigen verein, die Tabelle
 * in der Diese Mannschaft zu finden ist, das Zorverhältnis nud PunkteVerhältnis der Mannschaft und die jugend ab.
 * Die Spiele der Mannschaft werden bei erstellen Der klasse aus dem SPiel repository nachgeladen. Die Spiele weden auch nicht
 * in der Datenbank Tabelle zur Mannschaft abgespeichert.
 */
@Entity
data class Mannschaft(
        // Lass hibernate die Id generieren val damit user die id nicht verändern kann
        @Id
        @GeneratedValue
        val id: Long = 0,
        val name: String,
        @ManyToOne
        @JoinColumn(name = "verein_id")
        val verein: Verein,
        @ManyToOne
        @JoinColumn(name = "tabelle_id")
        val tabelle: Tabelle,
        val torverhaeltnis: Pair<Int, Int> = Pair(0, 0),
        val punkteverhaeltnis: Pair<Int, Int> = Pair(0, 0),
        val jugend: Jugend
) {

    @Component
    companion object {
        @Transient
        @Autowired
        private lateinit var spielRepository: SpielRepository
    }

    fun getAllSpiel(): List<Spiel> {
        return spielRepository.findAllByMannschaft(this)
    }

}

