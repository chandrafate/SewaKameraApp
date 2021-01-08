package com.candra.sewakameraapp.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.keranjang.KeranjangActivity
import com.candra.sewakameraapp.kategori.MenuKategoriActivity
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.adminpromo.Promo
import com.candra.sewakameraapp.barang.Barang
import com.candra.sewakameraapp.barang.DetailBarangActivity
import com.candra.sewakameraapp.barang.ListItemActivity
import com.candra.sewakameraapp.kategori.Kategori
import com.candra.sewakameraapp.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    lateinit var preferences: Preferences

    private var kategoriList = ArrayList<Kategori>()
    private var barangList = ArrayList<Barang>()
    private var promoList = ArrayList<Promo>()

    lateinit var mDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preferences = Preferences(activity!!.applicationContext)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        tv_nama_home.setText(preferences.getValues("nama"))

        btn_kategori_home.setOnClickListener {
            startActivity(Intent(context, MenuKategoriActivity::class.java))
        }

        iv_keranjang_home.setOnClickListener {
            startActivity(Intent(context, KeranjangActivity::class.java))
        }

        rc_item_promo_home.layoutManager = LinearLayoutManager(context)

        rc_item_kategori_home.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        getData()
    }


    private fun getData() {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                barangList.clear()
                promoList.clear()
                kategoriList.clear()

//                get data kategori
                for (getdatasnapsot in snapshot.child("kategori").children) {
                    val kategori = getdatasnapsot.getValue(Kategori::class.java)
                    kategoriList.add(kategori!!)
                }

//                get data promo
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


                rc_item_promo_home.adapter = ListPromoAdapter(barangList, promoList) {
                    startActivity(Intent(context, DetailBarangActivity::class.java).putExtra("detailitem", it))
                }

                rc_item_kategori_home.adapter = KategoriAdapter(kategoriList) {
                    startActivity(Intent(context, ListItemActivity::class.java).putExtra("kategori", it))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

}