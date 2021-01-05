package com.candra.sewakameraapp.sign

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.candra.sewakameraapp.HomeActivity
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    lateinit var sUsername: String
    lateinit var sPassword: String
    lateinit var sPassword2: String
    lateinit var sNama: String
    lateinit var sEmail: String

    lateinit var mDatabase: DatabaseReference
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mDatabase = FirebaseDatabase.getInstance().getReference("member")
        preferences = Preferences(this)

        iv_back_sign_up.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        btn_sign_up.setOnClickListener {
            sUsername = et_username_signup.text.toString()
            sPassword = et_password_signup.text.toString()
            sPassword2 = et_password2_signup.text.toString()
            sNama = et_nama_signup.text.toString()
            sEmail = et_email_signup.text.toString()


            if (sNama.equals("")) {
                et_nama_signup.error = "Silahkan isi nama anda"
                et_nama_signup.requestFocus()
            }else if (sUsername.equals("")) {
                et_username_signup.error = "Silahkan isi username anda"
                et_username_signup.requestFocus()
            } else if (sEmail.equals("")) {
                et_email_signup.error = "Silahkan isi email anda"
                et_email_signup.requestFocus()
            } else if (sPassword.equals("")) {
                et_password_signup.error = "Silahkan isi password anda"
                et_password_signup.requestFocus()
            }  else if (!(sPassword.equals(sPassword2))) {
                et_password2_signup.error = "Password tidak match"
                et_password2_signup.requestFocus()
            } else {
                var statusUsername = sUsername.indexOf(".")
                if (statusUsername >= 0) {
                    et_username_signup.error = "Silahkan tulis Username Anda tanpa ."
                    et_username_signup.requestFocus()
                } else {
                    saveUsername(sUsername, sPassword, sNama, sEmail)
                }
            }
        }
    }

    private fun saveUsername(sUsername: String, sPassword: String, sNama: String, sEmail: String) {
        var member = Member()
        member.email = sEmail
        member.nama = sNama
        member.password = sPassword
        member.username = sUsername
        member.status = "tidak"

        if (sUsername != null) {
            checkingUsername(sUsername, member)
        }
    }

    private fun checkingUsername(iUsername: String, data: Member) {

        var berhasil: Boolean = true

        mDatabase.child(iUsername).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                var user = snapshot.getValue(Member::class.java)

                if (user == null) {

                    berhasil = false

                    mDatabase.child(iUsername).setValue(data)

                    preferences.setValues("nama", data.nama.toString())
                    preferences.setValues("username", data.username.toString())
                    preferences.setValues("email", data.email.toString())
                    preferences.setValues("password", data.password.toString())
                    preferences.setValues("gambar", "")
                    preferences.setValues("status", data.status.toString())
//                        set auto login
                    preferences.setValues("login", "ya")

                    finishAffinity()

                    var goHome = Intent(
                        this@SignUpActivity,
                        HomeActivity::class.java
                    ).putExtra("nama", data.nama)
                    startActivity(goHome)

                } else if (berhasil) {

                    Toast.makeText(
                        this@SignUpActivity,
                        "Username sudah digunakan!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@SignUpActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}