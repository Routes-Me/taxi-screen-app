package com.routesme.taxi_screen.Tracking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.routesme.taxiscreen.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketScreen extends AppCompatActivity implements View.OnClickListener {


    private EditText editText;
    private Button send, open, close;
    private TextView textView;
    private WebSocketClient mWebSocketClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_socket_screen);

        initialize();

    }

    private void initialize() {
        editText = findViewById(R.id.editText);
        send = findViewById(R.id.send);
        send.setOnClickListener(this);
        open = findViewById(R.id.open);
        open.setOnClickListener(this);
        close = findViewById(R.id.close);
        close.setOnClickListener(this);
        textView = findViewById(R.id.textView);
        connectWebSocket();
    }

    private void connectWebSocket() {

        URI uri;
        try {
            uri = new URI("ws://echo.websocket.org");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(textView.getText() + "\n" + message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.send:
                sendMessage();
            break;

            case R.id.open:
                openWebSocketConnection();
                break;

            case R.id.close:
                closeWebSocketConnection();
                break;

        }
    }

    private void openWebSocketConnection() {
        mWebSocketClient.connect();
    }


    private void sendMessage() {
        mWebSocketClient.send(editText.getText().toString());
        editText.setText("");
    }


    private void closeWebSocketConnection(){
        mWebSocketClient.close();
    }

}
