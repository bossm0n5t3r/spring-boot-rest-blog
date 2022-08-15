package me.bossm0n5t3r.blog.comment.domain

import me.bossm0n5t3r.blog.article.domain.Article
import me.bossm0n5t3r.blog.comment.application.dto.CommentDto
import me.bossm0n5t3r.blog.common.CommonUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var sut: CommentRepository

    private val faker = CommonUtil.faker

    private fun getArticle(): Article {
        val subject = faker.lorem().characters()
        val content = faker.lorem().characters()

        return entityManager.persist(Article(subject, content))
    }

    @Test
    fun should_find_no_comments_if_repository_is_empty() {
        val comments = sut.findAll()
        assertThat(comments).isEmpty()
    }

    @Test
    fun should_store_a_comment() {
        // given
        val content = faker.lorem().characters()
        val article = getArticle()

        // when
        val comment = sut.save(Comment(content, article))

        // then
        assertEquals(comment.content, content)
        assertEquals(comment.article, article)
    }

    @Test
    fun should_find_all_comments() {
        // given
        val comments = (1..3).map {
            val content = faker.lorem().characters()
            val article = getArticle()
            Comment(content, article)
                .also { entityManager.persist(it) }
        }

        // when
        val allComments = sut.findAll()

        // then
        assertThat(allComments).containsExactlyElementsOf(comments)
    }

    @Test
    fun should_find_all_by_article_id() {
        // given
        val comments = (1..3).map {
            val content = faker.lorem().characters()
            val article = getArticle()
            Comment(content, article)
                .also { entityManager.persist(it) }
        }
        val articleIds = comments.mapNotNull { it.article.id }
        val selectedArticleId = articleIds.random()

        // when
        val allCommentByArticleId = sut.findAllByArticleId(selectedArticleId)

        // then
        assertThat(allCommentByArticleId)
            .containsExactlyInAnyOrderElementsOf(comments.filter { it.article.id == selectedArticleId })
    }

    @Test
    fun should_update_comment() {
        // given
        val content = faker.lorem().characters()
        val article = getArticle()
        val comment = sut.save(Comment(content, article))

        val newContent = faker.lorem().characters()
        assertNotEquals(content, newContent)

        // when
        comment.updateComment(CommentDto(content = newContent))

        // then
        val optionalNewComment = sut.findById(article.id!!)
        assertThat(optionalNewComment.isPresent).isTrue

        val newComment = optionalNewComment.get()
        assertThat(newComment.content)
            .isNotEqualTo(content)
            .isEqualTo(newContent)
    }

    @Test
    fun should_delete_by_id() {
        // given
        val content = faker.lorem().characters()
        val article = getArticle()
        val comment = sut.save(Comment(content, article))
        assertThat(sut.findAll()).isNotEmpty

        // when
        sut.deleteById(comment.id!!)

        // then
        assertThat(sut.findAll()).isEmpty()
    }
}
