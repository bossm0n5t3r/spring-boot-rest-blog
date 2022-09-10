package me.bossm0n5t3r.blog.message.presentation

import me.bossm0n5t3r.blog.message.application.MessageService
import me.bossm0n5t3r.blog.message.presentation.dto.Message
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController(
    private val messageService: MessageService
) {
    @PostMapping("/send/message")
    fun sendMessage(@RequestBody message: Message) {
        messageService.sendMessage(message)
    }
}
