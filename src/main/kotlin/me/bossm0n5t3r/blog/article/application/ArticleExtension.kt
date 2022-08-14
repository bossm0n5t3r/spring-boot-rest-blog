package me.bossm0n5t3r.blog.article.application

import me.bossm0n5t3r.blog.article.domain.Article
import me.bossm0n5t3r.blog.article.presentation.dto.ArticleDto

fun Article.toDto() = ArticleDto(
    id = this.id ?: 0L,
    subject = this.subject,
    content = this.content,
    hits = this.hits,
    recommend = this.recommend
)
