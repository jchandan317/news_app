package com.practise.newsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practise.newsapp.models.Article
import com.practise.newsapp.repositories.NewsRepository
import kotlinx.coroutines.launch

class ArticleViewModel(
    val newsRepository: NewsRepository
) : ViewModel() {

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }
}