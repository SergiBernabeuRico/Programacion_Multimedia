package com.example.u2p4conversor;

import android.os.Bundle;
import android.util.Log; //  Logs en Logcat
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.RadioButton;
import android.widget.TextView;

import java.text.DecimalFormat;

// Spinner y su adaptador
import android.widget.Spinner;
import android.widget.ArrayAdapter;

// Para abrir la Activity de ayuda
import android.content.Intent;




public class MainActivity extends AppCompatActivity {

    // [CONSTANTE NUEVA] Etiqueta para identificar todos tus mensajes en Logcat (filtra por “U2P4Conversor”)
    private static final String TAG = "U2P4Conversor";

    // [ESTADO] Claves para guardar/restaurar el estado tras cambio de orientación
    private static final String K_IN      = "state_input";        // texto introducido en et_Pulgada
    private static final String K_RES     = "state_result";       // texto mostrado en et_Resultado
    private static final String K_ERR_TXT = "state_error_text";   // texto del tv_Error
    private static final String K_ERR_VIS = "state_error_vis";    // visibilidad del tv_Error (true = visible)
    private static final String K_DIR_IN2CM = "state_dir_in2cm";  // true si está seleccionado Pulgadas→Centímetros


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
        // [LOG CICLO DE VIDA] Indica que la Activity está en fase de creación
        // Log.d(TAG, "onCreate()");
        Log.d(TAG, "onCreate() - recreada por cambio de orientación si savedInstanceState != null");



