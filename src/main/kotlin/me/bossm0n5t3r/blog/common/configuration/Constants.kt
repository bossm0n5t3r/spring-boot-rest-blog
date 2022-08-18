package me.bossm0n5t3r.blog.common.configuration

import java.time.Duration

object Constants {
    object RecentArticles {
        const val RedisKey = "blog-recent-articles"
        const val MAX_RECENT_ARTICLES_COUNT = 20
        val TIMEOUT: Duration = Duration.ofHours(3L)
    }
}
