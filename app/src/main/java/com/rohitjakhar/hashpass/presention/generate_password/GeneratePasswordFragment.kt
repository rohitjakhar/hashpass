package com.rohitjakhar.hashpass.presention.generate_password

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rohitjakhar.hashpass.R
import com.rohitjakhar.hashpass.databinding.FragmentGeneratePasswordBinding
import com.rohitjakhar.hashpass.utils.PasswordStrength
import com.rohitjakhar.hashpass.utils.copyText
import com.rohitjakhar.hashpass.utils.getText
import com.rohitjakhar.hashpass.utils.loadingView
import com.rohitjakhar.hashpass.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GeneratePasswordFragment : Fragment() {
    private var _binding: FragmentGeneratePasswordBinding? = null
    private val binding get() = _binding!!
    private val loadingView by lazy { requireActivity().loadingView(cancelable = false) }
    private val viewModel by viewModels<GeneratePasswordVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneratePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFirstTime()
        initClick()
        passwordStrengthCollect()
    }

    private fun passwordStrengthCollect() {
        binding.inputPasswordCreated.editText?.addTextChangedListener {
            updatePasswordStrengthView(it.toString())
        }
    }

    private fun collectFirstTime() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isFirstTimePassword.collectLatest {
                if (it) {
                    binding.btnRefreshPassword.text = resources.getString(R.string.create_password)
                } else {
                    binding.btnRefreshPassword.text = resources.getString(R.string.refresh_password)
                }
            }
        }
    }

    private fun initClick() = binding.apply {
        btnRefreshPassword.setOnClickListener {
            loadingView.show()
            if (validation()) {
                viewModel.changeFirstTime(false)
                val passwordLength: Int =
                    inputPasswordLength.editText!!.text.toString().toIntOrNull() ?: 0
                val password = viewModel.generatePassword(
                    length = passwordLength,
                    includeNumbers = switchDigit.isOn,
                    includeSymbols = switchSymbol.isOn,
                    includeLowerCaseLetters = switchLowerLetter.isOn,
                    includeUpperCaseLetters = switchUpperLetter.isOn
                )
                inputPasswordCreated.editText!!.setText(password)
                loadingView.hide()
            }
        }

        btnCopyPassword.setOnClickListener {
            copyText(
                binding.inputPasswordCreated.editText!!.text.toString()
            )
            toast("Copied!")
        }

        inputPasswordCreated.setEndIconOnClickListener {
            copyText(
                binding.inputPasswordCreated.editText!!.text.toString()
            )
            toast("Copied!")
        }
    }

    private fun validation(): Boolean {
        binding.apply {
            return when {
                !switchDigit.isOn && !switchLowerLetter.isOn && !switchSymbol.isOn && !switchUpperLetter.isOn -> {
                    toast("Please check one option")
                    false
                }
                inputPasswordLength.editText == null || inputPasswordLength.editText!!.text.toString() == "0" -> {
                    inputPasswordLength.error = "Please Write Password Length"
                    false
                }
                else -> {
                    true
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
