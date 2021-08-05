package tw.edu.pu.s1080310.flappybird

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread (val surfaceHolder: SurfaceHolder, val gameView: GameView) : Thread() {

    var running : Boolean = false


    override fun run() {
        while (running) {
            gameView.update()

            try {
                var canvas: Canvas = surfaceHolder.lockCanvas()
                gameView.draw(canvas)
                surfaceHolder.unlockCanvasAndPost(canvas)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                sleep(50)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}