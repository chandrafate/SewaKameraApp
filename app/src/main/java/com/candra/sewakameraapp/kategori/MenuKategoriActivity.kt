package com.candra.sewakameraapp.kategori

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.candra.sewakameraapp.barang.ListItemActivity
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.barang.Barang
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_menu_kategori.*

class MenuKategoriActivity : AppCompatActivity() {

    private var dataList = ArrayList<Kategori>()

    lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_kategori)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        rc_menu_kategori.layoutManager = GridLayoutManager(this, 2)

        getData()

        iv_back_menu_kategori.setOnClickListener {
            finish()
        }

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

                rc_menu_kategori.adapter = MenuKategoriAdapter(dataList) {
                    val intent = Intent(
                        this@MenuKategoriActivity,
                        ListItemActivity::class.java
                    ).putExtra("kategori", it)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MenuKategoriActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}