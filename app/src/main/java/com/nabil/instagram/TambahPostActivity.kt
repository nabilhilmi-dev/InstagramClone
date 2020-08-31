package com.nabil.instagram

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_tambah_post.*

class TambahPostActivity : AppCompatActivity() {
    private var myUri = ""
    private var imageUri : Uri? = null
    private var storagePostPictureRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_post)
        storagePostPictureRef = FirebaseStorage.getInstance().reference.child("Post Picture")

        save_new_post_btn.setOnClickListener { uploadImage() } //create method upload image

        CropImage.activity()
            .setAspectRatio(2,1) //Ukuran post
            .start(this@TambahPostActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK
            && data!= null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            img_post.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {

        when{
            imageUri == null -> Toast.makeText(this, "pilih gambar dulu atuh", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(desc_post.text.toString()) -> Toast.makeText(this, "Tulis Caption Biar Makin Kece", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Tambah Gambar Gan")
                progressDialog.setMessage("ngopi dulu gan, sembari kita upload gambar...")
                progressDialog.show()

                val fileRef = storagePostPictureRef!!.child(System.currentTimeMillis().toString() + "Posts")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){

                        task.exception.let {
                            throw it!!
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        myUri = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = ref.push().key

                        val postMap = HashMap<String, Any>()
//                        sesuai dengan firebase database
                        postMap["postid"] = postId!!
                        postMap["description"] = desc_post.text.toString().toLowerCase()!!
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postimage"] = myUri

                        ref.child(postId).updateChildren(postMap)

                        Toast.makeText(this, "Sukses Gan...", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@TambahPostActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }else {
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }

}

