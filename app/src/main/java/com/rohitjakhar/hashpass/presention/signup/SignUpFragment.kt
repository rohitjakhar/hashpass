package com.rohitjakhar.hashpass.presention.signup

import android.app.Activity
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
    private lateinit var googleSignInClient: GoogleSignInClient
    private val googleSignResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            result.data?.extras?.keySet()?.forEach {
            }
        }
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            if (task.isSuccessful) {
                // loadingView.dismiss()
                val account = task.result
                account.idToken?.let {
                    viewModel.loginWithGoogle(it)
                }
            } else {
                //loadingView.dismiss()
            }
        } catch (e: Exception) {
            //loadingView.dismiss()
        }
    }

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
        btnSignUp.setOnClickListener {
            signUp(
                inputLayoutEmail.editText!!.text.toString(),
                inputLayoutPassword.editText!!.text.toString(),
                inputLayoutName.editText!!.text.toString()
            )
        }
        btnGoogleLogin.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(resources.getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            val signInIntent = googleSignInClient.signInIntent
            collectSignUp()
            googleSignResult.launch(signInIntent)
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
