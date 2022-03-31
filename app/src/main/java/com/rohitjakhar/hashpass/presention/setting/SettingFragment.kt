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
import com.rohitjakhar.hashpass.utils.openInBrowser
import com.rohitjakhar.hashpass.utils.optionDialog
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
                switchNotification.isOn = it
            }
        }
    }

    private fun initClick() = binding.apply {
        cvSignOut.setOnClickListener {
            requireContext().optionDialog("Are you want to logout!", { yesClick ->
                yesClick.dismiss()
                collectLogoutState()
                viewModel.logout()
            }, { noClick ->
                noClick.dismiss()
            })
        }
        cvDeleteAccount.setOnClickListener {
            requireContext().optionDialog("Are you want to delete your account!", { yesClick ->
                yesClick.dismiss()
                collectLogoutState()
                viewModel.deleteAccount()
            }, { noClick ->
                noClick.dismiss()
            })
        }
        switchNotification.setOnToggledListener { toggleableView, isOn ->
            viewModel.changeNotification(isOn)
        }
        cvUpdateProfile.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionNavSettingToEditProfileFragment())
        }
        cvPrivacy.setOnClickListener {
            requireActivity().openInBrowser("https://sites.google.com/view/hash-pass-privacy/")
        }
        cvAboutHasura.setOnClickListener {
            requireActivity().openInBrowser("https://hasura.io/")
        }
        cvAbout.setOnClickListener {
            requireActivity().openInBrowser("https://rohitjakhar.me/")
        }
        floatingShareApp.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Download HashPass App for saving password : https://play.google.com/store/apps/details?id=com.rohit.hashpass"
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share App")
            requireContext().startActivity(shareIntent)
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
