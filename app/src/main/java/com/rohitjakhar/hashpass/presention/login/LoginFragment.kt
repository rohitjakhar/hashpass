package com.rohitjakhar.hashpass.presention.login

import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.rohitjakhar.hashpass.presention.MainActivity
import com.rohitjakhar.hashpass.databinding.FragmentLoginBinding
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.loadingView
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginVM by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }
    private val googleSignResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_CANCELED) {
            loadingView.dismiss()
        }
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            if (task.isSuccessful) {
                loadingView.dismiss()
                val account = task.result
                account.idToken?.let {
                    viewModel.loginWithGoogle(it)
                }
            } else {
                loadingView.dismiss()
            }
        } catch (e: Exception) {
            loadingView.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
    }

    private fun initClick() = binding.apply {
        tvForgetPassword.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment())
        }
        tvRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }
        btnGoogleLogin.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(resources.getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            val signInIntent = googleSignInClient.signInIntent
            collectLogin()
            googleSignResult.launch(signInIntent)
        }
        btnLogin.setOnClickListener {
            // TODO: Invalidate User Details
            if (invalidate()) {
                loginUser(
                    inputLayoutEmail.editText!!.text.toString(),
                    inputLayoutPassword.editText!!.text.toString()
                )
            }
        }
    }

    private fun invalidate(): Boolean {
        binding.apply {
            return when {
                inputLayoutEmail.editText == null || inputLayoutEmail.editText!!.length() < 1 -> {
                    inputLayoutEmail.error = "Enter Email!"
                    false
                }
                inputLayoutPassword.editText == null || inputLayoutPassword.editText!!.length() < 1 -> {
                    inputLayoutPassword.error = "Enter Password!"
                    false
                }
                else -> true
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        collectLogin()
        viewModel.loginUser(email, password)
    }

    private fun collectLogin() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loginUser.collectLatest {
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
                        requireActivity().startActivity(
                            Intent(
                                requireActivity(),
                                MainActivity::class.java
                            )
                        ).apply {
                            requireActivity().finish()
                        }
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
