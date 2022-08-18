package me.bossm0n5t3r.blog.article.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    fun findAllByOrderByCreatedAtDesc(): List<Article>
}
