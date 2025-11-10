package com.example.u2p4conversor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.RadioButton;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setUI();
    }

    private void setUI() {
        EditText etPulgada = findViewById(R.id.et_Pulgada);   // Referencia al campo de texto donde el usuario introduce el valor en pulgadas o centímetros
        EditText etResultado = findViewById(R.id.et_Resultado); // Referencia al campo de texto donde se mostrará el resultado de la conversión
        Button buttonConvertir = findViewById(R.id.button_Convertir); // Referencia al botón que el usuario pulsa para realizar la conversión
        RadioButton rbInToCm = findViewById(R.id.rbInToCm);   // Referencia al botón de opción "Pulgadas → Centímetros"
        RadioButton rbCmToIn = findViewById(R.id.rbCmToIn);   // Referencia al botón de opción "Centímetros → Pulgadas"


        // buttonConvertir.setOnClickListener(view -> etResultado.setText(convertir(etPulgada.getText().toString())));

        buttonConvertir.setOnClickListener(view -> {
            // Leemos el texto del EditText de entrada y quitamos espacios
            String txt = etPulgada.getText().toString().trim();

            // Si está vacío, limpiamos el resultado y salimos
            if (txt.isEmpty()) {
                etResultado.setText("");
                return;
            }

            // Permitimos que el usuario use coma o punto como separador decimal
            double valor = Double.parseDouble(txt.replace(',', '.'));

            // (1.1) Dirección de conversión según el RadioButton marcado:
            // - Si rbInToCm está marcado: pulgadas → centímetros (× 2.54)
            // - Si rbCmToIn está marcado: centímetros → pulgadas (÷ 2.54)
            double res = rbInToCm.isChecked() ? (valor * 2.54) : (valor / 2.54);

            // (1.2) Mostramos el resultado con DOS decimales
            etResultado.setText(formatTwo(res));
        });

    }
    /*private String convertir(String pulgadaText) {
        double pulgadaValue = Double.parseDouble(pulgadaText) * 2.54;
        return String.valueOf(pulgadaValue);
    } Metodo deshabilitado por no usar la lambda que lo llama */

    // Formatea un double a 2 decimales (por ejemplo: 3.1 → "3.10")
    private String formatTwo(double n) {
        return new DecimalFormat("#0.00").format(n);
    }

}