package de.schreib.handball.handballtag.repositories

import de.schreib.handball.handballtag.entities.Jugend
import de.schreib.handball.handballtag.entities.Mannschaft
import de.schreib.handball.handballtag.entities.Spiel
import de.schreib.handball.handballtag.entities.Verein
import de.schreib.handball.handballtag.enums.JugendEnum
import de.schreib.handball.handballtag.enums.JugendGender
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@SpringBootTest
class TestRepositories {
    @Autowired
    lateinit var spielRepository: SpielRepository
    @Autowired
    lateinit var vereinRepository: VereinRepository
    @Autowired
    lateinit var tabelleRepository: TabelleRepository
    @Autowired
    lateinit var mannschaftRepository: MannschaftRepository


    lateinit var vereinHeim: Verein
    lateinit var vereinGast: Verein
    lateinit var heimMannschaftAjugend: Mannschaft
    lateinit var gastMannschaftAjugend: Mannschaft
    lateinit var heimMannschaftCjugend: Mannschaft
    lateinit var gastMannschaftCjugend: Mannschaft
    lateinit var aJugend: Jugend
    lateinit var cJugend: Jugend
    lateinit var tabelleAJugend: Tabelle
    lateinit var tabelleCJugend: Tabelle
    lateinit var spielAJugend: Spiel
    lateinit var spielCJugend: Spiel

    @Before
    fun initialize() {
        vereinHeim = Verein(name = "HeimVerein", allMannschaft = emptyList())
        vereinGast = Verein(name = "GastVerein", allMannschaft = emptyList())
        aJugend = Jugend(typ = JugendGender.MAENNLICH, jahrgang = JugendEnum.AJUGEND)
        cJugend = Jugend(typ = JugendGender.MAENNLICH, jahrgang = JugendEnum.CJUGEND)
        tabelleAJugend = Tabelle(allMannschaft = emptyList(), jugend = aJugend)
        tabelleCJugend = Tabelle(allMannschaft = emptyList(), jugend = cJugend)



        tabelleRepository.saveAll(listOf(tabelleAJugend, tabelleCJugend))
        vereinRepository.saveAll(listOf(vereinHeim, vereinGast))


        //Mannschaften A-Jugend
        heimMannschaftAjugend = Mannschaft(name = "HeimMannschaftAJugend", verein = vereinHeim, tabelle = tabelleAJugend, jugend = aJugend)
        gastMannschaftAjugend = Mannschaft(name = "GastMannschaftAJugend", verein = vereinGast, tabelle = tabelleAJugend, jugend = aJugend)
        //Mannschaften C-Jugend
        heimMannschaftCjugend = Mannschaft(name = "HeimMannschaftCJugend", verein = vereinGast, tabelle = tabelleCJugend, jugend = aJugend)
        gastMannschaftCjugend = Mannschaft(name = "GastMannschaftCJugend", verein = vereinGast, tabelle = tabelleCJugend, jugend = aJugend)

        mannschaftRepository.saveAll(listOf(heimMannschaftAjugend, gastMannschaftAjugend, heimMannschaftCjugend, gastMannschaftCjugend))

        vereinHeim = vereinHeim.copy(allMannschaft = listOf(heimMannschaftAjugend,heimMannschaftCjugend))
        vereinGast = vereinHeim.copy(allMannschaft = listOf(gastMannschaftCjugend,gastMannschaftAjugend))
        tabelleAJugend = tabelleAJugend.copy(allMannschaft = listOf(heimMannschaftAjugend,gastMannschaftCjugend))
        tabelleCJugend = tabelleAJugend.copy(allMannschaft = listOf(heimMannschaftCjugend,gastMannschaftCjugend))

        vereinRepository.saveAll(listOf(vereinGast,vereinHeim))
        tabelleRepository.saveAll(listOf(tabelleAJugend,tabelleCJugend))

        spielAJugend = Spiel(heimMannschaft = heimMannschaftAjugend, gastMannschaft = gastMannschaftAjugend, dateTime = LocalDateTime.now())
        spielCJugend = Spiel(heimMannschaft = heimMannschaftCjugend, gastMannschaft = gastMannschaftCjugend, dateTime = LocalDateTime.now())

        spielRepository.saveAll(listOf(spielAJugend, spielCJugend))
    }

    @Test
    fun `test if findByAllMannschaft for AJugemd Mannschaft returns Ajugend Spiel`() {
        val spiel = spielRepository.findAllByMannschaft(heimMannschaftCjugend)[0]


        assertThat(spiel.id, `is`(spielCJugend.id))
        assertThat(spiel.heimMannschaft.name, `is`(spielCJugend.heimMannschaft.name))
    }

    @After
    fun cleanDatabase(){
        spielRepository.deleteAll()
        mannschaftRepository.deleteAll()
        tabelleRepository.deleteAll()
        vereinRepository.deleteAll()
    }
}