package de.schreib.handball.handballtag.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


/**
 * In einem Verein werden alle mannschaften gespeichert, die von diesem Verein angemeldet sind, außerdem wird der Name
 * des Vereins noch abgespeichert.
 */
@Entity
data class Verein(
        // Lass hibernate die Id generieren val damit user die id nicht verändern kann
        @Id
        @GeneratedValue
        val id: Long = 0,
        @Column(unique = true)
        val name: String

) {

    companion object {
        @Transient
        lateinit var mannschaftRepository: MannschaftRepository
    }

    @JsonIgnore
    fun getAllMannschaft(): List<Mannschaft> {
        return mannschaftRepository.findAllByVerein(this)
    }
}


@Service
class VereinMannschaftService {
    @Autowired
    private lateinit var mannschaftRepository: MannschaftRepository

    val log = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun initializeVereinRepository() {
        Verein.mannschaftRepository = this.mannschaftRepository
        log.info("Initialized Mannschaft Repository in Verein")
    }
}