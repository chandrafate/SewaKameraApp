package com.candra.sewakameraapp.adminmember

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.candra.sewakameraapp.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail_data_member_verifed.*

class DetailDataMemberVerifedActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference

    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_data_member_verifed)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        iv_back_detail_data_member_verifed.setOnClickListener {
            finish()
        }

        username = intent.getStringExtra("username").toString()

        getData()
    }

    private fun getData() {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val memberVerifed = snapshot.child("data_verifikasi/$username").getValue(MemberVerifed::class.java)
                if (memberVerifed != null) {
                    tv_nama_detail_data_member_verifed.text = memberVerifed!!.nama
                    tv_nohp_detail_data_member_verifed.text = memberVerifed.nohp
                    tv_alamat_detail_data_member_verifed.text = memberVerifed.alamat

                    Glide.with(this@DetailDataMemberVerifedActivity).load(memberVerifed.ktp).into(iv_ktp_detail_data_member_verifed)
                    Glide.with(this@DetailDataMemberVerifedActivity).load(memberVerifed.sim).into(iv_sim_detail_data_member_verifed)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailDataMemberVerifedActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}