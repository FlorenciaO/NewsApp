package com.example.newsapp.ui.news

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.ViewNewsItemBinding
import com.example.newsapp.domain.model.News
import com.example.newsapp.ui.news.NewsAdapter.NewsViewHolder
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class NewsAdapter(
    private val picasso: Picasso,
    private val listener: NewsAdapterListener,
    private var list: MutableList<News> = mutableListOf()
) : RecyclerView.Adapter<NewsViewHolder>() {

    interface NewsAdapterListener {
        fun onNewsItemClicked(newsId: String)
    }

    inner class NewsViewHolder(private val binding: ViewNewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            val resources: Resources = binding.root.context.resources
            with(binding) {
                with(news) {
                    if (image.isNotBlank() && image.isNotEmpty()) {
                        picasso.load(image)
                            .error(R.drawable.no_image_placeholder)
                            .transform(RoundedCornersTransformation(20, 0))
                            .fit()
                            .into(imageView)
                    }
                    titleTv.text = title
                    usernameTimeAgoTv.text = resources.getString(
                        R.string.news_username_time_ago,
                        byUser,
                        updatedAt
                    )
                    root.setOnClickListener {
                        listener.onNewsItemClicked(id)
                    }
                }
            }
        }
    }

    fun setNewList(newList: List<News>) {
        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                areItemsTheSame(list[oldItemPosition], newList[newItemPosition])

            override fun getOldListSize() = list.size

            override fun getNewListSize() = newList.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                areContentsTheSame(list[oldItemPosition], newList[newItemPosition])
        })

        list = newList.toMutableList()
        result.dispatchUpdatesTo(this)
    }

    private fun areItemsTheSame(oldNews: News, news: News): Boolean {
        return oldNews.id == news.id
    }

    private fun areContentsTheSame(oldNews: News, news: News): Boolean {
        return oldNews == news
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ViewNewsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
