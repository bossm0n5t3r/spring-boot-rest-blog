package me.bossm0n5t3r.blog.message.application

import me.bossm0n5t3r.blog.common.configuration.Constants
import me.bossm0n5t3r.blog.message.presentation.dto.Message
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFutureCallback

@Service
class MessageService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {
    fun sendMessage(topic: String, msg: Message) {
        kafkaTemplate.send(topic, msg.content)
            .also {
                it.addCallback(object : ListenableFutureCallback<SendResult<String, String>> {
                    override fun onSuccess(result: SendResult<String, String>?) {
                        println("Sent message=[${msg.content}] with offset=[${result!!.recordMetadata.offset()}]")
                    }

                    override fun onFailure(ex: Throwable) {
                        println("Unable to send message=[${msg.content}] due to : ${ex.message}")
                    }
                })
            }
    }

    @KafkaListener(topics = [Constants.Kafka.TOPIC_BLOG_MESSAGE])
    fun receiveMessage(consumerRecord: ConsumerRecord<String, String>) {
        println("Received Message : $consumerRecord")
    }
}
