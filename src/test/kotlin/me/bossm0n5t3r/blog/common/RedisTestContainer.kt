package me.bossm0n5t3r.blog.common

import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestInstance
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import javax.annotation.PreDestroy

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisTestContainer {
    @PreDestroy
    fun stop() = REDIS_CONTAINER.stop()

    companion object {
        @JvmStatic
        val REDIS_CONTAINER = GenericContainer<Nothing>(
            DockerImageName.parse("redis:latest")
        )
            .apply {
                withExposedPorts(6379)
                start()
            }
            .also {
                System.setProperty("spring.redis.host", it.host)
                System.setProperty("spring.redis.port", it.getMappedPort(6379).toString())
            }
    }
}
