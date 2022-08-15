package me.bossm0n5t3r.blog.comment.application

import me.bossm0n5t3r.blog.comment.application.dto.CommentDto
import me.bossm0n5t3r.blog.comment.domain.Comment

fun Comment.toDto() = CommentDto(this.content)
