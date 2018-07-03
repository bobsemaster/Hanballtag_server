package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Jugend
import de.schreib.handball.handballtag.entities.Spiel
import de.schreib.handball.handballtag.exceptions.SpielNotFoundException
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import de.schreib.handball.handballtag.repositories.SpielRepository
import de.schreib.handball.handballtag.spielplan.creator.SpielplanCreatorService
import de.schreib.handball.handballtag.tabelle.TabelleService
import kotlinx.coroutines.experimental.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AuthorizationServiceException
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.LocalDateTime
import javax.transaction.Transactional

@RestController
@RequestMapping("/spiel/")
class SpielController(
        @Autowired val mannschaftRepository: MannschaftRepository,
        @Autowired val spielRepository: SpielRepository,
        @Autowired val spielplanCreatorService: SpielplanCreatorService,
        @Autowired val tabelleService: TabelleService
) {
    @GetMapping("all")
    fun getAllSpiel(): List<Spiel> = spielRepository.findAll().sortedBy { it.dateTime }

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
        val updatedSpiel = spielRepository.save(spiel.copy(heimTore = ergebnis.toreHeim, gastTore = ergebnis.toreGast))
        tabelleService.processSpielergebnis(updatedSpiel)
    }

    @Secured(ROLE_SPIELLEITER)
    @PostMapping("{id}/spielstand")
    fun setSpielstandSpielleiter(@RequestBody ergebnis: SpielErgebnis, @PathVariable id: Long) {
        val spiel = getSpielById(id)
        val updatedSpiel = spielRepository.save(spiel.copy(heimTore = ergebnis.toreHeim, gastTore = ergebnis.toreGast))
        tabelleService.processSpielergebnis(updatedSpiel)
    }

    @Transactional
    @Secured(ROLE_KAMPFGERICHT)
    @PostMapping("createspielplan/one")
    fun createSpielplanForJugend(@RequestBody spielCreatorInfo: SpielCreatorInfo) {
        launch {
            spielplanCreatorService.createSpielplan(spielCreatorInfo.jugend, Duration.ofMinutes(spielCreatorInfo.spielDuration),
                    Duration.ofMinutes(spielCreatorInfo.pauseDuration), spielCreatorInfo.turnierBeginn, spielCreatorInfo.spielPlatz, spielCreatorInfo.sechsMannschaftenGruppe)
        }
    }


    @Secured(ROLE_KAMPFGERICHT)
    @PostMapping("createspielplan/multiple")
    fun createMultipleSpielplan(@RequestBody allSpielCreatorInfo: List<SpielCreatorInfo>) {
        launch {
            allSpielCreatorInfo.forEach {
                spielplanCreatorService.createSpielplan(it.jugend, Duration.ofMinutes(it.spielDuration), Duration.ofMinutes(it.pauseDuration),
                        it.turnierBeginn, it.spielPlatz, it.sechsMannschaftenGruppe)

            }
        }
    }

}

data class SpielCreatorInfo(
        val jugend: Jugend,
        val spielDuration: Long,
        val pauseDuration: Long,
        val turnierBeginn: LocalDateTime,
        val sechsMannschaftenGruppe: Boolean,
        val spielPlatz: Int
)

data class SpielErgebnis(val toreHeim: Int, val toreGast: Int)