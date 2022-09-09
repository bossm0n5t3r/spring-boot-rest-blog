package me.bossm0n5t3r.blog.common.configuration

object Constants {
    object RecentArticles {
        const val RedisKey = "blog-recent-articles"
        const val MAX_RECENT_ARTICLES_COUNT = 20
    }

    object Kafka {
        const val TOPIC_BLOG_MESSAGE = "blog-message"
    }
}
