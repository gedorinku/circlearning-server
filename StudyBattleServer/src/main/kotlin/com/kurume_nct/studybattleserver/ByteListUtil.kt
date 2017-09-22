package com.kurume_nct.studybattleserver

/**
 * Created by gedorinku on 2017/08/09.
 */
fun List<Byte>.startsWith(prefix: List<Byte>): Boolean
        = prefix.size < size && prefix.allIndexed { index, byte -> this[index] == byte }