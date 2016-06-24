package ort.proyectofinal;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainEvento extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {

    public static String url = "http://eventospf2016.herokuapp.com/";
    GoogleMap map;
    Evento e;
    Toolbar toolbar;
    TextView TVdescripcion, TVfecha;
    private FABToolbarLayout layout;
    private View salirfab, agregarobjetofab, three, four;
    private ListView list, listpersonas;
    private View fab;
    ArrayList<Objeto> objetos;
    ArrayList<Persona> personas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_evento);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TVdescripcion = (TextView) findViewById(R.id.descripcion);
        TVfecha = (TextView) findViewById(R.id.fecha);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        e = (Evento) extras.getSerializable("evento");
        toolbar.setTitle(e.getNombre());
        setSupportActionBar(toolbar);
        TVdescripcion.setText(e.getDescripcion());
        listpersonas = (ListView) findViewById(R.id.listpersonas);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        String fecha = e.getFecha().substring(0,10);
        TVfecha.setText(fecha);
        String dirStr = e.getLugar();
        if (!dirStr.isEmpty()) {
            new GeolocalizacionTask().execute(dirStr);
        }
        objetos = new ArrayList<>();
        new ObjetosTask().execute(url);
        new PersonasTask().execute();
        layout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
        salirfab = findViewById(R.id.salirfab);
        agregarobjetofab = findViewById(R.id.agregarobjetofab);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        list = (ListView) findViewById(R.id.list);
        fab = findViewById(R.id.fabtoolbar_fab);
        salirfab.setOnClickListener(this);
        agregarobjetofab.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.salirfab:
                layout.hide();
                break;
            case R.id.agregarobjetofab:
                showChangeLangDialog();
                break;
            case R.id.three:
                showChangeLangDialogPersona();
                break;
            case R.id.four:
                Toast.makeText(this, "Sin Acciones", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void showChangeLangDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.agregar_objeto, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Agregar Nuevo Objeto");
        dialogBuilder.setMessage("Ingrese los datos");
        dialogBuilder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText edt = (EditText) dialogView.findViewById(R.id.nombre);
                EditText edt2 = (EditText) dialogView.findViewById(R.id.precio);
                final String nombre = edt.getText().toString();
                final int precio = Integer.parseInt(edt2.getText().toString());
                final int idEvento = e.getIdEvento();
                AgregarObjetoSQL(nombre,precio,idEvento);
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void showChangeLangDialogPersona() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.agregar_objeto, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Agregar Nueva Persona");
        dialogBuilder.setMessage("Ingrese los datos");
        dialogBuilder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText edt = (EditText) dialogView.findViewById(R.id.nombre);
                final String nombre = edt.getText().toString();
                final int idEvento = e.getIdEvento();
                AgregarPersonaSQL(nombre,idEvento);
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }



    public void AgregarObjetoSQL (String nombre, int precio, int idEvento) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("nombre", nombre);
            json.put("precio", precio);
            json.put("idEvento", idEvento);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .url(url+"agregarobjeto.php")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            Log.d("Response", response.body().string());
        } catch (IOException | JSONException e) {
            Log.d("Error", e.getMessage());
        }
        new ObjetosTask().execute(url);

    }
    private class ObjetosTask extends AsyncTask<String, Void, ArrayList<Objeto>> {
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(ArrayList<Objeto> objetosResult) {
            super.onPostExecute(objetosResult);
            if (!objetosResult.isEmpty()) {
                objetos = objetosResult;
                ObjetoAdapter adapter = new ObjetoAdapter(getApplicationContext(),objetosResult);
                list.setAdapter(adapter);
            }
        }

        @Override
        protected ArrayList<Objeto> doInBackground(String... params) {

            Request request = new Request.Builder()
                    .url(url+"refreshobjetos.php")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return parsearResultado(response.body().string());
            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());
                return new ArrayList<Objeto>();
            }
        }

        ArrayList<Objeto> parsearResultado(String JSONstr) throws JSONException {
            ArrayList<Objeto> objetos = new ArrayList<>();
            JSONArray jsonObjetos = new JSONArray(JSONstr);
            for (int i = 0; i < jsonObjetos.length(); i++) {
                JSONObject jsonResultado = jsonObjetos.getJSONObject(i);
                int idObjeto = jsonResultado.getInt("idObjeto");
                String nombre = jsonResultado.getString("nombre");
                int precio = jsonResultado.getInt("precio");

                Objeto o = new Objeto(nombre,precio,idObjeto);
                objetos.add(o);
            }
            return objetos;
        }

    }


    public void AgregarPersonaSQL (String nombre, int idEvento) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("nombre", nombre);
            json.put("idEvento", idEvento);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .url(url +"agregarobjeto.php")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            Log.d("Response", response.body().string());
        } catch (IOException | JSONException e) {
            Log.d("Error", e.getMessage());
        }
        new ObjetosTask().execute(url);

    }
    private class PersonasTask extends AsyncTask<String, Void, ArrayList<Persona>> {
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(ArrayList<Persona> personasResult) {
            super.onPostExecute(personasResult);
            if (!personasResult.isEmpty()) {
                personas = personasResult;
                PersonaAdapter adapter = new PersonaAdapter(getApplicationContext(),personasResult);
                listpersonas.setAdapter(adapter);
            }
        }

        @Override
        protected ArrayList<Persona> doInBackground(String... params) {

            Request request = new Request.Builder()
                    .url(url+"refreshpersonas.php")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return parsearResultado(response.body().string());
            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());
                return new ArrayList<Persona>();
            }
        }

        ArrayList<Persona> parsearResultado(String JSONstr) throws JSONException {
            ArrayList<Persona> personas = new ArrayList<>();
            JSONArray jsonPersonas = new JSONArray(JSONstr);
            for (int i = 0; i < jsonPersonas.length(); i++) {
                JSONObject jsonResultado = jsonPersonas.getJSONObject(i);
                int idPersona = jsonResultado.getInt("idPersona");
                String nombre = jsonResultado.getString("nombre");

                Persona p = new Persona(idPersona,nombre);
                personas.add(p);
            }
            return personas;
        }

    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.map=map;
        map.getUiSettings().setZoomControlsEnabled(true);

    }
    private class GeolocalizacionTask extends AsyncTask<String, Void,List<Address>> {
        @Override
        protected void onPostExecute(List<Address> direcciones) {
            super.onPostExecute(direcciones);

            if (!direcciones.isEmpty()) {
                // Muestro la primera direccion recibida
                Address dirRecibida = direcciones.get(0);  // La primera direccion
                String addressStr=dirRecibida.getAddressLine(0);  // Primera linea del texto

                // Muestro coordenadas
                double lat = dirRecibida.getLatitude(); //
                double lng = dirRecibida.getLongitude();
                String coordStr = lat+ "," + lng;

                // Ubico la direccion en el mapa
                if (map != null) {
                    CameraUpdate center=
                            CameraUpdateFactory.newLatLng(new LatLng(lat,lng));
                    CameraUpdate zoom= CameraUpdateFactory.zoomTo(15);
                    map.moveCamera(center);
                    map.animateCamera(zoom);   // Posiciono la camara en las coordenadas recibidas

                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(dirRecibida.getAddressLine(0)));  // Dibujo el marker
                }
            }
        }
        @Override
        protected List<Address> doInBackground(String... params) {
            String address = params[0];
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> addresses = null;
            try {
                // Utilizo la clase Geocoder para buscar la direccion. Limito a 10 resultados
                addresses = geocoder.getFromLocationName(address, 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }
    }
}
