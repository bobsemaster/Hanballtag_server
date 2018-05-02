package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Tabelle
import de.schreib.handball.handballtag.exceptions.TabelleNotFoundException
import de.schreib.handball.handballtag.repositories.TabelleRepository
import de.schreib.handball.handballtag.security.SPIELLEITER
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("tabelle/")
class TabelleController(
        @Autowired val tabelleRepository: TabelleRepository
) {
    @GetMapping("all")
    fun getAllTabelle(): List<Tabelle> = tabelleRepository.findAll()

    @GetMapping("{id}")
    fun getTabelleById(@PathVariable id: Long): Tabelle {
        val tabelleOptional = tabelleRepository.findById(id)
        if (!tabelleOptional.isPresent) {
            throw TabelleNotFoundException("Tabelle mit id $id existiert nicht")
        }
        return tabelleOptional.get()
    }

    @Secured(SPIELLEITER)
    @PostMapping("new")
    fun newTabelle(@RequestBody tabelle: Tabelle) {
        if (tabelle.allMannschaft.isNotEmpty()) {
            throw IllegalStateException("Property allMannschaft von Tabelle darf nicht belegt sein!")
        }
        tabelleRepository.save(tabelle)
    }

    @Secured(SPIELLEITER)
    @DeleteMapping("{id}")
    fun deleteTabelle(@PathVariable id: Long){
        tabelleRepository.deleteById(id)
    }

}