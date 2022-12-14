package me.bossm0n5t3r.blog.article.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import me.bossm0n5t3r.blog.article.application.ArticleService
import me.bossm0n5t3r.blog.article.application.dto.CreateArticleDto
import me.bossm0n5t3r.blog.article.application.dto.UpdateArticleDto
import me.bossm0n5t3r.blog.article.presentation.dto.ArticleDto
import me.bossm0n5t3r.blog.common.CommonUtil
import me.bossm0n5t3r.blog.common.exception.ErrorMessage
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
internal class ArticleControllerTest {
    @MockkBean
    lateinit var articleService: ArticleService

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private val faker = CommonUtil.faker

    @Test
    fun should_200_response_if_request_get_all_articles() {
        every { articleService.findAll() } returns emptyList()

        mockMvc.perform(
            get("/articles")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun should_404_response_if_request_wrong_article_id() {
        val wrongArticleId = faker.number().randomNumber()
        every { articleService.findById(wrongArticleId) } throws
            ResourceNotFoundException(ErrorMessage.NOT_FOUND_ARTICLE_BY_ID.message)

        mockMvc.perform(
            get("/articles/$wrongArticleId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
        verify(exactly = 1) { articleService.findById(wrongArticleId) }
    }

    @Test
    fun should_200_response_if_request_valid_article_id() {
        val validArticleId = faker.number().randomNumber()
        every { articleService.findById(validArticleId) } returns
            ArticleDto(
                id = validArticleId,
                subject = faker.lorem().characters(),
                content = faker.lorem().characters(),
                hits = faker.number().randomDigit(),
                recommend = faker.number().randomDigit()
            )

        mockMvc.perform(
            get("/articles/$validArticleId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
        verify(exactly = 1) { articleService.findById(validArticleId) }
    }

    @Test
    fun should_404_response_if_invalid_create_article_request() {
        val dto = CreateArticleDto(subject = "", content = faker.lorem().characters())
        every { articleService.createArticle(dto) } throws
            ResourceNotFoundException(ErrorMessage.SUBJECT_IS_BLANK.message)

        mockMvc.perform(
            post("/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun should_201_response_if_create_article() {
        val dto = CreateArticleDto(
            subject = faker.lorem().characters(), content = faker.lorem().characters()
        )
        justRun { articleService.createArticle(dto) }

        mockMvc.perform(
            post("/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun should_404_response_if_invalid_update_article_request() {
        val articleId = faker.number().randomNumber()
        val dto = UpdateArticleDto(subject = null, content = faker.lorem().characters())
        every { articleService.updateArticle(articleId, dto) } throws
            ResourceNotFoundException(ErrorMessage.SUBJECT_IS_BLANK.message)

        mockMvc.perform(
            put("/articles/$articleId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun should_200_response_if_valid_update_article_request() {
        val articleId = faker.number().randomNumber()
        val dto = UpdateArticleDto(subject = faker.lorem().characters(), content = faker.lorem().characters())
        justRun { articleService.updateArticle(articleId, dto) }

        mockMvc.perform(
            put("/articles/$articleId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
        verify(exactly = 1) { articleService.updateArticle(articleId, dto) }
    }

    @Test
    fun should_204_response_when_delete_article_request() {
        val articleId = faker.number().randomNumber()
        justRun { articleService.deleteById(articleId) }

        mockMvc.perform(
            delete("/articles/$articleId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)
        verify(exactly = 1) { articleService.deleteById(articleId) }
    }

    @Test
    fun should_200_response_when_get_recent_articles() {
        every { articleService.getRecentArticles() } returns emptyList()

        mockMvc.perform(
            get("/articles/recent")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }
}
