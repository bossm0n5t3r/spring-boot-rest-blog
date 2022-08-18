package me.bossm0n5t3r.blog.comment.application

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockkClass
import io.mockk.verify
import me.bossm0n5t3r.blog.article.domain.Article
import me.bossm0n5t3r.blog.article.domain.ArticleRepository
import me.bossm0n5t3r.blog.comment.application.dto.CommentDto
import me.bossm0n5t3r.blog.comment.domain.Comment
import me.bossm0n5t3r.blog.comment.domain.CommentRepository
import me.bossm0n5t3r.blog.common.CommonUtil
import me.bossm0n5t3r.blog.common.exception.ErrorMessage
import me.bossm0n5t3r.blog.common.exception.InvalidException
import me.bossm0n5t3r.blog.common.exception.ResourceNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.Optional

internal class CommentServiceTest {
    private val articleRepository = mockkClass(ArticleRepository::class)
    private val commentRepository = mockkClass(CommentRepository::class)
    private val sut = CommentService(articleRepository, commentRepository)
    private val faker = CommonUtil.faker

    @AfterEach
    fun clearMock() {
        clearAllMocks()
    }

    @Test
    fun should_find_all_comments_from_article() {
        // given
        val articleId = faker.number().randomNumber()
        val comments = (1..3).map {
            val content = faker.lorem().characters()
            val article = Article(faker.lorem().characters(), faker.lorem().characters())
            Comment(content, article)
        }
        every { commentRepository.findAllByArticleId(articleId) } returns comments

        // when
        val allComments = sut.findAllCommentsByArticleId(articleId)

        // then
        assertThat(allComments)
            .isNotEmpty
            .hasSize(comments.size)
            .containsExactlyInAnyOrderElementsOf(
                comments.map { it.toDto() }
            )
        verify(exactly = 1) { commentRepository.findAllByArticleId(articleId) }
    }

    @Test
    fun should_throw_ResourceNotFoundException_if_no_article_when_create_comment() {
        // given
        val articleId = faker.number().randomNumber()
        val dto = CommentDto(faker.lorem().characters())
        every { articleRepository.findById(articleId) } returns Optional.empty()

        // when, then
        assertThrows<ResourceNotFoundException> { sut.createComment(articleId, dto) }
            .also { assertThat(it.message).isEqualTo(ErrorMessage.NOT_FOUND_ARTICLE_BY_ID.message) }
        verify(exactly = 1) { articleRepository.findById(articleId) }
    }

    @Test
    fun should_throw_InvalidException_if_content_is_blank_when_create_comment() {
        // given
        val articleId = faker.number().randomNumber()
        val dto = CommentDto("")
        every { articleRepository.findById(articleId) } returns Optional.of(mockkClass(Article::class))

        // when, then
        assertThrows<InvalidException> { sut.createComment(articleId, dto) }
            .also { assertThat(it.message).isEqualTo(ErrorMessage.CONTENT_IS_BLANK.message) }
        verify(exactly = 1) { articleRepository.findById(articleId) }
    }

    @Test
    fun should_create_comment_of_article() {
        // given
        val articleId = faker.number().randomNumber()
        val dto = CommentDto(faker.lorem().characters())
        val mockArticle = mockkClass(Article::class)
        every { articleRepository.findById(articleId) } returns Optional.of(mockArticle)
        every { commentRepository.save(any()) } returns mockkClass(Comment::class)

        // when
        assertDoesNotThrow { sut.createComment(articleId, dto) }

        // then
        verify(exactly = 1) { articleRepository.findById(articleId) }
        verify(exactly = 1) { commentRepository.save(any()) }
    }

    @Test
    fun should_throw_ResourceNotFoundException_if_find_no_comment() {
        // given
        val articleId = faker.number().randomNumber()
        val commentId = faker.number().randomNumber()
        every { commentRepository.findByArticleIdAndId(articleId, commentId) } returns null

        // when, then
        assertThrows<ResourceNotFoundException> {
            sut.findByArticleIdAndCommentId(articleId, commentId)
        }
            .also {
                assertThat(it.message).isEqualTo(ErrorMessage.NOT_FOUND_COMMENT.message)
            }
        verify(exactly = 1) { commentRepository.findByArticleIdAndId(articleId, commentId) }
    }

    @Test
    fun should_find_comment_by_id() {
        // given
        val articleId = faker.number().randomNumber()
        val commentId = faker.number().randomNumber()
        val mockComment = mockkClass(Comment::class)
        every { commentRepository.findByArticleIdAndId(articleId, commentId) } returns mockComment
        every { mockComment.content } returns faker.lorem().characters()

        // when
        val comment = assertDoesNotThrow { sut.findByArticleIdAndCommentId(articleId, commentId) }

        // then
        assertThat(comment).isNotNull
        verify(exactly = 1) { commentRepository.findByArticleIdAndId(articleId, commentId) }
    }

    @Test
    fun should_throw_ResourceNotFoundException_if_find_no_comment_when_update_comment() {
        // given
        val articleId = faker.number().randomNumber()
        val commentId = faker.number().randomNumber()
        val dto = CommentDto(faker.lorem().characters())
        every { commentRepository.findByArticleIdAndId(articleId, commentId) } returns null

        // when, then
        assertThrows<ResourceNotFoundException> {
            sut.updateComment(articleId, commentId, dto)
        }
            .also {
                assertThat(it.message).isEqualTo(ErrorMessage.NOT_FOUND_COMMENT.message)
            }
        verify(exactly = 1) { commentRepository.findByArticleIdAndId(articleId, commentId) }
    }

    @Test
    fun should_throw_InvalidException_if_comment_is_blank_when_update_comment() {
        // given
        val articleId = faker.number().randomNumber()
        val commentId = faker.number().randomNumber()
        val dto = CommentDto("")
        every { commentRepository.findByArticleIdAndId(articleId, commentId) } returns mockkClass(Comment::class)

        // when, then
        assertThrows<InvalidException> { sut.updateComment(articleId, commentId, dto) }
            .also {
                assertThat(it.message).isEqualTo(ErrorMessage.CONTENT_IS_BLANK.message)
            }
        verify(exactly = 1) { commentRepository.findByArticleIdAndId(articleId, commentId) }
    }

    @Test
    fun should_update_comment() {
        // given
        val articleId = faker.number().randomNumber()
        val commentId = faker.number().randomNumber()
        val dto = CommentDto(faker.lorem().characters())
        val mockComment = mockkClass(Comment::class)
        every { commentRepository.findByArticleIdAndId(articleId, commentId) } returns mockComment
        justRun { mockComment.updateComment(dto) }

        // when
        assertDoesNotThrow { sut.updateComment(articleId, commentId, dto) }

        // then
        verify(exactly = 1) { commentRepository.findByArticleIdAndId(articleId, commentId) }
    }

    @Test
    fun should_delete_comment_by_article_id_and_comment_id() {
        // given
        val articleId = faker.number().randomNumber()
        val commentId = faker.number().randomNumber()
        justRun { commentRepository.deleteByArticleIdAndId(articleId, commentId) }

        // when
        sut.deleteByArticleIdAndCommentId(articleId, commentId)

        // then
        verify(exactly = 1) { commentRepository.deleteByArticleIdAndId(articleId, commentId) }
    }
}
