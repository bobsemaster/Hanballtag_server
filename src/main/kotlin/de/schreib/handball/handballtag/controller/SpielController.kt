package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Spiel
import de.schreib.handball.handballtag.entities.SpielTor
import de.schreib.handball.handballtag.exceptions.SpielNotFoundException
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import de.schreib.handball.handballtag.repositories.SpielRepository
import de.schreib.handball.handballtag.repositories.SpielTorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AuthorizationServiceException
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("spiel/")
class SpielController(
        @Autowired val mannschaftRepository: MannschaftRepository,
        @Autowired val spielRepository: SpielRepository,
        @Autowired val spielTorRepository: SpielTorRepository
) {
    @GetMapping("all")
    fun getAllSpiel(): List<Spiel> = spielRepository.findAll()

    @GetMapping("{id}")
    fun getSpielById(@PathVariable id: Long): Spiel {
        val spielOptional = spielRepository.findById(id)
        if (!spielOptional.isPresent) {
            throw SpielNotFoundException("Spiel mit der id $id konnte nicht gefunden werden")
        }
        return spielOptional.get()
    }

    @Secured(ROLE_SPIELLEITER)
    @PostMapping("new")
    fun newSpiel(@RequestBody newSpiel: Spiel) {
        //TODO falls spielplan nicht autogeneriert wird
    }

    @Secured(ROLE_KAMPFGERICHT)
    @GetMapping("{id}/heimmannschaft/anwesend")
    fun setHeimmannschaftAnwesend(@PathVariable id: Long) {
        val spiel = getSpielById(id).copy(isHeimmannschaftAnwesend = true)
        spielRepository.save(spiel)
    }

    @Secured(ROLE_KAMPFGERICHT)
    @GetMapping("{id}/gastmannschaft/anwesend")
    fun setGastMannschaftAnwesend(@PathVariable id: Long) {
        val spiel = getSpielById(id).copy(isGastMannschaftAnwesend = true)
        spielRepository.save(spiel)
    }

    @Secured(ROLE_KAMPFGERICHT)
    @GetMapping("{id}/kampfgericht/anwesend")
    fun setKampfgerichtAnwesend(@PathVariable id: Long) {
        val spiel = getSpielById(id).copy(isKampfgerichtAnwesend = true)
        spielRepository.save(spiel)
    }

    @Secured(ROLE_KAMPFGERICHT)
    @GetMapping("{id}/schiedsrichter/anwesend")
    fun setSchiedsrichterAnwesend(@PathVariable id: Long) {
        val spiel = getSpielById(id).copy(isSchiedsrichterAnwesend = true)
        spielRepository.save(spiel)
    }

    @Secured(ROLE_KAMPFGERICHT)
    @PostMapping("{id}/ergebnis")
    fun setSpielErgebnisKampfgericht(@RequestBody ergebnis: SpielErgebnis, @PathVariable id: Long) {
        val spiel = getSpielById(id)
        if (spiel.gastTore != 0 || spiel.heimTore != 0) {
            throw AuthorizationServiceException("Kampfgericht ist nicht autorisert das spielergebnis nachträglich zu ändern")
        }
        spielRepository.save(spiel.copy(heimTore = ergebnis.toreHeim, gastTore = ergebnis.toreGast))
    }

    @Secured(ROLE_SPIELLEITER)
    @PostMapping("{id}/spielstand")
    fun setSpielstandSpielleiter(@RequestBody ergebnis: SpielErgebnis, @PathVariable id: Long) {
        val spiel = getSpielById(id)
        spielRepository.save(spiel.copy(heimTore = ergebnis.toreHeim, gastTore = ergebnis.toreGast))
    }

    @Secured(ROLE_KAMPFGERICHT)
    @GetMapping("{spielId}/tor/{mannschaftId}")
    fun tor(@PathVariable spielId: Long, @PathVariable mannschaftId: Long) {
        val spiel = getSpielById(spielId)
        if (!(spiel.heimMannschaft.id == mannschaftId || spiel.gastMannschaft.id == mannschaftId)) {
            throw IllegalStateException("Die mannschaft mit der Id $mannschaftId spielt nicht im spiel mit der ID $spielId")
        }
        if (spiel.gastMannschaft.id == mannschaftId) {
            val spielTor = SpielTor(mannschaft = spiel.gastMannschaft, time = spiel.currentDuration)
            spielTorRepository.save(spielTor)
            spielRepository.save(spiel.copy(gastTore = spiel.gastTore + 1, allGeworfeneTore = spiel.allGeworfeneTore.plus(spielTor)))
        } else if (spiel.heimMannschaft.id == mannschaftId) {
            val spielTor = SpielTor(mannschaft = spiel.heimMannschaft, time = spiel.currentDuration)
            spielTorRepository.save(spielTor)
            spielRepository.save(spiel.copy(gastTore = spiel.heimTore + 1, allGeworfeneTore = spiel.allGeworfeneTore.plus(spielTor)))
        }
    }
}

data class SpielErgebnis(val toreHeim: Int, val toreGast: Int)