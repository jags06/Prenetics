package com.example.preneticstest.login.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.preneticstest.db.models.UserModel
import io.reactivex.rxjava3.subjects.PublishSubject
import io.realm.Realm
import timber.log.Timber

class LoginViewModel(val realm: Realm) : ViewModel() {
    val loginClickSubject: PublishSubject<Unit> = PublishSubject.create()
    val emailField = ObservableField("")
    val passwordField = ObservableField("")

    fun clickLogin() {
        loginClickSubject.onNext(Unit)
    }

    private fun createUserFirstTime() {
        realm.beginTransaction()
        realm.executeTransactionAsync({
            val userModel = it.createObject(UserModel::class.java)
            userModel.email = "peter@prenetics.com"
            userModel.password = "Preneticstest"
        },
            {
                Timber.d("Check Transaction Success ")
            },
            {
                Timber.d("Check Transaction Failure ${it.message}")

            })
        realm.commitTransaction()
    }

    fun checkUserExist(email: String, password: String): Pair<String, Boolean> {

        val userModel = realm.where(UserModel::class.java).equalTo("email", email).findFirst()

        return if (userModel?.password == password) {
            Pair("Exist", true)
        } else {
            if (email == "peter@prenetics.com" && password == "Preneticstest") {
                createUserFirstTime()
                Pair("Exist", true)
            } else {
                Pair("", false)
            }
        }
    }

}