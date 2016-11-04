package ort.proyectofinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LightingColorFilter;
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
import android.widget.ImageView;
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
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ort.proyectofinal.Clases.CircleTransform;
import ort.proyectofinal.Clases.Evento;
import ort.proyectofinal.Clases.Objeto;
import ort.proyectofinal.Clases.Participante;
import ort.proyectofinal.Clases.Usuario;
import ort.proyectofinal.Clases.Votacion;


public class MainEvento extends AppCompatActivity implements View.OnClickListener {

    public static String url = "http://eventospf2016.azurewebsites.net/";
    Evento e;
    Toolbar toolbar;
    TextView TVdescripcion, TVfecha, TVlugar;
    private FABToolbarLayout layout;
    private View salirfab, agregarobjetofab, three, four;
    private ListView list, listparticipantes, listvotaciones;
    private View fab;
    ImageView imgevento;
    ArrayList<Objeto> objetos;
    ArrayList<Usuario> friends;
    ArrayList<Participante> participantes;
    ArrayList<Votacion> votaciones;
    AccessToken accessToken;
    FriendAdapter adapter;
    MainEvento mEvento;
    ParticipanteAdapter participanteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        accessToken = AccessToken.getCurrentAccessToken();
        setContentView(R.layout.activity_main_evento);
        TVdescripcion = (TextView) findViewById(R.id.descripcion);
        TVfecha = (TextView) findViewById(R.id.fecha);
        TVlugar = (TextView) findViewById(R.id.lugar);
        imgevento = (ImageView) findViewById(R.id.imagen_evento);

        Bundle extras = getIntent().getExtras();
        e = (Evento) extras.getSerializable("evento");
        TVdescripcion.setText(e.getDescripcion());
        listparticipantes = (ListView) findViewById(R.id.listparticipantes);
        listvotaciones = (ListView) findViewById(R.id.listvotaciones);
        setSupportActionBar(toolbar);
        String fecha = e.getFecha();
        TVfecha.setText(fecha);
        if(e.getLugar().indexOf(",") == -1){
            TVlugar.setText(e.getLugar());
        }else{
            int posicion = e.getLugar().indexOf(",");
            String lugar = e.getLugar().toString().substring(0, posicion);
            TVlugar.setText(lugar);
        }
        Picasso.with(getApplicationContext()).load(R.drawable.evento_default).transform(new CircleTransform()).into(imgevento);

        objetos = new ArrayList<>();
        participantes = new ArrayList<>();
        accessToken = AccessToken.getCurrentAccessToken();
        friends= new ArrayList<>();
        mEvento = this;

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

