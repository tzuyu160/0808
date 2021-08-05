package tw.edu.pu.s1080310.flappybird

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect

class bird (var context: Context?, var res: Resources, var image : Bitmap)  {
    //x,y軸座標
    var x: Int = 0
    var y: Int = 0

    //圖形寬度高度
    var w: Int = 0
    var h: Int = 0

    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    //切換圖片
    var pictNo : Int = 1

    init {
        //原始圖形較大，縮小為原圖的1/3
        w = image.width
        h = image.height

        //設定初始位置
        x =  screenWidth / 10
        y = (screenHeight - h) * 9 /15
    }

    fun update() {
        pictNo++
        if (pictNo==4){pictNo=1}

        image = BitmapFactory.decodeResource(res,
            res.getIdentifier("bird" + pictNo.toString(), "drawable",
                context!!.getPackageName()))
    }
    fun draw(canvas: Canvas) {
        //針對圖片進行裁切
        var SrcRect: Rect = Rect(0, 0, image.width, image.height)
        var DestRect: Rect = Rect(x, y, w + x, h + y)
        canvas.drawBitmap(image, SrcRect, DestRect, null)
    }

    fun getRect():Rect{
        //取得圖形範圍 (內縮10像素，比較不會太敏感)
        return Rect(x+10, y+10,x+w-10, y+h-10)
    }

    fun Jump(action:String, sensor:Boolean){
        if (action == "UP") {
            y -= 30
            if (y < 0) { y = 0 }
        }
        else if  (action == "DOWN") {
            y += 30
            if (y > screenHeight - h) { y = screenHeight - h }
        }
        if (!sensor) {
            x += 20
        }
    }

}