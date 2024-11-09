package com.example.prostapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_info = findViewById<Button>(R.id.btninfo)
        val btn_est = findViewById<Button>(R.id.btnest)
        val btn_prev = findViewById<Button>(R.id.btnprev)
        val btn_prof = findViewById<Button>(R.id.btnprof)
        val btn_test = findViewById<Button>(R.id.btntest)
        val btn_fuentes = findViewById<Button>(R.id.btnfuentes)



        btn_info.setOnClickListener{
            startActivity(Intent(baseContext,Informacion::class.java))
        }
        btn_est.setOnClickListener{
            startActivity(Intent(baseContext,Estadistica::class.java))
        }
        btn_prev.setOnClickListener{
            startActivity(Intent(baseContext,Prevencion::class.java))
        }
        btn_test.setOnClickListener{
            startActivity(Intent(baseContext,Test::class.java))
        }
        btn_prof.setOnClickListener{
            startActivity(Intent(baseContext,Profesionales::class.java))
        }
        btn_fuentes.setOnClickListener{
            startActivity(Intent(baseContext,Referencias::class.java))
        }

    }

}