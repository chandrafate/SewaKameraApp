package com.candra.sewakameraapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.produk.Produk
import com.candra.sewakameraapp.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.android.synthetic.main.activity_keranjang.iv_back
import kotlinx.android.synthetic.main.activity_list_item.*

class KeranjangActivity : AppCompatActivity() {

    lateinit var preferences: Preferences

    lateinit var mDatabase: DatabaseReference
    lateinit var mDatabase2: DatabaseReference

    var dataList = ArrayList<Produk>()
    var idProduk = ArrayList<Keranjang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)

        preferences = Preferences(this)

        mDatabase = FirebaseDatabase.getInstance().getReference("produk")
        mDatabase2 = FirebaseDatabase.getInstance().getReference("member")
            .child(preferences.getValues("username").toString()).child("keranjang")

        iv_back.setOnClickListener {
            finish()
        }

        rc_keranjang.layoutManager = LinearLayoutManager(this)

        getData()
    }


    private fun getData() {
        mDatabase2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                idProduk.clear()

                for (getdatasnapshot in snapshot.getChildren()) {
                    val keranjang = getdatasnapshot.getValue(Keranjang::class.java)
                    idProduk.add(keranjang!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@KeranjangActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })

        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                idProduk.forEach {

                    for (getdatasnapshot in snapshot.getChildren()) {

                        val produk = getdatasnapshot.getValue(Produk::class.java)

                        if (produk!!.id.equals(it.id)) {
                            dataList.add(produk!!)
                        }
                    }
                }


                rc_keranjang.adapter = KeranjangAdapter(dataList) {
//                    val intent = Intent(this@KeranjangActivity, DetailProdukActivity::class.java)
//                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@KeranjangActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}