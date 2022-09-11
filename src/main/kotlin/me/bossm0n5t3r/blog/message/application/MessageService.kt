package me.bossm0n5t3r.blog.message.application

import org.apache.kafka.clients.consumer.ConsumerRecord

interface MessageService<Message, K, V> {
    fun sendMessage(messageDto: Message)
    fun receiveMessage(record: ConsumerRecord<K, V>)
}
