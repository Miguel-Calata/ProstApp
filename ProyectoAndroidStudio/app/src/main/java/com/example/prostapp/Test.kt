package com.example.prostapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Test : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Configura la barra de herramientas
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Calculadora de riesgos"
            setDisplayHomeAsUpEnabled(true)
        }

        // Configuración de insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar elementos de la interfaz de usuario
        val editTextEdad = findViewById<EditText>(R.id.editTextEdad)
        val cbConocePSA = findViewById<CheckBox>(R.id.checkBoxConocePSA)
        val editTextPSA = findViewById<EditText>(R.id.editTextPSA)
        val spinnerRaza = findViewById<Spinner>(R.id.spinnerRaza)
        val checkBoxHistorialFamiliar = findViewById<CheckBox>(R.id.checkBoxHistorialFamiliar)
        val editTextIMC = findViewById<EditText>(R.id.editTextIMC)
        val checkBoxTabaquismo = findViewById<CheckBox>(R.id.checkBoxTabaquismo)
        val buttonCalcularRiesgo = findViewById<Button>(R.id.buttonCalcularRiesgo)
        val textViewResultado = findViewById<TextView>(R.id.textViewResultado)

        // Ocultar el campo de PSA por defecto
        editTextPSA.visibility = View.GONE

        // Mostrar u ocultar el campo de PSA basado en el CheckBox
        cbConocePSA.setOnCheckedChangeListener { _, isChecked ->
            editTextPSA.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Configuración del botón
        buttonCalcularRiesgo.setOnClickListener {
            // Ocultar el teclado
            ocultarTeclado()

            val razaSeleccionada = spinnerRaza.selectedItem.toString()

            if (razaSeleccionada == "Seleccionar") {
                // Muestra un mensaje de advertencia o evita el cálculo
                Toast.makeText(this, "Por favor, selecciona una raza", Toast.LENGTH_SHORT).show()
                textViewResultado.text = "Por favor, ingresa todos los valores necesarios correctamente."
                return@setOnClickListener
            }

            // Obtener y procesar los valores ingresados
            val edad = editTextEdad.text.toString().toIntOrNull() ?: 0
            val psa = if (cbConocePSA.isChecked) editTextPSA.text.toString().toDoubleOrNull() else null
            val raza = spinnerRaza.selectedItem.toString()
            val historialFamiliar = checkBoxHistorialFamiliar.isChecked
            val imc = editTextIMC.text.toString().toDoubleOrNull() ?: 0.0
            val tabaquismo = checkBoxTabaquismo.isChecked

            // Validar si los valores esenciales no son nulos
            if (edad == 0 || (cbConocePSA.isChecked && psa == null) || imc == 0.0) {
                textViewResultado.text = "Por favor, ingresa todos los valores necesarios correctamente."
                return@setOnClickListener
            }

            // Llamar a la función de cálculo de riesgo
            val resultado = calcularRiesgoCancerProstata(
                raza = raza,
                edad = edad,
                psa = psa,
                historialFamiliar = historialFamiliar,
                indiceMasaCorporal = imc,
                tabaquismo = tabaquismo
            )

            textViewResultado.text = resultado
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun ocultarTeclado() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this) // Si no hay vista enfocada, crea una nueva vista vacía
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun calcularRiesgoCancerProstata(
        raza: String,
        edad: Int,
        psa: Double? = null,
        historialFamiliar: Boolean,
        indiceMasaCorporal: Double,
        tabaquismo: Boolean
    ): String {
        var riesgo = 0.0

        // Factor por raza
        riesgo += when (raza.toLowerCase()) {
            "afroamericano" -> 1.5
            "caucásico" -> 1.2
            "hispano" -> 1.1
            else -> 1.0
        }

        // Factor por edad
        riesgo += when {
            edad >= 60 -> 2.0
            edad in 50..59 -> 1.5
            edad in 40..49 -> 1.0
            else -> 0.5
        }

        // Calcular PSA si no se proporciona
        val nivelPSA = psa ?: calcularPSAPorEdad(edad)

        // Factor por nivel de PSA
        riesgo += when {
            nivelPSA >= 10 -> 2.5
            nivelPSA in 4.0..9.9 -> 1.5
            nivelPSA < 4.0 -> 0.5
            else -> 0.0
        }

        // Factor por historial familiar
        if (historialFamiliar) riesgo += 1.3

        // Factor por índice de masa corporal (IMC)
        riesgo += when {
            indiceMasaCorporal >= 30 -> 1.2 // Obesidad
            indiceMasaCorporal in 25.0..29.9 -> 0.8 // Sobrepeso
            else -> 0.5 // Normal o bajo peso
        }

        // Factor por tabaquismo
        if (tabaquismo) riesgo += 1.3

        // Determinar nivel de riesgo en función del puntaje acumulado
        return when {
            riesgo >= 7 -> "Riesgo alto. Se recomienda consulta médica urgente."
            riesgo in 4.5..6.9 -> "Riesgo moderado. Consulta con tu médico."
            else -> "Riesgo bajo. Revisión médica periódica recomendada."
        }
    }

    private fun calcularPSAPorEdad(edad: Int): Double {
        return when {
            edad >= 60 -> 5.0
            edad in 50..59 -> 3.5
            edad in 40..49 -> 2.5
            else -> 1.5
        }
    }
}
