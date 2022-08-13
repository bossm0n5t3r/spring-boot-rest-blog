package me.bossm0n5t3r.blog.article.domain

import me.bossm0n5t3r.blog.article.application.dto.UpdateArticleDto
import me.bossm0n5t3r.blog.common.CommonUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class ArticleRepositoryTest {
    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var sut: ArticleRepository

    private val faker = CommonUtil.faker

    @Test
    fun should_find_no_articles_if_repository_is_empty() {
        val articles = sut.findAll()
        assertThat(articles).isEmpty()
    }

    @Test
    fun should_store_a_article() {
        // given
        val subject = faker.lorem().characters()
        val content = faker.lorem().characters()

        // when
        val article = sut.save(Article(subject, content))

        // then
        assertEquals(article.subject, subject)
        assertEquals(article.content, content)
    }

    @Test
    fun should_find_all_articles() {
        // given
        val articles = (1..3).map {
            val subject = faker.lorem().characters()
            val content = faker.lorem().characters()
            val article = Article(subject, content)
            entityManager.persist(article)
            article
        }

        // when
        val allArticles = sut.findAll()

        // then
        assertThat(allArticles).hasSize(articles.size).containsExactlyElementsOf(articles)
    }

    @Test
    fun should_update_article() {
        // given
        val subject = faker.lorem().characters()
        val content = faker.lorem().characters()
        val article = sut.save(Article(subject, content))

        val newSubject = faker.lorem().characters()
        val newContent = faker.lorem().characters()
        assertNotEquals(subject, newSubject)
        assertNotEquals(content, newContent)

        // when
        article.updateArticle(
            UpdateArticleDto(
                subject = newSubject,
                content = newContent,
            )
        )

        // then
        val optionalNewArticle = sut.findById(article.id!!)
        assertThat(optionalNewArticle.isPresent).isTrue
        val newArticle = optionalNewArticle.get()
        assertThat(newArticle.subject)
            .isNotEqualTo(subject)
            .isEqualTo(newSubject)
        assertThat(newArticle.content)
            .isNotEqualTo(content)
            .isEqualTo(newContent)
    }

    @Test
    fun should_delete_by_id() {
        // given
        val subject = faker.lorem().characters()
        val content = faker.lorem().characters()
        val article = sut.save(Article(subject, content))
        assertThat(sut.findAll()).isNotEmpty

        // when
        sut.deleteById(article.id!!)

        // then
        assertThat(sut.findAll()).isEmpty()
    }
}
