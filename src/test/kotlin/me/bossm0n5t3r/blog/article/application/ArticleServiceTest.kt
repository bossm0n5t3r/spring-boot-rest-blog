package me.bossm0n5t3r.blog.article.application

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockkClass
import io.mockk.verify
import me.bossm0n5t3r.blog.article.application.dto.UpdateArticleDto
import me.bossm0n5t3r.blog.article.domain.Article
import me.bossm0n5t3r.blog.article.domain.ArticleRepository
import me.bossm0n5t3r.blog.common.CommonUtil
import me.bossm0n5t3r.blog.common.exception.ErrorMessage
import me.bossm0n5t3r.blog.common.exception.InvalidException
import me.bossm0n5t3r.blog.common.exception.ResourceNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.Optional

internal class ArticleServiceTest {
    private val articleRepository = mockkClass(ArticleRepository::class)
    private val mockRedisTemplate = mockkClass(StringRedisTemplate::class)
    private val mockObjectMapper = mockkClass(ObjectMapper::class)
    private val sut = ArticleService(articleRepository, mockRedisTemplate, mockObjectMapper)
    private val faker = CommonUtil.faker

    @Test
    fun should_find_all_articles() {
        // given
        val dummyArticles = (1..3).map {
            Article(faker.lorem().characters(), faker.lorem().characters())
        }
        every { articleRepository.findAll() } returns dummyArticles

        // when
        val result = sut.findAll()

        // then
        assertThat(result).isNotEmpty.hasSize(dummyArticles.size)
        assertThat(result.map { it.subject })
            .containsExactlyInAnyOrderElementsOf(dummyArticles.map { it.subject })
        assertThat(result.map { it.content })
            .containsExactlyInAnyOrderElementsOf(dummyArticles.map { it.content })
    }

    @Test
    fun should_find_article_by_id() {
        // given
        val id = faker.number().randomNumber()
        every { articleRepository.findById(id) } returns
            Optional.of(Article(faker.lorem().characters(), faker.lorem().characters()))

        // when
        val article = sut.findById(id)

        // then
        assertThat(article).isNotNull
        verify(exactly = 1) { articleRepository.findById(id) }
    }

    @Test
    fun should_throw_ResourceNotFoundException_if_find_no_article_by_id() {
        // given
        val id = faker.number().randomNumber()
        every { articleRepository.findById(id) } returns Optional.empty()

        // when, then
        assertThrows<ResourceNotFoundException> {
            sut.findById(id)
        }
            .also {
                assertThat(it.message).isEqualTo(ErrorMessage.NOT_FOUND_ARTICLE_BY_ID.message)
            }
    }

    @Test
    fun should_throw_ResourceNotFoundException_if_update_article_not_found() {
        // given
        val id = faker.number().randomNumber()
        val subject = faker.lorem().characters()
        val content = faker.lorem().characters()
        every { articleRepository.findById(id) } returns Optional.empty()

        // when, then
        assertThrows<ResourceNotFoundException> {
            sut.updateArticle(id, UpdateArticleDto(subject, content))
        }
            .also {
                assertThat(it.message).isEqualTo(ErrorMessage.NOT_FOUND_ARTICLE_BY_ID.message)
            }
    }

    @Test
    fun should_throw_InvalidException_when_new_subject_is_blank() {
        // given
        val id = faker.number().randomNumber()
        every { articleRepository.findById(id) } returns Optional.of(mockkClass(Article::class))
        val newSubject = " ".repeat((1..9).random())
        val newContent = faker.lorem().characters()

        // when, then
        assertThrows<InvalidException> {
            sut.updateArticle(id, UpdateArticleDto(newSubject, newContent))
        }
            .also {
                assertThat(it.message).isEqualTo(ErrorMessage.SUBJECT_IS_BLANK.message)
            }
    }

    @Test
    fun should_update_article() {
        // given
        val currentArticle = Article(faker.lorem().characters(), faker.lorem().characters())
        val id = faker.number().randomNumber()
        every { articleRepository.findById(id) } returns Optional.of(currentArticle)

        val newSubject = faker.lorem().characters()
        val newContent = ""

        // when
        assertDoesNotThrow { sut.updateArticle(id, UpdateArticleDto(newSubject, newContent)) }

        // then
        verify(exactly = 1) { articleRepository.findById(id) }
    }

    @Test
    fun should_delete_article_by_id() {
        // given
        val id = faker.number().randomNumber()
        justRun { articleRepository.deleteById(id) }

        // when
        sut.deleteById(id)

        // then
        verify(exactly = 1) { articleRepository.deleteById(id) }
    }
}
