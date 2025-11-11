package com.example.u2p4conversor;

import android.os.Bundle;                    // Ciclo de vida
import android.util.Log;                     // Logcat (opcional)
import android.view.MenuItem;                // Para manejar el botón “Up” de la barra
import android.widget.Button;                // Referencia al botón Volver

import androidx.appcompat.app.AppCompatActivity;  // Activity base con barra de acción

public class HelpActivity extends AppCompatActivity {

    private static final String TAG = "U2P4Conversor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help); // Carga el layout con tvHelp y btnVolver

        // (Opcional) Log para confirmar que se abre
        Log.d(TAG, "HelpActivity onCreate()");

        // 1) Mostrar botón “Up” (flecha atrás) en la barra superior
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // muestra la flecha
            getSupportActionBar().setTitle("Ayuda");                // título de la pantalla
        }

        // 2) Wire del botón “Volver” (el del layout)
        Button btnVolver = findViewById(R.id.btnVolver); // debe existir en activity_help.xml
        btnVolver.setOnClickListener(v -> {
            Log.d(TAG, "Click btnVolver -> finish()");
            finish();  // cierra HelpActivity y vuelve a MainActivity
        });
    }

    // Maneja la pulsación de la flecha “Up” de la barra para volver también
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // id del botón “Up”
            finish();                                  // mismo efecto que el botón Volver
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
