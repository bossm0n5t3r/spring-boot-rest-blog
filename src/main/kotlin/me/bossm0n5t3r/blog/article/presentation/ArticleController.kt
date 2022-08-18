package me.bossm0n5t3r.blog.article.presentation

import me.bossm0n5t3r.blog.article.application.ArticleService
import me.bossm0n5t3r.blog.article.application.dto.CreateArticleDto
import me.bossm0n5t3r.blog.article.application.dto.UpdateArticleDto
import me.bossm0n5t3r.blog.article.presentation.dto.ArticleDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleController(
    private val articleService: ArticleService
) {
    @GetMapping("/articles")
    fun getAllArticles(): List<ArticleDto> {
        return articleService.findAll()
    }

    @GetMapping("/articles/{articleId}")
    fun getArticle(@PathVariable articleId: Long): ArticleDto {
        return articleService.findById(articleId)
    }

    @PostMapping("/articles")
    fun createArticle(@RequestBody dto: CreateArticleDto): ResponseEntity<Unit> {
        articleService.createArticle(dto)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @PutMapping("/articles/{articleId}")
    fun updateArticle(@PathVariable articleId: Long, @RequestBody dto: UpdateArticleDto) {
        articleService.updateArticle(articleId, dto)
    }

    @DeleteMapping("/articles/{articleId}")
    fun deleteArticle(@PathVariable articleId: Long): ResponseEntity<Unit> {
        articleService.deleteById(articleId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/articles/recent")
    fun getRecentArticles(): List<ArticleDto> {
        return articleService.getRecentArticles()
    }
}
