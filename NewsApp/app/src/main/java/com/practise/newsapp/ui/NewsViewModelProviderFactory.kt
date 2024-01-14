package com.practise.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practise.newsapp.NewsApplication
import com.practise.newsapp.repositories.NewsRepository
import com.practise.newsapp.ui.viewmodel.ArticleViewModel
import com.practise.newsapp.ui.viewmodel.BreakingNewsViewModel
import com.practise.newsapp.ui.viewmodel.SavedNewsViewModel
import com.practise.newsapp.ui.viewmodel.SearchNewsViewModel

@Suppress("UNCHECKED_CAST")
class NewsViewModelProviderFactory(
    private val application: NewsApplication,
    private val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BreakingNewsViewModel::class.java))
            BreakingNewsViewModel(application, newsRepository) as T
        else if (modelClass.isAssignableFrom(SearchNewsViewModel::class.java))
            SearchNewsViewModel(application, newsRepository) as T
        else if (modelClass.isAssignableFrom(SavedNewsViewModel::class.java))
            SavedNewsViewModel(newsRepository) as T
        else if (modelClass.isAssignableFrom(ArticleViewModel::class.java))
            ArticleViewModel(newsRepository) as T
        else
            throw IllegalArgumentException("ViewModel not found")
    }
}