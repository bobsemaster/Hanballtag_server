package de.schreib.handball.handballtag.entities

import de.schreib.handball.handballtag.repositories.SpielRepository
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.*


/**
 * Diese Klasse bildet eine Mannschaft ab und speichert den Namen der Mannschaft den Zugehörigen verein, die Tabelle
 * in der Diese Mannschaft zu finden ist, das Zorverhältnis nud PunkteVerhältnis der Mannschaft und die jugend ab.
 * Die Spiele der Mannschaft werden bei erstellen Der klasse aus dem SPiel repository nachgeladen. Die Spiele weden auch nicht
 * in der Datenbank Tabelle zur Mannschaft abgespeichert.
 */
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
        val torverhaeltnis: Pair<Int, Int> = Pair(0, 0),
        val punkteverhaeltnis: Pair<Int, Int> = Pair(0, 0),
        val jugend: Jugend
) {

        @Transient
        @Autowired
        private val spielRepository: SpielRepository

) {
    @Transient
    var allSpiel: List<Spiel> = spielRepository.findAllByMannschaft(this)

}