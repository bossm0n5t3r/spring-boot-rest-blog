package me.bossm0n5t3r.blog.message.application

import org.apache.kafka.clients.consumer.ConsumerRecord

interface MessageService<T, K, V> {
    fun sendMessage(messageDto: T)
    fun receiveMessage(record: ConsumerRecord<K, V>)
}
