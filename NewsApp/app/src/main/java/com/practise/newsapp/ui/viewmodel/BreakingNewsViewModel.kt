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

@Suppress("SameParameterValue")
class BreakingNewsViewModel(
    application: NewsApplication,
    private val newsRepository: NewsRepository
): AndroidViewModel(application) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    private var breakingNewsResponse: NewsResponse? = null

    init {
        getBreakingNews(countryCode = "IN")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse!!.articles
                    val newsArticles = resultResponse.articles
                    oldArticles.addAll(newsArticles)
                }
                return Resource.Success(breakingNewsResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (Utility.isInternetAvailable(getApplication())) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No internet connection!!!"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue((Resource.Error("Conversion Error")))
            }
        }
    }
}