package com.example.descargaficherohttp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button botn = findViewById(R.id.buttonDownload);
        botn.setOnClickListener(this::descargar);
    }

    TextView txtDescarga;

    public void descargar(View v){
        EditText edURL = findViewById(R.id.edURL);
        txtDescarga = findViewById(R.id.textView);
        txtDescarga.setMovementMethod(new ScrollingMovementMethod());

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo!=null&& networkInfo.isConnected()){
            new DescargaPaginaWeb().execute(edURL.getText().toString());
        } else {
            edURL.setText("No se ha poddio establecer la conexión");
        }
    }

    public class DescargaPaginaWeb extends AsyncTask<String, Void, String>{
        // params viene del método execute() call: params[0] es la url.
        @Override
        protected String doInBackground(String... urls) {
            try {
                return descargaURL(urls[0]);
            } catch (IOException e) {
                return "Imposible cargar, URL mal formada";
            }
        }
        // onPostExecute visualiza los resultados del AsyncTask.

        @Override
        protected void onPostExecute(String s) {
            txtDescarga.setText(s);
        }
/** Este método lee el inputstream convirtiéndolo en una cadena
* ayudándonos con un ByteArrayOutputStream()
*/
        private String leer(InputStream in){
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = in.read();
                while (i!=-1){
                    bo.write(i);
                    i = in.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }

        // Dada una URL, establece una conexión HttpUrlConnection y devuelve
// el contenido de la página web con un InputStream, y que se transforma a un String.
    private String descargaURL (String myurl) throws IOException {
            InputStream is = null;
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000); // en milisegnudos
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                //Aquí comienza la consulta
                conn.connect();
                int respuesta = conn.getResponseCode();
                is = conn.getInputStream();
                return leer(is);
            } finally {
                is.close();
            }
    }

    }


}