package com.laboratorio.inventarioapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.laboratorio.inventarioapp.R;
import com.laboratorio.inventarioapp.model.Categoria;
import com.laboratorio.inventarioapp.model.CategoriaRequest;
import com.laboratorio.inventarioapp.network.CategoriaApiService;
import com.laboratorio.inventarioapp.network.RetrofitClient;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriaActivity extends AppCompatActivity {

    private TextView tvResultado;
    private EditText etNombre, etDescripcion;
    private EditText etIdEliminar;
    private ScrollView scrollView;
    private CategoriaApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        api = RetrofitClient.getInstance().create(CategoriaApiService.class);

        tvResultado   = findViewById(R.id.tvResultadoCategoria);
        etNombre      = findViewById(R.id.etCategoriaNombre);
        etDescripcion = findViewById(R.id.etCategoriaDescripcion);
        etIdEliminar  = findViewById(R.id.etCategoriaIdEliminar);
        scrollView    = findViewById(R.id.scrollCategoria);

        findViewById(R.id.btnCategoriaListar).setOnClickListener(v -> listar());
        findViewById(R.id.btnCategoriaCrear).setOnClickListener(v -> crear());
        findViewById(R.id.btnCategoriaEliminar).setOnClickListener(v -> eliminar());

        listar();
    }

    private void listar() {
        tvResultado.setText("Cargando...");
        api.listarCategorias().enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call,
                                   Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (Categoria c : response.body()) {
                        sb.append("ID: ").append(c.getId()).append("\n");
                        sb.append("Nombre: ").append(c.getNombre()).append("\n");
                        sb.append("Descripción: ").append(
                                c.getDescripcion() != null
                                        ? c.getDescripcion()
                                        : "Sin descripción"
                        ).append("\n");
                        sb.append("─────────────────\n");
                    }
                    tvResultado.setText(
                            sb.length() > 0 ? sb.toString() : "No hay categorías"
                    );
                } else {
                    tvResultado.setText("Error: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                tvResultado.setText("Error de conexión:\n" + t.getMessage());
            }
        });
    }

    private void crear() {
        String nombre = etNombre.getText().toString().trim();
        String desc   = etDescripcion.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cambia: Categoria nueva = new Categoria()
        // Por: CategoriaRequest con solo nombre y descripcion
        CategoriaRequest request = new CategoriaRequest(
                nombre,
                desc.isEmpty() ? null : desc
        );

        api.crearCategoria(request).enqueue(new Callback<Categoria>() {
            @Override
            public void onResponse(Call<Categoria> call,
                                   Response<Categoria> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CategoriaActivity.this,
                            "Categoría creada", Toast.LENGTH_SHORT).show();
                    etNombre.setText("");
                    etDescripcion.setText("");
                    listar();
                    scrollView.post(() ->
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN));
                } else {
                    Toast.makeText(CategoriaActivity.this,
                            "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Categoria> call, Throwable t) {
                Toast.makeText(CategoriaActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminar() {
        String idStr = etIdEliminar.getText().toString().trim();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "Ingresa el ID", Toast.LENGTH_SHORT).show();
            return;
        }

        api.eliminarCategoria(Long.parseLong(idStr)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CategoriaActivity.this,
                            "Categoría eliminada", Toast.LENGTH_SHORT).show();
                    etIdEliminar.setText("");
                    listar();
                } else {
                    Toast.makeText(CategoriaActivity.this,
                            "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CategoriaActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}