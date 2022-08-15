package me.bossm0n5t3r.blog.comment.domain

import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByArticleId(articleId: Long): List<Comment>
    fun findByArticleIdAndId(articleId: Long, id: Long): Comment?
    fun deleteByArticleIdAndId(articleId: Long, id: Long)
}
