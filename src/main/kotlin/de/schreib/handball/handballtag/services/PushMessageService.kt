package de.schreib.handball.handballtag.services

import de.schreib.handball.handballtag.entities.PushMessage
import de.schreib.handball.handballtag.repositories.PushMessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PushMessageService @Autowired constructor(val pushMessageRepository: PushMessageRepository){
    fun publishMessage(pushMessage: PushMessage) {
        // TODO mit firebase an Client schicken
        pushMessageRepository.save(pushMessage)
    }
}
