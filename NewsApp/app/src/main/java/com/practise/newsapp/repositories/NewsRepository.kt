package com.practise.newsapp.repositories

import com.practise.newsapp.api.RetrofitInstance
import com.practise.newsapp.db.ArticleDatabase
import com.practise.newsapp.models.Article
import com.practise.newsapp.models.NewsResponse
import retrofit2.Response

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
    }

    suspend fun searchNews(search: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInstance.api.searchForNews(search, pageNumber)
    }

    suspend fun upsert(article: Article) {
        db.getArticleDao().upsert(article)
    }

    suspend fun delete(article: Article) {
        db.getArticleDao().delete(article)
    }

    fun getSavedArticles() = db.getArticleDao().getAllArticle()

}