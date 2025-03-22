package com.example.money.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


class KeywordMapping() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var keyword: String = ""
    var categoryName: String = ""

    companion object {
        fun create(keyword: String, category: String): KeywordMapping {
            return KeywordMapping().apply {
                this.keyword = keyword
                this.categoryName = category
            }
        }
    }
}
