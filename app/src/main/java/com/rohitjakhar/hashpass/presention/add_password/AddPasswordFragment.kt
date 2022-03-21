package com.rohitjakhar.hashpass.presention.add_password

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rohitjakhar.hashpass.data.model.PasswordModel
import com.rohitjakhar.hashpass.databinding.FragmentAddPasswordBinding
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.getText
import com.rohitjakhar.hashpass.utils.loadingView
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class AddPasswordFragment : Fragment() {
    private var _binding: FragmentAddPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AddPasswordVM>()
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
    }

    private fun initView() = binding.apply {
        collectCategory()
    }

    private fun collectCategory() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addPasswordStatus
        }
    }

    private fun initClick() = binding.apply {
        btnAddPassword.setOnClickListener {
            if (invalidate()) {
                collectAddPasswordState()
                viewModel.addPassword(
                    PasswordModel(
                        email = inputEmail.getText(),
                        title = inputTitle.getText(),
                        url = inputUrl.getText(),
                        passwordHash = inputUrl.getText(),
                        securityQuestion = inputSecurityQuesetion.getText(),
                        securityAnswer = inputSecurityQuesetion.getText(),
                        description = inputDescription.getText(),
                        remarks = inputRemarks.getText(),
                        createdAt = System.currentTimeMillis(),
                        userName = inputUserName.getText(),
                        uuid = UUID.randomUUID().toString(),
                        userId = ""
                    )
                )
            }
        }
    }

    private fun invalidate(): Boolean {
        binding.apply {
            when {
                inputTitle.editText == null || inputTitle.editText!!.length() < 1 -> {
                    inputTitle.error = "Write Title"
                    return false
                }
                inputUrl.editText == null || inputUrl.editText!!.length() < 1 -> {
                    inputUrl.error = "Write URL"
                    return false
                }
                inputPassword.editText == null || inputPassword.editText!!.length() < 1 -> {
                    inputPassword.error = "Write Password"
                    return false
                }
                else -> {
                    return true
                }
            }
        }
    }

    private fun collectAddPasswordState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addPasswordStatus.collectLatest {
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
