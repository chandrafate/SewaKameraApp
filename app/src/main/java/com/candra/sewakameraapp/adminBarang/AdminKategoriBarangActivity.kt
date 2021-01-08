package com.candra.sewakameraapp.adminBarang

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.barang.Barang
import com.candra.sewakameraapp.kategori.Kategori
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_admin_barang.*

class AdminKategoriBarangActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference

    private var dataList = ArrayList<Kategori>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_barang)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        iv_back_5.setOnClickListener {
            finish()
        }

        rc_kategori_admin.layoutManager = LinearLayoutManager(this)

        getData()
    }

    private fun getData() {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()

                var item: Int = 0

                for (getdatasnapsot in snapshot.child("kategori").children) {

                    val kategori = getdatasnapsot.getValue(Kategori::class.java)

                    item = 0

                    for (getdatasnapsot2 in snapshot.child("produk").children) {
                        val barang = getdatasnapsot2.getValue(Barang::class.java)

                        if (kategori!!.id!!.equals(barang!!.kategori)) {
                            item++
                        }
                    }

                    kategori!!.item = item

                    dataList.add(kategori)
                }

                rc_kategori_admin.adapter = AdminKategoriAdapter(dataList) {
                    startActivity(
                        Intent(
                            this@AdminKategoriBarangActivity,
                            AdminKelolaProdukActivity::class.java
                        ).putExtra("kategori", it)
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminKategoriBarangActivity, error.message, Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }
}