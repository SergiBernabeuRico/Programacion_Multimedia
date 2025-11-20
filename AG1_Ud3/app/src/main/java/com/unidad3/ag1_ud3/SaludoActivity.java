package com.unidad3.ag1_ud3;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SaludoActivity extends AppCompatActivity {

    private TextView txtSaludo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saludo);

        // Localizar los controles
        txtSaludo = (TextView) findViewById(R.id.TxtSaludo);

        // Recuperamos la información pasada en el intent
        String mensajeRecibido = this.getIntent().getStringExtra("NOMBRE");

        // Construimos el mensaje a mostrar
        txtSaludo.setText("Hola " + mensajeRecibido);


    }
}