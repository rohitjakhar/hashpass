package com.rohitjakhar.hashpass.presention.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rohitjakhar.hashpass.databinding.FragmentSignUpBinding
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SignUpVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
    }

    private fun initClick() = binding.apply {
        btnLogin.setOnClickListener {
            signUp(
                inputLayoutEmail.editText!!.text.toString(),
                inputLayoutPassword.editText!!.text.toString(),
                inputLayoutName.editText!!.text.toString()
            )
        }
        btnGoogleLogin.setOnClickListener {
        }
    }

    private fun signUp(email: String, password: String, name: String) {
        collectSignUp()
        viewModel.signUpUser(email = email, password = password, name = name)
    }

    private fun collectSignUp() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.signUpUser.collectLatest { resource ->
                when (resource) {
                    is Resource.Error -> {
                        toast(resource.message)
                    }
                    is Resource.Loading -> {
                        toast("Loading...")
                    }
                    is Resource.Sucess -> {
                        toast("Sign Up Success")
                    }
                }
            }
        }
    }

    private fun initView() = binding.apply {
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
