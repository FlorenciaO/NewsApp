package com.example.newsapp.ui.users

import android.graphics.drawable.ShapeDrawable
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentUsersBinding
import com.example.newsapp.ui.users.UsersAdapter.UsersAdapterListener
import com.example.newsapp.utils.ErrorCode
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsersFragment : Fragment(), UsersAdapterListener {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private val usersViewModel: UsersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        val usersAdapter = UsersAdapter(this)
        binding.usersList.apply {
            val orientation = RecyclerView.VERTICAL
            val itemDecoration = DividerItemDecoration(context, orientation)
            val drawable = ShapeDrawable().apply {
                paint.color = ContextCompat.getColor(context, R.color.teal_200);
            }
            itemDecoration.setDrawable(drawable)
            addItemDecoration(itemDecoration)
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, orientation, false)
            adapter = usersAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED) {
                usersViewModel.usersListState.collectLatest { state ->
                    usersAdapter.setNewList(state.users)

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

    override fun onGoToMapClicked(userId: String, userName: String) {
        findNavController().navigate(
            UsersFragmentDirections.actionNavigationUsersToNavigationMap(
                userId,
                userName
            )
        )
    }
}
