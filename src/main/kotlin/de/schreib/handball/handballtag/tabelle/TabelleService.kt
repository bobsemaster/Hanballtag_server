package de.schreib.handball.handballtag.tabelle

import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.entities.Spiel
import de.schreib.handball.handballtag.entities.SpielTyp
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import de.schreib.handball.handballtag.repositories.SpielRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TabelleService(@Autowired val mannschaftRepository: MannschaftRepository,
                     @Autowired val spielRepository: SpielRepository) {

    val log = LoggerFactory.getLogger(this::class.java)

    fun processSpielergebnis(spiel: Spiel) {
        updateMannschaften(spiel)
        when (spiel.spielTyp) {
            SpielTyp.GRUPPENSPIEL -> calculateNewTabellenPlatzGruppenphase(spiel)
            SpielTyp.ERSTES_HALBFINALE -> handleHalbfinale(spiel, SpielTyp.ERSTES_HALBFINALE)
            SpielTyp.ZWEITES_HALBFINALE -> handleHalbfinale(spiel, SpielTyp.ZWEITES_HALBFINALE)
            SpielTyp.SPIEL_UM_PLATZ_3 -> handleSpielUmPlatzDrei(spiel)
            SpielTyp.SPIEL_UM_PLATZ_5 -> handleSpielUmPlatzFuenf(spiel)
            SpielTyp.ERSTES_SPIEL_UM_PLATZ_5 -> TODO()
            SpielTyp.ZWEITES_SPIEL_UM_PLATZ_5 -> TODO()
            SpielTyp.DRITTES_SPIEL_UM_PLATZ_5 -> TODO()
            SpielTyp.SPIEL_UM_PLATZ_7 -> handleSpielUmPlatzSieben(spiel)
            SpielTyp.ERSTES_SPIEL_UM_PLATZ_7 -> TODO()
            SpielTyp.ZWEITES_SPIEL_UM_PLATZ_7 -> TODO()
            SpielTyp.DRITTES_SPIEL_UM_PLATZ_7 -> TODO()
            SpielTyp.SPIEL_UM_PLATZ_9 -> handleSpielUmPlatzNeun(spiel)
            SpielTyp.FINALE -> handleFinale(spiel)
            SpielTyp.NONE -> TODO()
        }

        TODO("Nach gruppenphase platzhalter spiele ersetzen")
        TODO("K.O phase plätze berechnen")
    }

    private fun handleFinale(spiel: Spiel) {
        val ersterPlatz = spiel.sieger().copy(tabellenPlatz = 1)
        val zweiterPlatz = spiel.verlierer().copy(tabellenPlatz = 2)
        mannschaftRepository.saveAll(listOf(ersterPlatz, zweiterPlatz))
    }

    private fun handleSpielUmPlatzNeun(spiel: Spiel) {
        val neunterPlatz = spiel.sieger().copy(tabellenPlatz = 9)
        val zehnterPlatz = spiel.sieger().copy(tabellenPlatz = 10)
        mannschaftRepository.saveAll(listOf(neunterPlatz, zehnterPlatz))
    }

    private fun handleSpielUmPlatzSieben(spiel: Spiel) {
        val siebterPlatz = spiel.sieger().copy(tabellenPlatz = 7)
        val achterPlatz = spiel.sieger().copy(tabellenPlatz = 8)
        mannschaftRepository.saveAll(listOf(siebterPlatz, achterPlatz))
    }

    private fun handleSpielUmPlatzFuenf(spiel: Spiel) {
        val fuenfterPlatz = spiel.sieger().copy(tabellenPlatz = 5)
        val sechsterPlatz = spiel.verlierer().copy(tabellenPlatz = 6)
        mannschaftRepository.saveAll(listOf(fuenfterPlatz, sechsterPlatz))
    }

    private fun handleSpielUmPlatzDrei(spiel: Spiel) {
        val dritterPlatz = spiel.sieger().copy(tabellenPlatz = 3)
        val vierterPlatz = spiel.verlierer().copy(tabellenPlatz = 4)
        mannschaftRepository.saveAll(listOf(dritterPlatz, vierterPlatz))
    }

    @Throws(IllegalArgumentException::class)
    private fun handleHalbfinale(spiel: Spiel, halbfinalTyp: SpielTyp) {
        val allFinale = spielRepository.findAllBySpielTypAndJugend(SpielTyp.FINALE, spiel.heimMannschaft.jugend)
        if (allFinale.size > 1) {
            log.error("Es kann nur ein Finale pro jugend geben, es wurden aber ${allFinale.size} final spiele gefunden!")
            throw IllegalStateException("Es wurde mehr als ein finalspiel gefunden!")
        }
        val finale = allFinale[0]
        val allSpielUmPlatzDrei = spielRepository.findAllBySpielTypAndJugend(SpielTyp.SPIEL_UM_PLATZ_3, spiel.heimMannschaft.jugend)
        if (allSpielUmPlatzDrei.size > 1) {
            log.error("Es kann nur ein Spiel um platz Drei pro jugend geben, es wurden aber ${allSpielUmPlatzDrei.size} final spiele gefunden!")
            throw IllegalStateException("Es wurde mehr als ein finalspiel gefunden!")
        }
        val spielUmPlatzDrei = allSpielUmPlatzDrei[0]

        when (halbfinalTyp) {
            SpielTyp.ERSTES_HALBFINALE -> {
                // Es muss einen sieger geben, weshalb man hier ohne probleme spiel.sieger() aufrufen kann
                val finaleUpdate = finale.copy(heimMannschaft = spiel.sieger())
                val spielUmPlatzDreiUpdate = spielUmPlatzDrei.copy(heimMannschaft = spiel.verlierer())
                spielRepository.saveAll(listOf(finaleUpdate, spielUmPlatzDreiUpdate))
            }
            SpielTyp.ZWEITES_HALBFINALE -> {
                // Es muss einen sieger geben, weshalb man hier ohne probleme spiel.sieger() aufrufen kann
                val finaleUpdate = finale.copy(gastMannschaft = spiel.sieger())
                val spielUmPlatzDreiUpdate = spielUmPlatzDrei.copy(gastMannschaft = spiel.verlierer())
                spielRepository.saveAll(listOf(finaleUpdate, spielUmPlatzDreiUpdate))
            }
            else -> {
                log.error("Es wurde der spielTyp $halbfinalTyp in die Methode handleHalbfinale übergeben!")
                throw IllegalArgumentException("Ungültiger Spieltyp ${halbfinalTyp}")
            }
        }
    }


    private fun calculateNewTabellenPlatzGruppenphase(spiel: Spiel) {
        val mannschaften = mannschaftRepository.findAllByJugend(spiel.heimMannschaft.jugend)
        val sortedByTabellenPlatz = sortMannschaftenByTabellenPlatz(mannschaften)
        sortedByTabellenPlatz.forEachIndexed { index, mannschaft ->
            mannschaftRepository.save(mannschaft.copy(tabellenPlatz = index + 1))
        }

    }

    private fun sortMannschaftenByTabellenPlatz(mannschaften: List<Mannschaft>): List<Mannschaft> {
        return mannschaften.sortedWith(Comparator { o1, o2 ->
            // Nur die punkte die die mannschaft hat interresiert hier, nicht die punkte die die mannschaft an andere vergeben hat
            when {
                o1.punkteverhaeltnis.first > o2.punkteverhaeltnis.first -> 1
                o1.punkteverhaeltnis.first < o2.punkteverhaeltnis.first -> -1
                else -> direkterVergleich(o1, o2)
            }

        })
    }

    private fun direkterVergleich(o1: Mannschaft, o2: Mannschaft): Int {
        val allSpieleDirekterVergleichPunkte = o1.getAllSpiel()
                .filter { it.gastMannschaft == o2 || it.heimMannschaft == o2 }
                .map { spiel ->
                    if (spiel.heimMannschaft == o2) {
                        // Flippen damit die summe am ende die puinkte von o1 an erster stelle hat
                        // und die punkte von o2 an zweiter
                        getPunkteVerhaeltnis(spiel).flip()
                    } else {
                        getPunkteVerhaeltnis(spiel)
                    }
                }
        val sumPunkte = Pair(allSpieleDirekterVergleichPunkte.sumBy { it.first }, allSpieleDirekterVergleichPunkte
                .sumBy { it.second })
        return when {
            sumPunkte.first > sumPunkte.second -> 1
            sumPunkte.first < sumPunkte.second -> -1
            else -> torVerhaeltnis(o1, o2)
        }


    }

    private fun torVerhaeltnis(o1: Mannschaft, o2: Mannschaft): Int {
        val o1TorDifferenz = o1.torverhaeltnis.first - o1.torverhaeltnis.second
        val o2TorDifferenz = o2.torverhaeltnis.first - o2.torverhaeltnis.second
        return when {
            o1TorDifferenz > o2TorDifferenz -> 1
            o1TorDifferenz < o2TorDifferenz -> -1
            else -> mehrTore(o1, o2)
        }
    }

    private fun mehrTore(o1: Mannschaft, o2: Mannschaft): Int {
        return when {
            o1.torverhaeltnis.first > o2.torverhaeltnis.first -> 1
            o1.torverhaeltnis.first < o2.torverhaeltnis.first -> -1
        // Es muss ein sieben meterwerfen stattfinden
            else -> siebenMeterWerfen(o1, o2)
        }
    }

    private fun siebenMeterWerfen(o1: Mannschaft, o2: Mannschaft): Int {
        return 0
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updateMannschaften(spiel: Spiel) {
        val punkteVerhaeltnisSpiel = getPunkteVerhaeltnis(spiel)
        val newHeimPunkteVerhaeltnis = punkteVerhaeltnisSpiel + spiel.heimMannschaft.punkteverhaeltnis

        val newGastPunkteVerhaeltnis = punkteVerhaeltnisSpiel.flip() + spiel.gastMannschaft.punkteverhaeltnis

        val newHeimTorVerhaeltnis = Pair(spiel.heimTore, spiel.gastTore) + spiel.heimMannschaft.torverhaeltnis

        val newGastTorVerhaeltnis = Pair(spiel.gastTore, spiel.heimTore) + spiel.gastMannschaft.torverhaeltnis


        val heimUpdate = spiel.heimMannschaft.copy(punkteverhaeltnis = newHeimPunkteVerhaeltnis, torverhaeltnis = newHeimTorVerhaeltnis)
        val gastUpdate = spiel.gastMannschaft.copy(punkteverhaeltnis = newGastPunkteVerhaeltnis, torverhaeltnis = newGastTorVerhaeltnis)

        mannschaftRepository.save(heimUpdate)
        mannschaftRepository.save(gastUpdate)
    }

    private fun getPunkteVerhaeltnis(spiel: Spiel): Pair<Int, Int> {
        return when {
            spiel.heimTore > spiel.gastTore -> Pair(2, 0)
            spiel.heimTore < spiel.gastTore -> Pair(0, 2)
            else -> Pair(1, 1)
        }
    }
}

private fun <A, B> Pair<A, B>.flip(): Pair<B, A> {
    return Pair(this.second, this.first)
}

private operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first + other.first, this.second + other.second)
}

/**
 *
 */
fun Spiel.isUnentschieden(): Boolean {
    return heimTore == gastTore
}

/**
 * Liefert den sieger des spiels wenn es nicht unentschieden ausgegangen ist!
 * @return Mannschaft die gewonnen hat
 * @throws IllegalStateException wenn das spiel unentschieden ausgeht
 */
@Throws(IllegalStateException::class)
fun Spiel.sieger(): Mannschaft {
    if (heimTore > gastTore) {
        return heimMannschaft
    } else if (heimTore < gastTore) {
        return gastMannschaft
    }
    throw IllegalStateException("Unentschieden")
}

/**
 * Liefert den verlierer des spiels wenn es nicht unentschieden ausgegangen ist!
 * @return Mannschaft die verloren hat
 * @throws IllegalStateException wenn das spiel unentschieden ausgeht
 */
@Throws(IllegalStateException::class)
fun Spiel.verlierer(): Mannschaft {
    if (heimTore > gastTore) {
        return gastMannschaft
    } else if (heimTore < gastTore) {
        return heimMannschaft
    }
    throw IllegalStateException("Unentschieden")
}

