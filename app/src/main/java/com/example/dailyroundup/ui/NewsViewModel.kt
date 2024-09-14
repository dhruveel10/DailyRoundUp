package com.example.dailyroundup.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailyroundup.models.Article
import com.example.dailyroundup.models.NewsResponse
import com.example.dailyroundup.repository.NewsRepository
import com.example.dailyroundup.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Query
import java.util.Locale.IsoCountryCode

class NewsViewModel (app: Application, val newsRepository: NewsRepository): AndroidViewModel(app){

    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage = 1
    var headlinesResponse: NewsResponse?= null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse?= null
    var newSearchQuery: String?= null
    var oldSearchQuery: String ?= null

    init {
        getHeadlines("us")
    }

    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlines.postValue(handleHeadlinesResponse(newsRepository.getHeadlines(countryCode, headlinesPage)))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(handleSearchNewsResponse(newsRepository.searchForNews(searchQuery, searchNewsPage)))
    }

    private fun handleHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
         if(response.isSuccessful){
             response.body()?.let { resultResponse ->
                 headlinesPage++
                 if(headlinesResponse == null){
                     headlinesResponse = resultResponse
                 }
                 else{
                     val oldArticles = headlinesResponse?.articles
                     val newArticles = resultResponse.articles
                     oldArticles?.addAll(newArticles)
                 }
                 return Resource.Success(headlinesResponse?: resultResponse)
             }
         }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                if(searchNewsResponse == null || newSearchQuery != oldSearchQuery){
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                }
                else{
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun addToFavorites(article: Article) = viewModelScope.launch{
        newsRepository.upsert(article)
    }

    fun getFavoriteNews() = newsRepository.getFavoriteNews()

    fun deleteArticle(article: Article) = viewModelScope.launch{
        newsRepository.deleteArticle(article)
    }

//    fun internetConnection(context: Context): Boolean {
//        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//
//        return networkCapabilities?.run {
//            when {
//                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//                else -> false
//            }
//        } ?: false
//    }

//    private suspend fun headlinesInternet(countryCode: String){
//        headlines.postValue(Resource.Loading())
//        try {
//            if (inter)
//        }
//    }
}