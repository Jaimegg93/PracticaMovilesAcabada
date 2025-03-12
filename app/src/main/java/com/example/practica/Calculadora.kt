package com.example.practica

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Calculadora : AppCompatActivity() {

    private var displayValue = "0"        // Valor mostrado en el display
    private var firstOperand: Int? = null  // Primer operando
    private var operation: String? = null   // Operación actual

    // Referencias a los botones
    private lateinit var display: TextView
    private lateinit var btnBorrar: Button
    private lateinit var btnInv: Button
    private lateinit var btnSuma: Button
    private lateinit var btnResta: Button
    private lateinit var btnMultiplicar: Button
    private lateinit var btnDividir: Button
    private lateinit var btnIgual: Button
    private lateinit var btn0: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button
    private lateinit var btn7: Button
    private lateinit var btn8: Button
    private lateinit var btn9: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)
        enableEdgeToEdge()
        display = findViewById(R.id.display)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.calculadora)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Inicializa todos los botones y establece los listeners
        initButtons()
        resetCalculator()

    }

    // Inicializa los botones y configura los listeners
    private fun initButtons() {
        // Ejemplo para inicializar algunos botones
        btnBorrar = findViewById(R.id.btn_borrar)
        btnInv = findViewById(R.id.btn_inv)
        btnIgual = findViewById(R.id.btn_igual)
        btn0 = findViewById(R.id.btn_0)
        btn1 = findViewById(R.id.btn_1)
        btn2 = findViewById(R.id.btn_2)
        btn3 = findViewById(R.id.btn_3)
        btn4 = findViewById(R.id.btn_4)
        btn5 = findViewById(R.id.btn_5)
        btn6 = findViewById(R.id.btn_6)
        btn7 = findViewById(R.id.btn_7)
        btn8 = findViewById(R.id.btn_8)
        btn9 = findViewById(R.id.btn_9)
        btnSuma = findViewById(R.id.btn_sumar)
        btnResta = findViewById(R.id.btn_restar)
        btnMultiplicar = findViewById(R.id.btn_multiplicar)
        btnDividir = findViewById(R.id.btn_dividir)

        // Establece listeners para los números
        btn0.setOnClickListener { appendToDisplay("0") }
        btn1.setOnClickListener { appendToDisplay("1") }
        btn2.setOnClickListener { appendToDisplay("2") }
        btn3.setOnClickListener { appendToDisplay("3") }
        btn4.setOnClickListener { appendToDisplay("4") }
        btn5.setOnClickListener { appendToDisplay("5") }
        btn6.setOnClickListener { appendToDisplay("6") }
        btn7.setOnClickListener { appendToDisplay("7") }
        btn8.setOnClickListener { appendToDisplay("8") }
        btn9.setOnClickListener { appendToDisplay("9") }

        // Establece listeners para las operaciones
        btnSuma.setOnClickListener { setOperation("+") }
        btnResta.setOnClickListener { setOperation("-") }
        btnMultiplicar.setOnClickListener { setOperation("x") }
        btnDividir.setOnClickListener { setOperation("/") }

        // Listener para igual
        btnIgual.setOnClickListener { calculateResult() }

        // Listener para invertir el número
        btnInv.setOnClickListener { invertDisplay() }

        // Listener para borrar
        btnBorrar.setOnClickListener { resetCalculator() }
    }

    private fun appendToDisplay(number: String) {
        // Si el display es "0", reemplaza con el número
        displayValue = if (displayValue == "0") number else displayValue + number
        display.text = displayValue
    }

    private fun setOperation(operador: String) {
        if (firstOperand == null) {
            // Guarda el primer operando y la operación
            firstOperand = displayValue.toInt()
            operation = operador
            displayValue = "0" // Resetea el display
        }

    }
    private fun calculateResult() {
        val secondOperand = displayValue.toInt()
        var result: Int? = null

        // Realiza la operación basada en el operador
        when (operation) {
            "+" -> result = firstOperand?.plus(secondOperand)
            "-" -> result = firstOperand?.minus(secondOperand)
            "x" -> result = firstOperand?.times(secondOperand)
            "/" -> result = if (secondOperand != 0) firstOperand?.div(secondOperand) else null
        }

        // Actualiza el display y resetea la operación
        if (result != null) {
            displayValue = result.toString()
            display.text = displayValue
        }
        operation = null // Resetea la operación
        firstOperand = null // Resetea el primer operando
    }

    private fun invertDisplay() {
        displayValue = displayValue.reversed()
        display.text = displayValue
    }

    private fun resetCalculator() {
        displayValue = "0"
        display.text = displayValue
        firstOperand = null
        operation = null
    }

}
