package com.candra.sewakameraapp.member

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        tv_nama_member_fragment.setText(preferences.getValues("nama"))

        if (preferences.getValues("status").equals("ya")) {
            tv_verifed_member_fragment.visibility = View.VISIBLE
            tv_unverifed_member_fragment.visibility = View.INVISIBLE
        } else {
            tv_verifed_member_fragment.visibility = View.INVISIBLE
            tv_unverifed_member_fragment.visibility = View.VISIBLE
        }

        iv_wa_fragment_member.setOnClickListener {
            val contact = "+62 82123457042" // use country code with your phone number

            val url = "https://api.whatsapp.com/send?phone=$contact"
            try {
                val pm = context!!.packageManager
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(
                    context,
                    "Whatsapp app not installed in your phone",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }

        iv_ig_fragment_member.setOnClickListener {
            val uri = Uri.parse("https://instagram.com/_u/chandrafate")
            val likeIng = Intent(Intent.ACTION_VIEW, uri)

            likeIng.setPackage("com.instagram.android")

            try {
                startActivity(likeIng)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://instagram.com")
                    )
                )
            }
        }

        iv_fb_fragment_member.setOnClickListener {
            val url = "https://facebook.com/chandrafate"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        rl_google_maps_fragment_member.setOnClickListener {

            val url = "https://maps.app.goo.gl/wmM2QWn7yXDVs7yn7"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        iv_settings_member_fragment.setOnClickListener {
            startActivity(Intent(context, EditMemberActivity::class.java))
        }

        if (preferences.getValues("gambar")!!.isNotEmpty()) {
            Glide.with(this)
                .load(preferences.getValues("gambar"))
                .apply(RequestOptions.circleCropTransform())
                .into(iv_pic_member_fragment);
        } else {
            iv_pic_member_fragment.setImageResource(R.drawable.user_pic)
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