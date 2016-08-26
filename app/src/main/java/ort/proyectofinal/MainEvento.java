package ort.proyectofinal;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.google.android.gms.maps.GoogleMap;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class MainEvento extends AppCompatActivity implements View.OnClickListener {

    public static String url = "http://eventospf2016.azurewebsites.net/";
    GoogleMap map;
    Evento e;
    Toolbar toolbar;
    TextView TVdescripcion, TVfecha, TVlugar;
    private FABToolbarLayout layout;
    private View salirfab, agregarobjetofab, three, four;
    private ListView list, listparticipantes;
    private View fab;
    ArrayList<Objeto> objetos;
    ArrayList<Usuario> friends;
    ArrayList<Participante> participantes;
    AccessToken accessToken;
    FriendAdapter adapter;
    int idEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        accessToken = AccessToken.getCurrentAccessToken();
        setContentView(R.layout.activity_main_evento);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TVdescripcion = (TextView) findViewById(R.id.descripcion);
        TVfecha = (TextView) findViewById(R.id.fecha);
        TVlugar = (TextView) findViewById(R.id.lugar);

        Bundle extras = getIntent().getExtras();
        e = (Evento) extras.getSerializable("evento");
        TVdescripcion.setText(e.getDescripcion());
        idEvento = e.getIdEvento();
        listparticipantes = (ListView) findViewById(R.id.listparticipantes);
        toolbar.setTitle(e.getNombre());
        setSupportActionBar(toolbar);
        String fecha = e.getFecha().substring(0, 10);
        TVfecha.setText(fecha);
        TVlugar.setText(e.getLugar());
        objetos = new ArrayList<>();
        participantes = new ArrayList<>();
        accessToken = AccessToken.getCurrentAccessToken();
        friends= new ArrayList<>();

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

        new ObjetosTask().execute(url);
        new ParticipantesTask().execute(url);
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
                showChangeLangDialogParticipante();
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

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);
        final ArrayList<String> participantesAL = new ArrayList<>();
        for (Participante item : participantes) {
            participantesAL.add(item.getNombre());
        }
        ArrayAdapter<String> partAdapter;
        partAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, participantesAL);
        partAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(partAdapter);

        dialogBuilder.setTitle("Agregar Nuevo Objeto");
        dialogBuilder.setMessage("Ingrese los datos");
        dialogBuilder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText edt = (EditText) dialogView.findViewById(R.id.nombre);
                EditText edt2 = (EditText) dialogView.findViewById(R.id.precio);
                final int idParticipantePosition = spinner.getSelectedItemPosition();
                Participante p = participantes.get(idParticipantePosition);
                final int idParticipante = p.getIdParticipante();
                final String nombre = edt.getText().toString();
                final int precio = Integer.parseInt(edt2.getText().toString());
                final int idEvento = e.getIdEvento();
                AgregarObjetoSQL(nombre, precio, idEvento, idParticipante);
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void showChangeLangDialogParticipante() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.agregar_participante, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Agregar Nuevo Participante");
        dialogBuilder.setMessage("Ingrese los datos");
        dialogBuilder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        ImageButton button = (ImageButton)dialogView.findViewById(R.id.button);
        button.setImageResource(R.drawable.ic_person_add_black_24dp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edt = (EditText) dialogView.findViewById(R.id.nombre);
                final String nombre = edt.getText().toString();
                Usuario personavirtual = new Usuario("personavirtual",nombre);
                AgregarParticipante(personavirtual);
            }
        });
        facebookfriends();
        adapter = new FriendAdapter(getApplicationContext(),friends);
        ListView listVwFriends = (ListView) dialogView.findViewById(R.id.listVwFriends);
        listVwFriends.setAdapter(adapter);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }




    public void AgregarObjetoSQL(String nombre, int precio, int idEvento, int idParticipante) {
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
            json.put("idParticipante", idParticipante);
            json.put("estado", "0");

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .addHeader("X-USER-ID",accessToken.getUserId())
                    .url(url + "agregarobjeto.php")
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
                ObjetoAdapter adapter = new ObjetoAdapter(getApplicationContext(), objetosResult);
                list.setAdapter(adapter);
            }
        }

        @Override
        protected ArrayList<Objeto> doInBackground(String... params) {

            Request request = new Request.Builder()
                    .addHeader("X-USER-ID",accessToken.getUserId())
                    .url(url + "refreshobjetos.php" + "?idEvento=" + e.getIdEvento())
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
            boolean checked;
            for (int i = 0; i < jsonObjetos.length(); i++) {
                JSONObject jsonResultado = jsonObjetos.getJSONObject(i);
                int idObjeto = jsonResultado.getInt("idObjeto");
                String nombre = jsonResultado.getString("nombre");
                int precio = jsonResultado.getInt("precio");
                int estado = jsonResultado.getInt("estado");
                if (estado == 1) {
                    checked = true;
                } else {
                    checked = false;
                }
                Objeto o = new Objeto(nombre, precio, idObjeto, checked);
                objetos.add(o);
            }
            return objetos;
        }

    }




    public void facebookfriends() {

        GraphRequest gr = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                //"/"+accessToken.getUserId()+"/friends",
                "/me/invitable_friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        if (response != null){
                            Log.v("","FriendListRequestONComplete");
                            try{
                                JSONObject jsonObject=new JSONObject(response.getRawResponse());
                                JSONArray d=jsonObject.getJSONArray("data");
                                int l=(d!=null?d.length():0);
                                for(int i=0;i<l;i++){
                                    JSONObject o=d.getJSONObject(i);
                                    String n=o.getString("name");
                                    String id=o.getString("id");
                                    Usuario f=new Usuario(id,n);
                                    friends.add(f);
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );
        gr.executeAsync();

    }

    public void AgregarParticipante(Usuario f){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        accessToken = AccessToken.getCurrentAccessToken();
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("idEvento", idEvento);
            json.put("idFacebook", f.getIdFacebook());
            json.put("nombre", f.getNombre());

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .addHeader("X-USER-ID",accessToken.getUserId())
                    .url(url + "agregarparticipante.php")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            Log.d("Response", response.body().string());
        } catch (IOException | JSONException e) {
            Log.d("Error", e.getMessage());
        }
        new ParticipantesTask().execute(url);
    }

    private class ParticipantesTask extends AsyncTask<String, Void, ArrayList<Participante>> {
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(ArrayList<Participante> participantesResult) {
            super.onPostExecute(participantesResult);
            if (!participantesResult.isEmpty()) {
                participantes = participantesResult;
                ParticipanteAdapter adapter = new ParticipanteAdapter(getApplicationContext(), participantesResult);
                listparticipantes.setAdapter(adapter);               //LISTPARTICIPANTES
            }
        }

        @Override
        protected ArrayList<Participante> doInBackground(String... params) {

            Request request = new Request.Builder()
                    .addHeader("X-USER-ID",accessToken.getUserId())
                    .url(url + "refreshparticipantes.php" + "?idEvento=" + idEvento)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String json = response.body().string();
                return parsearResultado(json);
            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());
                return new ArrayList<Participante>();
            }
        }

        ArrayList<Participante> parsearResultado(String JSONstr) throws JSONException {
            ArrayList<Participante> participantes = new ArrayList<>();
            JSONArray jsonParticipante = new JSONArray(JSONstr);
            for (int i = 0; i < jsonParticipante.length(); i++) {
                JSONObject jsonResultado = jsonParticipante.getJSONObject(i);
                int idParticipante = jsonResultado.getInt("idParticipante");
                String idFacebook = jsonResultado.getString("idFacebook");
                String nombre = jsonResultado.getString("nombre");
                Participante p = new Participante(idParticipante,idFacebook, nombre);
                participantes.add(p);
            }
            return participantes;
        }

    }

}
