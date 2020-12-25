package com.candra.sewakameraapp.sign

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.candra.sewakameraapp.HomeActivity
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {

    lateinit var iUsername :String
    lateinit var iPassword :String

    lateinit var mDatabase: DatabaseReference
    lateinit var preferences: Preferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mDatabase = FirebaseDatabase.getInstance().getReference("member")
        preferences = Preferences(this)

        //        cek jika sudah login
        if (preferences.getValues("login").equals("ya")) {
            finishAffinity()

            startActivity(Intent(this, HomeActivity::class.java))
        }

        btn_sign_up.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btn_sign_in.setOnClickListener {

            iUsername = et_username.text.toString()
            iPassword = et_password.text.toString()

            if (iUsername.equals("")) {
                et_username.error = "Silahkan tulis Email Anda"
                et_username.requestFocus()
            } else if (iPassword.equals("")) {
                et_password.error = "Silahkan tulis Password Anda"
                et_password.requestFocus()
            } else {
                var statusUsername = iUsername.indexOf(".")
                if (statusUsername >=0) {
                    et_username.error = "Silahkan tulis email dengan benar ."
                    et_username.requestFocus()
                } else {
                    pushLogin(iUsername, iPassword)
                }
            }


//            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun pushLogin(iUsername: String, iPassword: String) {
        mDatabase.child(iUsername).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val member = dataSnapshot.getValue(Member::class.java)

                if (member == null) {
                    Toast.makeText(this@SignInActivity, "Member tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    if (member.password.equals(iPassword)) {
                        preferences.setValues("email", member.email.toString())
                        preferences.setValues("nama", member.nama.toString())
                        preferences.setValues("status", member.status.toString())
                        preferences.setValues("gambar", member.gambar.toString())
                        preferences.setValues("username", member.username.toString())
                        preferences.setValues("password", member.password.toString())
//                        set auto login
                        preferences.setValues("login", "ya")

                        finishAffinity()

                        val intent = Intent(
                            this@SignInActivity,
                            HomeActivity::class.java
                        )
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            "Password Anda Salah",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignInActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}