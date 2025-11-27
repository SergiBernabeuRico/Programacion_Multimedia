package com.example.ag3_ud3;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ag3_ud3.R;

public class MainActivity extends AppCompatActivity {
    private EditText et1, et2;
    private TextView tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),(v, insets) ->{
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        tv3=findViewById(R.id.tv3);

    }
    // Este método se ejecutará cuando se presione el botón
    public void sumar(View view){
        String valor1=et1.getText().toString();
        String valor2=et2.getText().toString();

        int nro1=Integer.parseInt(valor1);
        int nro2=Integer.parseInt(valor2);
        int suma = nro1+nro2;

        String resu=String.valueOf(suma);

        tv3.setText(resu);
    }

}