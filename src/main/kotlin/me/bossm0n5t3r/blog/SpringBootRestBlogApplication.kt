package me.bossm0n5t3r.blog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class SpringBootRestBlogApplication

fun main(args: Array<String>) {
    runApplication<SpringBootRestBlogApplication>(*args)
}
