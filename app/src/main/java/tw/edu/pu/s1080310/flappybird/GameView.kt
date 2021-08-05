package tw.edu.pu.s1080310.flappybird

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.Thread.sleep

class GameView(context: Context?, attrs: AttributeSet?) :
    SurfaceView(context, attrs) , SurfaceHolder.Callback {
    var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels  //讀取螢幕寬度
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels  //讀取螢幕高度

    var background1: Background? = null
    var background2: Background? = null
    var backgroundMoveX = 5


    var countDown: Int = 200
    var thread: GameThread
    var bird: bird? = null

    var food: food? = null
    var cheese: cheese? = null
    var nut: nut? = null
    var goast: goast? = null
    var Score: Int = 0  //分數

    lateinit var mper: MediaPlayer

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)

    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        background1!!.draw(canvas)
        background2!!.draw(canvas)

        bird!!.draw(canvas)
        food!!.draw(canvas)
        cheese!!.draw(canvas)
        nut!!.draw(canvas)
        goast!!.draw(canvas)



        paint.color = Color.WHITE
        paint.textSize = 50f
        canvas.drawText("分數：" + Score.toString() + "分", 50f, 50f, paint)

//半透明背景
        paint.setARGB(5, 0, 0, 0)
        canvas.drawRect(0f, 0f, screenWidth.toFloat(), 80f, paint)


        /*
        canvas.drawText("螢幕解析度 : " +  screenWidth.toString()  + " * "
                + screenHeight.toString() , 400f,400f, paint)
        canvas.drawText("倒數計時:" + countDown.toString(), 200f,200f, paint)
        */
    }


    override fun surfaceCreated(p0: SurfaceHolder) {
        background1 = Background(BitmapFactory.decodeResource(resources, R.drawable.bkk))
        background2 = Background(BitmapFactory.decodeResource(resources, R.drawable.bkk))
        background2!!.x = background1!!.x + screenWidth

        /*
        var canvas:Canvas = holder.lockCanvas()
        draw(canvas)
        holder.unlockCanvasAndPost(canvas)
        */

        bird = bird(
            context, resources,
            BitmapFactory.decodeResource(resources, R.drawable.bird1)
        )

        food = food(
            context, resources,
            BitmapFactory.decodeResource(resources, R.drawable.food1)
        )

        cheese = cheese(
            context, resources,
            BitmapFactory.decodeResource(resources, R.drawable.food2)
        )

        nut = nut(
            context, resources,
            BitmapFactory.decodeResource(resources, R.drawable.food3)
        )

        goast = goast(
            context, resources,
            BitmapFactory.decodeResource(resources, R.drawable.goast)
        )





        thread.running = true
        thread.start()  //開始Thread


//遊戲背景音效
        mper = MediaPlayer()
        mper = MediaPlayer.create(context, R.raw.background)
        mper.setLooping(true)
        mper.start()


    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }



    fun update() {

        /*
        countDown--
        if (countDown<=0) {
            countDown = 0
            thread.running = false  //停止Thread
        }
         */
        //捲動背景處理
        background1!!.x -= backgroundMoveX
        background2!!.x -= backgroundMoveX

        if (background1!!.x + background1!!.image.getWidth() < 0) {
            background1!!.x = background2!!.x + screenWidth
        }

        if (background2!!.x + background2!!.image.getWidth() < 0) {
            background2!!.x = background1!!.x + screenWidth
        }
        bird!!.update()
        food!!.update()
        cheese!!.update()
        nut!!.update()
        goast!!.update()


        //判斷是否碰撞或男孩到達右邊邊界，結束遊戲
        if (bird!!.getRect().intersect(goast!!.getRect()) || (bird!!.x >= (screenWidth - bird!!.w))) {
            thread.running = false

            //呼叫GameActivity的GameOver方法
            var gameActivity: GameActivity = context as GameActivity
            gameActivity.GameOver()


                //遊戲結束之音效
                mper.reset()
                mper = MediaPlayer.create(context, R.raw.gameover)
                mper.setLooping(false)
                mper.start()

            }

        if (bird!!.getRect()
                .intersect(cheese!!.getRect()) || (bird!!.x >= (screenWidth - bird!!.w))) {
            thread.running = true
            Score++
        }

        if (bird!!.getRect()
                .intersect(food!!.getRect()) || (bird!!.x >= (screenWidth - bird!!.w))) {
            thread.running = true
            Score++
        }

        if (bird!!.getRect()
                .intersect(nut!!.getRect()) || (bird!!.x >= (screenWidth - bird!!.w))) {
            thread.running = true
            Score++

        }

        //判斷起司是否到達邊界
        if (cheese!!.ReachEdge()) {
            bird!!.x += 20
        }


        //判斷米是否到達邊界
        if (nut!!.ReachEdge()) {
            bird!!.x += 20
        }

        //判斷病橘子是否到達邊界
        if (food!!.ReachEdge()) {
            bird!!.x += 20
        }
        //判斷怪物是否到達邊界
        if (goast!!.ReachEdge()) {
            Score++
            bird!!.x += 20
        }

    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        var boyRect: Rect = bird!!.getRect()  //讀取男孩圖形區域

        if (boyRect.contains(event.getX().toInt(), event.getY().toInt())) {
            bird!!.Jump("UP", false)  //按到小男孩，往上跳躍30像素，並往右移動20像素
        } else if (food!!.getRect().contains(event.getX().toInt(), event.getY().toInt())) {
            //往右拖曳病毒
            if (event.action == MotionEvent.ACTION_MOVE) {
                if (food!!.x < event.getX() - food!!.w / 2) {
                    food!!.x = event.getX().toInt() - food!!.w / 2
                    food!!.y = event.getY().toInt() - food!!.h / 2
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                bird!!.x += 30
            }
        } else if (cheese!!.getRect().contains(event.getX().toInt(), event.getY().toInt())) {
            //往右拖曳病毒
            if (event.action == MotionEvent.ACTION_MOVE) {
                if (cheese!!.x < event.getX() - cheese!!.w / 2) {
                    cheese!!.x = event.getX().toInt() - cheese!!.w / 2
                    cheese!!.y = event.getY().toInt() - cheese!!.h / 2
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                bird!!.x += 30
            }
        } else if (nut!!.getRect().contains(event.getX().toInt(), event.getY().toInt())) {
            //往右拖曳病毒
            if (event.action == MotionEvent.ACTION_MOVE) {
                if (nut!!.x < event.getX() - nut!!.w / 2) {
                    nut!!.x = event.getX().toInt() - nut!!.w / 2
                    nut!!.y = event.getY().toInt() - nut!!.h / 2
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                bird!!.x += 30
            }
        } else if (goast!!.getRect().contains(event.getX().toInt(), event.getY().toInt())) {
            //往右拖曳病毒
            if (event.action == MotionEvent.ACTION_MOVE) {
                if (goast!!.x < event.getX() - goast!!.w / 2) {
                    goast!!.x = event.getX().toInt() - goast!!.w / 2
                    goast!!.y = event.getY().toInt() - goast!!.h / 2
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                bird!!.x += 30
            }
        } else if (event.action == MotionEvent.ACTION_DOWN) {
            bird!!.Jump("DOWN", false)  //按到其他區域，小男孩往下跳躍30像素，並往右移動20像素
        }
        return true
    }
}

