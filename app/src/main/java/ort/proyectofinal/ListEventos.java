package ort.proyectofinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ort.proyectofinal.Clases.CircleTransform;
import ort.proyectofinal.Clases.Evento;
import ort.proyectofinal.Clases.Usuario;

public class ListEventos extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ProfilePictureView profilePictureView;
    ImageView profile_pic;
    private TextView navUserName;
    TextView TVnombre,TVfecha, TVlugar, TVdescripcion;
    ListView listVW;
    ArrayList<Evento> eventos;
    AccessToken accessToken;
    String nombre = "";
    public static String url = "http://eventospf2016.azurewebsites.net/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        LoadPreferences();
        setContentView(R.layout.activity_list_eventos);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        inicializarToolbar();
        accessToken = AccessToken.getCurrentAccessToken();
        TVnombre = (TextView) findViewById(R.id.nombre);
        TVfecha = (TextView) findViewById(R.id.fecha);
        TVlugar = (TextView) findViewById(R.id.lugar);
        eventos = new ArrayList<>();
        listVW = (ListView) findViewById(R.id.listVw);
        TVdescripcion = (EditText) findViewById(R.id.descripcion);
        Bundle extras = getIntent().getExtras();
        new EventosTask().execute(url);
        getName();
        Picasso.with(getApplicationContext()).load("https://graph.facebook.com/"+accessToken.getUserId()+"/picture?type=large").transform(new CircleTransform()).into(profile_pic);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListEventos.this, AgregarEvento.class);
                startActivity(intent);
            }
        });

        listVW.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick (AdapterView<?> adapter, View V, int position, long l){
                Intent intent = new Intent(ListEventos.this, MainEvento.class);
                Evento e = eventos.get(position);
                intent.putExtra("evento", e);
                startActivityForResult(intent, 0);
            }
        });

    }

    private void inicializarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setearListener(navigationView);
        profile_pic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_pic);
        navUserName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_username);

    }

    private void setearListener(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                switch(item.getItemId()) {
                    case R.id.cerrarsesion:
                        LoginManager.getInstance().logOut();

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("log", "");
                        editor.commit();

                        Intent intent = new Intent(ListEventos.this, MainActivity.class);
                        startActivity(intent);
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

    }


    // Abre el drawer al clickear el burger
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            Request request = new Request.Builder()
                    .url(url+"refresheventos.php")
                    .addHeader("X-USER-ID",accessToken.getUserId())
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String json = response.body().string();
                return parsearResultado(json);
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

                Evento e = new Evento(idEvento,nombre,fecha,descripcion,lugar);
                eventos.add(e);
            }
            return eventos;
        }
    }

    public String getName(){
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response.getRawResponse());
                            nombre = jsonObject.getString("name");
                            navUserName.setText(nombre);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
        return nombre;
    }

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  data = sharedPreferences.getString("log", "") ;
        if(data.equals("")){
            Intent intent = new Intent(ListEventos.this, MainActivity.class);
            startActivity(intent);
        }
    }


}
