package ort.proyectofinal;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ListarEventos extends AppCompatActivity {

    TextView TVnombre,TVfecha, TVlugar, TVdescripcion;
    ListView listVW;
    ArrayList<Evento> eventos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_eventos);
        TVnombre = (TextView) findViewById(R.id.nombre);
        TVfecha = (TextView) findViewById(R.id.fecha);
        TVlugar = (TextView) findViewById(R.id.lugar);
        eventos = new ArrayList<>();
        listVW = (ListView) findViewById(R.id.listVw);
        TVdescripcion = (EditText) findViewById(R.id.descripcion);
        String url = "http://10.152.2.5/refresheventos.php";
        new EventosTask().execute(url);

        listVW.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick (AdapterView<?> adapter, View V, int position, long l){

                Intent intent = new Intent(ListarEventos.this, MainEvento.class);
                Evento e = eventos.get(position);
                intent.putExtra("evento", e);
                startActivity(intent);
            }
        });
    }


    public void btnCrear (View view)
    {
        Intent intent = new Intent(this, AgregarEvento.class);
        startActivity(intent);
    }

    private class EventosTask extends AsyncTask<String, Void, ArrayList<Evento>> {
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(ArrayList<Evento> eventosResult) {
            super.onPostExecute(eventosResult);
            if (!eventosResult.isEmpty()) {
                eventos = eventosResult;
                EventosAdapter adapter = new EventosAdapter(getApplicationContext(),eventosResult);
                listVW.setAdapter(adapter);
            }
        }

        @Override
        protected ArrayList<Evento> doInBackground(String... params) {
            String url = params[0];

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return parsearResultado(response.body().string());
            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());
                return new ArrayList<Evento>();
            }
        }

        ArrayList<Evento> parsearResultado(String JSONstr) throws JSONException {
            ArrayList<Evento> eventos = new ArrayList<>();
            JSONArray jsonEventos = new JSONArray(JSONstr);
            for (int i = 0; i < jsonEventos.length(); i++) {
                JSONObject jsonResultado = jsonEventos.getJSONObject(i);
                int idEvento = jsonResultado.getInt("idEvento");
                String nombre = jsonResultado.getString("nombre");
                String fecha = jsonResultado.getString("fecha");
                String descripcion = jsonResultado.getString("descripcion");
                String lugar = jsonResultado.getString("lugar");

                Evento e = new Evento(idEvento,fecha,lugar,descripcion,nombre);
                eventos.add(e);
            }
            return eventos;
        }


    }
}
