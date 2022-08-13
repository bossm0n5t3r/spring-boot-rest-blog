package me.bossm0n5t3r.blog.article.application

import me.bossm0n5t3r.blog.article.application.dto.CreateArticleDto
import me.bossm0n5t3r.blog.article.application.dto.UpdateArticleDto
import me.bossm0n5t3r.blog.article.domain.Article
import me.bossm0n5t3r.blog.article.domain.ArticleRepository
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository
) {
    fun createArticle(dto: CreateArticleDto) {
        TODO("Not yet implemented")
    }

    fun findById(id: Long): Article? {
        TODO("Not yet implemented")
    }

    fun updateArticle(dto: UpdateArticleDto) {
        TODO("Not yet implemented")
    }

    fun deleteById(id: Long) {
        TODO("Not yet implemented")
    }
}
