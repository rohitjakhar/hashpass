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
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ForgetPasswordFragment : Fragment() {
    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

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
            // TODO:Invalidate EmailID
            viewModel.resetPassword(inputLayoutEmail.editText!!.text.toString())
            collectForgetPassword()
        }
    }

    private fun collectForgetPassword() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        toast(it.message)
                    }
                    is Resource.Loading -> {
                        toast("Loading...")
                    }
                    is Resource.Sucess -> {
                        toast("Reset Password Email sent to your email address")
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }
}
