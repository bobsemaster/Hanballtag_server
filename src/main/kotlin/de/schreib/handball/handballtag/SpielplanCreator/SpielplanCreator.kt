package de.schreib.handball.handballtag.SpielplanCreator

import de.schreib.handball.handballtag.entities.*
import de.schreib.handball.handballtag.exceptions.NotEnoughMannschaftenException
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import de.schreib.handball.handballtag.repositories.SpielRepository
import de.schreib.handball.handballtag.repositories.VereinRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


@Service
class SpielplanCreatorService(@Autowired val mannschaftRepository: MannschaftRepository, @Autowired val spielRepository: SpielRepository,
                              @Autowired val vereinRepository: VereinRepository) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var spielDuration: Duration
    private lateinit var pauseDuration: Duration
    private lateinit var turnierBeginn: LocalDateTime
    private lateinit var allJugendMannschaft: List<Mannschaft>
    private val spielplanList: MutableList<Spiel> = mutableListOf()
    private var currentGroup = 1
    // Abwechselnd gruppe 1 und 2 muss aktiviert werden wenn man einen spielplan für 2 gruppen erstellen möchte
    private var alternateGroup = false
    private var spielplatz: Int = -1
    private val platzhalterVerein = loadPlatzhalter()

    private fun loadPlatzhalter(): Verein {
        val platzhalter: Verein? = vereinRepository.findByName("placeholder")
        return if (platzhalter == null) {
            val platzhalterVerein = Verein(name = "placeholder")
            vereinRepository.save(platzhalterVerein)
            platzhalterVerein
        } else {
            platzhalter
        }
    }


    // sechsMannschaftenGruppe entscheidung ob man den spielplasn mit gruppen erstellt oder nicht
    fun createSpielplan(jugend: Jugend, spielDuration: Duration, pauseDuration: Duration, turnierBeginn: LocalDateTime, spielplatz: Int, sechsMannschaftenGruppe: Boolean = false) {
        allJugendMannschaft = mannschaftRepository.findAllByJugend(jugend)
        // Alte Spiele löschen damit neuer Spielplan erzteugt werden kann
        spielRepository.deleteAllByHeimMannschaftInOrGastMannschaftIn(allJugendMannschaft, allJugendMannschaft)
        // NICHT deleteAllByVerein benutzen, da wir nur die Mannschaften löschen wollen, die in der Jugend spielen für
        // die wir einen Spielplan erstellen
        mannschaftRepository.deleteAll(allJugendMannschaft.filter { it.verein.name == platzhalterVerein.name })
        allJugendMannschaft = allJugendMannschaft.filter { it.verein.name != platzhalterVerein.name }


        this.pauseDuration = pauseDuration
        this.spielDuration = spielDuration
        this.turnierBeginn = turnierBeginn

        currentGroup = 1
        alternateGroup = false
        this.spielplatz = spielplatz

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

        // TODO validierung dass der Platz auf dem die Spiele stattfinden zu jedem Zeitpunkt frei ist
        spielRepository.saveAll(spielplanList)
        spielplanList.clear()
    }

    private fun createSpielplan10Mannschaften(allJugendMannschaft: List<Mannschaft>) {
        val ersterGruppeA = mannschaft("1. Gruppe A")
        val zweiterGruppeA = mannschaft("2. Gruppe A")
        val dritterGruppeA = mannschaft("3. Gruppe A")
        val vierterGruppeA = mannschaft("4. Gruppe A")
        val fuenfterGruppeA = mannschaft("5. Gruppe A")
        val ersterGruppeB = mannschaft("1. Gruppe B")
        val zweiterGruppeB = mannschaft("2. Gruppe B")
        val dritterGruppeB = mannschaft("3. Gruppe B")
        val vierterGruppeB = mannschaft("4. Gruppe B")
        val fuenfterGruppeB = mannschaft("5. Gruppe B")
        val verliererErstesHalbfinale = mannschaft("Verlierer 1. Halbfinale")
        val verliererZweitesHalbfinale = mannschaft("Verlierer 2. Halbfinale")
        val siegerErstesHalbfinale = mannschaft("Sieger 1. Halbfinale")
        val siegerZweitesHalbfinale = mannschaft("Sieger 2. Halbfinale")

        alternateGroup = true
        spiel(Kuerzel.A, Kuerzel.B)
        spiel(Kuerzel.F, Kuerzel.G)
        spiel(Kuerzel.C, Kuerzel.D)
        spiel(Kuerzel.H, Kuerzel.I)
        spiel(Kuerzel.E, Kuerzel.A)
        spiel(Kuerzel.J, Kuerzel.F)
        spiel(Kuerzel.B, Kuerzel.C)
        spiel(Kuerzel.G, Kuerzel.H)
        spiel(Kuerzel.D, Kuerzel.E)
        spiel(Kuerzel.I, Kuerzel.J)
        spiel(Kuerzel.A, Kuerzel.C)
        spiel(Kuerzel.F, Kuerzel.H)
        spiel(Kuerzel.E, Kuerzel.B)
        spiel(Kuerzel.J, Kuerzel.G)
        spiel(Kuerzel.D, Kuerzel.A)
        spiel(Kuerzel.I, Kuerzel.F)
        spiel(Kuerzel.C, Kuerzel.E)
        spiel(Kuerzel.H, Kuerzel.J)
        spiel(Kuerzel.B, Kuerzel.D)
        spiel(Kuerzel.G, Kuerzel.I)
        alternateGroup = false

        planSpiel(ersterGruppeA, zweiterGruppeB, SpielTyp.ERSTES_HALBFINALE)
        planSpiel(ersterGruppeB, zweiterGruppeA, SpielTyp.ZWEITES_HALBFINALE)
        planSpiel(fuenfterGruppeA, fuenfterGruppeB, SpielTyp.SPIEL_UM_PLATZ_9)
        planSpiel(vierterGruppeA, vierterGruppeB, SpielTyp.SPIEL_UM_PLATZ_7)
        planSpiel(dritterGruppeA, dritterGruppeB, SpielTyp.SPIEL_UM_PLATZ_5)
        planSpiel(verliererErstesHalbfinale, verliererZweitesHalbfinale, SpielTyp.SPIEL_UM_PLATZ_3)
        planSpiel(siegerErstesHalbfinale, siegerZweitesHalbfinale, SpielTyp.FINALE)
    }

    private fun createSpielplan9Mannschaften(allJugendMannschaft: List<Mannschaft>) {
        val ersterGruppeA = mannschaft("1. Gruppe A")
        val zweiterGruppeA = mannschaft("2. Gruppe A")
        val dritterGruppeA = mannschaft("3. Gruppe A")
        val vierterGruppeA = mannschaft("4. Gruppe A")
        val fuenfterGruppeA = mannschaft("5. Gruppe A")
        val ersterGruppeB = mannschaft("1. Gruppe B")
        val zweiterGruppeB = mannschaft("2. Gruppe B")
        val dritterGruppeB = mannschaft("3. Gruppe B")
        val vierterGruppeB = mannschaft("4. Gruppe B")
        val verliererErstesHalbfinale = mannschaft("Verlierer 1. Halbfinale")
        val verliererZweitesHalbfinale = mannschaft("Verlierer 2. Halbfinale")
        val siegerErstesHalbfinale = mannschaft("Sieger 1. Halbfinale")
        val siegerZweitesHalbfinale = mannschaft("Sieger 2. Halbfinale")

        // Gruppenphase
        currentGroup = 1
        spiel(Kuerzel.A, Kuerzel.B)
        spiel(Kuerzel.C, Kuerzel.D)
        currentGroup = 2
        spiel(Kuerzel.F, Kuerzel.G)
        currentGroup = 1
        spiel(Kuerzel.E, Kuerzel.A)
        currentGroup = 2
        spiel(Kuerzel.H, Kuerzel.I)
        currentGroup = 1
        spiel(Kuerzel.B, Kuerzel.C)
        spiel(Kuerzel.D, Kuerzel.E)
        currentGroup = 2
        spiel(Kuerzel.G, Kuerzel.H)
        currentGroup = 1
        spiel(Kuerzel.A, Kuerzel.C)
        currentGroup = 2
        spiel(Kuerzel.F, Kuerzel.H)
        currentGroup = 1
        spiel(Kuerzel.E, Kuerzel.B)
        spiel(Kuerzel.D, Kuerzel.A)
        currentGroup = 2
        spiel(Kuerzel.I, Kuerzel.F)
        currentGroup = 1
        spiel(Kuerzel.C, Kuerzel.E)
        currentGroup = 2
        spiel(Kuerzel.G, Kuerzel.I)
        currentGroup = 1
        spiel(Kuerzel.B, Kuerzel.D)

        // K.O phase
        planSpiel(fuenfterGruppeA, vierterGruppeB, SpielTyp.ERSTES_SPIEL_UM_PLATZ_7)
        planSpiel(ersterGruppeA, zweiterGruppeB, SpielTyp.ERSTES_HALBFINALE)
        planSpiel(vierterGruppeB, vierterGruppeA, SpielTyp.ZWEITES_SPIEL_UM_PLATZ_7)
        planSpiel(ersterGruppeB, zweiterGruppeA, SpielTyp.ZWEITES_HALBFINALE)
        planSpiel(vierterGruppeA, fuenfterGruppeA, SpielTyp.DRITTES_SPIEL_UM_PLATZ_7)
        planSpiel(dritterGruppeA, dritterGruppeB, SpielTyp.SPIEL_UM_PLATZ_5)
        planSpiel(verliererErstesHalbfinale, verliererZweitesHalbfinale, SpielTyp.SPIEL_UM_PLATZ_3)
        planSpiel(siegerErstesHalbfinale, siegerZweitesHalbfinale, SpielTyp.FINALE)
    }

    private fun createSpielplan8Mannschaften(allJugendMannschaft: List<Mannschaft>) {
        val ersterGruppeA = mannschaft("1. Gruppe A")
        val zweiterGruppeA = mannschaft("2. Gruppe A")
        val dritterGruppeA = mannschaft("3. Gruppe A")
        val vierterGruppeA = mannschaft("4. Gruppe A")
        val ersterGruppeB = mannschaft("1. Gruppe B")
        val zweiterGruppeB = mannschaft("2. Gruppe B")
        val dritterGruppeB = mannschaft("3. Gruppe B")
        val vierterGruppeB = mannschaft("4. Gruppe B")
        val verliererErstesHalbfinale = mannschaft("Verlierer 1. Halbfinale")
        val verliererZweitesHalbfinale = mannschaft("Verlierer 2. Halbfinale")
        val siegerErstesHalbfinale = mannschaft("Sieger 1. Halbfinale")
        val siegerZweitesHalbfinale = mannschaft("Sieger 2. Halbfinale")
        //Gruppenphase
        alternateGroup = true
        spiel(Kuerzel.A, Kuerzel.B)
        spiel(Kuerzel.E, Kuerzel.F)
        spiel(Kuerzel.C, Kuerzel.D)
        spiel(Kuerzel.G, Kuerzel.H)
        spiel(Kuerzel.B, Kuerzel.C)
        spiel(Kuerzel.F, Kuerzel.G)
        spiel(Kuerzel.A, Kuerzel.C)
        spiel(Kuerzel.E, Kuerzel.G)
        spiel(Kuerzel.D, Kuerzel.A)
        spiel(Kuerzel.H, Kuerzel.E)
        spiel(Kuerzel.B, Kuerzel.D)
        spiel(Kuerzel.F, Kuerzel.H)
        alternateGroup = false

        //K.O phase
        planSpiel(ersterGruppeA, zweiterGruppeB, SpielTyp.ERSTES_HALBFINALE)
        planSpiel(ersterGruppeB, zweiterGruppeA, SpielTyp.ZWEITES_HALBFINALE)
        planSpiel(vierterGruppeA, vierterGruppeB, SpielTyp.SPIEL_UM_PLATZ_7)
        planSpiel(dritterGruppeA, dritterGruppeB, SpielTyp.SPIEL_UM_PLATZ_5)
        planSpiel(verliererErstesHalbfinale, verliererZweitesHalbfinale, SpielTyp.SPIEL_UM_PLATZ_3)
        planSpiel(siegerErstesHalbfinale, siegerZweitesHalbfinale, SpielTyp.FINALE)
    }

    private fun createSpielplan7Mannschaften(allJugendMannschaft: List<Mannschaft>) {
        val ersterGruppeA = mannschaft("1. Gruppe A")
        val zweiterGruppeA = mannschaft("2. Gruppe A")
        val dritterGruppeA = mannschaft("3. Gruppe A")
        val vierterGruppeA = mannschaft("4. Gruppe A")
        val ersterGruppeB = mannschaft("1. Gruppe B")
        val zweiterGruppeB = mannschaft("2. Gruppe B")
        val dritterGruppeB = mannschaft("3. Gruppe B")
        val verliererErstesHalbfinale = mannschaft("Verlierer 1. Halbfinale")
        val verliererZweitesHalbfinale = mannschaft("Verlierer 2. Halbfinale")
        val siegerErstesHalbfinale = mannschaft("Sieger 1. Halbfinale")
        val siegerZweitesHalbfinale = mannschaft("Sieger 2. Halbfinale")

        // Gruppenphase
        alternateGroup = true
        spiel(Kuerzel.A, Kuerzel.B)
        spiel(Kuerzel.E, Kuerzel.F)
        spiel(Kuerzel.C, Kuerzel.D)
        spiel(Kuerzel.F, Kuerzel.G)
        spiel(Kuerzel.B, Kuerzel.C)
        spiel(Kuerzel.G, Kuerzel.E)
        spiel(Kuerzel.A, Kuerzel.C)
        spiel(Kuerzel.F, Kuerzel.E)
        spiel(Kuerzel.D, Kuerzel.A)
        spiel(Kuerzel.G, Kuerzel.F)
        spiel(Kuerzel.B, Kuerzel.D)
        spiel(Kuerzel.E, Kuerzel.G)
        alternateGroup = false
        currentGroup = 1

        // K.O phase
        planSpiel(vierterGruppeA, dritterGruppeB, SpielTyp.ERSTES_SPIEL_UM_PLATZ_5)
        planSpiel(ersterGruppeA, zweiterGruppeB, SpielTyp.ERSTES_HALBFINALE)
        planSpiel(dritterGruppeB, dritterGruppeA, SpielTyp.ZWEITES_SPIEL_UM_PLATZ_5)
        planSpiel(ersterGruppeB, zweiterGruppeA, SpielTyp.ZWEITES_HALBFINALE)
        planSpiel(dritterGruppeA, vierterGruppeA, SpielTyp.DRITTES_SPIEL_UM_PLATZ_5)
        planSpiel(verliererErstesHalbfinale, verliererZweitesHalbfinale, SpielTyp.SPIEL_UM_PLATZ_3)
        planSpiel(siegerErstesHalbfinale, siegerZweitesHalbfinale, SpielTyp.FINALE)
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
        val ersterGruppeA = mannschaft("1. Gruppe A")
        val zweiterGruppeA = mannschaft("2. Gruppe A")
        val dritterGruppeA = mannschaft("3. Gruppe A")
        val ersterGruppeB = mannschaft("1. Gruppe B")
        val zweiterGruppeB = mannschaft("2. Gruppe B")
        val dritterGruppeB = mannschaft("3. Gruppe B")
        val verliererErstesHalbfinale = mannschaft("Verlierer 1. Halbfinale")
        val verliererZweitesHalbfinale = mannschaft("Verlierer 2. Halbfinale")
        val siegerErstesHalbfinale = mannschaft("Sieger 1. Halbfinale")
        val siegerZweitesHalbfinale = mannschaft("Sieger 2. Halbfinale")

        //Gruppenphase
        alternateGroup = true
        spiel(Kuerzel.A, Kuerzel.B)
        spiel(Kuerzel.D, Kuerzel.E)
        spiel(Kuerzel.B, Kuerzel.C)
        spiel(Kuerzel.E, Kuerzel.F)
        spiel(Kuerzel.A, Kuerzel.C)
        spiel(Kuerzel.D, Kuerzel.F)
        spiel(Kuerzel.B, Kuerzel.A)
        spiel(Kuerzel.E, Kuerzel.D)
        spiel(Kuerzel.C, Kuerzel.B)
        spiel(Kuerzel.F, Kuerzel.E)
        spiel(Kuerzel.C, Kuerzel.A)
        spiel(Kuerzel.F, Kuerzel.D)
        alternateGroup = false

        // K.O Phase
        planSpiel(ersterGruppeA, zweiterGruppeB, SpielTyp.ERSTES_HALBFINALE)
        planSpiel(ersterGruppeB, zweiterGruppeA, SpielTyp.ZWEITES_HALBFINALE)
        planSpiel(dritterGruppeA, dritterGruppeB, SpielTyp.SPIEL_UM_PLATZ_5)
        planSpiel(verliererErstesHalbfinale, verliererZweitesHalbfinale, SpielTyp.SPIEL_UM_PLATZ_3)
        planSpiel(siegerErstesHalbfinale, siegerZweitesHalbfinale, SpielTyp.FINALE)

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

    /**
     * Erstellt ein spiel Objekt und fügt es der spielplanList hinzu.
     * dabei Wird der Spielbeginn aus turnNierbeginn genommen datraufhin wird Turnierbeginn um die Spiellänge + Pause zwischen Spielen
     * erhöht wenn alternateGroup auf true gesetzt ist wird abwechselnd gruppe 1 und 2 bei den SPielen genommen.
     * @param gast gast Mannschaft Kuerzel kommt aus blanco Spielplan
     * @param heim heim Mannschaft Kuerzel kommt aus blanco Spielplan
     *
     */
    private fun spiel(heim: Kuerzel, gast: Kuerzel, spielTyp: SpielTyp = SpielTyp.GRUPPENSPIEL) {
        spielplanList.add(allJugendMannschaft.createSpiel(heim, gast, spielDuration, turnierBeginn, spielTyp, currentGroup))
        if (alternateGroup) {
            if (currentGroup == 1) {
                currentGroup = 2
            } else {
                currentGroup = 1
            }
        }

        turnierBeginn = turnierBeginn.plus(spielDuration.plus(pauseDuration))
    }

    /**
     * Erzeugt ein spiel mit platzhalter mannschaften für spiele wo noch nicht bekannt ist, welche mannschaft dort spielt
     *
     * Gruppe wird hier NICHT beachtet
     *
     * @param heim muss als verein den platzhalter Verein haben
     * @param gast muss als verein den platzhalter Verein haben
     */
    private fun planSpiel(heim: Mannschaft, gast: Mannschaft, spielTyp: SpielTyp) {
        if (heim.verein != platzhalterVerein || gast.verein != platzhalterVerein) {
            throw IllegalArgumentException("Funktion planSpiel nur verwenden wenn man die Mannschaften nicht kennt die Spielen sollen!")
        }
        if (heim.name == gast.name) {
            throw IllegalArgumentException("Heim und gast müssen verschiedene Mannschaften sein!")
        }
        spielplanList.add(Spiel(heimMannschaft = heim, gastMannschaft = gast, halftimeDuration = spielDuration,
                dateTime = turnierBeginn, spielPlatz = spielplatz, spielTyp = spielTyp))

        turnierBeginn = turnierBeginn.plus(spielDuration.plus(pauseDuration))
    }

    private fun List<Mannschaft>.createSpiel(heim: Kuerzel, gast: Kuerzel, spielDuration: Duration, time: LocalDateTime, spielTyp: SpielTyp, gruppe: Int): Spiel {
        if (this[heim.index].id == this[gast.index].id) {
            throw IllegalArgumentException("Heim und gast können nicht die selbe mannschaft sein")
        }
        return Spiel(heimMannschaft = this[heim.index], gastMannschaft = this[gast.index], halftimeDuration = spielDuration, dateTime = time, spielTyp = spielTyp, spielPlatz = spielplatz, gruppe = currentGroup)

    }

    /**
     * Hilfsfunkltion um platzhaltermannschagft zu erzeugen
     */
    private fun mannschaft(name: String): Mannschaft {
        val mannschaft = Mannschaft(name = name, verein = platzhalterVerein, jugend = allJugendMannschaft[0].jugend)
        mannschaftRepository.save(mannschaft)
        return mannschaft
    }

}

enum class Kuerzel(val index: Int) {
    A(0), B(1), C(2), D(3), E(4), F(5), G(6), H(7), I(8), J(9)
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
