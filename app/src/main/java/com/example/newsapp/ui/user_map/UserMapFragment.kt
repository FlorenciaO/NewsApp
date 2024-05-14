package com.example.newsapp.ui.user_map

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
import com.example.newsapp.databinding.FragmentUserMapBinding
import com.example.newsapp.ui.callback.ToolbarListener
import com.example.newsapp.utils.ErrorCode
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserMapFragment : Fragment() {
    private var _binding: FragmentUserMapBinding? = null
    private val binding get() = _binding!!
    private val userMapViewModel: UserMapViewModel by viewModels()

    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserMapBinding.inflate(inflater, container, false)

        (activity as? ToolbarListener)?.showToolbar()

        with(binding.mapView) {
            onCreate(savedInstanceState)
            getMapAsync { map = it }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED) {
                userMapViewModel.userAddressState.collectLatest { state ->
                    state.address?.let {
                        setUpMap(it.lat, it.lng)
                        setUpTextViews(it.street, it.city)
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

    private fun setUpMap(lat: Double, lng: Double) {
        val latLng = LatLng(lat, lng)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        map?.let {
            it.clear()
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            it.addMarker(markerOptions)
        }
        binding.mapView.visibility = View.VISIBLE
    }

    private fun setUpTextViews(street: String, city: String) {
        with(binding) {
            val username: String? = arguments?.getString("username")
            userTv.text = resources.getString(R.string.user_name_title, username)
            addressTv.text = resources.getString(R.string.user_address_subtitle, street, city)
            clInnerLayout.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
