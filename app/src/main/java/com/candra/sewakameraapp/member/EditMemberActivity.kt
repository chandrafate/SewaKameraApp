package com.candra.sewakameraapp.member

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.sign.Member
import com.candra.sewakameraapp.utils.Preferences
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_edit_member.*
import java.util.*


class EditMemberActivity : AppCompatActivity() {

    lateinit var preferences: Preferences
    lateinit var mDatabase: DatabaseReference

    lateinit var sPassword: String
    lateinit var sPassword2: String
    lateinit var sNama: String
    lateinit var sEmail: String

    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    var statusAdd: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_member)

        preferences = Preferences(this)

        mDatabase = FirebaseDatabase.getInstance().getReference("member")

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        iv_back_edit_member.setOnClickListener {
            finish()
        }

        et_full_name_edit_member.setText(preferences.getValues("nama"))
        et_username_edit_member.setText(preferences.getValues("username"))
        et_email_edit_member.setText(preferences.getValues("email"))


        if (preferences.getValues("gambar")!!.isNotEmpty()) {
            Glide.with(this)
                .load(preferences.getValues("gambar"))
                .apply(RequestOptions.circleCropTransform())
                .into(iv_user_edit_member);
            iv_btn_plus_edit_member.setImageResource(R.drawable.ic_cancel_red)
            btn_simpan_edit_member.visibility = View.VISIBLE
            statusAdd = true
        } else {
            iv_user_edit_member.setImageResource(R.drawable.user_pic)
        }

        showButton(et_full_name_edit_member, preferences.getValues("nama").toString())
        showButton(et_email_edit_member, preferences.getValues("email").toString())
        showButton(et_password_edit_member, "")
        showButton(et_password_2_edit_member, "")

        btn_simpan_edit_member.setOnClickListener {
            sNama = et_full_name_edit_member.text.toString()
            sEmail = et_email_edit_member.text.toString()
            sPassword = et_password_edit_member.text.toString()
            sPassword2 = et_password_2_edit_member.text.toString()

            if (sNama.equals("")) {
                et_full_name_edit_member.error = "Silahkan isi nama anda"
                et_full_name_edit_member.requestFocus()
            } else if (sEmail.equals("")) {
                et_email_edit_member.error = "Silahkan isi email anda"
                et_email_edit_member.requestFocus()
            } else {
                if (sPassword.isNotEmpty() || sPassword2.isNotEmpty()) {
                    if (sPassword.equals(sPassword2)) {
                        prosesUpdate()
                    } else {
                        et_password_edit_member.error = "Password tidak sama"
                        et_password_2_edit_member.error = "Password tidak sama"
                        et_password_edit_member.requestFocus()
                    }
                } else {
                    sPassword = preferences.getValues("password").toString()
                    prosesUpdate()
                }

            }
        }

        iv_btn_plus_edit_member.setOnClickListener {
            if (statusAdd) {
                statusAdd = false
                iv_btn_plus_edit_member.setImageResource(R.drawable.ic_btn_plus)
                iv_user_edit_member.setImageResource(R.drawable.user_pic)
            } else {
                launchGallery()
            }
        }
    }

    private fun launchGallery() {

        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            statusAdd = true
            filePath = data?.data!!

            Glide.with(this)
                .load(filePath)
                .apply(RequestOptions.circleCropTransform())
                .into(iv_user_edit_member)

            iv_btn_plus_edit_member.setImageResource(R.drawable.ic_cancel_red)

        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setData(gambar: String) {
        var member = Member()
        member.email = sEmail
        member.nama = sNama
        member.password = sPassword
        member.status = preferences.getValues("status")
        member.username = preferences.getValues("username")
        member.gambar = gambar

        updateMember(member)
    }

    private fun updateMember(member: Member) {
        preferences.setValues("email", member.email.toString())
        preferences.setValues("nama", member.nama.toString())
        preferences.setValues("status", member.status.toString())
        preferences.setValues("gambar", member.gambar.toString())
        preferences.setValues("username", member.username.toString())

        mDatabase.child("${member.username}").setValue(member)
        showSuccess()
    }

    private fun prosesUpdate() {
        if (filePath != null) {
            var progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Proses...")
            progressDialog.show()


            val ref = storageReference?.child("foto_member/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask =
                uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                })?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss()

                        ref.downloadUrl.addOnSuccessListener {
                            setData(it.toString())

                        }
                    } else {
                        // Handle failures
                    }
                }?.addOnFailureListener {}
        } else {
            setData(preferences.getValues("gambar").toString())
        }
    }

    private fun showButton(editText: EditText, ori: String) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s.toString() == ori) {
                        btn_simpan_edit_member.visibility = View.INVISIBLE

                    } else {
                        btn_simpan_edit_member.visibility = View.VISIBLE
                    }
                } catch (e: NumberFormatException) {
                }
            }
        })
    }

    fun showSuccess() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.ubah_profile_success)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val btnClose = dialog.findViewById(R.id.btn_close) as Button

        btnClose.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }
}