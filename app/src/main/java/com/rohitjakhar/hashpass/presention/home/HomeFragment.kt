package com.rohitjakhar.hashpass.presention.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rohitjakhar.hashpass.databinding.FragmentHomeBinding
import com.rohitjakhar.hashpass.utils.ErrorType
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectData()
    }

    private fun collectData() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.passwordListState.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        when (it.errorType) {
                            ErrorType.UNKNOWN -> {
                                toast("Unknown Error")
                            }
                            ErrorType.EMPTY_DATA -> {
                                toast("Empty List")
                            }
                        }
                    }
                    is Resource.Loading -> {
                        toast("Loading")
                    }
                    is Resource.Sucess -> {
                        Log.d("test", "data list: ${it.data}")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
