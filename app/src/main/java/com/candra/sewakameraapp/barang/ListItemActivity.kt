package com.candra.sewakameraapp.barang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.kategori.Kategori
import com.candra.sewakameraapp.keranjang.KeranjangActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_list_item.*

class ListItemActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    private var dataList = ArrayList<Barang>()

    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_item)

        val data = intent.getParcelableExtra<Kategori>("kategori")

        id = data?.id!!

        mDatabase = FirebaseDatabase.getInstance().getReference("produk")

        tv_nama_kategori_barang.text = data.nama

        iv_back_list_barang.setOnClickListener {
            finish()
        }

        iv_keranjang_kategori_barang.setOnClickListener {
            startActivity(Intent(this, KeranjangActivity::class.java))
        }


        rc_item_list_barang.layoutManager = LinearLayoutManager(this)
        getData()
    }

    private fun getData() {
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                for (getdatasnapshot in snapshot.getChildren()) {

                    val produk = getdatasnapshot.getValue(Barang::class.java)

                    if (produk!!.kategori == id) {
                        dataList.add(produk)
                    }

                }

                rc_item_list_barang.adapter = ListItemAdapter(dataList) {
                    val intent = Intent(this@ListItemActivity, DetailBarangActivity::class.java).putExtra("detailitem", it)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListItemActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}