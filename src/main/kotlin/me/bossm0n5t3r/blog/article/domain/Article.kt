package me.bossm0n5t3r.blog.article.domain

import me.bossm0n5t3r.blog.article.application.dto.CreateArticleDto
import me.bossm0n5t3r.blog.article.application.dto.UpdateArticleDto
import me.bossm0n5t3r.blog.common.domain.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "article_info")
class Article(
    @Column(name = "subject", nullable = false)
    var subject: String,

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "hits", nullable = false, updatable = true)
    var hits: Int = 0,

    @Column(name = "recommend", nullable = false)
    var recommend: Int = 0,
) : BaseEntity<Long>() {
    constructor(dto: CreateArticleDto) : this(
        subject = dto.subject,
        content = dto.content
    )

    fun updateArticle(updateArticleDto: UpdateArticleDto) {
        updateArticleDto.subject?.let { this.subject = it }
        updateArticleDto.content?.let { this.content = it }
    }

    fun updateHit() {
        this.hits++
    }

    fun updateRecommend() {
        this.recommend++
    }
}
