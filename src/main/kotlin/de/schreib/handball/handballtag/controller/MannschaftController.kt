package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Gruppe
import de.schreib.handball.handballtag.entities.Jugend
import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.entities.Spiel
import de.schreib.handball.handballtag.exceptions.MannschaftNotFoundException
import de.schreib.handball.handballtag.exceptions.VereinNotFoundException
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import de.schreib.handball.handballtag.repositories.SpielRepository
import de.schreib.handball.handballtag.repositories.VereinRepository
import de.schreib.handball.handballtag.spielplan.creator.PLATZHALTER_VEREIN_NAME
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional

@RestController
@RequestMapping("/mannschaft/")
class MannschaftController(
        @Autowired val mannschaftRepository: MannschaftRepository,
        @Autowired val vereinRepository: VereinRepository,
        @Autowired val spielRepository: SpielRepository
) {

    @GetMapping("all")
    fun getAllMannschaft(): List<Mannschaft> = mannschaftRepository.findAll()
            .filter { it.verein.name != PLATZHALTER_VEREIN_NAME }
            .sortedBy { it.name }

    @GetMapping("{id}/spiele")
    fun getAllSpielToMannschaft(@PathVariable id: Long): List<Spiel> {
        val mannschaftOptional = mannschaftRepository.findById(id)
        if (mannschaftOptional.isPresent) {
            return mannschaftOptional.get().getAllSpiel().sortedBy { it.dateTime }
        } else {
            return emptyList()
        }
    }

    @Secured(ROLE_SPIELLEITER)
    @GetMapping("/{id}/gruppe/{neueGruppe}")
    fun updateGruppe(@PathVariable id: Long, @PathVariable neueGruppe: Gruppe) {
        val mannschaftOptional = mannschaftRepository.findById(id)
        if(!mannschaftOptional.isPresent){
            throw MannschaftNotFoundException("Mannschaft mit id $id existiert nicht!")
        }
        val mannschaft = mannschaftOptional.get()
        mannschaftRepository.save(mannschaft.copy(gruppe = neueGruppe))
    }

    @GetMapping("{id}")
    fun findMannschaftById(@PathVariable id: Long): Mannschaft {
        val mannschaftOptional = mannschaftRepository.findById(id)
        if (!mannschaftOptional.isPresent) {
            throw MannschaftNotFoundException("Mannschaft mit id $id konnte nicht gefunden werden!")
        }
        return mannschaftOptional.get()
    }

    @Secured(ROLE_SPIELLEITER)
    @PostMapping("new")
    fun createMannschaft(@RequestBody mannschaft: Mannschaft) {
        val vereinOptional = vereinRepository.findById(mannschaft.verein.id)
        if (!vereinOptional.isPresent) {
            throw VereinNotFoundException("Der Verein '${mannschaft.verein.name}' existiert nicht")
        }
        mannschaftRepository.save(mannschaft)
    }

    @GetMapping("jugend/all")
    fun getAllJugend(): List<Jugend> {
        return mannschaftRepository.findAll().map { it.jugend }.distinct().sortedBy { it.jahrgang }
    }

    @GetMapping("{id}/{hasFoto}")
    @Secured(ROLE_KAMPFGERICHT)
    fun setHasFoto(@PathVariable hasFoto: Boolean, @PathVariable id: Long): Mannschaft {
        val mannschaftOptional = mannschaftRepository.findById(id)
        if (mannschaftOptional.isPresent) {
            val mannschaft = mannschaftOptional.get()
            val mannschaftUpdate = mannschaft.copy(hasFoto = hasFoto)
            mannschaftRepository.save(mannschaftUpdate)
            return mannschaftUpdate
        }
        throw MannschaftNotFoundException("Mannschaft mit id $id konnte nicht gefunden werden")
    }

    @Transactional
    @Secured(ROLE_SPIELLEITER)
    @DeleteMapping("{id}")
    fun deleteMannschaftWithId(@PathVariable id: Long) {
        val mannschaftOptional = mannschaftRepository.findById(id)
        if (mannschaftOptional.isPresent) {
            val mannschaft = mannschaftOptional.get()
            // Lösche alle spiele in der diese Mannschaft mitspielt
            spielRepository.deleteAllByHeimMannschaftInOrGastMannschaftIn(listOf(mannschaft), listOf(mannschaft))
            mannschaftRepository.deleteById(id)
        }
    }

    @PostMapping("all/jugend")
    fun getAllMannschaftenToJugend(@RequestBody jugend: Jugend): List<Mannschaft> {
        return mannschaftRepository.findAllByJugend(jugend).sortedBy { it.tabellenPlatz }
    }

    @Secured(ROLE_SPIELLEITER)
    @GetMapping("{id}/spielplan/{index}")
    fun setSpielplanIndex(@PathVariable id:Long, @PathVariable index:Int){
        val mannschaftOptional = mannschaftRepository.findById(id)
        if (!mannschaftOptional.isPresent) {
            throw MannschaftNotFoundException("Mannschaft mit der id $id konnte nicht gefunden werden!")
        }
        mannschaftRepository.save(mannschaftOptional.get().copy(spielplanIndex = index))
    }
}