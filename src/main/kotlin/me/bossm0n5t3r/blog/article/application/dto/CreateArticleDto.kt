package me.bossm0n5t3r.blog.article.application.dto

import me.bossm0n5t3r.blog.common.exception.ErrorMessage
import me.bossm0n5t3r.blog.common.exception.InvalidException

data class CreateArticleDto(
    val subject: String,
    val content: String,
) {
    fun validate() {
        if (subject.isBlank()) throw InvalidException(ErrorMessage.SUBJECT_IS_BLANK.message)
        if (content.isBlank()) throw InvalidException(ErrorMessage.CONTENT_IS_BLANK.message)
    }
}
