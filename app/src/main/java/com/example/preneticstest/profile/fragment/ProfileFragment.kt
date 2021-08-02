package com.example.preneticstest.profile.fragment

import android.os.Bundle
import android.view.View
import com.example.preneticstest.R
import com.example.preneticstest.base.fragment.BindingFragment
import com.example.preneticstest.databinding.ProfileLayoutBinding
import com.example.preneticstest.profile.viewmodel.ProfileViewModel
import org.koin.android.ext.android.inject

class ProfileFragment : BindingFragment<ProfileLayoutBinding>() {

    private val viewModel: ProfileViewModel by inject()

    override fun getLayoutResId(): Int {
        return R.layout.profile_layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.saveClickSubject.subscribe {
            viewModel.updateUserObject(
                binding.firstName.text.toString(),
                binding.lastName.text.toString(),
                binding.dob.text.toString()
            )
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserProfile("peter@prenetics.com")
    }
}