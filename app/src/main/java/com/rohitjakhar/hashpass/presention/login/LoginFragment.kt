package com.rohitjakhar.hashpass.presention.login

import android.app.Activity.RESULT_CANCELED
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.rohitjakhar.hashpass.R
import com.rohitjakhar.hashpass.databinding.FragmentLoginBinding
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginVM by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val googleSignResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_CANCELED) {
            result.data?.extras?.keySet()?.forEach {
            }
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
        btnGoogleLogin.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(resources.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            val signInIntent = googleSignInClient.signInIntent
            collectLogin()
            googleSignResult.launch(signInIntent)
        }
        btnLogin.setOnClickListener {
            // TODO: Invalidate User Details
            loginUser(
                inputLayoutEmail.editText!!.text.toString(),
                inputLayoutPassword.editText!!.text.toString()
            )
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
                        toast(it.message)
                    }
                    is Resource.Loading -> {
                        toast("Loading")
                    }
                    is Resource.Sucess -> {
                        toast("Login Success")
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
