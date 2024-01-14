package com.practise.newsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practise.newsapp.models.Article
import com.practise.newsapp.repositories.NewsRepository
import kotlinx.coroutines.launch

class SavedNewsViewModel(
    val newsRepository: NewsRepository
): ViewModel() {

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews()  = newsRepository.getSavedArticles()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }
}