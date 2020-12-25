package com.candra.sewakameraapp.member

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.utils.Preferences
import kotlinx.android.synthetic.main.fragment_member.*

class MemberFragment : Fragment() {

    lateinit var preferences: Preferences

//    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_member, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preferences = Preferences(activity!!.applicationContext)

        tv_nama.setText(preferences.getValues("nama"))

        if (preferences.getValues("status").equals("ya")) {
            tv_verifed.text = "Sudah Terverifikasi"
        } else {
            tv_verifed.text = "Belum Diverifikasi"
        }

        iv_settings.setOnClickListener {
            startActivity(Intent(context, EditMemberActivity::class.java))
        }

        if (preferences.getValues("gambar")!!.isNotEmpty()) {
            Glide.with(this)
                .load(preferences.getValues("gambar"))
                .apply(RequestOptions.circleCropTransform())
                .into(iv_pic);
        } else {
            iv_pic.setImageResource(R.drawable.user_pic)
        }

//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(
//            MarkerOptions()
//            .position(sydney)
//            .title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//    }


}