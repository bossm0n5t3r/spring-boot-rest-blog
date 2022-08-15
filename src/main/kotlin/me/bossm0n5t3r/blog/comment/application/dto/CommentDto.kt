package me.bossm0n5t3r.blog.comment.application.dto

import me.bossm0n5t3r.blog.common.exception.ErrorMessage
import me.bossm0n5t3r.blog.common.exception.InvalidException

data class CommentDto(
    val content: String
) {
    fun validate() {
        if (content.isBlank()) throw InvalidException(ErrorMessage.CONTENT_IS_BLANK.message)
    }
}
