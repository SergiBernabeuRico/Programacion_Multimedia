package com.unidad3.banco_seberi;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_NOMBRE = "EXTRA_NOMBRE_USUARIO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        }); */

        TextView txtSaludo = findViewById(R.id.txtSaludo);

        //  1. Leer el Intent que ha abierto esta Activity
        String nombre = getIntent().getStringExtra(EXTRA_NOMBRE);

        //  2. Si viene nulo, ponemos "usuario" por defecto
        if (nombre == null || nombre.isEmpty()) {
            nombre = "usuario";
        }

        //  3. Construimos el mensaje completo
        String mensaje = "Bienvenido/a, " + nombre;
        txtSaludo.setText(mensaje);

    }
}