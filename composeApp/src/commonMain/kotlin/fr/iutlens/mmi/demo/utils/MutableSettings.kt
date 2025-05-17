package fr.iutlens.mmi.demo.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield


class MutableSettings {
    val settings: Settings = Settings()

    inner class Saveable<T>(
            val setter : (String,T) -> Unit,
            val getter : (String) -> T?,
            val map : MutableMap<String,MutableState<T>> = mutableMapOf()
                ) {

        fun save(name : String, value : T){
            setter(name,value)
            var state = map[name]
            if (state == null){
                state = mutableStateOf(value)
            } else {
                state.value = value
            }
            map[name] = state
        }

        fun load(name : String, defaultValue: T) : T = getter(name) ?: defaultValue

        fun state(name : String, defaultValue: T) : MutableState<T>{
            var state = map[name]
            if (state == null) {
                val value = getter(name) ?: defaultValue
                state = mutableStateOf(value)
                map[name] = state
            }
            return state
        }

        operator fun get(name: String, default : T) = object : MutableState<T> {
            val state = state(name,default)
            override var value
                get() = component1()
                set(v)  = component2()(v)

            override fun component1(): T = state.value
            override fun component2(): (T) -> Unit  = { save(name,it) }

        }
    }

    val boolean = Saveable(settings::putBoolean, settings::getBooleanOrNull)
    val int = Saveable(settings::putInt, settings::getIntOrNull)
    val long = Saveable(settings::putLong, settings::getLongOrNull)
    val float = Saveable(settings::putFloat, settings::getFloatOrNull)
    val double = Saveable(settings::putDouble, settings::getDoubleOrNull)
    val string = Saveable(settings::putString, settings::getStringOrNull)
}

val settings by lazy { MutableSettings() }