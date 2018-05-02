package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.exceptions.MannschaftNotFoundException
import de.schreib.handball.handballtag.exceptions.TabelleNotFoundException
import de.schreib.handball.handballtag.exceptions.VereinNotFoundException
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import de.schreib.handball.handballtag.repositories.SpielRepository
import de.schreib.handball.handballtag.repositories.TabelleRepository
import de.schreib.handball.handballtag.repositories.VereinRepository
import de.schreib.handball.handballtag.security.SPIELLEITER
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("mannschaft/")
class MannschaftController(
        @Autowired val mannschaftRepository: MannschaftRepository,
        @Autowired val tabelleRepository: TabelleRepository,
        @Autowired val vereinRepository: VereinRepository,
        @Autowired val spielRepository: SpielRepository
) {

    @GetMapping("all")
    fun getAllMannschaft(): List<Mannschaft> = mannschaftRepository.findAll()

    @GetMapping("{id}")
    fun findMannschaftById(@PathVariable id: Long): Mannschaft {
        val mannschaftOptional = mannschaftRepository.findById(id)
        if (!mannschaftOptional.isPresent) {
            throw MannschaftNotFoundException("Mannschaft mit id $id konnte nicht gefunden werden!")
        }
        return mannschaftOptional.get()
    }

    @Secured(SPIELLEITER)
    @PostMapping("new")
    fun createMannschaft(@RequestBody mannschaft: Mannschaft) {
        if (!vereinRepository.findById(mannschaft.verein.id).isPresent) {
            throw VereinNotFoundException("Der Verein '${mannschaft.verein.name}' existiert nicht")
        }
        if (!tabelleRepository.findByJugend(mannschaft.jugend).isPresent) {
            throw TabelleNotFoundException("Tabelle der ${mannschaft.jugend.typ} ${mannschaft.jugend.jahrgang} konnte nicht gefunden werden")
        }
        mannschaftRepository.save(mannschaft)
    }

    @Secured(SPIELLEITER)
    @DeleteMapping("{id}")
    fun deleteMannschaftWithId(@PathVariable id: Long) {
        mannschaftRepository.deleteById(id)
    }
}