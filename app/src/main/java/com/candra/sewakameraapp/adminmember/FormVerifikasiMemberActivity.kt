package com.candra.sewakameraapp.adminmember

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.candra.sewakameraapp.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_form_verifikasi_member.*
import java.util.*


class FormVerifikasiMemberActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference

    private val PICK_IMAGE_REQUEST = 1
    private var switchUpload: Int = 0
    private var fileKtp: Uri? = null
    private var fileSim: Uri? = null

    private var urlKtp: String = ""
    private var urlSim: String = ""

    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_verifikasi_member)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        btn_ktp_form_verifikasi_admin.setOnClickListener {
            switchUpload = 1
            launchGallery()
        }

        btn_sim_form_verifikasi_admin.setOnClickListener {
            switchUpload = 2
            launchGallery()
        }

        iv_back_form_verifikasi_admin.setOnClickListener {
            finish()
        }

        btn_upload_form_verifikasi_admin.setOnClickListener {
            prosesUpload(intent.getStringExtra("verifikasi").toString())
        }

    }

    private fun launchGallery() {

        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && switchUpload == 1) {
            fileKtp = data?.data!!

            Glide.with(this)
                .load(fileKtp)
                .into(iv_ktp_form_verifikasi_admin)

        } else if (resultCode == Activity.RESULT_OK && switchUpload == 2) {
            fileSim = data?.data!!

            Glide.with(this)
                .load(fileSim)
                .into(iv_sim_form_verifikasi_admin)

        }
    }


    private fun prosesUpload(username: String) {
        if (fileKtp != null && fileSim != null) {
            var progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Upload Gambar KTP...")
            progressDialog.show()

            var ref = storageReference?.child("data_ktp_member/$username")
            var uploadTask = ref?.putFile(fileKtp!!)

            var urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref?.downloadUrl
            })?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {


                        ref?.downloadUrl?.addOnSuccessListener {
                            urlKtp = it.toString()
                            progressDialog.dismiss()
                        }
                    }
                }?.addOnFailureListener {}

            progressDialog.setTitle("Upload Gambar SIM...")
            progressDialog.show()

            var ref2 = storageReference?.child("data_sim_member/$username")
            var uploadTask2 = ref2?.putFile(fileSim!!)

            var urlTask2 = uploadTask2?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref2?.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {


                    ref2?.downloadUrl?.addOnSuccessListener {
                        progressDialog.dismiss()
                        urlSim = it.toString()
                        setData(username, urlKtp, urlSim)
                        setStatus(username)
                    }
                }
            }?.addOnFailureListener {}

        } else {
            Toast.makeText(this, "Silahkan Lengkapi Data Gambar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setData(username: String, ktp: String, sim: String) {
        val memberVerifed = MemberVerifed()
        memberVerifed.username = username
        memberVerifed.nama = et_nama_form_verifikasi_member.text.toString()
        memberVerifed.nohp = et_nohp_form_verifikasi_member.text.toString()
        memberVerifed.alamat = et_alamat_form_verifikasi_member.text.toString()
        memberVerifed.ktp = ktp
        memberVerifed.sim = sim

        mDatabase.child("data_verifikasi/$username").setValue(memberVerifed)
    }

    private fun setStatus(username: String) {
        mDatabase.child("member/$username/status").setValue("ya")
        finish()
    }
}