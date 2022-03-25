package com.rohitjakhar.hashpass.presention.edit_profile

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
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.rohitjakhar.hashpass.R
import com.rohitjakhar.hashpass.data.model.UserDetailsModel
import com.rohitjakhar.hashpass.databinding.FragmentEditProfileBinding
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.getText
import com.rohitjakhar.hashpass.utils.loadingView
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<EditProfileVM>()
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }
    private var photoUri: Uri = Uri.EMPTY
    private var imageUrl: String = ""
    private lateinit var userDetailsModel: UserDetailsModel

    private val getUserPhoto =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_CANCELED) {
                toast("Unknown Error!")
            }
            if (result.resultCode == Activity.RESULT_OK) {
                photoUri = result.data?.data ?: Uri.EMPTY
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        viewModel.getUserDetails()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectUserDetails()
        initClick()
    }

    private fun initClick() = binding.apply {
        ivUploadPhoto.setOnClickListener {
            val imageIntent = Intent()
            imageIntent.apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            getUserPhoto.launch(imageIntent)
        }

        btnSignUp.setOnClickListener {
            if (invalidate()) {
                viewModel.updateProfile(
                    userDetailsModel.copy(
                        name = inputLayoutName.getText(),
                        email = inputLayoutEmail.getText(),
                        userImage = imageUrl
                    )
                )
                collectUploadState()
            }
        }
    }

    private fun collectUploadState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.updateState.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        toast("Error: ${it.message}")
                        loadingView.dismiss()
                    }
                    is Resource.Loading -> {
                        loadingView.show()
                    }
                    is Resource.Sucess -> {
                        loadingView.dismiss()
                        toast("Updated!!!")
                    }
                }
            }
        }
    }

    private fun invalidate(): Boolean {
        binding.apply {
            return when {
                inputLayoutEmail.editText == null || inputLayoutEmail.editText!!.length() < 1 -> {
                    inputLayoutEmail.error = "Write Email"
                    false
                }
                inputLayoutName.editText == null || inputLayoutName.editText!!.length() < 1 -> {
                    inputLayoutName.error = "Write Name"
                    false
                }
                else -> true
            }
        }
    }

    private fun collectUserDetails() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userDetailsState.collectLatest {
                if (it != null) {
                    updateProfile(it)
                }
            }
        }
    }

    private fun updateProfile(userDetailsModel: UserDetailsModel) = binding.apply {
        ivUploadPhoto.load(userDetailsModel.userImage) {
            transformations(CircleCropTransformation())
            error(
                AvatarGenerator.avatarImage(
                    requireContext(),
                    AvatarConstants.COLOR400,
                    shape = AvatarConstants.CIRCLE,
                    userDetailsModel.name,
                    colorModel = AvatarConstants.COLOR400
                )
            )
            placeholder(
                AvatarGenerator.avatarImage(
                    requireContext(),
                    AvatarConstants.COLOR400,
                    shape = AvatarConstants.CIRCLE,
                    userDetailsModel.name,
                    colorModel = AvatarConstants.COLOR400
                )
            )
        }

        inputLayoutEmail.editText?.setText(userDetailsModel.email)
        inputLayoutName.editText?.setText(userDetailsModel.name)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
