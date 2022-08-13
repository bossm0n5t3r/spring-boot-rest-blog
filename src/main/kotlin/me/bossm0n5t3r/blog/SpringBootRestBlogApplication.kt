package me.bossm0n5t3r.blog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootRestBlogApplication

fun main(args: Array<String>) {
    runApplication<SpringBootRestBlogApplication>(*args)
}
