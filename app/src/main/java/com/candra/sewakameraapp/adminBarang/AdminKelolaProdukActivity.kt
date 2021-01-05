package com.candra.sewakameraapp.adminBarang

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.barang.Barang
import com.candra.sewakameraapp.kategori.Kategori
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_admin_kelola_barang.*
import kotlinx.android.synthetic.main.activity_list_item.*


class AdminKelolaProdukActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    private var dataList = ArrayList<Barang>()

    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_kelola_barang)

        val data = intent.getParcelableExtra<Kategori>("kategori")

        id = data?.id!!

        mDatabase = FirebaseDatabase.getInstance().getReference()

        rc_kelola_barang_admin.layoutManager = LinearLayoutManager(this)

        iv_back_kelola_barang_admin.setOnClickListener {
            finish()
        }
        btn_tambah_kelola_barang_admin.setOnClickListener {
            startActivity(
                Intent(this, FormTambahbarangActivity::class.java).putExtra(
                    "kategori",
                    data
                ).putExtra("action", "tambah")
            )
        }

        getData()
    }

    private fun getData() {
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                for (getdatasnapshot in snapshot.child("produk").getChildren()) {

                    val produk = getdatasnapshot.getValue(Barang::class.java)

                    if (produk!!.kategori == id) {
                        dataList.add(produk!!)
                    }

                }

                rc_kelola_barang_admin.adapter = ListBarangAdminAdapter(dataList) {
                    val builder = AlertDialog.Builder(this@AdminKelolaProdukActivity)
                    builder.setTitle("${it.nama}")

                    val x = arrayOf("Update", "Hapus")
                    builder.setItems(x) { dialog, which ->
                        when (which) {
                            0 -> {
                                startActivity(
                                    Intent(
                                        this@AdminKelolaProdukActivity,
                                        FormTambahbarangActivity::class.java
                                    ).putExtra("edit", it).putExtra("action", "edit")
                                )
                            }
                            1 -> {
                                val builder2 = AlertDialog.Builder(this@AdminKelolaProdukActivity)
                                builder2.setCancelable(true)
                                builder2.setTitle("Konfirmasi")
                                builder2.setMessage("Apakah Anda ingin menghapus")
                                builder2.setPositiveButton("Tidak") { dialog, which -> }
                                builder2.setNegativeButton("Ya") { dialog, which -> hapusBarang(it)}

                                val dialog2 = builder2.create()
                                dialog2.show()
                            }
                        }
                    }

                    val dialog = builder.create()
                    dialog.show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AdminKelolaProdukActivity,
                    "" + error.message,
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun hapusBarang(it: Barang) {
        val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(it.gambar.toString())
        photoRef.delete()

        mDatabase.child("produk/${it.id}").removeValue()
    }
}