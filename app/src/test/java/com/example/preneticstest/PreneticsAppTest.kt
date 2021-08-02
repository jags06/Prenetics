package com.example.preneticstest

import com.example.preneticstest.login.viewmodel.LoginViewModel
import org.koin.test.KoinTest
import org.koin.test.inject

class PreneticsAppTest:KoinTest {
    val model by inject<LoginViewModel>()

}