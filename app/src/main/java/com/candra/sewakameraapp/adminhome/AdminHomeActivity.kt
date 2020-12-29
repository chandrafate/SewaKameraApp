package com.candra.sewakameraapp.adminhome

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.admindetailbooking.AdminDetailBookingActivity
import com.candra.sewakameraapp.booking.Booking2
import com.candra.sewakameraapp.keranjang.Keranjang
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_admin_home.*
import java.util.ArrayList


class AdminHomeActivity : AppCompatActivity() {

    val CAMERA_RO = 102

    private var idProduk = ArrayList<Keranjang>()

    lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        iv_scan_menu.setOnClickListener {
            cekPermission(android.Manifest.permission.CAMERA, "camera", CAMERA_RO)
            showScanMenu()
        }
    }

    private fun cekPermission(permission: String, name: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {

                }
                shouldShowRequestPermissionRationale(permission) -> showDialog(permission, name, requestCode)

                else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        when(requestCode) {
            CAMERA_RO -> innerCheck("camera")
        }
    }

    private fun showDialog(permission: String, name: String,requestCode: Int) {
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage("Permission to access your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("OK") {dialog, which ->
                ActivityCompat.requestPermissions(this@AdminHomeActivity, arrayOf(permission), requestCode)
            }
        }

        val dialog = builder.create()
        dialog.show()
    }



    fun showScanMenu() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_scan)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val btnClose = dialog.findViewById(R.id.btn_close) as Button
        val ivqr = dialog.findViewById(R.id.iv_scan) as ImageView
        val ivinput = dialog.findViewById(R.id.iv_input) as ImageView

        ivqr.setOnClickListener {

            startActivity(Intent(this, ScanQrActivity::class.java))
        }

        ivinput.setOnClickListener {
            dialogInputKode()
            dialog.dismiss()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun dialogTidakKetemu() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cari_booking_gagal)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val btnClose = dialog.findViewById(R.id.btn_close) as Button

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun dialogInputKode() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.input_kode_booking)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val btnClose = dialog.findViewById(R.id.iv_back3) as ImageView
        val btnSubmit = dialog.findViewById(R.id.btn_submit) as Button
        val etInput = dialog.findViewById(R.id.et_input_kode) as EditText

        btnSubmit.setOnClickListener {
            getData(etInput.text.toString())
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getData(id: String) {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val data = snapshot.child("booking/$id").getValue(Booking2::class.java)


                idProduk.clear()

                for (getdatasnapshot in snapshot.child("booking/$id/barang").getChildren()) {
                    val keranjang = getdatasnapshot.getValue(Keranjang::class.java)
                    idProduk.add(keranjang!!)
                }

                if (data == null) {
                    dialogTidakKetemu()
                } else {
                    val nama = snapshot.child("member/${data!!.username}/nama").getValue()

                    startActivity(Intent(this@AdminHomeActivity, AdminDetailBookingActivity::class.java)
                        .putExtra("detailBooking", data)
                        .putExtra("nama", nama.toString())
                        .putExtra("listBarang", idProduk))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminHomeActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}