package com.kurume_nct.studybattleserver

import org.junit.Test

import java.io.File
import kotlin.test.assertEquals

/**
 * Created by gedorinku on 2017/08/09.
 */
class ImageUploadKtTest {
    @Test
    fun getFileFormatExtensionTest() {
        val getFileExtensionFromFile: (File) -> String = {
            val data = it.inputStream().use {
                it.readBytes()
            }
            getFileExtension(data.toList())
        }

        assertEquals(".jpg", getFileExtensionFromFile(File("assets/worry.jpg")))
        assertEquals(".png", getFileExtensionFromFile(File("assets/worry.png")))
        assertEquals("", getFileExtensionFromFile(File("assets/worry.pdf")))
    }

}