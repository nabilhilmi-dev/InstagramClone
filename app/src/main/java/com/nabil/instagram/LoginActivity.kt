package com.nabil.instagram

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btm_signup_link.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        btn_login.setOnClickListener {
            loginuser()
        }
    }

    private fun loginuser() {
       val email = email_login.text.toString()
        val password = password_login.text.toString()

        when{

            TextUtils.isEmpty(email) -> Toast.makeText(this, "email is Required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "password is Required", Toast.LENGTH_SHORT).show()

            else ->{
                val progressDialog = ProgressDialog (this)
                progressDialog.setTitle("Daftar Dulu gan")
                progressDialog.setMessage("Tunggu Ya gan...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            progressDialog.dismiss()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }else{
                            val message = task.exception!!.toString()
                            Toast.makeText(this,"error: $message", Toast.LENGTH_SHORT).show()
                            FirebaseAuth.getInstance().signOut()
                            progressDialog.dismiss()
                        }

                    }

            }
        }



    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser !=null){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
