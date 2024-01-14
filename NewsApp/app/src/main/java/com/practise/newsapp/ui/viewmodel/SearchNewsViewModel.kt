package com.practise.newsapp.ui.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practise.newsapp.NewsApplication
import com.practise.newsapp.models.NewsResponse
import com.practise.newsapp.repositories.NewsRepository
import com.practise.newsapp.ui.util.Resource
import com.practise.newsapp.ui.util.Utility
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class SearchNewsViewModel(
    application: NewsApplication,
    private val newsRepository: NewsRepository
) : AndroidViewModel(application) {

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                searchNewsPage++
                if (searchNewsResponse == null)
                    searchNewsResponse = it
                else {
                    val oldArticles = searchNewsResponse!!.articles
                    val newArticles = it.articles
                    oldArticles.addAll(newArticles)
                }
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (Utility.isInternetAvailable(getApplication())) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet connection!!!"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue((Resource.Error("Conversion Error")))
            }
        }
    }
}