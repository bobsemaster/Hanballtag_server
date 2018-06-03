package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.entities.Verein
import de.schreib.handball.handballtag.exceptions.VereinAlreadyExistException
import de.schreib.handball.handballtag.exceptions.VereinNotFoundException
import de.schreib.handball.handballtag.repositories.MannschaftRepository
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


const val ROLE_BASIC_USER = "ROLE_BASIC_USER"
const val ROLE_SPIELLEITER = "ROLE_SPIELLEITER"
const val ROLE_KAMPFGERICHT = "ROLE_KAMPFGERICHT"


@RestController
@RequestMapping("/verein/")
class VereinController(
        @Autowired val vereinRepository: VereinRepository,
        @Autowired val mannschaftRepository: MannschaftRepository
) {

    @GetMapping("all")
    fun getAllVerein(): List<Verein> = vereinRepository.findAll().filter { it.name != PLATZHALTER_VEREIN_NAME }

    @GetMapping("{id}/mannschaften")
    fun getAllVereinMannschaften(@PathVariable id: Long): List<Mannschaft> {
        val vereinOptional = vereinRepository.findById(id)
        return if (vereinOptional.isPresent) {
            vereinOptional.get().getAllMannschaft()
        } else {
            listOf()
        }
    }

    @GetMapping("{id}")
    @Throws(VereinNotFoundException::class)
    fun findVereinById(@PathVariable id: Long): Verein {
        val verein = vereinRepository.findById(id)
        if (verein.isPresent) {
            return verein.get()
        }
        throw VereinNotFoundException("Verein mit id $id konnte nicht gefunden werden!")
    }

    @Secured(ROLE_SPIELLEITER)
    @PostMapping("new")
    @Throws(VereinAlreadyExistException::class, IllegalArgumentException::class)
    fun createNewVerein(@RequestBody verein: Verein) {
        if (vereinRepository.findByName(verein.name) != null) {
            throw VereinAlreadyExistException("Der Verein mit dem namen ${verein.name} existiert bereits")
        }
        vereinRepository.save(verein)
    }

    @Secured(ROLE_SPIELLEITER)
    @DeleteMapping("delete/{id}")
    @Transactional
    fun deleteVerein(@PathVariable id: Long) {
        val verein = findVereinById(id)

        mannschaftRepository.deleteAllByVerein(verein)
        mannschaftRepository.flush()
        vereinRepository.deleteById(id)
    }

}