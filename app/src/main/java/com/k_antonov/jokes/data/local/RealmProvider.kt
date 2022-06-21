package com.k_antonov.jokes.data.local

import io.realm.Realm

interface RealmProvider {

    fun provide(): Realm

    class Base : RealmProvider {

        override fun provide(): Realm = Realm.getDefaultInstance()
    }
}