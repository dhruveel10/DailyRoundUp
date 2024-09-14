package com.example.dailyroundup.repository

import androidx.room.Query
import com.example.dailyroundup.api.RetrofitInstance
import com.example.dailyroundup.db.ArticleDatabase
import com.example.dailyroundup.models.Article

class NewsRepository (val db: ArticleDatabase) {

    suspend fun getHeadlines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getHeadlines(countryCode, pageNumber)

    suspend fun searchForNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) =
        db.getArticleDao().upsert(article)

    suspend fun deleteArticle(article: Article) =
        db.getArticleDao().deleteArticle(article)

    fun getFavoriteNews() =
        db.getArticleDao().getAllArticles()

}