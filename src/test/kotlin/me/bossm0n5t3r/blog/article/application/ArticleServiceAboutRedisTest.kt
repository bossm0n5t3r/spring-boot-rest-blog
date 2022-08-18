package me.bossm0n5t3r.blog.article.application

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.verify
import me.bossm0n5t3r.blog.article.application.dto.CreateArticleDto
import me.bossm0n5t3r.blog.article.domain.Article
import me.bossm0n5t3r.blog.article.domain.ArticleRepository
import me.bossm0n5t3r.blog.article.presentation.dto.ArticleDto
import me.bossm0n5t3r.blog.common.AbstractRedisTest
import me.bossm0n5t3r.blog.common.CommonUtil
import me.bossm0n5t3r.blog.common.configuration.Constants.RecentArticles
import me.bossm0n5t3r.blog.common.exception.ErrorMessage
import me.bossm0n5t3r.blog.common.exception.InvalidException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import javax.annotation.PostConstruct

internal class ArticleServiceAboutRedisTest : AbstractRedisTest() {
    private val articleRepository = mockkClass(ArticleRepository::class)
    private lateinit var sut: ArticleService
    private val faker = CommonUtil.faker

    @PostConstruct
    fun construct() {
        // @Autowired stringRedisTemplate 을 생성 시에 주입받아야 하므로,
        // 테스트 실행하기 전에 PostConstruct 해줍니다.
        sut = ArticleService(articleRepository, stringRedisTemplate, objectMapper)
    }

    @AfterEach
    fun clearMock() {
        clearAllMocks()
    }

    private fun getDummyArticles(size: Int): List<Article> {
        return (1..size).map {
            Article(faker.lorem().characters(), faker.lorem().characters())
        }
    }

    @Test
    fun should_throw_InvalidException_if_subject_is_blank_when_create_article() {
        // given
        val blankSubjectDto = CreateArticleDto(" ".repeat((1..9).random()), faker.lorem().characters())

        // when, then
        assertThrows<InvalidException> {
            sut.createArticle(blankSubjectDto)
        }
            .also {
                assertThat(it.message).isEqualTo(ErrorMessage.SUBJECT_IS_BLANK.message)
            }
        verify(exactly = 0) { articleRepository.save(any()) }
    }

    @Test
    fun should_throw_InvalidException_if_content_is_empty_when_create_article() {
        // given
        val emptyContentDto = CreateArticleDto(faker.lorem().characters(), "")

        // when, then
        assertThrows<InvalidException> {
            sut.createArticle(emptyContentDto)
        }
            .also {
                assertThat(it.message).isEqualTo(ErrorMessage.CONTENT_IS_BLANK.message)
            }
        verify(exactly = 0) { articleRepository.save(any()) }
    }

    @Test
    fun should_create_article_when_not_exist_in_redis() {
        // given
        val subject = faker.lorem().characters()
        val content = faker.lorem().characters()
        val article = Article(subject, content)
        every { articleRepository.save(article) } returns article
        val readOpsForList = stringRedisTemplate.opsForList()
        assertThat(readOpsForList.range(RecentArticles.RedisKey, 0, -1))
            .isNotNull.isEmpty()

        // when
        assertDoesNotThrow { sut.createArticle(CreateArticleDto(subject, content)) }

        // then
        verify(exactly = 1) { articleRepository.save(article) }
        assertThat(readOpsForList.range(RecentArticles.RedisKey, 0, -1))
            .isNotNull.isNotEmpty.hasSize(1)
    }

    @Test
    fun should_create_article_when_exist_maximum_in_redis() {
        // given
        val subject = faker.lorem().characters()
        val content = faker.lorem().characters()
        val article = Article(subject, content)
        every { articleRepository.save(article) } returns article
        val readOpsForList = stringRedisTemplate.opsForList()
        getDummyArticles(RecentArticles.MAX_RECENT_ARTICLES_COUNT)
            .also {
                readOpsForList.rightPushAll(
                    RecentArticles.RedisKey,
                    it.map { article -> objectMapper.writeValueAsString(article.toDto()) },
                )
            }
        assertThat(readOpsForList.range(RecentArticles.RedisKey, 0, -1))
            .isNotNull.isNotEmpty.hasSize(RecentArticles.MAX_RECENT_ARTICLES_COUNT)

        // when
        assertDoesNotThrow { sut.createArticle(CreateArticleDto(subject, content)) }

        // then
        verify(exactly = 1) { articleRepository.save(article) }
        val recentArticlesInRedis =
            readOpsForList.range(RecentArticles.RedisKey, 0, -1) ?: emptyList()

        assertThat(recentArticlesInRedis).isNotNull.isNotEmpty.hasSize(RecentArticles.MAX_RECENT_ARTICLES_COUNT)
        val newArticle = Article(CreateArticleDto(subject, content)).toDto()
        assertThat(objectMapper.readValue(recentArticlesInRedis.first(), ArticleDto::class.java))
            .isEqualTo(newArticle)
    }

    @Test
    fun should_get_recent_articles_from_db_when_not_exist_in_redis() {
        // given
        val readOpsForList = stringRedisTemplate.opsForList()
        val dummyArticles = getDummyArticles(3)
        every { articleRepository.findAllByOrderByCreatedAtDesc() } returns dummyArticles
        assertThat(readOpsForList.range(RecentArticles.RedisKey, 0, -1))
            .isNotNull
            .isEmpty()

        // when
        val result = sut.getRecentArticles()

        // then
        assertThat(result).isNotEmpty.hasSizeLessThanOrEqualTo(RecentArticles.MAX_RECENT_ARTICLES_COUNT)
        assertThat(result.map { it.subject })
            .containsExactlyElementsOf(dummyArticles.map { it.subject })
        assertThat(result.map { it.content })
            .containsExactlyElementsOf(dummyArticles.map { it.content })
        verify(exactly = 1) { articleRepository.findAllByOrderByCreatedAtDesc() }
        assertThat(readOpsForList.range(RecentArticles.RedisKey, 0, -1)).isNotNull
    }

    @Test
    fun should_get_recent_articles_from_redis_when_exist_in_redis() {
        // given
        val readOpsForList = stringRedisTemplate.opsForList()
        val dummyArticles = getDummyArticles(3)
            .also {
                readOpsForList.rightPushAll(
                    RecentArticles.RedisKey,
                    it.map { article -> objectMapper.writeValueAsString(article.toDto()) },
                )
            }
        assertThat(readOpsForList.range(RecentArticles.RedisKey, 0, -1))
            .isNotNull.isNotEmpty.hasSize(dummyArticles.size)

        // when
        val result = sut.getRecentArticles()

        // then
        assertThat(result).isNotEmpty.hasSizeLessThanOrEqualTo(RecentArticles.MAX_RECENT_ARTICLES_COUNT)
        assertThat(result.map { it.subject })
            .containsExactlyElementsOf(dummyArticles.map { it.subject })
        assertThat(result.map { it.content })
            .containsExactlyElementsOf(dummyArticles.map { it.content })
        verify(exactly = 0) { articleRepository.findAllByOrderByCreatedAtDesc() }
    }
}
