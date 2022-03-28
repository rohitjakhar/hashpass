package com.rohitjakhar.hashpass.presention.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohitjakhar.hashpass.databinding.FragmentHomeBinding
import com.rohitjakhar.hashpass.utils.ErrorType
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.loadingView
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeVM>()
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }
    private val passwordAdapter by lazy {
        PasswordAdapter { passwordId ->
            findNavController().navigate(
                HomeFragmentDirections.actionNavHomeToPasswordDetailsFragment(
                    passwordId
                )
            )
        }
    }

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
        viewModel.getPasswordList()
        handleSearch()
        initPasswordRV()
        collectData()
    }

    private fun handleSearch() = binding.apply {
        searchViewPassword.setOnApplySearchListener {
            Log.d("test", "clicked: $it")
        }
        searchViewPassword.setOnSearchListener {
            Log.d("test", "change text: $it")
        }
        searchViewPassword.initToggleListener {
            Log.d("test", "is open: $it")
        }
    }

    private fun initPasswordRV() {
        binding.rvPassword.apply {
            adapter = passwordAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun collectData() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.passwordListState.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        loadingView.dismiss()
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
                        loadingView.show()
                    }
                    is Resource.Sucess -> {
                        loadingView.dismiss()
                        passwordAdapter.submitList(it.data)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingView.dismiss()
        _binding = null
    }
}
