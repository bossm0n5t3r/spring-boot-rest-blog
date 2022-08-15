package me.bossm0n5t3r.blog.comment.application

import me.bossm0n5t3r.blog.article.domain.ArticleRepository
import me.bossm0n5t3r.blog.comment.application.dto.CommentDto
import me.bossm0n5t3r.blog.comment.domain.CommentRepository
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
) {
    fun findAllCommentsByArticleId(articleId: Long): List<CommentDto> {
        TODO("Not yet implemented")
    }

    fun createComment(articleId: Long, dto: CommentDto) {
        TODO("Not yet implemented")
    }

    fun findByArticleIdAndCommentId(articleId: Long, commentId: Long): CommentDto {
        TODO("Not yet implemented")
    }

    fun updateComment(articleId: Long, commentId: Long, dto: CommentDto) {
        TODO("Not yet implemented")
    }

    fun deleteByArticleIdAndCommentId(articleId: Long, commentId: Long) {
        TODO("Not yet implemented")
    }
}
