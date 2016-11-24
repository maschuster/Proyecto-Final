package ort.proyectofinal;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ort.proyectofinal.Clases.CircleTransform;
import ort.proyectofinal.Clases.Objeto;
import ort.proyectofinal.Clases.Participante;
import ort.proyectofinal.Clases.Votacion;

/**
 * Created by Schuster on 04/10/2016.
 */
public class VotacionAdapter  extends BaseAdapter {

    ArrayList<Votacion> votaciones;
    Context context;
    MainEvento mEvento;
    AccessToken accessToken;
    ImageButton positivoIB,negativoIB;
    int posicion;
    String idParticipante = "0";
    int voto;

    String url = "http://eventospf2016.azurewebsites.net/votar.php";

    public VotacionAdapter(MainEvento mEvento, ArrayList<Votacion> votaciones) {
        this.context = mEvento.getApplicationContext();
        this.mEvento = mEvento;
        this.votaciones=votaciones;
    }

    @Override
    public int getCount() {
        return votaciones.size();
    }

    @Override
    public Object getItem(int i) {
        return votaciones.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_votaciones, viewGroup, false);
        }

        accessToken = accessToken.getCurrentAccessToken();
        TextView preguntaTV = (TextView)view.findViewById(R.id.pregunta);
        TextView negativoTV = (TextView)view.findViewById(R.id.negativo);
        TextView positivoTV = (TextView)view.findViewById(R.id.positivo);
        positivoIB = (ImageButton) view.findViewById(R.id.positivoimg);
        negativoIB = (ImageButton) view.findViewById(R.id.negativoimg);
        final Votacion v = votaciones.get(position);
        preguntaTV.setText(String.valueOf(v.getPregunta()));
        negativoTV.setText(String.valueOf(v.getNegativos()));
        positivoTV.setText(String.valueOf(v.getAfirmativos()));

        if(v.getVoto() == 1){
            positivoIB.setImageResource(R.drawable.ic_thumb_up_black_24dp);
            negativoIB.setImageResource(R.drawable.ic_thumb_down_black_transparent_24dp);
            negativoIB.setEnabled(false);
            positivoIB.setEnabled(false);
        }else if (v.getVoto() == 2){
            positivoIB.setImageResource(R.drawable.ic_thumb_up_black_transparent_24dp);
            negativoIB.setImageResource(R.drawable.ic_thumb_down_black_24dp);
            negativoIB.setEnabled(false);
            positivoIB.setEnabled(false);
        }else if (v.getVoto() == 0){
            positivoIB.setImageResource(R.drawable.ic_thumb_up_black_transparent_24dp);
            negativoIB.setImageResource(R.drawable.ic_thumb_down_black_transparent_24dp);
        }

        negativoIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                negativoIB.setEnabled(false);
                positivoIB.setEnabled(false);
                if(v.getVoto() == 0){
                    v.setVoto(2);
                    voto = 2;
                    posicion = position;
                    for (Participante p: (mEvento).participantes) {
                        if(accessToken.getUserId().equals(p.getIdFacebook())){
                            idParticipante = String.valueOf(p.getIdParticipante());
                        }
                    }
                    new VotarTask().execute();
                    //Votar(2,position);
                }
            }
        });

        positivoIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                negativoIB.setEnabled(false);
                positivoIB.setEnabled(false);
                if(v.getVoto() == 0){
                    v.setVoto(1);
                    voto = 1;
                    posicion = position;
                    for (Participante p: (mEvento).participantes) {
                        if(accessToken.getUserId().equals(p.getIdFacebook())){
                            idParticipante = String.valueOf(p.getIdParticipante());
                        }
                    }
                    new VotarTask().execute();
                    //Votar(1,position);
                }
            }
        });

        return view;
    }


    public void Votar(int voto, int position) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            String idParticipante = "0";
            for (Participante p: (mEvento).participantes) {
                if(accessToken.getUserId().equals(p.getIdFacebook())){
                     idParticipante = String.valueOf(p.getIdParticipante());
                }
            }
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            json.put("idParticipante", idParticipante);
            json.put("idPregunta", votaciones.get(position).getIdPregunta());
            json.put("voto", voto);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .addHeader("X-USER-ID",accessToken.getUserId())
                    .url("http://eventospf2016.azurewebsites.net/votar.php")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            Log.d("Response", response.body().string());
        } catch (IOException | JSONException e) {
            Log.d("Error", e.getMessage());
        }
        mEvento.ActualizarVotacionesTask();
    }



    private class VotarTask extends AsyncTask<String, Void, String> {
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);
            Toast registro;
            if (!resultado.isEmpty()) {
                if (resultado.equals("NO")) {
                    registro = Toast.makeText(mEvento.getApplicationContext(), "Hubo un error, intente en un instante", Toast.LENGTH_SHORT);
                    registro.show();
                }
                mEvento.ActualizarVotacionesTask();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                RequestBody body = generarJSON();

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                return parsearRespuesta(response.body().string());
            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());
                return "";
            }
        }

        RequestBody generarJSON() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("idParticipante", idParticipante);
            json.put("idPregunta", votaciones.get(posicion).getIdPregunta());
            json.put("voto", voto);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            return body;
        }

        String parsearRespuesta(String JSONstr) throws JSONException {
            org.json.JSONObject respuesta = new org.json.JSONObject(JSONstr);
            if (respuesta.has("Mensaje")) {
                String msj = respuesta.getString("Mensaje");
                return msj;
            } else {
                String error = respuesta.getString("Error");
                return error;
            }
        }
    }
}
