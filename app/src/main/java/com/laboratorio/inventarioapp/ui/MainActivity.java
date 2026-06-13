package com.laboratorio.inventarioapp.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.laboratorio.inventarioapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnRest).setOnClickListener(v ->
                startActivity(new Intent(this, RestActivity.class)));

        findViewById(R.id.btnGraphQL).setOnClickListener(v ->
                startActivity(new Intent(this, GraphQLActivity.class)));

        findViewById(R.id.btnWebSocket).setOnClickListener(v ->
                startActivity(new Intent(this, WebSocketActivity.class)));

        findViewById(R.id.btnCategorias).setOnClickListener(v ->
                startActivity(new Intent(this, CategoriaActivity.class)));
    }
}