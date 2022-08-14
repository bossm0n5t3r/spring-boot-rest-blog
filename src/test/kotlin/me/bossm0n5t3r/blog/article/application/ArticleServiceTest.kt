package me.bossm0n5t3r.blog.article.application

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockkClass
import io.mockk.verify
import me.bossm0n5t3r.blog.article.application.dto.CreateArticleDto
import me.bossm0n5t3r.blog.article.application.dto.UpdateArticleDto
import me.bossm0n5t3r.blog.article.domain.Article
import me.bossm0n5t3r.blog.article.domain.ArticleRepository
import me.bossm0n5t3r.blog.common.CommonUtil
import me.bossm0n5t3r.blog.common.exception.ErrorMessage
import me.bossm0n5t3r.blog.common.exception.ResourceNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.Optional

internal class ArticleServiceTest {
    private val articleRepository = mockkClass(ArticleRepository::class)
    private val sut = ArticleService(articleRepository)
    private val faker = CommonUtil.faker

    @Test
    fun should_throw_ResourceNotFoundException_if_subject_is_blank() {
        // given
        val blankSubjectDto = CreateArticleDto(" ".repeat((1..9).random()), faker.lorem().characters())

        // when, then
        assertThrows<ResourceNotFoundException> {
            sut.createArticle(blankSubjectDto)
        }
            .also {
                assertThat(it.message).isEqualTo(ErrorMessage.SUBJECT_IS_BLANK.message)
            }
        verify(exactly = 0) { articleRepository.save(any()) }
    }

    @Test
    fun should_throw_ResourceNotFoundException_if_content_is_empty() {
        // given
        val emptyContentDto = CreateArticleDto(faker.lorem().characters(), "")

        // when, then
        assertThrows<ResourceNotFoundException> {
            sut.createArticle(emptyContentDto)
        }
            .also {
                assertThat(it.message).isEqualTo(ErrorMessage.CONTENT_IS_BLANK.message)
            }
        verify(exactly = 0) { articleRepository.save(any()) }
    }

    @Test
    fun should_create_article() {
        // given
        val subject = faker.lorem().characters()
        val content = faker.lorem().characters()
        val article = Article(subject, content)
        every { articleRepository.save(article) } returns article

        // when
        assertDoesNotThrow { sut.createArticle(CreateArticleDto(subject, content)) }

        // then
        verify(exactly = 1) { articleRepository.save(article) }
    }

    @Test
    fun should_find_article_by_id() {
        // given
        val id = faker.number().randomNumber()
        every { articleRepository.findById(id) } returns Optional.empty()

        // when
        val article = sut.findById(id)

        // then
        assertThat(article).isNull()
        verify(exactly = 1) { articleRepository.findById(id) }
    }

    @Test
    fun should_throw_ResourceNotFoundException_if_article_id_does_not_exists() {
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
    fun should_throw_ResourceNotFoundException_when_new_subject_is_blank() {
        // given
        val id = faker.number().randomNumber()
        every { articleRepository.findById(id) } returns
                Optional.of(Article(faker.lorem().characters(), faker.lorem().characters()))
        val newSubject = " ".repeat((1..9).random())
        val newContent = faker.lorem().characters()

        // when, then
        assertThrows<ResourceNotFoundException> {
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
