package ort.proyectofinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainEvento extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {

    GoogleMap map;
    Evento e;
    Toolbar toolbar;
    TextView TVdescripcion;
    CalendarView calendarioVW;
    DatePicker Datepicker;
    private FABToolbarLayout layout;
    private View one, two, three, four;
    private ListView list;
    private View fab;
    ArrayList<Objeto> objetos;
    String url = "http://192.168.0.7/refreshobjetos.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_evento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView TVdescripcion = (TextView) findViewById(R.id.descripcion);
        CalendarView CalendarioVW = (CalendarView) findViewById(R.id.calendarView);
        DatePicker Datepicker = (DatePicker) findViewById(R.id.datePicker);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle extras = getIntent().getExtras();
        e = (Evento) extras.getSerializable("evento");
        toolbar.setTitle(e.getNombre());
        setSupportActionBar(toolbar);
        TVdescripcion.setText(e.getDescripcion());
        String dirStr = e.getLugar();
        if (!dirStr.isEmpty()) {
            new GeolocalizacionTask().execute(dirStr);
        }
        /*SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            date = (Date)formatter.parse(e.getFecha());
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        long mills = date.getTime();
        calendarioVW.setDate(mills);*/

        objetos = new ArrayList<>();
        new ObjetosTask().execute(url);
        layout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        list = (ListView) findViewById(R.id.list);
        fab = findViewById(R.id.fabtoolbar_fab);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
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
    public void onBackPressed() {
        layout.hide();
    }
    @Override
    public void onClick(View v) {
        showChangeLangDialog();
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
                AgregarObjetoSQL(nombre,precio);
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }



    public void AgregarObjetoSQL (String nombre, int precio) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            OkHttpClient client = new OkHttpClient();
            String url ="http://192.168.0.7/agregarobjeto.php";
            JSONObject json = new JSONObject();
            json.put("nombre", nombre);
            json.put("precio", precio);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .url(url)
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
            String url = params[0];

            Request request = new Request.Builder()
                    .url(url)
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
