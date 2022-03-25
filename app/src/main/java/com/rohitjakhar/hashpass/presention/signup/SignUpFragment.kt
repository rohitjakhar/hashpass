package com.rohitjakhar.hashpass.presention.signup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.rohitjakhar.hashpass.R
import com.rohitjakhar.hashpass.databinding.FragmentSignUpBinding
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.loadingView
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SignUpVM>()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }
    private var photoUri: Uri = Uri.EMPTY
    private var imageUrl: String = ""

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

    private val getUserPhoto =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_CANCELED) {
                toast("Unknown Error!")
            }
            if (result.resultCode == Activity.RESULT_OK) {
                photoUri = result.data?.data ?: Uri.EMPTY
            }
        }

    override fun onResume() {
        super.onResume()
        if (photoUri != Uri.EMPTY) {
            viewModel.uploadImage(photoUri)
            collectImageUpload()
            photoUri = Uri.EMPTY
        }
    }

    private fun collectImageUpload() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uploadImageUrl.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        loadingView.dismiss()
                    }
                    is Resource.Loading -> {
                        loadingView.show()
                    }
                    is Resource.Sucess -> {
                        imageUrl = it.data!!
                        binding.ivUploadPhoto.load(imageUrl) {
                            transformations(CircleCropTransformation())
                            error(R.drawable.ic_user)
                        }
                        loadingView.dismiss()
                    }
                }
            }
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
            if (invalidate()) {
                signUp(
                    inputLayoutEmail.editText!!.text.toString(),
                    inputLayoutPassword.editText!!.text.toString(),
                    inputLayoutName.editText!!.text.toString()
                )
            }
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

        ivUploadPhoto.setOnClickListener {
            val imageIntent = Intent()
            imageIntent.apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            getUserPhoto.launch(imageIntent)
        }
    }

    private fun invalidate(): Boolean {
        binding.apply {
            return when {
                inputLayoutEmail.editText == null || inputLayoutEmail.editText!!.length() < 1 -> {
                    inputLayoutEmail.error = "Enter Email!"
                    false
                }
                inputLayoutName.editText == null || inputLayoutName.editText!!.length() < 1 -> {
                    inputLayoutName.error = "Enter Email!"
                    false
                }
                inputLayoutPassword.editText == null || inputLayoutPassword.editText!!.length() < 1 -> {
                    inputLayoutPassword.error = "Enter Email!"
                    false
                }
                else -> true
            }
        }
    }

    private fun signUp(email: String, password: String, name: String) {
        collectSignUp()
        viewModel.signUpUser(
            email = email,
            password = password,
            name = name,
            profilePhoto = imageUrl
        )
    }

    private fun collectSignUp() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.signUpUser.collectLatest { resource ->
                when (resource) {
                    is Resource.Error -> {
                        loadingView.dismiss()
                        toast(resource.message)
                    }
                    is Resource.Loading -> {
                        loadingView.show()
                    }
                    is Resource.Sucess -> {
                        loadingView.dismiss()
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
