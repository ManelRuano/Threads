package com.example.threads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView textViewData;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewData = findViewById(R.id.textViewData);
        imageView = findViewById(R.id.imageView);

        findViewById(R.id.buttonGetData).setOnClickListener(this::onClickGetData);
    }

    public void onClickGetData(View view) {
        // Crida getDataFromUrl amb la URL desitjada
        String url = "https://api.myip.com";
        getDataFromUrl(url);

        // Crida a la funció per descarregar i mostrar la imatge
        loadAndDisplayImage();
    }

    private void getDataFromUrl(String urlString) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String data = stringBuilder.toString();

                // Mostra les dades a la consola de debug
                Log.i("NetworkResponse", "Data: " + data);

                // Actualitza el TextView des del fil principal
                runOnUiThread(() -> textViewData.setText(data));

            } catch (Exception e) {
                Log.e("NetworkResponse", "Error: " + e.getMessage());
            }
        });
    }

    private void loadAndDisplayImage() {
        new Thread(() -> {
            String urldisplay = "https://randomfox.ca/images/122.jpg";
            Bitmap bitmap = null;
            try {
                URL url = new URL(urldisplay);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                bitmap = BitmapFactory.decodeStream(connection.getInputStream());
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            // Mostra la imatge al Handler en el fil de l'UI
            if (bitmap != null) {
                Handler handler = new Handler(Looper.getMainLooper());
                Bitmap finalBitmap = bitmap;
                handler.post(() -> imageView.setImageBitmap(finalBitmap));
            }
        }).start();
    }
}
