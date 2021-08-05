package tw.edu.pu.s1080310.flappybird

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog

class OverActivity : AppCompatActivity(), DialogInterface.OnClickListener  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_over)

        SetFullScreen()  //設定全螢幕

        var Score:Int = intent.getIntExtra("分數" ,0)
        AlertDialog.Builder(this)
            .setTitle("遊戲結束")
            .setMessage("您此次的成績為：" + Score.toString() + "分，還想再玩一次嗎？")
            .setIcon(R.drawable.goast)
            .setCancelable(false)  //按對話框外面，不會關閉對話框

            .setPositiveButton("當然要囉",this)
            .setNegativeButton("結束再見",this)
            .show()

    }

    fun SetFullScreen(){
        //隱藏狀態列
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        // 隱藏動作列
        val actionBar = supportActionBar
        actionBar!!.hide()

        //不要自動休眠
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        if (p1 == DialogInterface.BUTTON_POSITIVE){
            intent = Intent(this@OverActivity, GameActivity::class.java)
            startActivity(intent)
            finish()
        }
        else if (p1 == DialogInterface.BUTTON_NEGATIVE){
            finish()
        }

    }

}