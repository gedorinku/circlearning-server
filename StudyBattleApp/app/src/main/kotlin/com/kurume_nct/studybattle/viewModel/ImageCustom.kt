package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.graphics.*
import android.net.Uri
import java.io.BufferedInputStream

/**
 * Created by hanah on 8/12/2017.
 */
class ImageCustom {
    fun onImageCircle(bitmap : Bitmap) : Bitmap{
        val bmp = onImageToSquare(bitmap)
        val circleBitmap = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val shader = BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = shader

        val circleCenter : Float = bmp.width / 2.toFloat()

        val canvas : Canvas = Canvas(circleBitmap)
        canvas.drawCircle(circleCenter, circleCenter, circleCenter, paint)

        return circleBitmap
    }

    fun onImageToSquare(bitmap: Bitmap) : Bitmap{
        val width = bitmap.width
        val height = bitmap.height
        var bmp : Bitmap
        if(width > height){
            bmp = Bitmap.createBitmap(bitmap, width/2 - height/2, 0, height, height)
        }else{
            bmp = Bitmap.createBitmap(bitmap, height/2 - width/2, 0, width, width)
        }
        return bmp
    }

    fun onUriToBitmap(context: Context, uri: Uri) : Bitmap{
        val inputstream =  context.contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(BufferedInputStream(inputstream))
    }
}