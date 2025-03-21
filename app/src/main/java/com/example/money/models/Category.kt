package com.example.money.models

import androidx.compose.ui.graphics.Color

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Category() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var name: String = ""

    // Store color as a string (Realm cannot store Color directly)
    private var _colorValue: String = "0,0,0"

    var color: Color
        get() {
            val colorComponents = _colorValue.split(",").map { it.toFloat() }
            return Color(colorComponents[0], colorComponents[1], colorComponents[2])
        }
        set(value) {
            _colorValue = "${value.red},${value.green},${value.blue}"
        }

        constructor(name: String, color: Color) : this() {
        this.name = name
        this.color = color // ✅ Uses the setter to update `_colorValue`
    }
//    companion object {
//        fun create(name: String, color: Color): Category {
//            val category = Category()
//            category.name = name
//            category.color = color // ✅ Uses setter to update `_colorValue`
//            return category
//        }
//    }
}



