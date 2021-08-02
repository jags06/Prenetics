package com.example.preneticstest.db.models

import io.realm.RealmObject

open class HeartRateModel(

    var rate: String = "",
    var timeStamp: String = ""
): RealmObject()