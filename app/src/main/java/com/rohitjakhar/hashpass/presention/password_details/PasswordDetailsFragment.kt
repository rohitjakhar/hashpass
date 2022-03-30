package com.rohitjakhar.hashpass.presention.password_details

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
