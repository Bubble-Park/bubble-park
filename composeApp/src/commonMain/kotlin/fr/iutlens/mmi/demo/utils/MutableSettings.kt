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
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield


class MutableSettings {
    val settings: Settings = Settings()

    inner class Saveable<T>(
            val setter : (String,T) -> Unit,
            val getter : (String) -> T?,
            val map : MutableMap<String,MutableStateFlow<T>> = mutableMapOf()
                ) {

        fun save(name : String, value : T){
            setter(name,value)
            var flow = map[name]
            if (flow == null){
                flow = MutableStateFlow(value)
                map[name] = flow
            } else {
                CoroutineScope(Dispatchers.Main).launch{
                    flow.emit(value)
                }
            }
        }

        fun load(name : String, defaultValue: T) : T = getter(name) ?: defaultValue

        fun flow(name : String, defaultValue: T) : MutableStateFlow<T>{
            var flow = map[name]
            if (flow == null) {
                val value = getter(name) ?: defaultValue
                flow = MutableStateFlow(value)
                map[name] = flow
            }
            return flow
        }

        operator fun get(name: String, default : T) = object : MutableState<T>{
            var current by mutableStateOf(default)
            override var value: T
                get() = component1()
                set(value) = component2()(value)

            override fun component1(): T = current

            override fun component2(): (T) -> Unit  = { save(name,it) }

            init {
                CoroutineScope(Dispatchers.Main).launch {
                    flow(name,default).collect({
                            current = it
                        })
                }
            }
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