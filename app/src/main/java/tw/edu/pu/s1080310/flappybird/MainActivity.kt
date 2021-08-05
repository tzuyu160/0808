package tw.edu.pu.s1080310.flappybird

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    lateinit var email: String

    lateinit var password: String

    lateinit var flag: String

    var UID: String = ""

    var db = FirebaseFirestore.getInstance()

    var userData: MutableMap<String, Any> = HashMap()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 獲取FirebaseAuth對象的共享實例

        auth = Firebase.auth

        SetFullScreen()  //設定全螢幕


        //註冊

        btnReg.setOnClickListener(object : View.OnClickListener {

            override fun onClick(p0: View?) {

                email = edtEmail.text.toString()

                password = edtPassword.text.toString()

                flag = "註冊"



                auth.createUserWithEmailAndPassword(email, password)

                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            val user = auth.currentUser

                            updateUI(user)

                        } else {

                            Toast.makeText(
                                baseContext, "註冊失敗：" + task.exception?.message,

                                Toast.LENGTH_SHORT
                            ).show()

                            updateUI(null)

                        }

                    }

            }

        })

        //登入

        btnLogIn.setOnClickListener(object : View.OnClickListener {

            override fun onClick(p0: View?) {

                email = edtEmail.text.toString()

                password = edtPassword.text.toString()

                flag = "登入"



                auth.signInWithEmailAndPassword(email, password)

                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            val user = auth.currentUser

                            updateUI(user)

                        } else {

                            Toast.makeText(
                                baseContext, "登入失敗：" + task.exception?.message,

                                Toast.LENGTH_SHORT
                            ).show()

                            updateUI(null)

                        }

                    }

            }

        })
        //登出

        btnLogOut.setOnClickListener(object : View.OnClickListener {

            override fun onClick(p0: View?) {

                Firebase.auth.signOut()

                Toast.makeText(
                    baseContext, "您已成功登出",

                    Toast.LENGTH_SHORT
                ).show()


            }

        })


    }

    fun SetFullScreen() {
        //隱藏狀態列
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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

    private fun updateUI(fUser: FirebaseUser?) {
        if (fUser != null) {


            UID = fUser.uid.toString()
            when (flag) {
                "註冊" -> {
                    userData["名字"] = edtEmail.text.toString()

                    userData["分數"] = 0

                    db.collection("Users")

                        .document(UID)

                        .set(userData)

                        .addOnSuccessListener {
                            Toast.makeText(
                                baseContext, "恭喜您註冊成功\n您的UID為：" + UID,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .addOnFailureListener { e ->

                            Toast.makeText(baseContext, "註冊失敗：" + e.toString(),

                                Toast.LENGTH_LONG).show()

                        }

                }


                "登入" -> {
                    Toast.makeText(
                        baseContext, "恭喜您登入成功\n您的UID為：" + UID,

                        Toast.LENGTH_LONG
                    ).show()

                }
            }

        }
    }

//初始化活動時，先檢查用戶當前是否登錄

    public override fun onStart() {

        super.onStart()

        var user = auth.currentUser

        if (user != null) {

            flag = "登入"

            updateUI(user)

        }

    }

}