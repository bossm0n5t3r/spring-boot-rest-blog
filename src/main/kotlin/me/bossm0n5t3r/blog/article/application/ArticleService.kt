package me.bossm0n5t3r.blog.article.application

import com.fasterxml.jackson.databind.ObjectMapper
import me.bossm0n5t3r.blog.article.application.dto.CreateArticleDto
import me.bossm0n5t3r.blog.article.application.dto.UpdateArticleDto
import me.bossm0n5t3r.blog.article.domain.Article
import me.bossm0n5t3r.blog.article.domain.ArticleRepository
import me.bossm0n5t3r.blog.article.presentation.dto.ArticleDto
import me.bossm0n5t3r.blog.common.configuration.Constants.RecentArticles
import me.bossm0n5t3r.blog.common.configuration.readValueWithTypeReference
import me.bossm0n5t3r.blog.common.exception.ErrorMessage
import me.bossm0n5t3r.blog.common.exception.ResourceNotFoundException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val stringRedisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
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

    fun getRecentArticles(): List<ArticleDto> {
        val readOpsForValue = stringRedisTemplate.opsForValue()
        val recentArticlesInRedis = readOpsForValue.get(RecentArticles.RedisKey)
        return if (recentArticlesInRedis != null) {
            objectMapper.readValueWithTypeReference(recentArticlesInRedis)
        } else {
            articleRepository.findAllByOrderByCreatedAtDesc()
                .take(RecentArticles.MAX_RECENT_ARTICLES_COUNT)
                .map { it.toDto() }
                .also {
                    readOpsForValue.set(
                        RecentArticles.RedisKey,
                        objectMapper.writeValueAsString(it),
                        RecentArticles.TIMEOUT,
                    )
                }
        }
    }
}
