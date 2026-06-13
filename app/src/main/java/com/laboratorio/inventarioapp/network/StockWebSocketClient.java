package com.laboratorio.inventarioapp.network;

import com.google.gson.Gson;
import com.laboratorio.inventarioapp.model.StockUpdateMessage;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class StockWebSocketClient {

    private static final String SOCKET_URL = "wss://appinventario-production-9286.up.railway.app/ws-inventario";

    private StompClient stompClient;
    private Disposable topicDisposable;
    private Disposable connectionDisposable;
    private final Gson gson = new Gson();

    public interface StockListener{
        void onMessaje(StockUpdateMessage mensaje);
        void onError(String error);
        void onConectado();
    }

    // abriendo el canal
    public void conectar(StockListener listener){
        Map<String, String> headers = new HashMap<>();
        headers.put("accept-version", "1.1,1.0");
        headers.put("heart-beat", "10000,10000");

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL);
        stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);


        // escuchar el estado de la conexion
        connectionDisposable = stompClient.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()){
                        case OPENED:
                            listener.onConectado(); // conexion correcta
                            break;
                        case ERROR:
                            listener.onError("Error de conexion WebSocket");
                            break;
                        case CLOSED:
                            listener.onError("Conexion cerrada");
                            break;
                    }
                });

        // conexion al canal /topic/stock
        topicDisposable = stompClient
                .topic("/topic/stock")
                .subscribe(frame -> {
                    // convierte el mensaje en json para usarlo en java
                    StockUpdateMessage mensaje = gson.fromJson(
                            frame.getPayload(),
                            StockUpdateMessage.class
                        );
                        listener.onMessaje(mensaje);
                    },
                error -> listener.onError(error.getMessage())
            );
        stompClient.connect();
    }

    public void desconectar(){
        if (topicDisposable != null) topicDisposable.dispose();
        if (connectionDisposable != null) connectionDisposable.dispose();
        if (stompClient != null) stompClient.disconnect();
    }
}
