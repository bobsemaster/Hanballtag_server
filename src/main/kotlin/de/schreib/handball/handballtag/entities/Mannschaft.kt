package de.schreib.handball.handballtag.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import de.schreib.handball.handballtag.repositories.SpielRepository
import org.jboss.logging.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Transient

/**
 * Diese Klasse bildet eine Mannschaft ab und speichert den Namen der Mannschaft den Zugehörigen verein, die Tabelle
 * in der Diese Mannschaft zu finden ist, das Zorverhältnis nud PunkteVerhältnis der Mannschaft und die jugend ab.
 * Die Spiele der Mannschaft werden bei erstellen Der klasse aus dem SPiel repository nachgeladen. Die Spiele weden
 *  auch nicht in der Datenbank Tabelle zur Mannschaft abgespeichert.
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
        val torverhaeltnis: Pair<Int, Int> = Pair(0, 0),
        val punkteverhaeltnis: Pair<Int, Int> = Pair(0, 0),
        val jugend: Jugend,
        val hasFoto: Boolean = false,
        val tabellenPlatz: Int = 0,
        val gruppe: Int = 0
) {

    @Component
    companion object {
        @Transient
        lateinit var spielRepository: SpielRepository
        private val log = Logger.getLogger(this::class.java)

    }

    @JsonIgnore
    fun getAllSpiel(): List<Spiel> {
        return spielRepository.findAllByMannschaft(this)
    }

}

@Service
class MannschaftSpielService {
    @Autowired
    private lateinit var spielRepository: SpielRepository
    val log = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun initializeVereinRepository() {
        Mannschaft.spielRepository = this.spielRepository
        log.info("Initialized Spiel Repository in Mannschaft")
    }
}

