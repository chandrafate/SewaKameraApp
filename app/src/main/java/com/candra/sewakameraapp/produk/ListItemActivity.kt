package com.candra.sewakameraapp.produk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.kategori.Kategori
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_list_item.*
import kotlinx.android.synthetic.main.activity_menu_kategori.*

class ListItemActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    private var dataList = ArrayList<Produk>()

    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_item)

        val data = intent.getParcelableExtra<Kategori>("kategori")

        id = data?.id!!

        mDatabase = FirebaseDatabase.getInstance().getReference("produk")

        tv_nama.text = data?.nama

        iv_back.setOnClickListener {
            finish()
        }


        rc_list_item.layoutManager = LinearLayoutManager(this)

        getData()
    }

    private fun getData() {
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                for (getdatasnapshot in snapshot.getChildren()) {

                    val produk = getdatasnapshot.getValue(Produk::class.java)

                    if (produk!!.kategori == id) {
                        dataList.add(produk!!)
                    }

                }

                rc_list_item.adapter = ListItemAdapter(dataList) {
                    val intent = Intent(this@ListItemActivity, DetailProdukActivity::class.java).putExtra("detailitem", it)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListItemActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}