package ort.proyectofinal;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainEvento extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    Evento e;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_evento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = getIntent().getExtras();
        e = (Evento) extras.getSerializable("evento");
        toolbar.setTitle(e.getIdNombre());
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.map=map;
        map.getUiSettings().setZoomControlsEnabled(true);

    }

    public void consultarDireccion(View v) {
        String dirStr = e.getLugar();
        if (!dirStr.isEmpty()) {
            new GeolocalizacionTask().execute(dirStr);
        }
    }


    // Utiliza la clase android.location.Geocoder
    // Parametros
    // String - la direccion a buscar que recibe doInBackground
    // Void -  Progreso (no se usa)
    // List<Address> - lo que devuelve doInBackground
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
