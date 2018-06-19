package de.schreib.handball.handballtag.tabelle

import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.entities.Spiel
import de.schreib.handball.handballtag.entities.SpielTyp
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import de.schreib.handball.handballtag.repositories.SpielRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TabelleService(@Autowired val mannschaftRepository: MannschaftRepository,
                     @Autowired val spielRepository: SpielRepository) {
    fun processSpielergebnis(spiel: Spiel) {
        updateMannschaften(spiel)
        if (spiel.spielTyp == SpielTyp.GRUPPENSPIEL) {
            calculateNewTabellenPlatzGruppenphase(spiel)
        }

        TODO("Nach gruppenphase platzhalter spiele ersetzen")
        TODO("K.O phase plätze berechnen")
    }

    private fun calculateNewTabellenPlatzGruppenphase(spiel: Spiel) {
        val mannschaften = mannschaftRepository.findAllByJugend(spiel.heimMannschaft.jugend)
        val sortedByTabellenPlatz = sortMannschaftenByTabellenPlatz(mannschaften)
        sortedByTabellenPlatz.forEachIndexed({ index, mannschaft ->
            mannschaftRepository.save(mannschaft.copy(tabellenPlatz = index + 1))
        })

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

