package com.rohitjakhar.hashpass.presention.password_details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rohitjakhar.hashpass.R
import com.rohitjakhar.hashpass.data.model.PasswordModel
import com.rohitjakhar.hashpass.databinding.FragmentPasswordDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordDetailsFragment : Fragment() {
    private var _binding: FragmentPasswordDetailsBinding? = null
    private val binding get() = _binding!!
    private val navArgs: PasswordDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
    }

    private fun buildPasswordShare(passwordModel: PasswordModel): String {
        val passwordBuilder = StringBuilder()
        with(passwordModel) {
            passwordBuilder.append("Title: $title")
            passwordBuilder.append("\nUrl: $url")
            passwordBuilder.append("\nPassword: $passwordHash")
            if (email != null && email.isNotEmpty()) passwordBuilder.append("\nEmail: $email")
            if (userName != null && userName.isNotEmpty()) passwordBuilder.append("\nUser Name: $userName")
            if (description != null && description.isNotEmpty()) passwordBuilder.append("\nDescription: $description")
            if (securityQuestion != null && securityQuestion.isNotEmpty()) passwordBuilder.append("\nSecurity Question: $securityQuestion")
            if (securityAnswer != null && securityAnswer.isNotEmpty()) passwordBuilder.append("\nSecurity Answer: $securityAnswer")
            if (remarks != null && remarks.isNotEmpty()) passwordBuilder.append("\nRemarks: $remarks")
            passwordBuilder.append("\nDownload HashPass for saving password at free!\nLink:- https://play.google.com/store/apps/details?id=com.rohit.hashpass")
        }
        return passwordBuilder.toString()
    }

    private fun initClick() = binding.apply {
        val passwordDetails = navArgs.passwordDetails
        floatingShare.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    buildPasswordShare(passwordDetails)
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Password")
            requireContext().startActivity(shareIntent)
        }
    }

    private fun initView() = binding.apply {
        navArgs.passwordDetails.also {
            tvTitle.text = it.title
            tvUrl.text = it.url
            tvEmail.text = it.email ?: "Null"
            tvUserName.text = it.userName ?: "Null"
            tvPassword.text = it.passwordHash
            tvDescription.text = it.description ?: "Null"
            tvSecurityQuestion.text = it.securityQuestion ?: "Null"
            tvSecurtiyAnswer.text = it.securityAnswer ?: "Null"
            tvRemarks.text = it.remarks ?: "Null"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit -> {
                findNavController().navigate(
                    PasswordDetailsFragmentDirections.actionPasswordDetailsFragmentToNavAddPassword(
                        passwordDetails = navArgs.passwordDetails
                    )
                )
                true
            }
            else -> {
                false
            }
        }
    }
}
