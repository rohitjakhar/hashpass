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
import com.rohitjakhar.hashpass.data.model.PasswordModel
import com.rohitjakhar.hashpass.databinding.FragmentHomeBinding
import com.rohitjakhar.hashpass.utils.ErrorType
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.hide
import com.rohitjakhar.hashpass.utils.loadingView
import com.rohitjakhar.hashpass.utils.show
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeVM>()
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }
    private val passwordModelList = mutableSetOf<PasswordModel>()
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
        initClick()
    }

    private fun initClick() = binding.apply {
        btnTryAgain.setOnClickListener {
            viewModel.getPasswordList()
            collectData()
        }
    }

    private fun handleSearch() = binding.apply {
        searchViewPassword.setOnApplySearchListener {
            Log.d("test", "clicked: $it")
        }
        searchViewPassword.setOnSearchListener { changedText ->
            passwordAdapter.submitList(
                passwordModelList.filter { passwordModel ->
                    passwordModel.title == changedText || passwordModel.email == changedText || passwordModel.description == changedText || passwordModel.userName == changedText
                }
            )
        }
        searchViewPassword.initToggleListener {
            if (it) {
                Log.d("test", "true ")
            } else {
                passwordAdapter.currentList.clear()
                passwordAdapter.submitList(passwordModelList.toList())
                Log.d("test", "false")
            }
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
                        binding.tvErrorMessage.show()
                        binding.btnTryAgain.show()
                        binding.rvPassword.hide()
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
                        binding.tvErrorMessage.hide()
                        binding.btnTryAgain.hide()
                        binding.rvPassword.show()
                        loadingView.show()
                    }
                    is Resource.Sucess -> {
                        binding.tvErrorMessage.hide()
                        binding.btnTryAgain.hide()
                        binding.rvPassword.show()
                        loadingView.dismiss()
                        passwordModelList.addAll(it.data!!)
                        passwordAdapter.submitList(passwordModelList.toList())
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
