package me.bossm0n5t3r.blog.common.configuration.kafka

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "spring.kafka")
data class KafkaProperties(
    val consumer: Consumer,
    val bootstrapServers: String,
) {
    data class Consumer(
        val autoOffsetReset: String,
        val groupId: String,
    )
}
