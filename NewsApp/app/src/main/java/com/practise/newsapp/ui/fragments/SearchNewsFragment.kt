package com.practise.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
import com.practise.newsapp.ui.util.Constants
import com.practise.newsapp.ui.util.Resource
import com.practise.newsapp.ui.viewmodel.SearchNewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    lateinit var viewModel: SearchNewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(activity?.application as NewsApplication, newsRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[SearchNewsViewModel::class.java]

        setupRecyclerView()

        newsAdapter.setOnItemClickListener{
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment, bundle)
        }

        var job: Job? = null

        view.findViewById<EditText>(R.id.etSearch)?.addTextChangedListener {
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                it.let {
                    if (it.toString().isNotEmpty() && it.toString().length > 3) {
                        viewModel.searchNews(it.toString())
                    }
                }
            }
        }

        /*view.findViewById<EditText>(R.id.etSearch).setOnKeyListener(object : OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event?.action == KEYCODE_ENTER && (v as EditText).text.toString()
                        .isNotEmpty()
                ) {
                    viewModel.searchNews(v.text.toString())
                }
                return true
            }
        })*/

        viewModel.searchNews.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    it.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                        println("Chandan---------Total results :: " + it.totalResults)
                        val totalPages = it.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages

                        if (isLastPage) {
                            this@SearchNewsFragment.view?.findViewById<RecyclerView>(R.id.rvBreakingNews)?.setPadding(0,0,0,0)
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let {
                        Log.e("BreakingNewsFrag", "An Error occurred : ${it}")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })


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

        this.view?.findViewById<RecyclerView>(R.id.rvSearchNews)?.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.onItemScroll)
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
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            println(" " + isNotLoadingAndNotLastPage + " " + isAtLastItem + " " + isNotAtBeginning
                    + " " + isTotalMoreThanVisible + " " + isScrolling)

            println(shouldPaginate)
            if (shouldPaginate) {
                viewModel.searchNews("IN")
                isScrolling =false
            }

        }
    }
}