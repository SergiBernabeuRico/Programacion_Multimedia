package com.unidad3.banco_seberi;

import static android.text.TextUtils.isEmpty;

import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsuario;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        }); */

        //  1.Referencia al botón ENTRAR de activity_login.xml
        edtUsuario = findViewById(R.id.edtUsuario);
        // 2.Referencia al botón SALIR de activity_login.xml
        edtPassword = findViewById(R.id.edtPassword);

        Button btnEntrar = findViewById(R.id.btnEntrarLogin);
        Button btnSalir = findViewById(R.id.btnSalirLogin);

        //  3.Al hacer click abrimos el MainActivity.java
        btnEntrar.setOnClickListener(v -> {
            String nombreUsuario = edtUsuario.getText().toString().trim();

            // Si está vacío, muestra texto por defecto
            if (nombreUsuario.isEmpty()) {
                nombreUsuario = "usuario";
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

            // Envia el nombre a MainActivity usando la constante EXTRA_NOMBRE
            intent.putExtra(MainActivity.EXTRA_NOMBRE, nombreUsuario);
            startActivity(intent);
        });

        //  4.SALIR ->  Volver a la pantalla anterior (WelcomeActivity)
        btnSalir.setOnClickListener(v -> {
            finish();
        });
    }
}