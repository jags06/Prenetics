package com.example.preneticstest.heart.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.preneticstest.db.models.HeartRateModel
import com.example.preneticstest.db.models.UserModel
import io.reactivex.rxjava3.subjects.PublishSubject
import io.realm.Realm
import java.text.SimpleDateFormat
import java.util.*

class HeartViewModel(val realm: Realm) : ViewModel() {

    val heartField = ObservableField("")
    val lastHeartField = ObservableField("")
    val saveClickSubject: PublishSubject<Unit> = PublishSubject.create()
    private lateinit var userModel: UserModel

    fun clickSave() {
        saveClickSubject.onNext(Unit)
    }

    fun getlastHeartRate(email: String) {
        userModel = realm.where(UserModel::class.java).equalTo("email", email).findFirst()!!
        lastHeartField.set((userModel.heartRateModel?.rate) + (userModel.heartRateModel?.timeStamp))
    }

    fun saveHeartRate(heartRate: String) {
        realm.beginTransaction()
        val obj: HeartRateModel = realm.createObject(HeartRateModel::class.java)
        obj.rate = heartRate
        obj.timeStamp = getCurrentTimeUsingDate()

        userModel.heartRateModel = obj
        realm.commitTransaction()

        getlastHeartRate("peter@prenetics.com")

    }

    private fun getCurrentTimeUsingDate(): String {
        val date = Date()
        val currentDate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(date)
        val currentTime = SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        ).format(date)
        return "$currentDate $currentTime"
    }

}