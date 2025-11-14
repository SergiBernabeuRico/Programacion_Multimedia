// MainActivity.java — Conversor con pulgadas↔cm, km↔mi y °C↔°F (5.1 completo)
package com.example.u2p4conversor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    // Etiqueta para Logcat
    private static final String TAG = "U2P4Conversor";

    // Claves para guardar/restaurar estado (cambio de orientación)
    private static final String K_IN       = "state_input";       // texto introducido en et_Pulgada
    private static final String K_RES      = "state_result";      // texto mostrado en et_Resultado
    private static final String K_ERR_TXT  = "state_error_text";  // texto del tv_Error
    private static final String K_ERR_VIS  = "state_error_vis";   // visibilidad del tv_Error
    private static final String K_DIR_IN2CM = "state_dir_in2cm";  // true si está seleccionado dirección A→B

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajuste de insets (barras del sistema)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "onCreate() - recreada por cambio de orientación si savedInstanceState != null");

        setUI();
    }

    // Configura las referencias a la UI y los listeners
    private void setUI() {
        // Referencias principales
        EditText etPulgada      = findViewById(R.id.et_Pulgada);      // Entrada numérica
        EditText etResultado    = findViewById(R.id.et_Resultado);    // Resultado
        Button buttonConvertir  = findViewById(R.id.button_Convertir);// Botón convertir
        RadioButton rbInToCm    = findViewById(R.id.rbInToCm);        // Dirección A→B
        RadioButton rbCmToIn    = findViewById(R.id.rbCmToIn);        // Dirección B→A
        Spinner spTipo          = findViewById(R.id.spTipo);          // Tipo de conversión
        Button btnHelp          = findViewById(R.id.btnHelp);         // Botón de ayuda
        TextView tvError        = findViewById(R.id.tv_Error);        // Mensajes de error

        // Carga de opciones del Spinner desde arrays.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.conv_options,               // Pulgadas↔cm, Km/Mi, °C/°F
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        // Botón de ayuda: abre HelpActivity
        btnHelp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        // --- [5.1] Hints y texto del botón dinámicos según tipo + dirección ---

        // Función que refresca hints y texto del botón
        Runnable refreshHints = () -> {
            boolean forward = rbInToCm.isChecked(); // true = unidad A / unidad B
            updatePlaceholders(etPulgada, etResultado, buttonConvertir,
                    spTipo.getSelectedItemPosition(), forward);
        };

        // Cuando cambia el tipo en el Spinner
        spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshHints.run();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { /* no-op */ }
        });

        // Cuando cambia la dirección (RadioButtons)
        CompoundButton.OnCheckedChangeListener dirChangeListener =
                (buttonView, isChecked) -> refreshHints.run();

        rbInToCm.setOnCheckedChangeListener(dirChangeListener);
        rbCmToIn.setOnCheckedChangeListener(dirChangeListener);

        // Inicializamos hints y texto del botón al arrancar
        refreshHints.run();

        // Listener del botón convertir (con validación y excepciones)
        buttonConvertir.setOnClickListener(view -> {
            // Log del click
            String btnName = getResources().getResourceEntryName(view.getId());
            Log.d(TAG, "Click botón: " + btnName);

            // Limpiamos error anterior
            tvError.setText("");
            tvError.setVisibility(View.GONE);

            try {
                // Texto de entrada
                String txt = etPulgada.getText().toString().trim();

                // Dirección: true = A→B (rbInToCm marcado), false = B→A
                boolean forward = rbInToCm.isChecked();

                // Tipo de conversión (Spinner)
                int tipo = spTipo.getSelectedItemPosition();

                double res;

                // Tipo:
                // 0: Pulgadas / Centímetros (factor 2.54)
                // 1: Kilómetros / Millas    (km = mi * 1.60934)
                // 2: Grados °C / Grados °F (fórmula específica)
                switch (tipo) {
                    case 0:
                        // Pulgadas↔Centímetros
                        res = convertirConFactor(txt, forward, 2.54);
                        break;
                    case 1:
                        // Km↔Mi
                        res = convertirConFactor(txt, forward, 1.60934);
                        break;
                    case 2:
                    default:
                        // °C↔°F (no es lineal por factor, lleva +32)
                        res = convertirTemperatura(txt, forward);
                        break;
                }

                // Mostramos el resultado con dos decimales
                etResultado.setText(formatTwo(res));

            } catch (Exception e) {
                // Gestión de errores: vacío, <1, no numérico...
                etResultado.setText("");
                tvError.setText(e.getMessage());
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    // Formatea un double a 2 decimales (3.1 → "3.10")
    private String formatTwo(double n) {
        return new DecimalFormat("#0.00").format(n);
    }

    // Conversión genérica con validaciones (punto 2) para factores simples (pulgadas↔cm, km↔mi)
    // forward = true  -> primera unidad -> segunda (multiplica factor)
    // forward = false -> segunda unidad -> primera (divide factor)
    private double convertirConFactor(String input, boolean forward, double factor) throws Exception {
        // Vacío
        if (input == null || input.trim().isEmpty()) {
            throw new Exception("El valor no puede estar vacío");
        }

        // Parseo numérico (permite coma o punto)
        double valor;
        try {
            valor = Double.parseDouble(input.trim().replace(',', '.'));
        } catch (NumberFormatException nfe) {
            throw new Exception("Valor no válido");
        }

        // Restricción: >= 1
        if (valor < 1.0) {
            throw new Exception("Sólo números >=1");
        }

        // Cálculo
        return forward ? (valor * factor) : (valor / factor);
    }

    // Conversión de temperatura °C / °F con las mismas validaciones y excepciones
    // forward = true  -> °C / °F
    // forward = false -> °F / °C
    private double convertirTemperatura(String input, boolean forward) throws Exception {
        // Vacío
        if (input == null || input.trim().isEmpty()) {
            throw new Exception("El valor no puede estar vacío");
        }

        // Parseo numérico (permite coma o punto)
        double valor;
        try {
            valor = Double.parseDouble(input.trim().replace(',', '.'));
        } catch (NumberFormatException nfe) {
            throw new Exception("Valor no válido");
        }

        // Restricción: >= 1 (el enunciado pide lo mismo)
        if (valor < 1.0) {
            throw new Exception("Sólo números >=1");
        }

        if (forward) {
            // °C -> °F
            return valor * 9.0 / 5.0 + 32.0;
        } else {
            // °F -> °C
            return (valor - 32.0) * 5.0 / 9.0;
        }
    }

    // --- [5.1] Unidades A/B según opción del Spinner ---
    // tipo: 0 = Pulgadas/Centímetros, 1 = Km/Mi, 2 = °C/°F
    private String[] getUnitsPair(int tipo) {
        switch (tipo) {
            case 0:
                return new String[]{"pulgadas", "centímetros"};
            case 1:
                return new String[]{"kilómetros", "millas"};
            case 2:
            default:
                return new String[]{"grados °C", "grados °F"};
        }
    }

    // --- [5.1] Actualiza hints y texto del botón según tipo y dirección ---
    private void updatePlaceholders(EditText etIn,
                                    EditText etOut,
                                    Button btn,
                                    int tipo,
                                    boolean forward) {

        String[] units = getUnitsPair(tipo); // units[0] = A, units[1] = B
        String from = forward ? units[0] : units[1];
        String to   = forward ? units[1] : units[0];

        etIn.setHint("Introduce " + from);
        etOut.setHint("Resultado en " + to);
        btn.setText("Convertir " + from + " → " + to);
    }

    // Ciclo de vida (logs)
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    // Guardar estado al girar la pantalla
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");

        EditText etPulgada   = findViewById(R.id.et_Pulgada);
        EditText etResultado = findViewById(R.id.et_Resultado);
        RadioButton rbInToCm = findViewById(R.id.rbInToCm);
        TextView tvError     = findViewById(R.id.tv_Error);

        outState.putString(K_IN,  etPulgada != null ? etPulgada.getText().toString() : "");
        outState.putString(K_RES, etResultado != null ? etResultado.getText().toString() : "");
        outState.putBoolean(K_DIR_IN2CM, rbInToCm != null && rbInToCm.isChecked());

        if (tvError != null) {
            outState.putString(K_ERR_TXT, tvError.getText() != null ? tvError.getText().toString() : "");
            outState.putBoolean(K_ERR_VIS, tvError.getVisibility() == View.VISIBLE);
        }
    }

    // Restaurar estado tras girar
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState()");

        EditText etPulgada   = findViewById(R.id.et_Pulgada);
        EditText etResultado = findViewById(R.id.et_Resultado);
        RadioButton rbInToCm = findViewById(R.id.rbInToCm);
        RadioButton rbCmToIn = findViewById(R.id.rbCmToIn);
        TextView tvError     = findViewById(R.id.tv_Error);

        if (etPulgada != null) {
            etPulgada.setText(savedInstanceState.getString(K_IN, ""));
        }
        if (etResultado != null) {
            etResultado.setText(savedInstanceState.getString(K_RES, ""));
        }

        boolean wasInToCm = savedInstanceState.getBoolean(K_DIR_IN2CM, true);
        if (rbInToCm != null && rbCmToIn != null) {
            rbInToCm.setChecked(wasInToCm);
            rbCmToIn.setChecked(!wasInToCm);
        }

        if (tvError != null) {
            String errText = savedInstanceState.getString(K_ERR_TXT, "");
            boolean errVis = savedInstanceState.getBoolean(K_ERR_VIS, false);
            tvError.setText(errText);
            tvError.setVisibility(errVis ? View.VISIBLE : View.GONE);
        }
    }
}
