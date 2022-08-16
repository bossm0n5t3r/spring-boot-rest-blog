package me.bossm0n5t3r.blog.comment.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import me.bossm0n5t3r.blog.comment.application.CommentService
import me.bossm0n5t3r.blog.comment.application.dto.CommentDto
import me.bossm0n5t3r.blog.common.CommonUtil
import me.bossm0n5t3r.blog.common.exception.ErrorMessage
import me.bossm0n5t3r.blog.common.exception.InvalidException
import me.bossm0n5t3r.blog.common.exception.ResourceNotFoundException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
internal class CommentControllerTest {
    @MockkBean
    lateinit var commentService: CommentService

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private val faker = CommonUtil.faker
    private val articleId = faker.number().randomNumber()

    @Test
    fun should_200_response_if_request_get_all_comments_by_article_id() {
        // given
        every { commentService.findAllCommentsByArticleId(articleId) } returns emptyList()

        // when, then
        mockMvc.perform(
            get("/articles/{articleId}/comments", articleId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
        verify(exactly = 1) { commentService.findAllCommentsByArticleId(articleId) }
    }

    @Test
    fun should_404_response_if_no_article_when_create_comment() {
        // given
        val dto = CommentDto(faker.lorem().characters())
        every { commentService.createComment(articleId, dto) } throws
            ResourceNotFoundException(ErrorMessage.NOT_FOUND_ARTICLE_BY_ID.message)

        // when, then
        mockMvc.perform(
            post("/articles/{articleId}/comments", articleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isNotFound)
        verify(exactly = 1) { commentService.createComment(articleId, dto) }
    }

    @Test
    fun should_400_response_if_comment_content_is_blank_when_create_comment() {
        // given
        val dto = CommentDto(faker.lorem().characters())
        every { commentService.createComment(articleId, dto) } throws
            InvalidException(ErrorMessage.CONTENT_IS_BLANK.message)

        // when, then
        mockMvc.perform(
            post("/articles/{articleId}/comments", articleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isBadRequest)
        verify(exactly = 1) { commentService.createComment(articleId, dto) }
    }

    @Test
    fun should_201_response_when_create_comment_successfully() {
        // given
        val dto = CommentDto(faker.lorem().characters())
        justRun { commentService.createComment(articleId, dto) }

        // when, then
        mockMvc.perform(
            post("/articles/{articleId}/comments", articleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isCreated)
        verify(exactly = 1) { commentService.createComment(articleId, dto) }
    }

    @Test
    fun should_404_response_if_no_comment_when_find_comment_by_article_id_and_comment_id() {
        // given
        val commentId = faker.number().randomNumber()
        every { commentService.findByArticleIdAndCommentId(articleId, commentId) } throws
            ResourceNotFoundException(ErrorMessage.NOT_FOUND_COMMENT.message)

        // when, then
        mockMvc.perform(
            get("/articles/{articleId}/comments/{commentId}", articleId, commentId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
        verify(exactly = 1) { commentService.findByArticleIdAndCommentId(articleId, commentId) }
    }

    @Test
    fun should_200_response_when_find_comment_by_article_id_and_comment_id() {
        // given
        val commentId = faker.number().randomNumber()
        every { commentService.findByArticleIdAndCommentId(articleId, commentId) } returns
            CommentDto(faker.lorem().characters())

        // when, then
        mockMvc.perform(
            get("/articles/{articleId}/comments/{commentId}", articleId, commentId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
        verify(exactly = 1) { commentService.findByArticleIdAndCommentId(articleId, commentId) }
    }

    @Test
    fun should_404_response_if_no_comment_when_update_comment() {
        // given
        val commentId = faker.number().randomNumber()
        val dto = CommentDto(faker.lorem().characters())
        every { commentService.updateComment(articleId, commentId, dto) } throws
            ResourceNotFoundException(ErrorMessage.NOT_FOUND_ARTICLE_BY_ID.message)

        // when, then
        mockMvc.perform(
            put("/articles/{articleId}/comments/{commentId}", articleId, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isNotFound)
        verify(exactly = 1) { commentService.updateComment(articleId, commentId, dto) }
    }

    @Test
    fun should_400_response_if_comment_content_is_blank_when_update_comment() {
        // given
        val commentId = faker.number().randomNumber()
        val dto = CommentDto(faker.lorem().characters())
        every { commentService.updateComment(articleId, commentId, dto) } throws
            InvalidException(ErrorMessage.CONTENT_IS_BLANK.message)

        // when, then
        mockMvc.perform(
            put("/articles/{articleId}/comments/{commentId}", articleId, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isBadRequest)
        verify(exactly = 1) { commentService.updateComment(articleId, commentId, dto) }
    }

    @Test
    fun should_200_response_when_update_comment_successfully() {
        // given
        val commentId = faker.number().randomNumber()
        val dto = CommentDto(faker.lorem().characters())
        justRun { commentService.updateComment(articleId, commentId, dto) }

        // when, then
        mockMvc.perform(
            put("/articles/{articleId}/comments/{commentId}", articleId, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
        verify(exactly = 1) { commentService.updateComment(articleId, commentId, dto) }
    }

    @Test
    fun should_204_response_when_delete_comment() {
        // given
        val commentId = faker.number().randomNumber()
        justRun { commentService.deleteByArticleIdAndCommentId(articleId, commentId) }

        // when, then
        mockMvc.perform(
            delete("/articles/{articleId}/comments/{commentId}", articleId, commentId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)
        verify(exactly = 1) { commentService.deleteByArticleIdAndCommentId(articleId, commentId) }
    }
}
