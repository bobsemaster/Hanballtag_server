package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.VerkaufArtikel
import de.schreib.handball.handballtag.repositories.VerkaufArtikelRepository
import de.schreib.handball.handballtag.repositories.VerkaufRepository
import de.schreib.handball.handballtag.security.SPIELLEITER
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
@WithMockUser(roles = [SPIELLEITER])
@Rollback
@ActiveProfiles("test")
class VerkaufControllerTest {

    @Autowired
    lateinit var verkaufRepository: VerkaufRepository

    @Autowired
    lateinit var verkaufArtikelRepository: VerkaufArtikelRepository

    @Autowired
    lateinit var verkaufController: VerkaufController


    @Test
    @Transactional
    fun `teste ob alle artikel mitgeliefert werde in findAll und setArtikelList funktioniert`() {
        verkaufController.addArtikelList(
            listOf(
                VerkaufArtikel(
                    artikelName = "Kuchen",
                    artikelPreis = 1.00,
                    verkaufsplatz = "Kuchenstand"
                )
            )
        )

        val verkauf = verkaufController.getVerkaufObject()
        val expected = verkaufRepository.findAll()[0].verkaufArtikel
        // Wird benutzt um artikel nachzuladen, da diese erst lazy nachgeldaen werden wenn darauf eine methode ausgeführt wird
        expected.count()
        assertThat(verkauf.verkaufArtikel, `is`(verkaufRepository.findAll()[0].verkaufArtikel))
    }

    @Test
    @Ignore
    fun `teste Ob set artikel list funktioniert`() {
        val artikel = VerkaufArtikel(artikelName = "Kuchen", artikelPreis = 1.00, verkaufsplatz = "Kuchenstand")
        verkaufController.addArtikelList(listOf(artikel))
        assertThat(verkaufArtikelRepository.findById(artikel.id).get(), `is`(artikel))
    }

    @Test
    fun `setze grill auf an`() {
        verkaufController.setGrillStatus(true)
        assertThat(verkaufRepository.findAll()[0].isGrillAn, `is`(true))
    }
}
