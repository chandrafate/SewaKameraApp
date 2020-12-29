package com.candra.sewakameraapp.adminhome

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.admindetailbooking.AdminDetailBookingActivity
import com.candra.sewakameraapp.booking.Booking2
import com.candra.sewakameraapp.keranjang.Keranjang
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_list_booking.*
import java.util.ArrayList

class ScanQrActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    private var idProduk = ArrayList<Keranjang>()

    lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                getData(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
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

                   startActivity(Intent(this@ScanQrActivity, AdminDetailBookingActivity::class.java)
                       .putExtra("detailBooking", data)
                       .putExtra("nama", nama.toString())
                       .putExtra("listBarang", idProduk))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ScanQrActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })
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
            finish()
        }

        dialog.show()
    }
}