package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Verein
import de.schreib.handball.handballtag.exceptions.VereinAlreadyExistException
import de.schreib.handball.handballtag.exceptions.VereinNotFoundException
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import de.schreib.handball.handballtag.repositories.VereinRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("Verein/")
class VereinController(
        @Autowired val vereinRepository: VereinRepository,
        @Autowired val mannschaftRepository: MannschaftRepository
) {

    @GetMapping("all")
    fun getAllVerein() = vereinRepository.findAll()

    @GetMapping("{id}")
    fun findVereinById(@PathVariable id: Long): Verein {
        val verein = vereinRepository.findById(id)
        if(verein.isPresent){
            return verein.get()
        }
        throw VereinNotFoundException("Verein mit id $id konnte nicht gefunden werden!")
    }

    @PostMapping("new")
    fun createNewVerein(@RequestBody verein: Verein) {
        if (vereinRepository.findByName(verein.name) != null) {
            throw VereinAlreadyExistException("Der Verein mit der Id ${verein.id} existiert bereits")
        }
        if (verein.allMannschaft.isNotEmpty()) {
            throw IllegalArgumentException("Ein neuer Verein kann keine Mannschaften haben bitte " +
                    "schicke für allMannschaft eine leere Liste")
        }
        vereinRepository.save(verein)
    }

    @DeleteMapping("delete/{id}")
    fun deleteVerein(id: Long) {
        vereinRepository.deleteById(id)
    }

}