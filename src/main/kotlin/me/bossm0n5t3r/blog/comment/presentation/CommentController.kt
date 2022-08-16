package me.bossm0n5t3r.blog.comment.presentation

import me.bossm0n5t3r.blog.comment.application.CommentService
import me.bossm0n5t3r.blog.comment.application.dto.CommentDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentController(
    private val commentService: CommentService
) {
    @GetMapping("/articles/{articleId}/comments")
    fun getAllCommentsByArticleId(@PathVariable articleId: Long): List<CommentDto> {
        return commentService.findAllCommentsByArticleId(articleId)
    }

    @GetMapping("/articles/{articleId}/comments/{commentId}")
    fun getCommentByArticleIdAndCommentId(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
    ): CommentDto {
        return commentService.findByArticleIdAndCommentId(articleId, commentId)
    }

    @PostMapping("/articles/{articleId}/comments")
    fun createComment(@PathVariable articleId: Long, @RequestBody dto: CommentDto): ResponseEntity<Unit> {
        commentService.createComment(articleId, dto)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @PutMapping("/articles/{articleId}/comments/{commentId}")
    fun updateComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @RequestBody dto: CommentDto,
    ) {
        commentService.updateComment(articleId, commentId, dto)
    }

    @DeleteMapping("/articles/{articleId}/comments/{commentId}")
    fun deleteComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
    ): ResponseEntity<Unit> {
        commentService.deleteByArticleIdAndCommentId(articleId, commentId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
