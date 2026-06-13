package com.laboratorio.inventarioapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.laboratorio.inventarioapp.R;
import com.laboratorio.inventarioapp.model.StockUpdateMessage;
import com.laboratorio.inventarioapp.network.ProductoApiService;
import com.laboratorio.inventarioapp.network.RetrofitClient;
import com.laboratorio.inventarioapp.network.StockWebSocketClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebSocketActivity extends AppCompatActivity {

    private TextView tvEstado, tvMensajes;
    private EditText etIdProducto, etStockPrueba;
    private final StockWebSocketClient wsClient = new StockWebSocketClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websocket);

        tvEstado      = findViewById(R.id.tvEstadoWS);
        tvMensajes    = findViewById(R.id.tvMensajesWS);
        etIdProducto  = findViewById(R.id.etIdProductoWS);
        etStockPrueba = findViewById(R.id.etStockPrueba);

        tvEstado.setText("Conectando...");

        wsClient.conectar(new StockWebSocketClient.StockListener() {
            @Override
            public void onConectado() {
                runOnUiThread(() ->
                        tvEstado.setText("Conectado — escuchando cambios de stock")
                );
            }
            @Override
            public void onMessaje(StockUpdateMessage mensaje) {
                runOnUiThread(() ->
                        tvMensajes.append(
                                "\n[EVENTO RECIBIDO EN TIEMPO REAL]\n" +
                                        "Producto: " + mensaje.getNombreProducto() + "\n" +
                                        "Nuevo stock: " + mensaje.getNuevoStock() + "\n" +
                                        "─────────────────────\n"
                        )
                );
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        tvEstado.setText("Error: " + error)
                );
            }
        });

        // Botón que actualiza stock via REST y dispara el evento WebSocket
        findViewById(R.id.btnDispararEvento).setOnClickListener(v -> {
            String idStr    = etIdProducto.getText().toString().trim();
            String stockStr = etStockPrueba.getText().toString().trim();

            if (idStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, "Ingresa ID y stock", Toast.LENGTH_SHORT).show();
                return;
            }

            ProductoApiService api = RetrofitClient.getInstance()
                    .create(ProductoApiService.class);

            api.actualizarStock(Long.parseLong(idStr), Integer.parseInt(stockStr))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                               Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                // El evento WebSocket llegará automáticamente
                                // desde Spring por /topic/stock
                                Toast.makeText(WebSocketActivity.this,
                                        "Stock actualizado — esperando evento WS...",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(WebSocketActivity.this,
                                        "Error: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(WebSocketActivity.this,
                                    "Error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wsClient.desconectar(); // Obligatorio para evitar fuga de memoria
    }
}