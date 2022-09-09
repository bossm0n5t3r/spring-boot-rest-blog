package me.bossm0n5t3r.blog.common.configuration.kafka

import me.bossm0n5t3r.blog.common.configuration.Constants
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaTopicConfig(
    private val kafkaProperties: KafkaProperties,
) {
    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        return KafkaAdmin(
            mapOf(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            )
        )
    }

    @Bean
    fun topic() = NewTopic(
        Constants.Kafka.TOPIC_BLOG_MESSAGE,
        1,
        1
    )
}
