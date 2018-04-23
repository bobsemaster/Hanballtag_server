package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Spiel
import de.schreib.handball.handballtag.entities.SpielTor
import de.schreib.handball.handballtag.exceptions.MannschaftAlreadyPlaysException
import de.schreib.handball.handballtag.exceptions.MannschaftDoesNotExsistException
import de.schreib.handball.handballtag.exceptions.SpielAlreadyExistsException
import de.schreib.handball.handballtag.exceptions.SpielNotFoundException
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import de.schreib.handball.handballtag.repositories.SpielRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("spiel/")
class SpielController(
        @Autowired
        val spielRepository: SpielRepository,
        @Autowired
        val mannschaftRepository: MannschaftRepository
) {
    @GetMapping("{id}")
    fun getSpielById(@PathVariable id: Long): Spiel {
        val spiel = spielRepository.findById(id)
        if (spiel.isPresent) {
            return spiel.get()
        } else {
            throw SpielNotFoundException("Spiel mit $id konnte nicht gefunden werden!")
        }
    }

    @GetMapping("all")
    fun getAllSpiel(): List<Spiel> {
        return spielRepository.findAll()
    }

    @PostMapping("new")
    fun addSpiel(@RequestBody spiel: Spiel) {
        if (!spielRepository.findById(spiel.id).isPresent) {
            throw SpielAlreadyExistsException("Du darft keine id zum Spiel mitsenden die ID wird autogeneriert!")
        }
        val spieleHeim = spielRepository.findAllByMannschaft(spiel.heimMannschaft)
        val spieleGast = spielRepository.findAllByMannschaft(spiel.gastMannschaft)
        if (spieleHeim.map { it.dateTime }.contains(spiel.dateTime) || spieleGast.map { it.dateTime }.contains(spiel.dateTime)) {
            throw MannschaftAlreadyPlaysException("Die heim oder gastmannschaft spielt bereits um ${spiel.dateTime}")
        }

        spielRepository.save(spiel)
    }

    @GetMapping("{id}/tor/{mannschaftsId}")
    fun tor(@PathVariable id: Long, @PathVariable mannschaftsId: Long) {
        val spielOptional = spielRepository.findById(id)
        if (!spielOptional.isPresent) {
            throw SpielNotFoundException("Es existiert kein spiel mit der Id $id")
        }
        val spiel = spielOptional.get()
        if (spiel.heimMannschaft.id == mannschaftsId) {
            val newSpiel = spiel.copy(heimTore = spiel.heimTore + 1, allGeworfeneTore = spiel.allGeworfeneTore
                    .plus(SpielTor(-1, spiel.heimMannschaft, spiel.currentDuration)))
            spielRepository.save(newSpiel)
            return
        }
        if (spiel.gastMannschaft.id == mannschaftsId) {
            // Minus eins als Id bei Spiel tor, damit eine passende Id autogeneriert wird
            val newSpiel = spiel.copy(gastTore = spiel.gastTore + 1, allGeworfeneTore = spiel.allGeworfeneTore
                    .plus(SpielTor(-1, spiel.gastMannschaft, spiel.currentDuration)))
            spielRepository.save(newSpiel)
            return
        }
        throw MannschaftDoesNotExsistException("Mannschaft mit der id $id spielt nicht im Spiel mit der id $id")
    }
}