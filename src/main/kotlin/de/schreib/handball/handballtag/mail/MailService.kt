package de.schreib.handball.handballtag.mail

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct
import javax.mail.MessagingException

@Service
class MailService(@Autowired val mailSender: JavaMailSender) {
    val sendMail = true


    val mailFrom: String = "handballtag@schreib.io"
    val log = LoggerFactory.getLogger(this::class.java)

    fun sendEmail(subject: String, body: String, allTo: List<String>, attachment: Attachment? = null, bcc: String? = null) {
        if (allTo.isEmpty()) {
            return
        }
        val receiver = allTo.toTypedArray()

        val mimeMessage = mailSender.createMimeMessage()
        try {
            val messageHelper = MimeMessageHelper(mimeMessage, true)
            messageHelper.setFrom(mailFrom)
            messageHelper.setSubject(subject)
            messageHelper.setText(body)
            messageHelper.setTo(receiver)
            bcc?.let { messageHelper.setBcc(it) }
            attachment?.let { messageHelper.addAttachment(it.name, it.dataSource) }
        } catch (e: MessagingException) {
            log.error("Could not build email. {}", e)
        }

        try {
            if (!sendMail) {
                log.info("NOT SENDING Mail:\nSubject: {}\nto: {}:\nBody: {}", subject, receiver[0], body)
                return
            }
            mailSender.send(mimeMessage)
            log.info("Message $subject successfully sent to ${Arrays.toString(receiver)}")
        } catch (e: Exception) {
            log.error("Error sending mail $subject. Reason: {}", e)
            throw e
        }
    }
}