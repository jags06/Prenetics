package com.example.preneticstest.profile.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.preneticstest.db.models.UserModel
import io.reactivex.rxjava3.subjects.PublishSubject
import io.realm.Realm

class ProfileViewModel(val realm: Realm) : ViewModel() {

    var firstNameField = ObservableField("")
    var lastNameField = ObservableField("")
    var emailField = ObservableField("")
    var dobField = ObservableField("")
    val saveClickSubject: PublishSubject<Unit> = PublishSubject.create()

    private lateinit var userModel: UserModel


    fun getUserProfile(email: String) {
        userModel = realm.where(UserModel::class.java).equalTo("email", email).findFirst()!!
        firstNameField.set(userModel.firstName)
        lastNameField.set(userModel.lastName)
        emailField.set(userModel.email)
        dobField.set(userModel.dob)
    }

    fun clickSave() {
        saveClickSubject.onNext(Unit)
    }

    fun updateUserObject(firstName: String, lastname: String, dob: String) {
        realm.beginTransaction()
        userModel.firstName = firstName
        userModel.lastName = lastname
        userModel.dob = dob
        realm.commitTransaction()
    }
}