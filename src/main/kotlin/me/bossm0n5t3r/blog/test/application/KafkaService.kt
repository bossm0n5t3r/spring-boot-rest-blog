package me.bossm0n5t3r.blog.test.application

import me.bossm0n5t3r.blog.common.configuration.Constants
import me.bossm0n5t3r.blog.test.presentation.Message
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFutureCallback

@Service
class KafkaService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {
    fun sendMessage(msg: Message) {
        kafkaTemplate.send(Constants.Kafka.TOPIC_BLOG_MESSAGE, msg.content)
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
