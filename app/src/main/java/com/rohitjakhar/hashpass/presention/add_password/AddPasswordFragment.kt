package com.rohitjakhar.hashpass.presention.add_password

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rohitjakhar.hashpass.data.model.PasswordModel
import com.rohitjakhar.hashpass.databinding.FragmentAddPasswordBinding
import com.rohitjakhar.hashpass.utils.PasswordStrength
import com.rohitjakhar.hashpass.utils.Resource
import com.rohitjakhar.hashpass.utils.getText
import com.rohitjakhar.hashpass.utils.loadingView
import com.rohitjakhar.hashpass.utils.messageDialog
import com.rohitjakhar.hashpass.utils.setText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class AddPasswordFragment : Fragment() {
    private var _binding: FragmentAddPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AddPasswordVM>()
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }
    private val navArgs: AddPasswordFragmentArgs by navArgs()

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
        watchPasswordStrength()
        initView()
        initClick()
    }

    private fun initView() = binding.apply {
        navArgs.passwordDetails?.let {
            inputPassword.setText(it.passwordHash)
            inputDescription.setText(it.description)
            inputEmail.setText(it.email)
            inputRemarks.setText(it.remarks)
            inputSecurityAnswer.setText(it.securityAnswer)
            inputSecurityQuesetion.setText(it.securityQuestion)
            inputTitle.setText(it.title)
            inputUrl.setText(it.url)
            inputUserName.setText(it.userName)
        }
    }

    private fun watchPasswordStrength() {
        binding.inputPassword.editText?.addTextChangedListener { textt ->
            updatePasswordStrengthView(textt.toString())
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

        inputPassword.setEndIconOnClickListener {
            inputPassword.editText?.append(viewModel.generatePassword())
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
                    }
                    is Resource.Loading -> {
                        loadingView.show()
                    }
                    is Resource.Sucess -> {
                        loadingView.dismiss()
                        requireContext().messageDialog("Password Added Successfully!") {
                            it.dismiss()
                            findNavController().navigateUp()
                        }.show()
                    }
                }
            }
        }
    }

    private fun updatePasswordStrengthView(password: String) {
        if (TextUtils.isEmpty(password)) {
            binding.pbPasswordStrength.progress = 0
            return
        }
        val str = PasswordStrength.calculateStrength(password)
        binding.pbPasswordStrength.progressDrawable.setColorFilter(
            str.color,
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        when {
            str.getText(requireContext()) == "Weak" -> {
                binding.pbPasswordStrength.progress = 25
            }
            str.getText(requireContext()) == "Medium" -> {
                binding.pbPasswordStrength.progress = 50
            }
            str.getText(requireContext()) == "Strong" -> {
                binding.pbPasswordStrength.progress = 75
            }
            else -> {
                binding.pbPasswordStrength.progress = 100
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
