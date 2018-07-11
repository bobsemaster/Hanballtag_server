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
import javax.transaction.Transactional

@RestController
@RequestMapping("/verkauf/")
class VerkaufController(
        @Autowired val verkaufRepository: VerkaufRepository,
        @Autowired val verkaufArtikelRepository: VerkaufArtikelRepository
) {
    lateinit var verkauf: Verkauf

    @PostConstruct
    @Transactional
    fun initVerkauf() {
        when (verkaufRepository.count()) {
            0L -> {
                val newVerkauf = Verkauf(verkaufArtikel = emptyList())
                verkaufRepository.save(newVerkauf)
                verkauf = newVerkauf
            }
            1L -> {
                verkauf = verkaufRepository.findAll()[0]
                verkauf.verkaufArtikel.count()
            }
            else -> {
                verkaufRepository.deleteAll(mutableListOf(verkaufRepository.findAll().removeAt(0)))
                verkauf = verkaufRepository.findAll()[0]
                throw IllegalStateException("Es sollte immer nur ein Verkauf objekt in der Datenbank " +
                        "gespeichert sein es waren aber mehrere Gespeichert -> Alle ausser das erste Objekt wurden gelöscht")
            }
        }

    }

    @GetMapping("all")
    fun getVerkaufObject(): Verkauf = verkauf

    @Secured(ROLE_SPIELLEITER)
    @DeleteMapping("artikel/{id}")
    fun deleteArtikel(@PathVariable id: Long) {
        verkauf = verkauf.copy(verkaufArtikel = verkauf.verkaufArtikel.filter { it.id != id })
        verkaufArtikelRepository.deleteById(id)
    }

    @Secured(ROLE_SPIELLEITER)
    @PostMapping("artikel/all")
    fun addArtikelList(@RequestBody allVerkaufArtikel: List<VerkaufArtikel>) {
        verkaufArtikelRepository.saveAll(allVerkaufArtikel)
        verkauf = verkauf.copy(verkaufArtikel = verkauf.verkaufArtikel.plus(allVerkaufArtikel))
        verkaufRepository.save(verkauf)
    }

    @Secured(ROLE_SPIELLEITER)
    @PostMapping("artikel")
    fun addOrUpdateArtikel(@RequestBody verkaufArtikel: VerkaufArtikel) {
        var new = false
        if (verkauf.verkaufArtikel.find { it.id == verkaufArtikel.id } == null) {
            new = true
        }
        verkaufArtikelRepository.save(verkaufArtikel)
        verkauf = if (!new) {
            verkauf.copy(verkaufArtikel = verkauf.verkaufArtikel.plus(verkaufArtikel))
        } else {
            verkauf.copy(verkaufArtikel = verkauf.verkaufArtikel
                    .filter { it.id != verkaufArtikel.id }.plus(verkaufArtikel))
        }
        verkaufRepository.save(verkauf)
    }

    @GetMapping("tombola")
    fun getTombolaInfo(): Pair<Boolean, Boolean> {
        return Pair(verkauf.isLosverkaufGestartet, verkauf.isPreisvergabeGestartet)
    }

    @Secured(ROLE_SPIELLEITER)
    @GetMapping("/tombola/verkauf/{newStatus}")
    fun setTombolaVerkauf(@PathVariable newStatus: Boolean) {
        verkauf = verkauf.copy(isLosverkaufGestartet = newStatus)
        verkaufRepository.save(verkauf)
    }

    @Secured(ROLE_SPIELLEITER)
    @GetMapping("/tombola/preisvergabe/{newStatus}")
    fun setTombolaPreisvergabe(@PathVariable newStatus: Boolean) {
        verkauf = verkauf.copy(isPreisvergabeGestartet = newStatus)
        verkaufRepository.save(verkauf)
    }

    @GetMapping("grill")
    fun getGrillStatus(): Boolean = verkauf.isGrillAn

    @Secured(ROLE_SPIELLEITER)
    @GetMapping("grill/{newStatus}")
    fun setGrillStatus(@PathVariable newStatus: Boolean) {
        verkauf = verkauf.copy(isGrillAn = newStatus)
        verkaufRepository.save(verkauf)
    }

}