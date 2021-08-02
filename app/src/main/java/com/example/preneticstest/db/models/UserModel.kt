package com.example.preneticstest.db.models

import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class UserModel(
    var firstName: String = "",
    var lastName: String = "",

    var email: String = "",
    var dob: String = "",

    var password: String = "",
    var userName: String = "",
    var pdfURI: String = "",
    var heartRateModel: HeartRateModel? = null,
    var genoTypeModel: GenoTypeModel? = null
): RealmModel