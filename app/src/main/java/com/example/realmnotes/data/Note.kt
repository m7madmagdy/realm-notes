package com.example.realmnotes.data

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
open class Note : RealmModel {
    @PrimaryKey
    var id: String = ""

    @Required
    var title: String? = ""

    @Required
    var description: String? = ""
}
