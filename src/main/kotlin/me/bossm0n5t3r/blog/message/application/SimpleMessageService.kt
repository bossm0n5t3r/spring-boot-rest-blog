package me.bossm0n5t3r.blog.message.application

import me.bossm0n5t3r.blog.common.configuration.Constants
import me.bossm0n5t3r.blog.message.dto.SimpleMessage
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFutureCallback

@Service
class SimpleMessageService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : MessageService<SimpleMessage, String, String> {
    override fun sendMessage(messageDto: SimpleMessage) {
        val (topic, message) = messageDto
        kafkaTemplate.send(topic, message)
            .also {
                it.addCallback(object : ListenableFutureCallback<SendResult<String, String>> {
                    override fun onSuccess(result: SendResult<String, String>?) {
                        println("Sent topic=[$topic] message=[$message] with offset=[${result!!.recordMetadata.offset()}]")
                    }

                    override fun onFailure(ex: Throwable) {
                        println("Unable to send topic=[$topic] message=[$message] due to : ${ex.message}")
                    }
                })
            }
    }

    @KafkaListener(topics = [Constants.Kafka.TOPIC_BLOG_MESSAGE])
    override fun receiveMessage(record: ConsumerRecord<String, String>) {
        println("Received Message : $record")
    }
}
