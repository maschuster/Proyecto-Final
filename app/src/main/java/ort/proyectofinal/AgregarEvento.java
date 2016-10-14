package ort.proyectofinal;


import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ort.proyectofinal.Clases.AutocompleteCustomArrayAdapter;
import ort.proyectofinal.Clases.CustomAutoCompleteTextChangedListener;
import ort.proyectofinal.Clases.CustomAutoCompleteView;
import ort.proyectofinal.Clases.Evento;


public class AgregarEvento extends AppCompatActivity {

    EditText nombreET, lugarET, descripcionET;
    ArrayList<Evento> eventos;
    DatePicker fechaDT;
    String mensaje;
    AccessToken accessToken;

    public CustomAutoCompleteView myAutoComplete;
    public AutocompleteCustomArrayAdapter myAdapter;
    ArrayList<Address> direccs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_evento);

        try{

            // instantiate database handler
            direccs = new ArrayList<>();

            // autocompletetextview is in activity_main.xml
            myAutoComplete = (CustomAutoCompleteView) findViewById(R.id.myautocomplete);

            myAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                    RelativeLayout rl = (RelativeLayout) arg1;
                    TextView tv = (TextView) rl.getChildAt(0);
                    myAutoComplete.setText(tv.getText().toString());

                }

            });

            // add the listener so it will tries to suggest while the user types
            myAutoComplete.addTextChangedListener(new CustomAutoCompleteTextChangedListener(this));

            // ObjectItemData has no value at first
            ArrayList<Address> ObjectItemData = new ArrayList<>();

            // set the custom ArrayAdapter
            myAdapter = new AutocompleteCustomArrayAdapter(this, R.layout.list_view_row, ObjectItemData);
            myAutoComplete.setAdapter(myAdapter);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        nombreET = (EditText) findViewById(R.id.nombre);
        descripcionET = (EditText) findViewById(R.id.descripcion);
        fechaDT = (DatePicker) findViewById(R.id.datePicker);
        eventos = new ArrayList<>();
        accessToken = accessToken.getCurrentAccessToken();
    }


    public void popup (String mensaje)
    {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(mensaje)
                .setPositiveButton("Ok", null)
                .create();
        dialog.show();
    }


    public void btnCrear (View view) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        String nombre = nombreET.getText().toString();
        String lugar = myAutoComplete.getText().toString();
        String descripcion = descripcionET.getText().toString();
        int año  = fechaDT.getYear();
        int mes = fechaDT.getMonth();
        int dia = fechaDT.getDayOfMonth();
        String fecha = String.valueOf(año) +"-" + String.valueOf(mes) + "-" + String.valueOf(dia);
        if (nombre.length() == 0 | descripcion.length() == 0 | lugar.length() == 0)
        {
            mensaje = "Debe llenar todos los campos";
            popup(mensaje);
        }else{
            try {
                OkHttpClient client = new OkHttpClient();
                String url ="http://eventospf2016.azurewebsites.net/agregarevento.php";
                JSONObject json = new JSONObject();
                json.put("idAdmin", String.valueOf(accessToken.getUserId()));
                json.put("nombre", nombre);
                json.put("fecha", fecha);
                json.put("lugar", lugar);
                json.put("descripcion", descripcion);
                json.put("foto", "foto.jpg");

                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

                Request request = new Request.Builder()
                        .addHeader("X-USER-ID",accessToken.getUserId())
                        .url(url)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                Log.d("Response", response.body().string());
                Intent intent = new Intent(this, ListarEventos.class);
                startActivity(intent);
            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());
            }
        }
    }
}
