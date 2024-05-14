package com.example.newsapp.ui.news_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentNewsDetailsBinding
import com.example.newsapp.domain.model.News
import com.example.newsapp.ui.callback.ToolbarListener
import com.example.newsapp.utils.ErrorCode
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsDetailsFragment : Fragment() {
    private var _binding: FragmentNewsDetailsBinding? = null
    private val binding get() = _binding!!
    private val newsDetailsViewModel: NewsDetailsViewModel by viewModels()

    @Inject
    lateinit var picasso: Picasso

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsDetailsBinding.inflate(inflater, container, false)

        (activity as? ToolbarListener)?.showToolbar()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED) {
                newsDetailsViewModel.newsDetailsState.collectLatest { state ->
                    state.details?.let {
                        setScreenView(it)
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

    private fun setScreenView(news: News) {
        with(binding) {
            clInnerLayout.visibility = View.VISIBLE
            with(news) {
                if (image.isBlank() || image.isEmpty()) {
                    newsImage.visibility = View.GONE
                } else {
                    picasso.load(image).transform(RoundedCornersTransformation(10, 10)).fit()
                        .into(newsImage, object : Callback {
                            override fun onSuccess() {
                                newsImage.visibility = View.VISIBLE
                            }

                            override fun onError(e: Exception?) {
                                newsImage.visibility = View.GONE
                            }
                        }
                        )
                }
                titleTv.text = title
                usernameTimeAgoTv.text = resources.getString(
                    R.string.news_username_time_ago,
                    byUser,
                    updatedAt
                )
                contentTv.text = content
            }
        }
    }

    private fun showLoading() {
        binding.circularProgressIndicator.visibility = View.VISIBLE
    }

    private fun finishLoading() {
        binding.circularProgressIndicator.visibility = View.INVISIBLE
    }

    private fun showError(errorCode: ErrorCode) {
        val errorMessageRes = when (errorCode) {
            ErrorCode.CONNECTION_ERROR -> R.string.connection_error
            else -> R.string.unexpected_error
        }

        Snackbar.make(binding.root, errorMessageRes, BaseTransientBottomBar.LENGTH_LONG).apply {
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            view.background = ContextCompat.getDrawable(context, R.drawable.rounded_shape)
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
