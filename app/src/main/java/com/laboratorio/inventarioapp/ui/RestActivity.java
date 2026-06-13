package com.laboratorio.inventarioapp.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.laboratorio.inventarioapp.R;
import com.laboratorio.inventarioapp.model.Categoria;
import com.laboratorio.inventarioapp.model.Producto;
import com.laboratorio.inventarioapp.model.ProductoRequest;
import com.laboratorio.inventarioapp.network.CategoriaApiService;
import com.laboratorio.inventarioapp.network.ProductoApiService;
import com.laboratorio.inventarioapp.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestActivity extends AppCompatActivity {

    private TextView tvResultado;
    private EditText etNombre, etPrecio, etStock;
    private EditText etIdStock, etNuevoStock;
    private EditText etIdEliminar;
    private Spinner spinnerCategorias;
    private ScrollView scrollView;
    private final List<Categoria> listaCategorias = new ArrayList<>();
    private ProductoApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);

        scrollView = findViewById(R.id.scrollRest);

        api = RetrofitClient.getInstance().create(ProductoApiService.class);

        tvResultado       = findViewById(R.id.tvResultadoRest);
        etNombre          = findViewById(R.id.etNombre);
        etPrecio          = findViewById(R.id.etPrecio);
        etStock           = findViewById(R.id.etStock);
        spinnerCategorias = findViewById(R.id.spinnerCategoriasRest);
        etIdStock         = findViewById(R.id.etIdStock);
        etNuevoStock      = findViewById(R.id.etNuevoStock);
        etIdEliminar      = findViewById(R.id.etIdEliminar);

        findViewById(R.id.btnListar).setOnClickListener(v -> listar());
        findViewById(R.id.btnCrear).setOnClickListener(v -> crear());
        findViewById(R.id.btnActualizarStock).setOnClickListener(v -> actualizarStock());
        findViewById(R.id.btnEliminar).setOnClickListener(v -> eliminar());

        cargarCategorias();
        listar();
    }

    private void listar() {
        tvResultado.setText("Cargando...");
        api.listarProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call,
                                   Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (Producto p : response.body()) {
                        sb.append("ID: ").append(p.getId()).append("\n");
                        sb.append("Nombre: ").append(p.getNombre()).append("\n");
                        sb.append("Precio: Bs. ").append(p.getPrecio()).append("\n");
                        sb.append("Stock: ").append(p.getStock()).append("\n");
                        sb.append("Categoría: ").append(
                                p.getCategoria() != null
                                        ? p.getCategoria().getNombre()
                                        : "Sin categoría"
                        ).append("\n");
                        sb.append("─────────────────\n");
                    }
                    tvResultado.setText(
                            sb.length() > 0 ? sb.toString() : "No hay productos"
                    );
                } else {
                    tvResultado.setText("Error: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                tvResultado.setText("Error de conexión:\n" + t.getMessage());
            }
        });
    }

    private void crear() {
        String nombre    = etNombre.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String stockStr  = etStock.getText().toString().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listaCategorias.isEmpty()) {
            Toast.makeText(this, "Espera a que carguen las categorías",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int posicion = spinnerCategorias.getSelectedItemPosition();
        Long categoriaId = listaCategorias.get(posicion).getId();

        ProductoRequest req = new ProductoRequest(
                nombre,
                categoriaId,
                Integer.parseInt(stockStr),
                Double.parseDouble(precioStr)
        );



        api.crearProducto(req).enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call,
                                   Response<Producto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RestActivity.this,
                            "Producto creado", Toast.LENGTH_SHORT).show();
                    etNombre.setText("");
                    etPrecio.setText("");
                    etStock.setText("");

                    listar();
                    scrollView.post(() ->
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN));
                } else {
                    Toast.makeText(RestActivity.this,
                            "Error al crear: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Toast.makeText(RestActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarStock() {
        String idStr    = etIdStock.getText().toString().trim();
        String stockStr = etNuevoStock.getText().toString().trim();

        if (idStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Ingresa ID y nuevo stock", Toast.LENGTH_SHORT).show();
            return;
        }

        api.actualizarStock(Long.parseLong(idStr), Integer.parseInt(stockStr))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RestActivity.this,
                                    "Stock actualizado", Toast.LENGTH_SHORT).show();
                            etIdStock.setText("");
                            etNuevoStock.setText("");
                            listar();
                        } else {
                            Toast.makeText(RestActivity.this,
                                    "Error: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(RestActivity.this,
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

        api.eliminarProducto(Long.parseLong(idStr))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RestActivity.this,
                                    "Producto eliminado", Toast.LENGTH_SHORT).show();
                            etIdEliminar.setText("");
                            listar();
                        } else {
                            Toast.makeText(RestActivity.this,
                                    "Error: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(RestActivity.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarCategorias() {
        CategoriaApiService catApi = RetrofitClient.getInstance()
                .create(CategoriaApiService.class);

        catApi.listarCategorias().enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call,
                                   Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCategorias.clear();
                    listaCategorias.addAll(response.body());

                    List<String> nombres = new ArrayList<>();
                    for (Categoria c : listaCategorias) {
                        nombres.add(c.getNombre());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            RestActivity.this,
                            android.R.layout.simple_spinner_item,
                            nombres
                    );
                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                    );
                    spinnerCategorias.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Toast.makeText(RestActivity.this,
                        "Error cargando categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }
}