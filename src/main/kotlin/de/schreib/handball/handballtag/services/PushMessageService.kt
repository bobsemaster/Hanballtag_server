package de.schreib.handball.handballtag.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import de.schreib.handball.handballtag.entities.PushMessage
import de.schreib.handball.handballtag.entities.TargetTopic
import de.schreib.handball.handballtag.repositories.PushMessageRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.io.FileInputStream


@Service
class PushMessageService @Autowired constructor(
    val pushMessageRepository: PushMessageRepository,
    @Value("classpath:firebase-serviceaccount.json")
    val secret: Resource
) {
    val log = LoggerFactory.getLogger(this.javaClass)
    init {
        val serviceAccount = FileInputStream(secret.file)

        val options = FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build()
        //setDatabaseUrl("https://kubernetes-241709.firebaseio.com") Brauch ich hoffentlich nicht
        FirebaseApp.initializeApp(options)
    }

    fun registerClientToTopic(clientToken: String, topic: TargetTopic) {
        val subscribeToTopic = FirebaseMessaging.getInstance().subscribeToTopic(listOf(clientToken), topic.name)
        log.info("Client $clientToken subscribed to Topic ${topic.name}")
    }

    fun publishMessage(pushMessage: PushMessage) {
        pushMessageRepository.save(pushMessage)
        val message = Message.builder().setTopic(pushMessage.targetTopic.name)
            .setNotification(Notification(pushMessage.title, pushMessage.content)).build()
        log.info("Sende push message")
        val send = FirebaseMessaging.getInstance().send(message)
        log.info(send)

    }
}
