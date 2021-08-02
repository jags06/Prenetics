package com.example.preneticstest.login.fragment

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.preneticstest.R
import com.example.preneticstest.base.fragment.BindingFragment
import com.example.preneticstest.databinding.LoginLayoutBinding
import com.example.preneticstest.login.viewmodel.LoginViewModel
import com.example.preneticstest.util.util.isValidEmail
import org.koin.android.ext.android.inject


class LoginFragment : BindingFragment<LoginLayoutBinding>() {

    private val viewModel: LoginViewModel by inject()

    override fun getLayoutResId(): Int {
        return R.layout.login_layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this
        setupListeners()
        viewModel.loginClickSubject.subscribe {
            hideKeyboard()
            if (isValidate()) {
                if (viewModel.checkUserExist(
                        binding.email.text.toString(),
                        binding.password.text.toString()
                    ).second
                ) {
                    findNavController().navigate(R.id.action_login_fragment_dest_to_dashboard_fragment_dest)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Username/password not matched",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.view?.windowToken, 0)
    }

    private fun setupListeners() {
        binding.email.addTextChangedListener(TextFieldValidation(binding.email))
        binding.password.addTextChangedListener(TextFieldValidation(binding.password))
    }

    private fun validateEmail(): Boolean {
        if (binding.email.text.toString().trim().isEmpty()) {
            binding.email.error = "Required Field!"
            binding.email.requestFocus()
            return false
        } else if (!isValidEmail(binding.email.text.toString())) {
            binding.emailInputLayout.error = "Invalid Email!"
            binding.email.requestFocus()
            return false
        } else {
            binding.emailInputLayout.isErrorEnabled = false
        }
        return true
    }

    private fun isValidate(): Boolean =
        validateEmail() && validatePassword()


    private fun validatePassword(): Boolean {
        if (binding.password.text.toString().trim().isEmpty()) {
            binding.passwordInputLayout.error = "Required Field!"
            binding.password.requestFocus()
            return false
        } else if (binding.password.text.toString().length < 6) {
            binding.passwordInputLayout.error = "password can't be less than 6"
            binding.password.requestFocus()
            return false
        } else {
            binding.passwordInputLayout.isErrorEnabled = false
        }
        return true
    }


    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            when (view.id) {
                R.id.email -> {
                    validateEmail()
                }
                R.id.password -> {
                    validatePassword()
                }

            }

        }

    }

}