        setupToolbar();
        new ParticipantesTask().execute(url);
        new ObjetosTask().execute(url);
        new VotacionesTask().execute();
        facebookfriends();

    }

    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Hide the title
        getSupportActionBar().setTitle(null);
        // Set onClickListener to customView
        TextView tvTitle = (TextView) findViewById(R.id.toolbar_title);
        tvTitle.setText(e.getNombre());
        ImageButton back = (ImageButton) findViewById(R.id.toolbar_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainEvento.this, ListEventos.class);
                startActivity(intent);
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
                showChangeLangDialogParticipante();
                break;
            case R.id.four:
                showChangeLangDialogVotacion();
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
        participantesAL.add("Sin Asignar");
        ArrayAdapter<String> partAdapter;
        partAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, participantesAL);
        partAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(partAdapter);

        dialogBuilder.setTitle("Agregar Nueva Compra");
        dialogBuilder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText edt = (EditText) dialogView.findViewById(R.id.nombre);
                EditText edt2 = (EditText) dialogView.findViewById(R.id.precio);
                final String nombre = edt.getText().toString();
                if(nombre.equals("") || edt2.getText().toString().length() == 0){
                    Toast.makeText(MainEvento.this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show();
                }else {
                    final int precio = Integer.parseInt(edt2.getText().toString());
                    if(precio >0){
                        if (spinner.getSelectedItem().toString() == "Sin Asignar") {
                            AgregarObjetoSQL(nombre, precio, 0);
                        } else {
                            final int idParticipantePosition = spinner.getSelectedItemPosition();
                            Participante p = participantes.get(idParticipantePosition);
                            final int idParticipante = p.getIdParticipante();
                            AgregarObjetoSQL(nombre, precio, idParticipante);
                        }
                    }else{
                        Toast.makeText(MainEvento.this, "Precio Inválido", Toast.LENGTH_SHORT).show();
                    }
                }
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
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        ImageButton button = (ImageButton)dialogView.findViewById(R.id.button);
        button.setImageResource(R.drawable.ic_person_add_black_24dp);
        button.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edt = (EditText) dialogView.findViewById(R.id.nombre);
                final String nombre = edt.getText().toString();
                if(nombre.equals("")){
                    Toast.makeText(MainEvento.this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show();
                }else{
                    Usuario personavirtual = new Usuario("personavirtual",nombre);
                    AgregarParticipante(personavirtual);
                }
            }
        });
        adapter = new FriendAdapter(this,friends);
        ListView listVwFriends = (ListView) dialogView.findViewById(R.id.listVwFriends);
        listVwFriends.setAdapter(adapter);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void showChangeLangDialogVotacion() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.agregar_pregunta, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Agregar Nueva Pregunta");
        dialogBuilder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final EditText preguntaET = (EditText) dialogView.findViewById(R.id.preguntaET);
                String pregunta = preguntaET.getText().toString();
                if(pregunta.length() == 0){
                    Toast.makeText(MainEvento.this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show();
                }else{
                    AgregarPreguntaSQL(pregunta);
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }




    public void AgregarPreguntaSQL(String pregunta){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("pregunta", pregunta);
            json.put("idEvento", e.getIdEvento());

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .addHeader("X-USER-ID",accessToken.getUserId())
                    .url("http://eventospf2016.azurewebsites.net/agregarpregunta.php")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            Log.d("Response", response.body().string());
        } catch (IOException | JSONException e) {
            Log.d("Error", e.getMessage());
        }
        new VotacionesTask().execute();
    }

    public void AgregarObjetoSQL(String nombre, int precio, int idParticipante) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("nombre", nombre);
            json.put("precio", precio);
            json.put("idEvento", e.getIdEvento());
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

    public void AgregarParticipante(Usuario f){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        accessToken = AccessToken.getCurrentAccessToken();
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("idEvento", e.getIdEvento());
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



    public class ObjetosTask extends AsyncTask<String, Void, ArrayList<Objeto>> {
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(ArrayList<Objeto> objetosResult) {
            super.onPostExecute(objetosResult);
            if (!objetosResult.isEmpty()) {
                objetos = objetosResult;
                ObjetoAdapter objetoAdapter = new ObjetoAdapter(mEvento , objetosResult);
                list.setAdapter(objetoAdapter);
                participanteAdapter.setItem(objetos);
                participanteAdapter.notifyDataSetChanged();
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
                int idParticipante = jsonResultado.getInt("idParticipante");
                if (estado == 1) {
                    checked = true;
                } else {
                    checked = false;
                }
                Objeto o = new Objeto(nombre, precio, idObjeto, checked, idParticipante);
                objetos.add(o);
            }
            return objetos;
        }

    }

    public class VotacionesTask extends AsyncTask<String, Void, ArrayList<Votacion>> {
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(ArrayList<Votacion> votacionesResult) {
            super.onPostExecute(votacionesResult);
            if (!votacionesResult.isEmpty()) {
                votaciones = votacionesResult;
                VotacionAdapter adapter = new VotacionAdapter(mEvento , votacionesResult);
                listvotaciones.setAdapter(adapter);
            }
        }

        @Override
        protected ArrayList<Votacion> doInBackground(String... params) {
            String idParticipante = "0";
            for (Participante p: participantes) {
                if(accessToken.getUserId().equals(p.getIdFacebook())){
                    idParticipante = String.valueOf(p.getIdParticipante());
                }
            }
            Request request = new Request.Builder()
                    .addHeader("X-USER-ID",accessToken.getUserId())
                    .addHeader("X-PARTICIPANTE-ID",idParticipante)
                    .url(url + "refreshpreguntas.php" + "?idEvento=" + e.getIdEvento())
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return parsearResultado(response.body().string());
            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());
                return new ArrayList<Votacion>();
            }
        }

        ArrayList<Votacion> parsearResultado(String JSONstr) throws JSONException {
            ArrayList<Votacion> votaciones = new ArrayList<>();
            JSONArray jsonVotaciones = new JSONArray(JSONstr);
            for (int i = 0; i < jsonVotaciones.length(); i++) {
                JSONObject jsonResultado = jsonVotaciones.getJSONObject(i);
                int idPregunta = jsonResultado.getInt("idPregunta");
                int idEvento = jsonResultado.getInt("idEvento");
                String pregunta = jsonResultado.getString("pregunta");
                int afirmativos = jsonResultado.getInt("afirmativos");
                int negativos = jsonResultado.getInt("negativos");
                int voto = jsonResultado.getInt("voto");

                Votacion v = new Votacion(idPregunta, idEvento, pregunta, afirmativos, negativos,voto);
                votaciones.add(v);
            }
            return votaciones;
        }

    }

    public void facebookfriends() {
        GraphRequest gr = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+accessToken.getUserId()+"/friends",
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
                                    boolean existe = false;
                                    JSONObject o=d.getJSONObject(i);
                                    String n=o.getString("name");
                                    String id=o.getString("id");
                                    Usuario f=new Usuario(id,n);
                                    friends.add(f);
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }

                            recorrerFriends();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );
        gr.executeAsync();
    }

    private class ParticipantesTask extends AsyncTask<String, Void, ArrayList<Participante>> {
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(ArrayList<Participante> participantesResult) {
            super.onPostExecute(participantesResult);
            if (!participantesResult.isEmpty()) {
                participantes = participantesResult;
                participanteAdapter = new ParticipanteAdapter(mEvento, participantesResult, objetos);
                listparticipantes.setAdapter(participanteAdapter);               //LISTPARTICIPANTES
            }
        }

        @Override
        protected ArrayList<Participante> doInBackground(String... params) {

            Request request = new Request.Builder()
                    .addHeader("X-USER-ID",accessToken.getUserId())
                    .url(url + "refreshparticipantes.php" + "?idEvento=" + e.getIdEvento())
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


    public void recorrerFriends(){
        for(Usuario friend : friends){
            for(Participante p : mEvento.participantes){
                if(p.getIdFacebook().equals(friend.getIdFacebook())){
                    friends.remove(friend);
                    break;
                }
            }
        }
    }

    public void ActualizarObjetoTask(){
        new ObjetosTask().execute(url);
    }

    public void ActualizarVotacionesTask(){
        new VotacionesTask().execute(url);
    }
}
