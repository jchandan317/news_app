package com.practise.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.practise.newsapp.NewsApplication
import com.practise.newsapp.R
import com.practise.newsapp.db.ArticleDatabase
import com.practise.newsapp.repositories.NewsRepository
import com.practise.newsapp.ui.NewsViewModelProviderFactory
import com.practise.newsapp.ui.viewmodel.ArticleViewModel

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: ArticleViewModel
    private val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("Chandan", "ArticleFragment $args")

        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(activity?.application as NewsApplication, newsRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(ArticleViewModel::class.java)

        val article = args.article

        view.findViewById<WebView>(R.id.webView).apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}