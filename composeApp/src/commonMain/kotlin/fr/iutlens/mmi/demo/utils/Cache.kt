package fr.iutlens.mmi.demo.utils

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun<V> cached(key : ()->Any?, eval : ()->V) = object : ReadOnlyProperty<Any?, V> {
    var currentkey = key()
    var value = eval()

    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        val newKey = key()
        if (newKey==currentkey) return value

        currentkey = newKey
        value = eval()
        return value
    }
}