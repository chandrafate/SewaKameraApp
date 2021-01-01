package com.candra.sewakameraapp.adminBarang

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
import com.candra.sewakameraapp.barang.Barang
import com.candra.sewakameraapp.kategori.Kategori
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_form_tambahbarang.*
import java.util.*

class FormTambahbarangActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference

    lateinit var nama: String
    lateinit var jenis: String
    lateinit var keterangan: String
    var harga: Int = 0
    var stok: Int = 0
    var idKat: Int = 0

    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    var statusAdd: Boolean = false

    lateinit var idProduk : String
    lateinit var gambarrrr : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_tambahbarang)

        if (intent.getStringExtra("action").equals("tambah")) {
            val data = intent.getParcelableExtra<Kategori>("kategori")
            idKat = data!!.id!!
        } else {
            statusAdd = true

            val data = intent.getParcelableExtra<Barang>("edit")
            idKat = data!!.kategori!!

            idProduk = data.id!!
            gambarrrr = data.gambar!!

            et_nama.setText(data.nama)
            et_jenis.setText(data.jenis)
            et_ket.setText(data.keterangan)
            et_harga.setText(data.harga.toString())
            et_stok.setText(data.stok.toString())

            Glide.with(this)
                .load(data.gambar)
                .into(iv_gambar_produk)

            btn_add.setImageResource(R.drawable.ic_cancel_red)

            textView54.text = "Edit Barang"
            btn_tambah.setText("Perbaruhi")
        }

        mDatabase = FirebaseDatabase.getInstance().getReference()

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        iv_back12.setOnClickListener {
            finish()
        }

        btn_tambah.setOnClickListener {
            nama = et_nama.text.toString()
            jenis = et_jenis.text.toString()
            keterangan = et_ket.text.toString()

            if (nama.equals("")) {
                et_nama.error = "Silahkan isi nama produk"
                et_nama.requestFocus()
            } else if (jenis.equals("")) {
                et_jenis.error = "Silahkan isi jenis barang"
                et_jenis.requestFocus()
            }else if (keterangan.equals("")) {
                et_ket.error = "Silahkan isi keterangan produk"
                et_ket.requestFocus()
            }else if (et_harga.text.toString().equals("")) {
                et_harga.error = "Silahkan isi harga produk"
                et_harga.requestFocus()
            }else if (et_stok.text.toString().equals("")) {
                et_harga.error = "Silahkan isi stok produk"
                et_harga.requestFocus()
            } else {
                harga = et_harga.text.toString().toInt()
                stok = et_stok.text.toString().toInt()

                prosesUpload()
            }
        }

        btn_add.setOnClickListener {
            if (statusAdd) {
                statusAdd = false
                btn_add.setImageResource(R.drawable.ic_btn_plus)
                iv_gambar_produk.setImageResource(R.drawable.unnamed_image)
            }else {
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
            statusAdd = true
            filePath = data?.data!!

            Glide.with(this)
                .load(filePath)
                .into(iv_gambar_produk)

            btn_add.setImageResource(R.drawable.ic_cancel_red)

        }
    }

    private fun prosesUpload() {
        if (filePath != null && statusAdd) {
            var progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Proses...")
            progressDialog.show()

            if (intent.getStringExtra("action").equals("edit")) {
                val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(gambarrrr)
                photoRef.delete()
            }

            val ref = storageReference?.child("foto_produk/" + UUID.randomUUID().toString())
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


                        ref.downloadUrl.addOnSuccessListener {
                            setData(it.toString())
                            progressDialog.dismiss()

                        }
                    }
                }?.addOnFailureListener {}
        } else {
            if (intent.getStringExtra("action").equals("tambah")) {
                Toast.makeText(this, "Silahkan Masukan Gambar", Toast.LENGTH_SHORT).show()
            }else {
                setData(gambarrrr)
            }

        }
    }

    private fun setData(gambar: String) {
        var barang = Barang()

        if (intent.getStringExtra("action").equals("edit")) {
            barang.id = idProduk
        } else {
            barang.id = mDatabase.child("produk").push().key
        }

        barang.gambar = gambar
        barang.harga = harga
        barang.jenis = jenis
        barang.kategori = idKat
        barang.keterangan = keterangan
        barang.nama = nama
        barang.stok = stok

        InsertBarang(barang)
    }

    private fun InsertBarang(barang: Barang) {
        mDatabase.child("produk/${barang.id}").setValue(barang)
        finish()
    }
}