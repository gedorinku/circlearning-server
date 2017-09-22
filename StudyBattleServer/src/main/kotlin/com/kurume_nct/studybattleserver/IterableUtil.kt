package com.kurume_nct.studybattleserver

/**
 * Created by gedorinku on 2017/08/09.
 */
inline fun <T> Iterable<T>.allIndexed(predicate: (Int, T) -> Boolean): Boolean {
    forEachIndexed {
        index, element ->
        if (!predicate(index, element)) {
            return false
        }
    }
    return true
}