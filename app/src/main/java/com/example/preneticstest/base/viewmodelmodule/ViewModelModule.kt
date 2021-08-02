package com.example.preneticstest.base.viewmodelmodule

import com.example.preneticstest.genetics.viewmodel.GeneticsViewModel
import com.example.preneticstest.heart.viewmodel.HeartViewModel
import com.example.preneticstest.login.viewmodel.LoginViewModel
import com.example.preneticstest.profile.viewmodel.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { GeneticsViewModel() }
    viewModel { HeartViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
}