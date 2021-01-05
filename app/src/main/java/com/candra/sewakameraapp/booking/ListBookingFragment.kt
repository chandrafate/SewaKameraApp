package com.candra.sewakameraapp.booking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_list_booking.*


class ListBookingFragment : Fragment() {

    lateinit var preferences: Preferences

    private var dataList = ArrayList<Booking2>()

    lateinit var mDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_booking, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preferences = Preferences(activity!!.applicationContext)

        mDatabase = FirebaseDatabase.getInstance().getReference("booking")

        rc_list_fragment_booking.layoutManager = LinearLayoutManager(context)

        getData()
    }


    private fun getData() {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                dataList.clear()

                for (getdatasnapshot in snapshot.getChildren()) {

                    val booking = getdatasnapshot.getValue(Booking2::class.java)

                    if (booking!!.username == preferences.getValues("username")) {
                        dataList.add(
                            Booking2(
                                booking.key,
                                booking.denda,
                                booking.status,
                                booking.tgl_in,
                                booking.tgl_out,
                                booking.total,
                                booking.username,
                                getdatasnapshot.child("barang").getChildrenCount()
                            )
                        )
                    }

                }

                rc_list_fragment_booking.adapter = ListBookingAdapter(dataList) {
                    val intent = Intent(context, DetailBookingActivity::class.java).putExtra("detailBooking", it)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

}