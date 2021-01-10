package com.candra.sewakameraapp.sign

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.candra.sewakameraapp.HomeActivity
import com.candra.sewakameraapp.R
import com.candra.sewakameraapp.admin.Admin
import com.candra.sewakameraapp.admin.AdminActivity
import com.candra.sewakameraapp.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {

    lateinit var iUsername: String
    lateinit var iPassword: String

    lateinit var mDatabase: DatabaseReference
    lateinit var preferences: Preferences

    var loginAdmin: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        preferences = Preferences(this)

        //                        auto skip intro
        preferences.setValues("intro", "sudah")

        //        cek jika sudah login
        if (preferences.getValues("login").equals("member")) {
            syncMember(preferences.getValues("username").toString())
            finishAffinity()

            startActivity(Intent(this, HomeActivity::class.java))
        } else if (preferences.getValues("login").equals("admin")) {
            finishAffinity()

            startActivity(Intent(this, AdminActivity::class.java))
        }

        btn_sign_up.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btn_sign_in.setOnClickListener {

            iUsername = et_username_signin.text.toString()
            iPassword = et_password_signin.text.toString()

            if (iUsername.equals("")) {
                et_username_signin.error = "Silahkan tulis Email Anda"
                et_username_signin.requestFocus()
            } else if (iPassword.equals("")) {
                et_password_signin.error = "Silahkan tulis Password Anda"
                et_password_signin.requestFocus()
            } else {
                var statusUsername = iUsername.indexOf(".")
                if (statusUsername >= 0) {
                    et_username_signin.error = "Silahkan tulis email dengan benar ."
                    et_username_signin.requestFocus()
                } else {
                    pushLogin(iUsername, iPassword)
                }
            }


//            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun pushLogin(iUsername: String, iPassword: String) {
        mDatabase.child("admin").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var admin = dataSnapshot.getValue(Admin::class.java)

                if (admin != null) {

                    if (admin.username.equals(iUsername) && admin.password.equals(iPassword)) {
                        //                        set auto login
                        preferences.setValues("login", "admin")

                        finishAffinity()

                        loginAdmin = false

                        val intent = Intent(
                            this@SignInActivity,
                            AdminActivity::class.java
                        )
                        startActivity(intent)

                        return
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignInActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }

        })


        mDatabase.child("member/$iUsername")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val member = dataSnapshot.getValue(Member::class.java)

                    if (member == null) {
                        if (loginAdmin) {
                            Toast.makeText(
                                this@SignInActivity,
                                "Member tidak ditemukan",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        if (member.password.equals(iPassword)) {
                            preferences.setValues("email", member.email.toString())
                            preferences.setValues("nama", member.nama.toString())
                            preferences.setValues("status", member.status.toString())
                            preferences.setValues("gambar", member.gambar.toString())
                            preferences.setValues("username", member.username.toString())
                            preferences.setValues("password", member.password.toString())
//                        set auto login
                            preferences.setValues("login", "member")

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
                    Toast.makeText(this@SignInActivity, "" + error.message, Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun syncMember(username: String) {
        mDatabase.child("member/$username").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val member = snapshot.getValue(Member::class.java)

                if (member != null) {
                    preferences.setValues("email", member.email.toString())
                    preferences.setValues("nama", member.nama.toString())
                    preferences.setValues("status", member.status.toString())
                    preferences.setValues("gambar", member.gambar.toString())
                    preferences.setValues("password", member.password.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignInActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}