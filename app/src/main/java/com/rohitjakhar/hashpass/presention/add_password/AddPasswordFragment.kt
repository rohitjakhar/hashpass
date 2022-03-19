package com.rohitjakhar.hashpass.presention.add_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rohitjakhar.hashpass.databinding.FragmentAddPasswordBinding
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddPasswordFragment : Fragment() {
    private var _binding: FragmentAddPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AddPasswordVM>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
    }

    private fun initView() = binding.apply {
    }

    private fun initClick() = binding.apply {
        btnAddPassword.setOnClickListener {
            if (invalidate()) {
                collectAddPasswordState()
                viewModel.addPassword()
            }
        }
    }

    private fun invalidate(): Boolean {
        return true
    }

    private fun collectAddPasswordState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addPasswordStatus.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        toast(it.message)
                    }
                    is Resource.Loading -> {
                        toast("Loading...")
                    }
                    is Resource.Sucess -> {
                        toast("Password Added")
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
