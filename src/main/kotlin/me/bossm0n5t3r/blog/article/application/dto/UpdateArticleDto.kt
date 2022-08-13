package me.bossm0n5t3r.blog.article.application.dto

data class UpdateArticleDto(
    val id: Long = 0L,
    val subject: String? = null,
    val content: String? = null,
)
