package me.bossm0n5t3r.blog.article.application.dto

import me.bossm0n5t3r.blog.common.exception.ErrorMessage
import me.bossm0n5t3r.blog.common.exception.InvalidException

data class UpdateArticleDto(
    val subject: String? = null,
    val content: String? = null,
) {
    fun validate() {
        if (subject.isNullOrBlank()) throw InvalidException(ErrorMessage.SUBJECT_IS_BLANK.message)
    }
}