        setUI();
    }

    private void setUI() {
        // Referencias existentes
        EditText etPulgada = findViewById(R.id.et_Pulgada);         // Entrada numérica (usuario escribe)
        EditText etResultado = findViewById(R.id.et_Resultado);     // Salida (resultado formateado)
        Button buttonConvertir = findViewById(R.id.button_Convertir); // Botón para ejecutar la conversión
        RadioButton rbInToCm = findViewById(R.id.rbInToCm);         // Dirección: primera → segunda (ej. pulgadas → cm)
        RadioButton rbCmToIn = findViewById(R.id.rbCmToIn);         // Dirección: segunda → primera (ej. cm → pulgadas)

        // [NUEVO] Referencias para el punto 5
        Spinner spTipo = findViewById(R.id.spTipo);                 // Selector del tipo de conversión
        Button btnHelp = findViewById(R.id.btnHelp);                // Botón de ayuda (se usará en el 5.2)
        TextView tvError = findViewById(R.id.tv_Error);             // TextView para mostrar mensajes de error en rojo

        // [NUEVO] Cargar el Spinner UNA sola vez (NO dentro del click)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,                               // contexto de Activity
                R.array.conv_options,               // opciones: in↔cm, cm↔mm, km↔mi (definidas en arrays.xml)
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        // Listener del botón convertir (reemplaza por completo al anterior)
        buttonConvertir.setOnClickListener(view -> {
            // [LOG CLIC BOTÓN] (punto 3.2)
            String btnName = getResources().getResourceEntryName(view.getId());
            Log.d(TAG, "Click botón: " + btnName);

            // Limpiar estado de error previo
            tvError.setVisibility(View.GONE);
            tvError.setText("");

            try {
                // 1) Leemos entrada
                String txt = etPulgada.getText().toString().trim();

                // 2) Dirección: true = primera→segunda (rbInToCm), false = inversa
                boolean forward = rbInToCm.isChecked();

                // 3) Tipo de conversión elegido en el Spinner
                //    0 = Pulgadas ↔ Centímetros  (factor 2.54)
                //    1 = Centímetros ↔ Milímetros (factor 10.0)
                //    2 = Kilómetros ↔ Millas      (km = mi * 1.60934)
                int tipo = spTipo.getSelectedItemPosition();

                double res;
                switch (tipo) {
                    case 0: // Pulgadas ↔ Centímetros
                        res = convertirConFactor(txt, forward, 2.54);
                        break;
                    case 1: // Centímetros ↔ Milímetros
                        res = convertirConFactor(txt, forward, 10.0);
                        break;
                    case 2: // Kilómetros ↔ Millas
                    default:
                        res = convertirConFactor(txt, forward, 1.60934);
                        break;
                }

                // 4) Mostrar salida con 2 decimales (punto 1.2)
                etResultado.setText(formatTwo(res));

            } catch (Exception e) {
                // Errores del punto 2 (vacío, <1, no numérico...): se muestran en rojo
                etResultado.setText("");             // limpiamos el resultado
                tvError.setText(e.getMessage());     // mensaje exacto de la excepción
                tvError.setVisibility(View.VISIBLE); // visible solo cuando hay error
            }
        });

        // onClick de btnHelp para abrir HelpActivity
        // [5.2] Botón de ayuda: abre una nueva Activity con instrucciones de uso
        btnHelp.setOnClickListener(v -> {
            Log.d(TAG, "Click botón: btnHelp (abre HelpActivity)"); // Log del clic de ayuda
            Intent i = new Intent(MainActivity.this, HelpActivity.class); // Intent explícito a HelpActivity
            startActivity(i); // Lanzamos la nueva pantalla
        });

    }

    // Formatea un double a 2 decimales (por ejemplo: 3.1 → "3.10")
    private String formatTwo(double n) {
        return new DecimalFormat("#0.00").format(n);
    }

    // (2.1) Convierte validando que el valor no esté vacío y sea >= 1.
    // Lanza Exception con mensajes EXACTOS del enunciado para que se muestren en el TextView de error.
    /*private double convertir(String input, boolean inToCm) throws Exception {
        // Comprobación de vacío
        if (input == null || input.trim().isEmpty()) {
            throw new Exception("El valor no puede estar vacío");
        }

        // Parseo numérico (acepta coma o punto)
        double valor;
        try {
            valor = Double.parseDouble(input.trim().replace(',', '.'));
        } catch (NumberFormatException nfe) {
            // Mensaje libre (no lo exige el enunciado), pero útil si ponen "abc"
            throw new Exception("Valor no válido");
        }

        // Restricción: sólo números >= 1
        if (valor < 1.0) {
            throw new Exception("Sólo números >=1");
        }

        // Conversión según dirección seleccionada
        return inToCm ? (valor * 2.54) : (valor / 2.54);
    }*/
    // [CICLO DE VIDA] La Activity está a punto de hacerse visible para el usuario
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    // [CICLO DE VIDA] La Activity empieza a interactuar con el usuario (adquiere foco)
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    // [CICLO DE VIDA] La Activity pierde foco (por ejemplo, llega otra Activity encima)
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    // [CICLO DE VIDA] La Activity ya no es visible para el usuario
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    // [CICLO DE VIDA] La Activity vuelve a primer plano tras haber estado parada
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    // [CICLO DE VIDA] La Activity está a punto de destruirse (fin de su ciclo)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
    // [CICLO DE VIDA 4.2] Se llama antes de destruir la Activity: guardamos el estado que queremos restaurar
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()"); // Log para ver el momento del guardado

        // Obtenemos referencias a las vistas actuales
        EditText etPulgada   = findViewById(R.id.et_Pulgada);
        EditText etResultado = findViewById(R.id.et_Resultado);
        RadioButton rbInToCm = findViewById(R.id.rbInToCm);
        TextView tvError     = findViewById(R.id.tv_Error); // si no existe en tu layout, comenta estas dos líneas

        // Guardamos textos y dirección de conversión
        outState.putString(K_IN,  etPulgada != null ? etPulgada.getText().toString() : "");
        outState.putString(K_RES, etResultado != null ? etResultado.getText().toString() : "");

        // Guardamos el estado del selector (true si Pulgadas→Centímetros)
        outState.putBoolean(K_DIR_IN2CM, rbInToCm != null && rbInToCm.isChecked());

        // Guardamos mensaje y visibilidad del error (si usas tv_Error)
        if (tvError != null) {
            outState.putString(K_ERR_TXT, tvError.getText() != null ? tvError.getText().toString() : "");
            outState.putBoolean(K_ERR_VIS, tvError.getVisibility() == View.VISIBLE);
        }
    }
    // [CICLO DE VIDA 4.2] Se llama después de onStart: restauramos el estado guardado
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState()"); // Log para ver el momento de la restauración

        // Obtenemos referencias a las vistas
        EditText etPulgada     = findViewById(R.id.et_Pulgada);
        EditText etResultado   = findViewById(R.id.et_Resultado);
        RadioButton rbInToCm   = findViewById(R.id.rbInToCm);
        RadioButton rbCmToIn   = findViewById(R.id.rbCmToIn);
        TextView tvError       = findViewById(R.id.tv_Error); // si no existe en tu layout, comenta estas líneas

        // Restauramos textos de entrada y resultado
        if (etPulgada != null)   etPulgada.setText(savedInstanceState.getString(K_IN, ""));
        if (etResultado != null) etResultado.setText(savedInstanceState.getString(K_RES, ""));

        // Restauramos la dirección seleccionada
        boolean wasInToCm = savedInstanceState.getBoolean(K_DIR_IN2CM, true);
        if (rbInToCm != null && rbCmToIn != null) {
            rbInToCm.setChecked(wasInToCm);
            rbCmToIn.setChecked(!wasInToCm);
        }

        // Restauramos error (texto + visibilidad)
        if (tvError != null) {
            String errText = savedInstanceState.getString(K_ERR_TXT, "");
            boolean errVis = savedInstanceState.getBoolean(K_ERR_VIS, false);
            tvError.setText(errText);
            tvError.setVisibility(errVis ? View.VISIBLE : View.GONE);
        }
    }
// [NUEVO] Conversión genérica con validaciones (punto 2) y factor configurable.
// forward = true  → primera unidad → segunda (multiplica por factor)
// forward = false → segunda unidad → primera (divide por factor)
private double convertirConFactor(String input, boolean forward, double factor) throws Exception {
    // Vacío → excepción con el mensaje EXACTO del enunciado
    if (input == null || input.trim().isEmpty()) {
        throw new Exception("El valor no puede estar vacío");
    }

    // Parseo numérico (admite coma o punto)
    double valor;
    try {
        valor = Double.parseDouble(input.trim().replace(',', '.'));
    } catch (NumberFormatException nfe) {
        throw new Exception("Valor no válido");
    }

    // Restricción: sólo números >= 1
    if (valor < 1.0) {
        throw new Exception("Sólo números >=1");
    }

    // Cálculo según dirección y factor
    return forward ? (valor * factor) : (valor / factor);
}


}