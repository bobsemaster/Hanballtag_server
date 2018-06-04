package de.schreib.handball.handballtag.controller

import de.schreib.handball.handballtag.mail.MailService
import de.schreib.handball.handballtag.security.SPIELLEITER
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("mail/")
class MailController(@Autowired val mailService: MailService) {

    @GetMapping
    @Secured(SPIELLEITER)
    fun sendTestMail() {
        mailService.sendEmail("Test", "Test", listOf("robert.schreib@gmx.de"))
    }
}