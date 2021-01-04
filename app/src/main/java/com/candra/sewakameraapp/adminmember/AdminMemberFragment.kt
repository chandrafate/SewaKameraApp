package com.candra.sewakameraapp.adminmember

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.sign.Member
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_admin_member.*

class AdminMemberFragment : Fragment() {

    private var dataList = ArrayList<Member>()

    lateinit var mDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_member, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        rc_list_member.layoutManager = LinearLayoutManager(context)

        getData()
    }

    private fun getData() {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                for (getdatasnapshot in snapshot.child("member").getChildren()) {

                    val member = getdatasnapshot.getValue(Member::class.java)
                    dataList.add(member!!)

                }

                rc_list_member.adapter = ListMemberAdapter(dataList) {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("${it.nama}")

                    val x = arrayOf("Verifikasi", "Hapus")
                    builder.setItems(x) { dialog, which ->
                        when (which) {
                            0 -> {
                                startActivity(Intent(context,FormVerifikasiMemberActivity::class.java).putExtra("verifikasi", it.username))
                            }
                            1 -> {
                                val builder2 = AlertDialog.Builder(context)
                                builder2.setCancelable(true)
                                builder2.setTitle("Konfirmasi")
                                builder2.setMessage("Apakah Anda ingin menghapus")
                                builder2.setPositiveButton("Tidak") { dialog, which -> }
                                builder2.setNegativeButton("Ya") { dialog, which -> hapusMember(it) }

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
                Toast.makeText(context, "" + error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun hapusMember(it: Member) {
        if (it.gambar!!.isNotEmpty()) {
            val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(it.gambar.toString())
            photoRef.delete()
        }

        mDatabase.child("member/${it.username}").removeValue()
    }
}