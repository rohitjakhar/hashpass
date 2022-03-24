package com.rohitjakhar.hashpass.presention.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.rohitjakhar.hashpass.databinding.FragmentSettingBinding
import com.rohitjakhar.hashpass.presention.AuthActivity
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.loadingView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SettingVM>()
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        viewModel.getNotification()
        viewModel.getUserDetails()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
        collectNotification()
        collectUserDetails()
    }

    private fun collectUserDetails() = binding.apply {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userDetails.collectLatest {
                if (it != null) {
                    val avatarDrawable = AvatarGenerator.avatarImage(
                        requireContext(),
                        150,
                        AvatarConstants.CIRCLE,
                        it.name,
                        AvatarConstants.COLOR900
                    )
                    ivUserImage.load(it.userImage) {
                        placeholder(
                            avatarDrawable
                        )
                        error(
                            avatarDrawable
                        )
                        crossfade(enable = true)
                        crossfade(500)
                    }
                    tvUserName.text = it.name
                    tvUserEmail.text = it.email
                }
            }
        }
    }

    private fun collectNotification() = binding.apply {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.notificationState.collectLatest {
                switchNotification.isChecked = it
            }
        }
    }

    private fun initClick() = binding.apply {
        cvSignOut.setOnClickListener {
            collectLogoutState()
            viewModel.logout()
        }
        cvDeleteAccount.setOnClickListener {
        }
        switchNotification.setOnCheckedChangeListener { compoundButton, isChecked ->
            viewModel.changeNotification(isChecked)
        }
        cvUpdateProfile.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionNavSettingToEditProfileFragment())
        }
    }

    private fun collectLogoutState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.logoutState.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        loadingView.dismiss()
                    }
                    is Resource.Loading -> {
                        loadingView.show()
                    }
                    is Resource.Sucess -> {
                        loadingView.dismiss()
                        requireActivity().apply {
                            startActivity(Intent(requireActivity(), AuthActivity::class.java))
                            finish()
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
