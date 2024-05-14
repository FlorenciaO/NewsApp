package com.example.newsapp.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentNewsBinding
import com.example.newsapp.ui.news.NewsAdapter.NewsAdapterListener
import com.example.newsapp.utils.ErrorCode
import com.example.newsapp.utils.ErrorCode.CONNECTION_ERROR
import com.example.newsapp.utils.ErrorCode.NO_COINCIDENCE
import com.google.android.material.R.id.snackbar_text
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsFragment : Fragment(), NewsAdapterListener {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val newsViewModel: NewsViewModel by viewModels()

    @Inject
    lateinit var picasso: Picasso

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)

        val newsAdapter = NewsAdapter(picasso, this)
        binding.newsList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = newsAdapter
        }

        setSearchView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED) {
                newsViewModel.newsListState.collectLatest { state ->
                    newsAdapter.setNewList(state.newsList)
                    if (state.newsList.isNotEmpty()) {
                        binding.noCoincidenceTv.visibility = View.GONE
                    }

                    if (state.isLoading) {
                        showLoading()
                    } else {
                        finishLoading()
                    }

                    state.error?.let { code ->
                        showError(code)
                    }
                }
            }

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNewsItemClicked(newsId: String) {
        findNavController().navigate(
            NewsFragmentDirections.actionNavigationNewsToNavigationNewsDetails(
                newsId
            )
        )
    }

    private fun setSearchView() {
        with(binding.searchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    clearFocus()
                    query?.let {
                        newsViewModel.onSearch(it)
                        return true
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        newsViewModel.resetList()
                        return true
                    }
                    return false
                }

            })
        }
    }

    private fun showLoading() {
        binding.circularProgressIndicator.visibility = View.VISIBLE
    }

    private fun finishLoading() {
        binding.circularProgressIndicator.visibility = View.INVISIBLE
    }

    private fun showError(errorCode: ErrorCode) {
        if (errorCode == NO_COINCIDENCE) {
            binding.noCoincidenceTv.visibility = View.VISIBLE
        } else {
            val errorMessageRes = when (errorCode) {
                CONNECTION_ERROR -> R.string.connection_error
                else -> R.string.unexpected_error
            }

            Snackbar.make(binding.root, errorMessageRes, BaseTransientBottomBar.LENGTH_LONG).apply {
                animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                view.findViewById<TextView>(snackbar_text).apply {
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                }
                view.background = ContextCompat.getDrawable(context, R.drawable.rounded_shape)
            }.show()
        }
    }
}
