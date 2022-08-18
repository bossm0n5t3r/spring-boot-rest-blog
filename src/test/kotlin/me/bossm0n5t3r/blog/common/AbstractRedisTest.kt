package me.bossm0n5t3r.blog.common

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate

@SpringBootTest
class AbstractRedisTest : RedisTestContainer() {
    @Autowired
    lateinit var stringRedisTemplate: StringRedisTemplate

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @AfterEach
    fun cleanUp() {
        stringRedisTemplate.connectionFactory?.connection?.flushAll()
    }
}
