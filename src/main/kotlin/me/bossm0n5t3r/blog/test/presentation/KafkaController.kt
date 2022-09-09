package me.bossm0n5t3r.blog.test.presentation

import me.bossm0n5t3r.blog.test.application.KafkaService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class KafkaController(
    private val kafkaService: KafkaService
) {
    @PostMapping("/send/message")
    fun sendMessage(@RequestBody message: Message) {
        kafkaService.sendMessage(message)
    }
}

data class Message(
    val content: String
)
