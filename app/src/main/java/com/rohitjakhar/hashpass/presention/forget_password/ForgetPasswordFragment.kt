package com.rohitjakhar.hashpass.presention.forget_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rohitjakhar.hashpass.databinding.FragmentForgetPasswordBinding
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.loadingView
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ForgetPasswordFragment : Fragment() {
    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }

    private val viewModel: ForgetPasswordVM by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    private fun initClick() = binding.apply {
        btnSendForgetEmail.setOnClickListener {
            if (invalidate()) {
                viewModel.resetPassword(inputLayoutEmail.editText!!.text.toString())
                collectForgetPassword()
            }
        }
    }

    private fun invalidate(): Boolean {
        return when {
            binding.inputLayoutEmail.editText == null || binding.inputLayoutEmail.editText!!.length() < 1 -> {
                binding.inputLayoutEmail.error = "Enter Email"
                false
            }
            else -> true
        }
    }

    private fun collectForgetPassword() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        loadingView.dismiss()
                        toast(it.message)
                    }
                    is Resource.Loading -> {
                        loadingView.show()
                    }
                    is Resource.Sucess -> {
                        loadingView.dismiss()
                        toast("Reset Password Email sent to your email address")
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }
}
