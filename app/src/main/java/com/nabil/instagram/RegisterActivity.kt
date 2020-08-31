package com.nabil.instagram
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_signin_link.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        btn_register.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {

        // untuk memberikan aksi ketika text dimasukan kedalam edit text dan datanya dijadikan / di tampung dalam string
        val fullName = fullname_register.text.toString()
        val userName = username_register.text.toString()
        val email = email_register.text.toString()
        val password = password_register.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, "Full Name is Required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this, "username is Required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "email is Required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "password is Required", Toast.LENGTH_SHORT).show()

            else ->{
                val progressDialog = ProgressDialog (this)
                progressDialog.setTitle("Daftar Dulu gan")
                progressDialog.setMessage("Tunggu Ya gan...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            saveUserInfo(fullName, userName, email, progressDialog)
                        }else{
                            val message = task.exception!!.toString()
                            Toast.makeText(this,"error: $message", Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }

                    }

            }
        }

    }

    private fun saveUserInfo(
        fullName: String,
        userName: String,
        email: String,
        progressDialog: ProgressDialog
    ) {
        val currentUseID = FirebaseAuth.getInstance().currentUser!!.uid
        val  usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUseID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = email
        //default bio dan data profile
        userMap["bio"] = "Hey Im Student of Boarding School"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/social-media-89759.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=a61cbaab-52e6-41db-bfbe-6de1850d820a"

        usersRef.child(currentUseID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this@RegisterActivity,"Account Sudah Dibuat Gan", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    val message = task.exception!!.toString()
                    Toast.makeText(this,"Error: $message", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }


    }



}