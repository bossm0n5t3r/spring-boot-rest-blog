package me.bossm0n5t3r.blog.article.application

import io.mockk.every
import io.mockk.mockkClass
import io.mockk.verify
import me.bossm0n5t3r.blog.article.domain.Article
import me.bossm0n5t3r.blog.article.domain.ArticleRepository
import me.bossm0n5t3r.blog.common.AbstractRedisTest
import me.bossm0n5t3r.blog.common.CommonUtil
import me.bossm0n5t3r.blog.common.configuration.Constants.RecentArticles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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

    @Test
    fun should_get_recent_articles_from_db_when_not_exist_in_redis() {
        // given
        val readOpsForList = stringRedisTemplate.opsForList()
        val dummyArticles = (1..3).map {
            Article(faker.lorem().characters(), faker.lorem().characters())
        }
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
        val dummyArticles = (1..3).map {
            Article(faker.lorem().characters(), faker.lorem().characters())
        }.also {
            readOpsForList.rightPushAll(
                RecentArticles.RedisKey,
                it.map { article -> objectMapper.writeValueAsString(article.toDto()) },
            )
        }
        assertThat(readOpsForList.range(RecentArticles.RedisKey, 0, -1)).isNotNull.isNotEmpty

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
