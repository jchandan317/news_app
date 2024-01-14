package com.practise.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practise.newsapp.NewsApplication
import com.practise.newsapp.R
import com.practise.newsapp.adapters.NewsAdapter
import com.practise.newsapp.db.ArticleDatabase
import com.practise.newsapp.repositories.NewsRepository
import com.practise.newsapp.ui.NewsViewModelProviderFactory
import com.practise.newsapp.ui.util.Constants.Companion.QUERY_PAGE_SIZE
import com.practise.newsapp.ui.util.Resource
import com.practise.newsapp.ui.viewmodel.BreakingNewsViewModel

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: BreakingNewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(activity?.application as NewsApplication, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[BreakingNewsViewModel::class.java]

        setupRecyclerView()

        newsAdapter.setOnItemClickListener{
               val bundle = Bundle().apply {
                   putSerializable("article", it)
               }

            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)
        }

        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                        println("Chandan---------Total results :: " + it.totalResults)
                        val totalPages = it.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages

                        if (isLastPage) {
                            this@BreakingNewsFragment.view?.findViewById<RecyclerView>(R.id.rvBreakingNews)?.setPadding(0,0,0,0)
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Log.e("BreakingNewsFrag", "An Error occurred : ${it}")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    var isLoading = false
    var isScrolling = false
    var isLastPage = false

    private var onItemScroll = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPos = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPos + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPos > 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            println(" " + isNotLoadingAndNotLastPage + " " + isAtLastItem + " " + isNotAtBeginning
                    + " " + isTotalMoreThanVisible + " " + isScrolling)
            println(shouldPaginate)
            if (shouldPaginate) {
                viewModel.getBreakingNews("IN")
                isScrolling =false
            }
        }
    }

    private fun hideProgressBar() {
        this.view?.findViewById<ProgressBar>(R.id.paginationProgressBar)?.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        this.view?.findViewById<ProgressBar>(R.id.paginationProgressBar)?.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()

        this.view?.findViewById<RecyclerView>(R.id.rvBreakingNews)?.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.onItemScroll)
        }
    }
}