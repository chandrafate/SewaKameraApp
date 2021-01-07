package com.candra.sewakameraapp.adminpromo

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.adminBarang.FormTambahbarangActivity
import com.candra.sewakameraapp.barang.Barang
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_admin_list_promo.*

class AdminListPromoActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference

    private var barangList = ArrayList<Barang>()
    private var promoList = ArrayList<Promo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_promo)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        iv_back_list_promo_admin.setOnClickListener {
            finish()
        }

        btn_add_list_promo_admin.setOnClickListener {
            startActivity(Intent(this, TambahPromoActivity::class.java))
        }

        rc_list_promo_admin.layoutManager = LinearLayoutManager(this)

        getData()

    }

    private fun getData() {
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                barangList.clear()

                for (getdatasnapsot in snapshot.child("diskon").children) {

                    val promo = getdatasnapsot.getValue(Promo::class.java)

                    for (getdatasnapsot2 in snapshot.child("produk").children) {

                        val barang = getdatasnapsot2.getValue(Barang::class.java)

                        if (promo!!.id.equals(barang!!.id)) {
                            barangList.add(barang)
                            promoList.add(promo)
                        }
                    }
                }

                rc_list_promo_admin.adapter = ListPromoAdminAdapter(barangList, promoList) {
                    val builder = AlertDialog.Builder(this@AdminListPromoActivity)
                    builder.setTitle("Aksi")

                    val x = arrayOf("Hapus")
                    builder.setItems(x) { dialog, which ->
                        when (which) {
                            0 -> {
                                val builder2 = AlertDialog.Builder(this@AdminListPromoActivity)
                                builder2.setCancelable(true)
                                builder2.setTitle("Konfirmasi")
                                builder2.setMessage("Apakah Anda ingin menghapus")
                                builder2.setPositiveButton("Tidak") { dialog, which -> }
                                builder2.setNegativeButton("Ya") { dialog, which -> hapusPromo(it)}

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
                Toast.makeText(this@AdminListPromoActivity, error.message, Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    private fun hapusPromo(it: Promo) {
//        balikin harga
        mDatabase.child("produk/${it.id}/harga").setValue(it.harga)
//        hapus promo
        mDatabase.child("diskon/${it.key}").removeValue().addOnSuccessListener {
            Toast.makeText(this, "Berhasil Dihapus", Toast.LENGTH_SHORT).show()
        }
    }
}