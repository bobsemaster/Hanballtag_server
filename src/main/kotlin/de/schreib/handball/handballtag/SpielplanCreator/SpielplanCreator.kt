package de.schreib.handball.handballtag.SpielplanCreator

import de.schreib.handball.handballtag.entities.Jugend
import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.entities.Spiel
import de.schreib.handball.handballtag.entities.SpielTyp
import de.schreib.handball.handballtag.exceptions.NotEnoughMannschaftenException
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import de.schreib.handball.handballtag.repositories.SpielRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


@Service
class SpielplanCreatorService(@Autowired val mannschaftRepository: MannschaftRepository, @Autowired val spielRepository: SpielRepository) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var spielDuration: Duration
    private lateinit var pauseDuration: Duration
    private lateinit var turnierBeginn: LocalDateTime
    private lateinit var allJugendMannschaft: List<Mannschaft>
    private val spielplanList: MutableList<Spiel> = mutableListOf()
    private var currentGroup = 1
    private var spielplatz: Int = -1

    // sechsMannschaftenGruppe entscheidung ob man den spielplasn mit gruppen erstellt oder nicht
    public fun createSpielplan(jugend: Jugend, spielDuration: Duration, pauseDuration: Duration, turnierBeginn: LocalDateTime, spielplatz: Int, sechsMannschaftenGruppe: Boolean = false) {
        allJugendMannschaft = mannschaftRepository.findAllByJugend(jugend)

        this.pauseDuration = pauseDuration
        this.spielDuration = spielDuration
        this.turnierBeginn = turnierBeginn

        currentGroup = 1
        this.spielplatz = spielplatz
        spielplanList.clear()

        when (allJugendMannschaft.size) {
            1 -> {
                log.error("Es werden mindestens 2 Mannschaften für einen Spielplan gebraucht!")
                throw NotEnoughMannschaftenException("Es sind mindestens 2 mannschaften nötig um einen Spielplan zu erstellen!")
            }
            2 -> createSpielplan2Mannschaften(allJugendMannschaft)
            3 -> createSpielplan3Mannschaften(allJugendMannschaft)
            4 -> createSpielplan4Mannschaften(allJugendMannschaft)
            5 -> createSpielplan5Mannschaften(allJugendMannschaft)
            6 -> createSpielplan6Mannschaften(allJugendMannschaft, sechsMannschaftenGruppe)
            7 -> createSpielplan7Mannschaften(allJugendMannschaft)
            8 -> createSpielplan8Mannschaften(allJugendMannschaft)
            9 -> createSpielplan9Mannschaften(allJugendMannschaft)
            10 -> createSpielplan10Mannschaften(allJugendMannschaft)
            else -> log.error("Es dürfen höchstens 10 Mannschaften für einen Spielplan übergeben werden!")
        }

        spielRepository.saveAll(spielplanList)
    }

    private fun createSpielplan10Mannschaften(allJugendMannschaft: List<Mannschaft>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createSpielplan9Mannschaften(allJugendMannschaft: List<Mannschaft>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createSpielplan8Mannschaften(allJugendMannschaft: List<Mannschaft>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createSpielplan7Mannschaften(allJugendMannschaft: List<Mannschaft>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createSpielplan6Mannschaften(allJugendMannschaft: List<Mannschaft>, sechsMannschaftenGruppe: Boolean) {
        if (sechsMannschaftenGruppe) {
            createSpielplan6MannschaftenGruppe(allJugendMannschaft)
        } else {
            createSpielplan6MannschaftenKeineGruppe(allJugendMannschaft)
        }
    }

    private fun createSpielplan6MannschaftenKeineGruppe(allJugendMannschaft: List<Mannschaft>) {
        spiel(Kuerzel.A, Kuerzel.B)
        spiel(Kuerzel.C, Kuerzel.D)
        spiel(Kuerzel.E, Kuerzel.F)
        spiel(Kuerzel.A, Kuerzel.C)
        spiel(Kuerzel.B, Kuerzel.E)
        spiel(Kuerzel.D, Kuerzel.F)
        spiel(Kuerzel.E, Kuerzel.A)
        spiel(Kuerzel.B, Kuerzel.D)
        spiel(Kuerzel.F, Kuerzel.C)
        spiel(Kuerzel.A, Kuerzel.D)
        spiel(Kuerzel.F, Kuerzel.B)
        spiel(Kuerzel.C, Kuerzel.E)
        spiel(Kuerzel.F, Kuerzel.A)
        spiel(Kuerzel.B, Kuerzel.C)
        spiel(Kuerzel.D, Kuerzel.E)

    }

    private fun createSpielplan6MannschaftenGruppe(allJugendMannschaft: List<Mannschaft>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createSpielplan5Mannschaften(allJugendMannschaft: List<Mannschaft>) {
        spiel(Kuerzel.A, Kuerzel.B)
        spiel(Kuerzel.C, Kuerzel.D)
        spiel(Kuerzel.E, Kuerzel.A)
        spiel(Kuerzel.B, Kuerzel.C)
        spiel(Kuerzel.D, Kuerzel.E)
        spiel(Kuerzel.A, Kuerzel.C)
        spiel(Kuerzel.E, Kuerzel.B)
        spiel(Kuerzel.D, Kuerzel.A)
        spiel(Kuerzel.C, Kuerzel.E)
        spiel(Kuerzel.B, Kuerzel.D)
    }

    private fun createSpielplan3Mannschaften(allJugendMannschaft: List<Mannschaft>) {
        spiel(Kuerzel.A, Kuerzel.B)
        spiel(Kuerzel.C, Kuerzel.A)
        spiel(Kuerzel.B, Kuerzel.C)
        spiel(Kuerzel.B, Kuerzel.A)
        spiel(Kuerzel.A, Kuerzel.C)
        spiel(Kuerzel.C, Kuerzel.B)

        // Doppelrunde! Jedes Spiel wird einfach wiederholt.
        // Wir brauchen eine zweite liste um über spielplanList iterieren zu können und die Spiele später der spielplanliste hinzuzufügen
        // Wenn man über die spielplanList iteriert und versucht dieser ein Element hinzuzufügen
        // wird eine ConcurrentModificationException geworfen
        val doppelRundeList = mutableListOf<Spiel>()
        spielplanList.forEach {
            val spielCopy = it.copy(dateTime = turnierBeginn, heimMannschaft = it.gastMannschaft, gastMannschaft = it.heimMannschaft)
            doppelRundeList.add(spielCopy)
            turnierBeginn = turnierBeginn.plus(spielDuration.plus(pauseDuration))
        }
        spielplanList.addAll(doppelRundeList)
    }

    private fun createSpielplan2Mannschaften(allJugendMannschaft: List<Mannschaft>) {
        spiel(Kuerzel.A, Kuerzel.B)
        spiel(Kuerzel.B, Kuerzel.A)
    }


    private fun createSpielplan4Mannschaften(allJugendMannschaft: List<Mannschaft>) {


        // A gegen B
        spiel(Kuerzel.A, Kuerzel.B)
        // C gegen D
        spiel(Kuerzel.C, Kuerzel.D)
        // A gegen C
        spiel(Kuerzel.A, Kuerzel.C)
        // B gegen D
        spiel(Kuerzel.B, Kuerzel.D)
        // D gegen A
        spiel(Kuerzel.D, Kuerzel.A)
        // C gegen B
        spiel(Kuerzel.C, Kuerzel.B)
        // A gegen D
        spiel(Kuerzel.A, Kuerzel.D)
        // C gegen A
        spiel(Kuerzel.C, Kuerzel.A)
        // D gegen B
        spiel(Kuerzel.D, Kuerzel.B)
        // B gegen C
        spiel(Kuerzel.B, Kuerzel.C)
        // D gegen C
        spiel(Kuerzel.D, Kuerzel.C)
        // B gegen A
        spiel(Kuerzel.B, Kuerzel.A)


        // Doppelrunde! Jedes Spiel wird einfach wiederholt.
        // Wir brauchen eine zweite liste um über spielplanList iterieren zu können und die Spiele später der spielplanliste hinzuzufügen
        // Wenn man über die spielplanList iteriert und versucht dieser ein Element hinzuzufügen
        // wird eine ConcurrentModificationException geworfen
        val doppelRundeList = mutableListOf<Spiel>()
        spielplanList.forEach {
            val spielCopy = it.copy(dateTime = turnierBeginn, heimMannschaft = it.gastMannschaft, gastMannschaft = it.heimMannschaft)
            doppelRundeList.add(spielCopy)
            turnierBeginn = turnierBeginn.plus(spielDuration.plus(pauseDuration))
        }
        spielplanList.addAll(doppelRundeList)
    }

    private fun spiel(heim: Kuerzel, gast: Kuerzel, spielTyp: SpielTyp = SpielTyp.GRUPPENSPIEL) {
        if (currentGroup == 1) {
            spielplanList.add(allJugendMannschaft.createSpiel(Kuerzel.A, Kuerzel.B, spielDuration, turnierBeginn, spielTyp))
        }
        turnierBeginn = turnierBeginn.plus(spielDuration.plus(pauseDuration))
    }

    private fun List<Mannschaft>.createSpiel(heim: Kuerzel, gast: Kuerzel, spielDuration: Duration, time: LocalDateTime, spielTyp: SpielTyp): Spiel {
        return Spiel(heimMannschaft = this[heim.index], gastMannschaft = this[gast.index], halftimeDuration = spielDuration, dateTime = time, spielTyp = spielTyp, spielPlatz = spielplatz)

    }

}

enum class Kuerzel(val index: Int) {
    A(0), B(1), C(2), D(3), E(4), F(5)
}


// https://stackoverflow.com/questions/18802997/given-a-set-of-number-how-do-you-permute-a-pair-of-numbers
fun createUniquePairPermutations(dataSet: List<Kuerzel>): List<List<Kuerzel>> {
    val result = ArrayList<ArrayList<Kuerzel>>()

    for (i in dataSet.indices) {
        for (j in dataSet.indices) {
            if (i == j)
                continue
            val tmp = ArrayList<Kuerzel>()
            tmp.add(dataSet[i])
            tmp.add(dataSet[j])
            result.add(tmp)
        }
    }
    return result
}
