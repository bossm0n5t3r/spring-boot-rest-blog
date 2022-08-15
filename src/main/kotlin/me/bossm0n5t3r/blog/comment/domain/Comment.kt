package me.bossm0n5t3r.blog.comment.domain

import me.bossm0n5t3r.blog.article.domain.Article
import me.bossm0n5t3r.blog.comment.application.dto.CommentDto
import me.bossm0n5t3r.blog.common.domain.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "comment_info")
class Comment(
    content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false, updatable = false)
    var article: Article,

) : BaseEntity<Long>() {
    @Column(name = "content", nullable = false, updatable = true)
    var content: String = content

    fun updateComment(dto: CommentDto) {
        this.content = dto.content
    }
}
