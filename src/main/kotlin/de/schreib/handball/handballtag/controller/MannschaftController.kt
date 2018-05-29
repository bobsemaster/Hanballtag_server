package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.exceptions.MannschaftNotFoundException
import de.schreib.handball.handballtag.exceptions.VereinNotFoundException
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import de.schreib.handball.handballtag.repositories.SpielRepository
import de.schreib.handball.handballtag.repositories.VereinRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@RequestMapping("mannschaft/")
class MannschaftController(
        @Autowired val mannschaftRepository: MannschaftRepository,
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

    @Secured(ROLE_SPIELLEITER)
    @PostMapping("new")
    fun createMannschaft(@RequestBody mannschaft: Mannschaft) {
        val vereinOptional = vereinRepository.findById(mannschaft.verein.id)
        if (!vereinOptional.isPresent) {
            throw VereinNotFoundException("Der Verein '${mannschaft.verein.name}' existiert nicht")
        }
        mannschaftRepository.save(mannschaft)
    }

    @Transactional
    @Secured(ROLE_SPIELLEITER)
    @DeleteMapping("{id}")
    fun deleteMannschaftWithId(@PathVariable id: Long) {
        val mannschaftOptional = mannschaftRepository.findById(id)
        if(mannschaftOptional.isPresent) {
            val mannschaft = mannschaftOptional.get()
            // Lösche alle spiele in der diese Mannschaft mitspielt
            spielRepository.deleteAllByHeimMannschaftInOrGastMannschaftIn(listOf(mannschaft), listOf(mannschaft))
            mannschaftRepository.deleteById(id)
        }
    }
}