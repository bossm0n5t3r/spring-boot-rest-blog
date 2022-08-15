package me.bossm0n5t3r.blog.article.application

import me.bossm0n5t3r.blog.article.application.dto.CreateArticleDto
import me.bossm0n5t3r.blog.article.application.dto.UpdateArticleDto
import me.bossm0n5t3r.blog.article.domain.Article
import me.bossm0n5t3r.blog.article.domain.ArticleRepository
import me.bossm0n5t3r.blog.article.presentation.dto.ArticleDto
import me.bossm0n5t3r.blog.common.exception.ErrorMessage
import me.bossm0n5t3r.blog.common.exception.ResourceNotFoundException
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository
) {
    fun findAll(): List<ArticleDto> {
        return articleRepository.findAll().map { it.toDto() }
    }

    fun createArticle(dto: CreateArticleDto) {
        dto.validate()
        articleRepository.save(Article(dto))
    }

    fun findById(id: Long): ArticleDto {
        return articleRepository.findById(id).orElseThrow {
            ResourceNotFoundException(ErrorMessage.NOT_FOUND_ARTICLE_BY_ID.message)
        }.toDto()
    }

    fun updateArticle(id: Long, dto: UpdateArticleDto) {
        val article = articleRepository.findById(id).orElseThrow {
            ResourceNotFoundException(ErrorMessage.NOT_FOUND_ARTICLE_BY_ID.message)
        }
        dto.validate()
        article.updateArticle(dto)
    }

    fun deleteById(id: Long) {
        articleRepository.deleteById(id)
    }
}
