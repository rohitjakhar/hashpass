package com.rohitjakhar.hashpass.presention.edit_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.rohitjakhar.hashpass.data.model.UserDetailsModel
import com.rohitjakhar.hashpass.databinding.FragmentEditProfileBinding
import com.rohitjakhar.hashpass.utils.loadingView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<EditProfileVM>()
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
