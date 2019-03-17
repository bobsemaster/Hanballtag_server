package de.schreib.handball.handballtag.tabelle

import de.schreib.handball.handballtag.services.TabelleService
import de.schreib.handball.handballtag.entities.Jugend
import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.entities.Verein
import de.schreib.handball.handballtag.enums.JugendEnum
import de.schreib.handball.handballtag.enums.JugendGender
import de.schreib.handball.handballtag.repositories.MannschaftRepository
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner


@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner::class)
@Rollback
class TabelleServiceTest {


    @Autowired
    lateinit var tabelleService: TabelleService

    @Autowired
    lateinit var mannschaftRepository: MannschaftRepository

    val allMannschaft: MutableList<Mannschaft> = mutableListOf()

    @Before
    fun setUp() {
        allMannschaft.add(mannschaft("fuenf", 0, 10))
        allMannschaft.add(mannschaft("vier", 2, 8))
        allMannschaft.add(mannschaft("drei", 4, 6))
        allMannschaft.add(mannschaft("zwei", 6, 4))
        allMannschaft.add(mannschaft("eins", 10, 0))
    }

    @Test
    fun `test richtige reihenfolge bei vergleich nach punkten`() {
        val sortedMannschaftenByTabellenPlatz = tabelleService.sortMannschaftenByTabellenPlatz(allMannschaft)
        assertThat(sortedMannschaftenByTabellenPlatz.first().name, `is`("eins"))
        assertThat(sortedMannschaftenByTabellenPlatz.last().name, `is`("fuenf"))

    }

    fun mannschaft(name: String, gewonnen: Int, verloren: Int): Mannschaft {
        return Mannschaft(
            name = name,
            punkteverhaeltnis = Pair(gewonnen, verloren),
            jugend = Jugend(JugendGender.MAENNLICH, JugendEnum.CJUGEND),
            verein = Verein(name = "test")
        )
    }
}
