package fr.iutlens.mmi.demo.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.russhwolf.settings.Settings

/*
MutableSettings représente un ensemble de clés/valeurs persitantes (sauvegardées)

L'accès se fait via des propriétés nommées selon le type (int, string etc..) utilisables
comme tableaux associatifs (map)
 */
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

    inline operator fun<reified T :  Comparable<*>> get(key : String, default : T) : MutableState<T> =
        when (T::class) {
            Boolean::class -> boolean[key, default as Boolean]
            Int::class -> int[key, default as Int]
            Long::class -> long[key, default as Long]
            Float::class -> float[key, default as Float]
            Double::class -> double[key, default as Double]
            String::class -> string[key, default as String]
            else -> throw RuntimeException("Type not saveable")
        } as MutableState<T>
}

val savedSettings by lazy { MutableSettings() }