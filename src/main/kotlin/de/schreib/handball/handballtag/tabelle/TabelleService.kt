package de.schreib.handball.handballtag.tabelle

import de.schreib.handball.handballtag.entities.Gruppe
import de.schreib.handball.handballtag.entities.Jugend
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
            SpielTyp.DRITTES_SPIEL_UM_PLATZ_5 -> handleDreiSpielePlatzFuenf(spiel)
            SpielTyp.SPIEL_UM_PLATZ_7 -> handleSpielUmPlatzSieben(spiel)
            SpielTyp.DRITTES_SPIEL_UM_PLATZ_7 -> handleDreiSpielePlatzSieben(spiel)
            SpielTyp.SPIEL_UM_PLATZ_9 -> handleSpielUmPlatzNeun(spiel)
            SpielTyp.FINALE -> handleFinale(spiel)
            SpielTyp.NONE -> return

            SpielTyp.ERSTES_SPIEL_UM_PLATZ_5 -> return
            SpielTyp.ZWEITES_SPIEL_UM_PLATZ_5 -> return
            SpielTyp.ERSTES_SPIEL_UM_PLATZ_7 -> return
            SpielTyp.ZWEITES_SPIEL_UM_PLATZ_7 -> return
        }

        checkIfGruppenphaseOver(spiel.gastMannschaft.jugend)

        TODO("Nach gruppenphase platzhalter spiele ersetzen")
        TODO("K.O phase plätze berechnen")
    }

    private fun checkIfGruppenphaseOver(jugend: Jugend) {
        val gruppenSpiele = spielRepository.findAllBySpielTypAndJugend(SpielTyp.GRUPPENSPIEL, jugend)

        // kein spiel mehr das 0:0 als ergebnis hat -> alle spiele gespielt!
        if (gruppenSpiele.filter { it.heimTore == 0 && it.gastTore == 0 }.count() == 0) {
            updateKOSpiele(jugend)
        }
    }

    private fun updateKOSpiele(jugend: Jugend) {
        // Mannschaften in Reihenfolge erster hat index 0 letzter hat letzten index
        val mannschaften = mannschaftRepository.findAllByJugend(jugend).sortedBy { it.tabellenPlatz }
        val spieleKORunde = spielRepository.findAllByJugend(jugend).filter { it.spielTyp != SpielTyp.GRUPPENSPIEL }
        if (spieleKORunde.size == 0) {
            log.info("Die jugend $jugend hat keine K.O phase!")
            return
        }

        val allSpielUpdateList = mutableListOf<Spiel>()
        // halbfinals updaten da jeder mit ko runde diese hat!
        val erstesHalbfinale = spieleKORunde.find { it.spielTyp == SpielTyp.ERSTES_HALBFINALE }!!
        val zweitesHalbfinale = spieleKORunde.find { it.spielTyp == SpielTyp.ZWEITES_HALBFINALE }!!
        val erstesHalbfinaleUpdate = erstesHalbfinale.copy(heimMannschaft = mannschaften.findMannschaft(1, Gruppe.A),
                gastMannschaft = mannschaften.findMannschaft(2, Gruppe.B))
        val zweitesHalbfinaleUpdate = zweitesHalbfinale.copy(heimMannschaft = mannschaften.findMannschaft(1, Gruppe.B),
                gastMannschaft = mannschaften.findMannschaft(2, Gruppe.A))
        allSpielUpdateList.addAll(listOf(erstesHalbfinaleUpdate, zweitesHalbfinaleUpdate))
        when (mannschaften.size) {
            6 -> {
                val spielUmPlatzFuenf = spieleKORunde.find { it.spielTyp == SpielTyp.SPIEL_UM_PLATZ_5 }!!
                allSpielUpdateList.add(spielUmPlatzFuenf.copy(heimMannschaft = mannschaften.findMannschaft(3, Gruppe.A),
                        gastMannschaft = mannschaften.findMannschaft(3, Gruppe.B)))
            }
            7 -> {
                val erstesSpielUmPlatzFuenf = spieleKORunde.find { it.spielTyp == SpielTyp.ERSTES_SPIEL_UM_PLATZ_5 }!!
                val zweitesSpielUmPlatzFuenf = spieleKORunde.find { it.spielTyp == SpielTyp.ZWEITES_SPIEL_UM_PLATZ_5 }!!
                val drittesSpielUmPlatzFuenf = spieleKORunde.find { it.spielTyp == SpielTyp.DRITTES_SPIEL_UM_PLATZ_5 }!!
                allSpielUpdateList.add(erstesSpielUmPlatzFuenf.copy(heimMannschaft = mannschaften.findMannschaft(4, Gruppe.A),
                        gastMannschaft = mannschaften.findMannschaft(3, Gruppe.B)))

                allSpielUpdateList.add(zweitesSpielUmPlatzFuenf.copy(heimMannschaft = mannschaften.findMannschaft(3, Gruppe.B),
                        gastMannschaft = mannschaften.findMannschaft(3, Gruppe.A)))

                allSpielUpdateList.add(drittesSpielUmPlatzFuenf.copy(heimMannschaft = mannschaften.findMannschaft(3, Gruppe.A),
                        gastMannschaft = mannschaften.findMannschaft(4, Gruppe.A)))
            }
            8 -> {
                val spielUmPlatzFuenf = spieleKORunde.find { it.spielTyp == SpielTyp.SPIEL_UM_PLATZ_5 }!!
                val spielUmPlatzSieben = spieleKORunde.find { it.spielTyp == SpielTyp.SPIEL_UM_PLATZ_7 }!!

                allSpielUpdateList.add(spielUmPlatzFuenf.copy(heimMannschaft = mannschaften.findMannschaft(3, Gruppe.A),
                        gastMannschaft = mannschaften.findMannschaft(3, Gruppe.B)))
                allSpielUpdateList.add(spielUmPlatzSieben.copy(heimMannschaft = mannschaften.findMannschaft(4, Gruppe.A),
                        gastMannschaft = mannschaften.findMannschaft(4, Gruppe.B)))
            }
            9 -> {
                val erstesSpielUmPlatzSieben = spieleKORunde.find { it.spielTyp == SpielTyp.ERSTES_SPIEL_UM_PLATZ_7 }!!
                val zweitesSpielUmPlatzSieben = spieleKORunde.find { it.spielTyp == SpielTyp.ZWEITES_SPIEL_UM_PLATZ_7 }!!
                val drittesSpielUmPlatzSieben = spieleKORunde.find { it.spielTyp == SpielTyp.DRITTES_SPIEL_UM_PLATZ_7 }!!
                allSpielUpdateList.add(erstesSpielUmPlatzSieben.copy(heimMannschaft = mannschaften.findMannschaft(5, Gruppe.A),
                        gastMannschaft = mannschaften.findMannschaft(4, Gruppe.B)))

                allSpielUpdateList.add(zweitesSpielUmPlatzSieben.copy(heimMannschaft = mannschaften.findMannschaft(4, Gruppe.B),
                        gastMannschaft = mannschaften.findMannschaft(4, Gruppe.A)))

                allSpielUpdateList.add(drittesSpielUmPlatzSieben.copy(heimMannschaft = mannschaften.findMannschaft(4, Gruppe.A),
                        gastMannschaft = mannschaften.findMannschaft(5, Gruppe.A)))
            }
            10 -> {
                val spielUmPlatzFuenf = spieleKORunde.find { it.spielTyp == SpielTyp.SPIEL_UM_PLATZ_5 }!!
                val spielUmPlatzSieben = spieleKORunde.find { it.spielTyp == SpielTyp.SPIEL_UM_PLATZ_7 }!!
                val spielUmPlatzNeun = spieleKORunde.find { it.spielTyp == SpielTyp.SPIEL_UM_PLATZ_9 }!!

                allSpielUpdateList.add(spielUmPlatzFuenf.copy(heimMannschaft = mannschaften.findMannschaft(3, Gruppe.A),
                        gastMannschaft = mannschaften.findMannschaft(3, Gruppe.B)))
                allSpielUpdateList.add(spielUmPlatzSieben.copy(heimMannschaft = mannschaften.findMannschaft(4, Gruppe.A),
                        gastMannschaft = mannschaften.findMannschaft(4, Gruppe.B)))
                allSpielUpdateList.add(spielUmPlatzNeun.copy(heimMannschaft = mannschaften.findMannschaft(5, Gruppe.A),
                        gastMannschaft = mannschaften.findMannschaft(5, Gruppe.B)))
            }
        }
        spielRepository.saveAll(allSpielUpdateList)
    }

    private fun List<Mannschaft>.findMannschaft(tabellenPlatz: Int, gruppe: Gruppe): Mannschaft {
        val mannschaft = this.find { it.tabellenPlatz == tabellenPlatz && it.gruppe == gruppe }
        if (mannschaft == null) {
            log.error("Es existiert keine Mannschaft mit tabellenplatz '$tabellenPlatz' und gruppe '$gruppe' in der '${this[0].jugend}'")
            throw IllegalStateException("Mannschaft nicht gefunden!")
        }
        return mannschaft
    }

    private fun handleDreiSpielePlatzSieben(spiel: Spiel) {
        val jugend = spiel.heimMannschaft.jugend
        val erstesSpiel = spielRepository.findAllBySpielTypAndJugend(SpielTyp.ERSTES_SPIEL_UM_PLATZ_7, jugend)[0]
        val zweitesSpiel = spielRepository.findAllBySpielTypAndJugend(SpielTyp.ZWEITES_SPIEL_UM_PLATZ_7, jugend)[0]
        val spiele = listOf(erstesSpiel, zweitesSpiel, spiel)
        val mannschaften = spiele.flatMap { listOf(it.heimMannschaft, it.gastMannschaft) }.distinctBy { it.id }
        val siegeMannschaftenMap = mutableMapOf<Mannschaft, Int>()
        mannschaften.forEach {
            siegeMannschaftenMap.put(it, 0)
        }
        spiele.forEach {
            // Muss da sein, da wir alle Mannschaften der Spiele in die Map eingetragen haben!
            siegeMannschaftenMap[it.sieger()] = siegeMannschaftenMap[it.sieger()]!! + 1
        }
        val siebter = siegeMannschaftenMap.maxBy { it.value }?.key
        val neunter = siegeMannschaftenMap.minBy { it.value }?.key
        if (siebter == null || neunter == null) {
            log.error("Es wurde kein siebter '$siebter' oder neunter platz '$neunter' gefunden!")
            throw IllegalStateException("Es muss einen siebten und einen neunten platz geben!")
        }
        siegeMannschaftenMap.remove(siebter)
        siegeMannschaftenMap.remove(neunter)
        if (siegeMannschaftenMap.size != 1) {
            log.error("Es gibt mehr als eine Mannschaft die achter werden könnte es sind noch ${siegeMannschaftenMap.size} Mannschaften vorhanden")
            throw IllegalStateException("Es gibt mehr als einen achten!")
        }
        val achter = siegeMannschaftenMap.keys.toList()[0].copy(tabellenPlatz = 8)
        val siebterUpdate = siebter.copy(tabellenPlatz = 7)
        val neunterUpdate = neunter.copy(tabellenPlatz = 9)
        mannschaftRepository.saveAll(listOf(siebterUpdate, neunterUpdate, achter))
    }

    private fun handleDreiSpielePlatzFuenf(spiel: Spiel) {
        val jugend = spiel.heimMannschaft.jugend
        val erstesSpiel = spielRepository.findAllBySpielTypAndJugend(SpielTyp.ERSTES_SPIEL_UM_PLATZ_5, jugend)[0]
        val zweitesSpiel = spielRepository.findAllBySpielTypAndJugend(SpielTyp.ZWEITES_SPIEL_UM_PLATZ_5, jugend)[0]
        val spiele = listOf(erstesSpiel, zweitesSpiel, spiel)
        val mannschaften = spiele.flatMap { listOf(it.heimMannschaft, it.gastMannschaft) }.distinctBy { it.id }
        val siegeMannschaftenMap = mutableMapOf<Mannschaft, Int>()
        mannschaften.forEach {
            siegeMannschaftenMap.put(it, 0)
        }
        spiele.forEach {
            // Muss da sein, da wir alle Mannschaften der Spiele in die Map eingetragen haben!
            siegeMannschaftenMap[it.sieger()] = siegeMannschaftenMap[it.sieger()]!! + 1
        }
        val fuenfter = siegeMannschaftenMap.maxBy { it.value }?.key
        val siebter = siegeMannschaftenMap.minBy { it.value }?.key
        if (fuenfter == null || siebter == null) {
            log.error("Es wurde kein fuenfter '$fuenfter' oder siebter platz '$siebter' gefunden!")
            throw IllegalStateException("Es muss einen fünften und einen siebten platz geben!")
        }
        siegeMannschaftenMap.remove(fuenfter)
        siegeMannschaftenMap.remove(siebter)
        if (siegeMannschaftenMap.size != 1) {
            log.error("Es gibt mehr als eine Mannschaft die sechster werden könnte es sind noch ${siegeMannschaftenMap.size} Mannschaften vorhanden")
            throw IllegalStateException("Es gibt mehr als einen sechsten!")
        }
        val sechster = siegeMannschaftenMap.keys.toList()[0].copy(tabellenPlatz = 6)
        val fuenfterUpdate = fuenfter.copy(tabellenPlatz = 5)
        val siebterUpdate = siebter.copy(tabellenPlatz = 7)
        mannschaftRepository.saveAll(listOf(fuenfterUpdate, sechster, siebterUpdate))
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
        val mannschaften = mannschaftRepository.findAllByJugendAndGruppe(spiel.heimMannschaft.jugend, spiel.heimMannschaft.gruppe)
        val sortedByTabellenPlatz = sortMannschaftenByTabellenPlatz(mannschaften)
        sortedByTabellenPlatz.forEachIndexed { index, mannschaft ->
            mannschaftRepository.save(mannschaft.copy(tabellenPlatz = index + 1))
        }
    }

    fun sortMannschaftenByTabellenPlatz(mannschaften: List<Mannschaft>): List<Mannschaft> {
        return mannschaften.sortedWith(Comparator { o1, o2 ->
            // Nur die punkte die die mannschaft hat interresiert hier, nicht die punkte die die mannschaft an andere vergeben hat
            when {
                o1.punkteverhaeltnis.first < o2.punkteverhaeltnis.first -> 1
                o1.punkteverhaeltnis.first > o2.punkteverhaeltnis.first -> -1
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

