package com.laboratorio.inventarioapp.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.laboratorio.inventarioapp.R;
import com.laboratorio.inventarioapp.model.Categoria;
import com.laboratorio.inventarioapp.network.CategoriaApiService;
import com.laboratorio.inventarioapp.network.GraphQLClient;
import com.laboratorio.inventarioapp.network.RetrofitClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphQLActivity extends AppCompatActivity {

    private TextView tvResultado, tvComparacion;
    private EditText etGqlNombre, etGqlPrecio, etGqlStock;
    private EditText etGqlIdStock, etGqlNuevoStock;
    private EditText etGqlIdEliminar;
    private Spinner spinnerCategorias;
    private ScrollView scrollView;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<Categoria> listaCategorias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphql);

        tvResultado       = findViewById(R.id.tvResultadoGraphQL);
        tvComparacion     = findViewById(R.id.tvComparacion);
        etGqlNombre       = findViewById(R.id.etGqlNombre);
        etGqlPrecio       = findViewById(R.id.etGqlPrecio);
        etGqlStock        = findViewById(R.id.etGqlStock);
        etGqlIdStock      = findViewById(R.id.etGqlIdStock);
        etGqlNuevoStock   = findViewById(R.id.etGqlNuevoStock);
        etGqlIdEliminar   = findViewById(R.id.etGqlIdEliminar);
        spinnerCategorias = findViewById(R.id.spinnerCategorias);
        scrollView        = findViewById(R.id.scrollGraphql);

        tvComparacion.setText(
                "REST devuelve TODOS los campos del producto.\n\n" +
                        "GraphQL devuelve SOLO lo que pedimos.\n\n" +
                        "→ Menos megas descargados = ideal para redes 3G/4G móviles."
        );

        cargarCategorias();

        // Botones de Crear, Actualizar y Eliminar
        findViewById(R.id.btnGqlCrear).setOnClickListener(v -> crear());
        findViewById(R.id.btnGqlActualizarStock).setOnClickListener(v -> actualizarStock());
        findViewById(R.id.btnGqlEliminar).setOnClickListener(v -> eliminar());

        // Botones para mostrar el over-feching
        findViewById(R.id.btnSoloNombrePrecio).setOnClickListener(v -> listarSoloNombrePrecio());
        findViewById(R.id.btnNombrePrecioStock).setOnClickListener(v -> listarNombrePrecioStock());
        findViewById(R.id.btnGqlListar).setOnClickListener(v -> listar());

        listar();
    }

    private void cargarCategorias() {
        CategoriaApiService api = RetrofitClient.getInstance()
                .create(CategoriaApiService.class);

        api.listarCategorias().enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call,
                                   Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCategorias.clear();
                    listaCategorias.addAll(response.body());

                    List<String> nombres = new ArrayList<>();
                    for (Categoria c : listaCategorias)
                        nombres.add(c.getNombre());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            GraphQLActivity.this,
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
                Toast.makeText(GraphQLActivity.this,
                        "Error cargando categorías: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mostrar solo nombre y precio
    private void listarSoloNombrePrecio() {
        tvResultado.setText("Consultando GraphQL...");

        String query = "{ productos { nombre precio } }";

        ejecutarQuery(query, respuesta -> {
            try {
                JsonObject root = new Gson().fromJson(respuesta, JsonObject.class);
                JsonArray productos = root
                        .getAsJsonObject("data")
                        .getAsJsonArray("productos");

                StringBuilder sb = new StringBuilder();
                sb.append("[ Campos pedidos: nombre, precio ]\n");
                sb.append("[ Stock, id y categoría NO se descargaron ]\n");
                sb.append("─────────────────\n");

                for (JsonElement el : productos) {
                    JsonObject p = el.getAsJsonObject();
                    sb.append("Nombre: ")
                            .append(p.get("nombre").getAsString()).append("\n");
                    sb.append("Precio: Bs. ")
                            .append(p.get("precio").getAsDouble()).append("\n");
                    sb.append("─────────────────\n");
                }

                runOnUiThread(() -> tvResultado.setText(
                        sb.length() > 0 ? sb.toString() : "No hay productos"
                ));
            } catch (Exception e) {
                runOnUiThread(() ->
                        tvResultado.setText("Error: " + e.getMessage())
                );
            }
        });
    }

    // Mostrar solo nombre, precio y stock
    private void listarNombrePrecioStock() {
        tvResultado.setText("Consultando GraphQL...");

        String query = "{ productos { nombre precio stock } }";

        ejecutarQuery(query, respuesta -> {
            try {
                JsonObject root = new Gson().fromJson(respuesta, JsonObject.class);
                JsonArray productos = root
                        .getAsJsonObject("data")
                        .getAsJsonArray("productos");

                StringBuilder sb = new StringBuilder();
                sb.append("[ Campos pedidos: nombre, precio, stock ]\n");
                sb.append("[ id y categoría NO se descargaron ]\n");
                sb.append("─────────────────\n");

                for (JsonElement el : productos) {
                    JsonObject p = el.getAsJsonObject();
                    sb.append("Nombre: ")
                            .append(p.get("nombre").getAsString()).append("\n");
                    sb.append("Precio: Bs. ")
                            .append(p.get("precio").getAsDouble()).append("\n");
                    sb.append("Stock: ")
                            .append(p.get("stock").getAsInt()).append("\n");
                    sb.append("─────────────────\n");
                }

                runOnUiThread(() -> tvResultado.setText(
                        sb.length() > 0 ? sb.toString() : "No hay productos"
                ));
            } catch (Exception e) {
                runOnUiThread(() ->
                        tvResultado.setText("Error: " + e.getMessage())
                );
            }
        });
    }

    // Mostrar todos los campos (id, nombre, stock, precio, nombreCategoria)
    private void listar() {
        tvResultado.setText("Consultando GraphQL...");

        String query = "{ productos { id nombre precio stock categoria { nombre } } }";

        ejecutarQuery(query, respuesta -> {
            try {
                JsonObject root = new Gson().fromJson(respuesta, JsonObject.class);
                JsonArray productos = root
                        .getAsJsonObject("data")
                        .getAsJsonArray("productos");

                StringBuilder sb = new StringBuilder();
                sb.append("[ Campos pedidos: id, nombre, precio, stock, categoria ]\n");
                sb.append("─────────────────\n");

                for (JsonElement el : productos) {
                    JsonObject p = el.getAsJsonObject();
                    sb.append("ID: ").append(p.get("id").getAsString()).append("\n");
                    sb.append("Nombre: ").append(p.get("nombre").getAsString()).append("\n");
                    sb.append("Precio: Bs. ").append(p.get("precio").getAsDouble()).append("\n");
                    sb.append("Stock: ").append(p.get("stock").getAsInt()).append("\n");
                    if (!p.get("categoria").isJsonNull()) {
                        sb.append("Categoría: ")
                                .append(p.getAsJsonObject("categoria")
                                        .get("nombre").getAsString())
                                .append("\n");
                    }
                    sb.append("─────────────────\n");
                }

                runOnUiThread(() -> tvResultado.setText(
                        sb.length() > 0 ? sb.toString() : "No hay productos"
                ));
            } catch (Exception e) {
                runOnUiThread(() ->
                        tvResultado.setText("Error al parsear: " + e.getMessage())
                );
            }
        });
    }

    private void crear() {
        String nombre = etGqlNombre.getText().toString().trim();
        String precio = etGqlPrecio.getText().toString().trim();
        String stock  = etGqlStock.getText().toString().trim();

        if (nombre.isEmpty() || precio.isEmpty() || stock.isEmpty()) {
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

        String mutation =
                "mutation CrearProducto($nombre: String!, $categoriaId: ID!, " +
                        "$stock: Int!, $precio: Float!) {" +
                        "  crearProducto(input: {" +
                        "    nombre: $nombre," +
                        "    categoriaId: $categoriaId," +
                        "    stock: $stock," +
                        "    precio: $precio" +
                        "  }) { id nombre precio }" +
                        "}";

        JsonObject variables = new JsonObject();
        variables.addProperty("nombre", nombre);
        variables.addProperty("categoriaId", categoriaId);
        variables.addProperty("stock", Integer.parseInt(stock));
        variables.addProperty("precio", Double.parseDouble(precio));

        ejecutarQueryConVariables(mutation, variables, respuesta -> {
            try {
                JsonObject root = new Gson().fromJson(respuesta, JsonObject.class);
                if (root.has("errors")) {
                    String error = root.getAsJsonArray("errors")
                            .get(0).getAsJsonObject()
                            .get("message").getAsString();
                    runOnUiThread(() -> Toast.makeText(this,
                            "Error: " + error, Toast.LENGTH_LONG).show());
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Producto creado via GraphQL",
                                Toast.LENGTH_SHORT).show();
                        etGqlNombre.setText("");
                        etGqlPrecio.setText("");
                        etGqlStock.setText("");
                        listar();
                        scrollView.post(() ->
                                scrollView.fullScroll(ScrollView.FOCUS_DOWN));
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void actualizarStock() {
        String idStr    = etGqlIdStock.getText().toString().trim();
        String stockStr = etGqlNuevoStock.getText().toString().trim();

        if (idStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Ingresa ID y nuevo stock", Toast.LENGTH_SHORT).show();
            return;
        }

        String mutation =
                "mutation ActualizarStock($id: ID!, $nuevoStock: Int!) {" +
                        "  actualizarStockProducto(id: $id, nuevoStock: $nuevoStock) {" +
                        "    id nombre stock" +
                        "  }" +
                        "}";

        JsonObject variables = new JsonObject();
        variables.addProperty("id", Long.parseLong(idStr));
        variables.addProperty("nuevoStock", Integer.parseInt(stockStr));

        ejecutarQueryConVariables(mutation, variables, respuesta -> {
            try {
                JsonObject root = new Gson().fromJson(respuesta, JsonObject.class);
                if (root.has("errors")) {
                    runOnUiThread(() -> Toast.makeText(this,
                            "Producto no encontrado", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Stock actualizado via GraphQL",
                                Toast.LENGTH_SHORT).show();
                        etGqlIdStock.setText("");
                        etGqlNuevoStock.setText("");
                        listar();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void eliminar() {
        String idStr = etGqlIdEliminar.getText().toString().trim();

        if (idStr.isEmpty()) {
            Toast.makeText(this, "Ingresa el ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String mutation =
                "mutation EliminarProducto($id: ID!) {" +
                        "  eliminarProducto(id: $id)" +
                        "}";

        JsonObject variables = new JsonObject();
        variables.addProperty("id", Long.parseLong(idStr));

        ejecutarQueryConVariables(mutation, variables, respuesta -> {
            try {
                JsonObject root = new Gson().fromJson(respuesta, JsonObject.class);
                boolean ok = root.getAsJsonObject("data")
                        .get("eliminarProducto").getAsBoolean();
                runOnUiThread(() -> {
                    if (ok) {
                        Toast.makeText(this, "Eliminado via GraphQL",
                                Toast.LENGTH_SHORT).show();
                        etGqlIdEliminar.setText("");
                        listar();
                    } else {
                        Toast.makeText(this, "No se encontró el producto",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void ejecutarQuery(String query, QueryCallback callback) {
        executor.execute(() -> {
            try {
                String respuesta = GraphQLClient.getInstance().ejecutar(query);
                callback.onResultado(respuesta);
            } catch (IOException e) {
                runOnUiThread(() ->
                        tvResultado.setText("Error de conexión:\n" + e.getMessage())
                );
            }
        });
    }

    private void ejecutarQueryConVariables(String query, JsonObject variables,
                                           QueryCallback callback) {
        executor.execute(() -> {
            try {
                String respuesta = GraphQLClient.getInstance()
                        .ejecutarConVariables(query, variables);
                callback.onResultado(respuesta);
            } catch (IOException e) {
                runOnUiThread(() ->
                        tvResultado.setText("Error de conexión:\n" + e.getMessage())
                );
            }
        });
    }

    interface QueryCallback {
        void onResultado(String respuesta);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}