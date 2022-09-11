package me.bossm0n5t3r.blog.message.dto

data class SimpleMessage(
    override val topic: String,
    override val message: String,
) : Message
