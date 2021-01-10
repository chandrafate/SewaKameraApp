package com.candra.sewakameraapp.booking

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.barang.Barang
import com.candra.sewakameraapp.keranjang.Keranjang
import com.candra.sewakameraapp.keranjang.KeranjangAdapter
import com.candra.sewakameraapp.transaksi.Transaksi
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.zxing.WriterException
import kotlinx.android.synthetic.main.activity_detail_booking.*
import kotlinx.android.synthetic.main.konfirmasi_pembayaran_dialog.*
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class DetailBookingActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    lateinit var mDatabase2: DatabaseReference
    lateinit var mDatabase3: DatabaseReference
    lateinit var idBooking: String

    private var dataList = ArrayList<Barang>()
    private var idProduk = ArrayList<Keranjang>()

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    public var ada: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_booking)

        val data = intent.getParcelableExtra<Booking2>("detailBooking")

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase2 = FirebaseDatabase.getInstance().getReference("booking")
        mDatabase3 = FirebaseDatabase.getInstance().getReference("transaksi")

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        idBooking = data!!.key.toString()

        tv_jumlah_detail_booking.text = data?.jumlah_item.toString() + " Items"

        tv_tgl_in_detail_booking.text = data.tgl_in
        tv_tgl_out_detail_booking.text = data.tgl_out
        tv_hari_detail_booking.text = data.tgl_in?.let { data.tgl_out?.let { it1 -> hitungHari(it, it1) } }
        tv_status_detail_booking.text = data.status
        tv_denda_detail_booking.text = hitungDenda(data.tgl_out!!, data.total!!)
        tv_total_detail_booking.text = formatHarga(data.total!!).substring(0, formatHarga(data.total!!).length - 3)

        iv_back_detail_booking.setOnClickListener {
            finish()
        }

        rc_item_detail_booking.layoutManager = LinearLayoutManager(this)

        getData()

        if (data.status.equals("pending")) {
            btn_konfirm_qr_detail_booking.setBackgroundResource(R.drawable.btn_primary)
        } else if (data.status.equals("success")) {
            btn_konfirm_qr_detail_booking.setBackgroundResource(R.drawable.btn_secondary)
        }

        checkKonfirm(idBooking)

        btn_konfirm_qr_detail_booking.setOnClickListener {
            if (ada) {
                showCode(idBooking)
            } else {
                showDialog()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun getData() {
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    idProduk.clear()
                    dataList.clear()

                    for (getdatasnapshot in snapshot.child("booking/$idBooking/barang").getChildren()) {
                        val keranjang = getdatasnapshot.getValue(Keranjang::class.java)
                        idProduk.add(keranjang!!)
                    }

                    idProduk.forEach {

                        for (getdatasnapshot in snapshot.child("produk").getChildren()) {

                            val produk = getdatasnapshot.getValue(Barang::class.java)

                            if (produk!!.id.equals(it.id)) {
                                dataList.add(produk!!)
                            }
                        }
                    }
                    rc_item_detail_booking.adapter = KeranjangAdapter(dataList) {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@DetailBookingActivity,
                        "" + error.message,
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
    }

    private fun formatHarga(denda: Int): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(denda).toString()
    }

    private fun hitungHari(tanggalIn: String, tanggalOut: String): String {
        val format = SimpleDateFormat("dd/MM/yyyy")
        val days = TimeUnit.DAYS.convert(
            format.parse(tanggalOut).getTime() -
                    format.parse(tanggalIn).getTime(),
            TimeUnit.MILLISECONDS
        )
        return days.toString() + " Hari"
    }


    fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.konfirmasi_pembayaran_dialog)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val btnClose = dialog.findViewById(R.id.iv_close) as ImageView
        val btn_choose_image = dialog.findViewById(R.id.btn_chose) as TextView
        val btn_upload_image = dialog.findViewById(R.id.btn_upload) as Button
        val btn_ovo = dialog.findViewById(R.id.iv_ovo) as ImageView
        val btn_dana = dialog.findViewById(R.id.iv_dana) as ImageView
        val btn_cod = dialog.findViewById(R.id.iv_cod) as ImageView
        val textView32 = dialog.findViewById(R.id.textView32) as TextView
        var tvnorek = dialog.findViewById(R.id.tv_no_rek) as TextView
        var textView35 = dialog.findViewById(R.id.textView35) as TextView
        var ettransfer = dialog.findViewById(R.id.et_transfer) as EditText


        var pembayaran = "ovo"

        btn_ovo.setOnClickListener {
            pembayaran = "ovo"
            btn_ovo.setImageResource(R.drawable.ovo_selected)
            btn_dana.setImageResource(R.drawable.dana_unselected)
            btn_cod.setImageResource(R.drawable.cod_unselected)
            btn_choose_image.visibility = View.VISIBLE
            textView32.visibility = View.VISIBLE
            tvnorek.visibility = View.VISIBLE
            ettransfer.visibility = View.VISIBLE
            tvnorek.text = "081 2345 6789"
            textView35.text =
                "1. Silahkan transfer sesuai total booking anda \n2. Harap mengirim foto / screenshot bukti pembayaran untuk mempercepat proses verifikasi\n3. Transfer sesuai nomor tujuan OVO di bawah ini :"
        }

        btn_dana.setOnClickListener {
            pembayaran = "dana"
            btn_ovo.setImageResource(R.drawable.ovo_unselected)
            btn_dana.setImageResource(R.drawable.dana_selected)
            btn_cod.setImageResource(R.drawable.cod_unselected)
            btn_choose_image.visibility = View.VISIBLE
            textView32.visibility = View.VISIBLE
            tvnorek.visibility = View.VISIBLE
            ettransfer.visibility = View.VISIBLE
            tvnorek.text = "089 8765 4321"
            textView35.text = "1. Silahkan transfer sesuai total booking anda \n2. Harap mengirim foto / screenshot bukti pembayaran untuk mempercepat proses verifikasi\n3. Transfer sesuai nomor tujuan DANA di bawah ini :"
        }

        btn_cod.setOnClickListener {
            pembayaran = "ditempat"
            btn_ovo.setImageResource(R.drawable.ovo_unselected)
            btn_dana.setImageResource(R.drawable.dana_unselected)
            btn_cod.setImageResource(R.drawable.cod_selected)
            btn_choose_image.visibility = View.INVISIBLE
            textView32.visibility = View.INVISIBLE
            tvnorek.visibility = View.INVISIBLE
            ettransfer.visibility = View.INVISIBLE
            textView35.text =
                "1. Pembayaran langsung di toko\n2. Kekurangan tidak dapat dipastikan stok barang tersedia"
        }

        btn_choose_image.setOnClickListener { launchGallery() }
        btn_upload_image.setOnClickListener {
            if (pembayaran.equals("ditempat")) {
                insertTransaksi("","ditempat", "0")
            } else {
                uploadImage(pembayaran, ettransfer.text.toString())
            }

        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showCode(idbooking: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.qr_code_booking)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val btnClose = dialog.findViewById(R.id.btn_close) as Button
        val iv_qr = dialog.findViewById(R.id.iv_sdc) as ImageView
        val tv_kode = dialog.findViewById(R.id.tv_kode_booking) as TextView

        tv_kode.text = idbooking
        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        val qrgEncoder = QRGEncoder(idbooking, null, QRGContents.Type.TEXT, 500)
        try {
            // Getting QR-Code as Bitmap
            val bitmap = qrgEncoder.bitmap
            // Setting Bitmap to ImageView
            iv_qr.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.v("qr",e.toString())
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data

            try {

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(pembayaran: String, transfer: String) {
        if (filePath != null) {
            var progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()


            val ref = storageReference?.child("bukti_transfer/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask =
                uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                })?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss()

                        ref.downloadUrl.addOnSuccessListener {
                            insertTransaksi(it.toString(), pembayaran, transfer)

                        }
                    } else {
                        // Handle failures
                    }
                }?.addOnFailureListener {

                }
        } else {
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun insertTransaksi(bukti_transfer: String, pembayaran: String, transfer: String) {
        val data = intent.getParcelableExtra<Booking2>("detailBooking")

        var key = mDatabase3.push().getKey()
        var transaksi = Transaksi()
        transaksi.key = key
        transaksi.bukti_transfer = bukti_transfer
        transaksi.kode_booking = data?.key
        transaksi.pembayaran = pembayaran
        transaksi.total = transfer.toInt()
        transaksi.username = data?.username
        transaksi.tanggal = SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault()).format(Date())

        mDatabase3.child(key!!).setValue(transaksi).addOnSuccessListener {
            startActivity(
                Intent(this, SuccessKonfirmPembayaranActivity::class.java).putExtra(
                    "detailBooking",
                    data
                )
            )
        }
    }

    private fun checkKonfirm(idbooking: String) {

        mDatabase3.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (getdatasnapshot in snapshot.getChildren()) {

                    val produk = getdatasnapshot.child("kode_booking").getValue()

                    if (produk!!.equals(idbooking)) {
                        ada = true
                        btn_konfirm_qr_detail_booking.setText("Tampilkan Kode Booking")

                    }
                }
                if (!ada) {
                    tv_status_detail_booking.text = "Belum Konfirmasi Pembayaran"
                    btn_konfirm_qr_detail_booking.setText("Konfirmasi Pembayaran")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun hitungDenda(tglout: String, total: Int): String {
        val skrang = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        val format = SimpleDateFormat("dd/MM/yyyy")
        val days = TimeUnit.DAYS.convert(
            format.parse(skrang).getTime() -
                    format.parse(tglout).getTime(),
            TimeUnit.MILLISECONDS
        )

        val totall: Int

        if (days > 0) {
            totall = total * days.toInt()
            tv_status_detail_booking.text = "Belum Dikembalikan"
        } else {
            totall = 0
        }

        return formatHarga(totall).substring(0, formatHarga(totall).length - 3)
    }
}