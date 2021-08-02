package com.example.preneticstest.db.module

import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
private const val realmDataStore = "prenetics_db"

var realmModule = module {
    factory {
        Realm.init(androidApplication())
        val config = RealmConfiguration.Builder()
            .schemaVersion(0)
            .name(realmDataStore)
            .build()
        Realm.setDefaultConfiguration(config)
        Realm.getDefaultInstance()

    }
}