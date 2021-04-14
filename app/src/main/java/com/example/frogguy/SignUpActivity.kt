package com.example.frogguy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        var email = findViewById<EditText>(R.id.emailSignUp)
        var pwd = findViewById<EditText>(R.id.pwdSignUp)
        var pwdConfirm = findViewById<EditText>(R.id.pwdConfirmSignUp)
        var signUpBtn = findViewById<Button>(R.id.signUpButton)

        signUpBtn.setOnClickListener {
            var validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()
            var pwdMatches = pwd.text.toString().equals(pwdConfirm.text.toString())
            if(validEmail) {
                if(pwdMatches) {
                    auth.createUserWithEmailAndPassword(email.text.toString(), pwd.text.toString())
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                Toast.makeText(this, "User created", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                        }
                }
                else {
                    Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            }
        }

    }
}