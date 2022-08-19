package me.bossm0n5t3r.blog.comment.application

import me.bossm0n5t3r.blog.article.domain.ArticleRepository
import me.bossm0n5t3r.blog.comment.application.dto.CommentDto
import me.bossm0n5t3r.blog.comment.domain.Comment
import me.bossm0n5t3r.blog.comment.domain.CommentRepository
import me.bossm0n5t3r.blog.common.exception.ErrorMessage
import me.bossm0n5t3r.blog.common.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CommentService(
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
) {
    fun findAllCommentsByArticleId(articleId: Long): List<CommentDto> {
        return commentRepository.findAllByArticleId(articleId).map { it.toDto() }
    }

    fun createComment(articleId: Long, dto: CommentDto) {
        val article = articleRepository.findById(articleId).orElseThrow {
            ResourceNotFoundException(ErrorMessage.NOT_FOUND_ARTICLE_BY_ID.message)
        }
        dto.validate()
        commentRepository.save(Comment(dto.content, article))
    }

    fun findByArticleIdAndCommentId(articleId: Long, commentId: Long): CommentDto {
        return commentRepository.findByArticleIdAndId(articleId, commentId)?.toDto()
            ?: throw ResourceNotFoundException(ErrorMessage.NOT_FOUND_COMMENT.message)
    }

    fun updateComment(articleId: Long, commentId: Long, dto: CommentDto) {
        val comment = commentRepository.findByArticleIdAndId(articleId, commentId)
            ?: throw ResourceNotFoundException(ErrorMessage.NOT_FOUND_COMMENT.message)
        dto.validate()
        comment.updateComment(dto)
    }

    fun deleteByArticleIdAndCommentId(articleId: Long, commentId: Long) {
        commentRepository.deleteByArticleIdAndId(articleId, commentId)
    }
}
