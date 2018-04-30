package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Verkauf
import de.schreib.handball.handballtag.entities.VerkaufArtikel
import de.schreib.handball.handballtag.repositories.VerkaufArtikelRepository
import de.schreib.handball.handballtag.repositories.VerkaufRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.annotation.PostConstruct

@RestController
@RequestMapping("verkauf/")
class VerkaufController(
        @Autowired val verkaufRepository: VerkaufRepository,
        @Autowired val verkaufArtikelRepository: VerkaufArtikelRepository
) {
    lateinit var verkauf: Verkauf

    @PostConstruct
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

    @PostMapping("artikel")
    fun setArtikelList(@RequestBody allVerkaufArtikel: List<VerkaufArtikel>) {
        verkaufArtikelRepository.saveAll(allVerkaufArtikel)
        verkauf = verkauf.copy(verkaufArtikel = allVerkaufArtikel)
        verkaufRepository.save(verkauf)
    }

    @GetMapping("tombola")
    fun getTombolaInfo(): Pair<Boolean, Boolean> {
        return Pair(verkauf.isLosverkaufGestartet, verkauf.isPreisvergabeGestartet)
    }

    @GetMapping("/tombola/verkauf/{newStatus}")
    fun setTombolaVerkauf(@PathVariable newStatus: Boolean) {
        verkauf = verkauf.copy(isLosverkaufGestartet = newStatus)
        verkaufRepository.save(verkauf)
    }

    @GetMapping("/tombola/preisvergabe/{newStatus}")
    fun setTombolaPreisvergabe(@PathVariable newStatus: Boolean) {
        verkauf = verkauf.copy(isPreisvergabeGestartet = newStatus)
        verkaufRepository.save(verkauf)
    }

    @GetMapping("grill")
    fun getGrillStatus(): Boolean = verkauf.isGrillAn

    @GetMapping("grill/{newStatus}")
    fun setGrillStatus(@PathVariable newStatus: Boolean) {
        verkauf = verkauf.copy(isGrillAn = newStatus)
        verkaufRepository.save(verkauf)
    }

}