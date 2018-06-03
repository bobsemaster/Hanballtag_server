package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.entities.Verein
import de.schreib.handball.handballtag.exceptions.VereinAlreadyExistException
import de.schreib.handball.handballtag.exceptions.VereinNotFoundException
import de.schreib.handball.handballtag.repositories.VereinRepository
import de.schreib.handball.handballtag.security.SPIELLEITER
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
@WithMockUser(roles = [SPIELLEITER])
@Rollback
class VereinControllerTest {


    @Autowired
    lateinit var vereinController: VereinController
    @Autowired
    lateinit var vereinRepository: VereinRepository
    val verein = Verein(name = "testVerein")
    var vereinId = -1L

    @Before

    fun setup() {
        vereinRepository.save(verein)
        vereinId = verein.id
    }

    @After
    fun after() {
        vereinRepository.deleteAll()
    }

    @Test
    fun `test ob Verein Gefunden Wird`() {
        assertThat(vereinController.getAllVerein()[0].id, `is`(verein.id))
    }

    @Test
    fun `test ob findById funktioniert mit richtiger id`() {
        assertThat(vereinController.findVereinById(vereinId).id, `is`(verein.id))
    }

    @Test(expected = VereinNotFoundException::class)
    fun `test ob findById funktioniert mit falscher id`() {
        vereinController.findVereinById(Long.MIN_VALUE)
    }

    @Test
    fun `test ob neuer verein gespeichert wird`() {
        val neuerVerein = Verein(name = "testVerein_2")
        vereinController.createNewVerein(neuerVerein)
        assertThat(vereinRepository.count(), `is`(2L))
    }

    @Test(expected = VereinAlreadyExistException::class)
    fun `test dass nur ein Verein mit gleichem namen existiert`() {
        val neuerVerein = Verein(name = "testVerein")
        vereinController.createNewVerein(neuerVerein)
    }

    @Test
    fun `test ob verein loeschen funktioniert`() {
        vereinController.deleteVerein(verein.id)
        assertThat(vereinRepository.count(), `is`(0L))
    }
}