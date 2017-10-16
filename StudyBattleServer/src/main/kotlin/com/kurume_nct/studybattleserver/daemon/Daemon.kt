package com.kurume_nct.studybattleserver.daemon

/**
 * Created by gedorinku on 2017/10/16.
 */
interface Daemon {
    /**
     * 1秒おきに呼ばれる
     */
    fun onFastUpdate()

    /**
     * 1分おきに呼ばれる
     */
    fun onSlowUpdate()
}