package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Verkauf
import de.schreib.handball.handballtag.entities.VerkaufArtikel
import de.schreib.handball.handballtag.repositories.VerkaufArtikelRepository
import de.schreib.handball.handballtag.repositories.VerkaufRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.PostConstruct

@RestController
@RequestMapping("/verkauf/")
class VerkaufController(
        @Autowired val verkaufRepository: VerkaufRepository,
        @Autowired val verkaufArtikelRepository: VerkaufArtikelRepository
) {

    @PostConstruct
    fun initVerkauf() {
        if (verkaufRepository.findAll().isEmpty()) {
            val verkauf = Verkauf()
            verkaufRepository.save(verkauf)
        }
    }

    @GetMapping("all")
    fun getVerkaufObject(): Verkauf = verkaufRepository.findAll()[0]

    @Secured(ROLE_SPIELLEITER)
    @DeleteMapping("artikel/{id}")
    fun deleteArtikel(@PathVariable id: Long) {
        verkaufArtikelRepository.deleteById(id)
    }

    @Secured(ROLE_SPIELLEITER)
    @PostMapping("artikel/all")
    fun addArtikelList(@RequestBody allNewVerkaufArtikel: List<VerkaufArtikel>) {
        verkaufArtikelRepository.saveAll(allNewVerkaufArtikel)
        val verkauf = verkaufRepository.findAll()[0]
        val allVerkaufArtikel = allNewVerkaufArtikel.plus(verkauf.verkaufArtikel).distinctBy { it.id }
        verkaufRepository.save(verkauf.copy(verkaufArtikel = allVerkaufArtikel))
    }

    @Secured(ROLE_SPIELLEITER)
    @PostMapping("artikel")
    fun addOrUpdateArtikel(@RequestBody verkaufArtikel: VerkaufArtikel) {
        verkaufArtikelRepository.save(verkaufArtikel)
        val verkauf = verkaufRepository.findAll()[0]
        if(verkauf.verkaufArtikel.find { it.id == verkaufArtikel.id } == null){
            verkaufRepository.save(verkauf.copy(verkaufArtikel = verkauf.verkaufArtikel.plus(verkaufArtikel)))
        }
    }

    @GetMapping("tombola")
    fun getTombolaInfo(): Pair<Boolean, Boolean> {
        val verkauf = verkaufRepository.findAll()[0]
        return Pair(verkauf.isLosverkaufGestartet, verkauf.isPreisvergabeGestartet)
    }

    @Secured(ROLE_SPIELLEITER)
    @GetMapping("/tombola/verkauf/{newStatus}")
    fun setTombolaVerkauf(@PathVariable newStatus: Boolean) {
        var verkauf = verkaufRepository.findAll()[0]
        verkauf = verkauf.copy(isLosverkaufGestartet = newStatus)
        verkaufRepository.save(verkauf)
    }

    @Secured(ROLE_SPIELLEITER)
    @GetMapping("/tombola/preisvergabe/{newStatus}")
    fun setTombolaPreisvergabe(@PathVariable newStatus: Boolean) {
        var verkauf = verkaufRepository.findAll()[0]
        verkauf = verkauf.copy(isPreisvergabeGestartet = newStatus)
        verkaufRepository.save(verkauf)
    }

    @GetMapping("grill")
    fun getGrillStatus(): Boolean = verkaufRepository.findAll()[0].isGrillAn

    @Secured(ROLE_SPIELLEITER)
    @GetMapping("grill/{newStatus}")
    fun setGrillStatus(@PathVariable newStatus: Boolean) {
        var verkauf = verkaufRepository.findAll()[0]
        verkauf = verkauf.copy(isGrillAn = newStatus)
        verkaufRepository.save(verkauf)
    }

}