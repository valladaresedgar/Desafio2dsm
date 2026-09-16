package com.example.desafio2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnLogin: Button
    private lateinit var textViewRegister: TextView
    private lateinit var textViewForgot: TextView
    private lateinit var checkRemember: CheckBox
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        txtEmail = findViewById(R.id.txtEmailAddress)
        txtPassword = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        checkRemember = findViewById(R.id.checkRemember)
        textViewRegister = findViewById(R.id.textViewRegister)
        textViewForgot = findViewById(R.id.textViewForgot)

        val prefs = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val remember = prefs.getBoolean("remember", false)
        if (remember) {
            txtEmail.setText(prefs.getString("email", ""))
            txtPassword.setText(prefs.getString("password", ""))
            checkRemember.isChecked = true
        }

        btnLogin.setOnClickListener {
            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()
            login(email, password)
        }

        textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        textViewForgot.setOnClickListener {
            val email = txtEmail.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo primero", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Correo enviado", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val prefs = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                    val editor = prefs.edit()
                    if (checkRemember.isChecked) {
                        editor.putString("email", email)
                        editor.putString("password", password)
                        editor.putBoolean("remember", true)
                    } else {
                        editor.clear()
                    }
                    editor.apply()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }
}