package ort.proyectofinal;


import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ort.proyectofinal.Clases.AutocompleteCustomArrayAdapter;
import ort.proyectofinal.Clases.CustomAutoCompleteTextChangedListener;
import ort.proyectofinal.Clases.CustomAutoCompleteView;
import ort.proyectofinal.Clases.Evento;


public class AgregarEvento extends AppCompatActivity {

    EditText nombreET, descripcionET;
    ArrayList<Evento> eventos;
    AccessToken accessToken;
    TextView horaTV, fechaTV;

    public CustomAutoCompleteView myAutoComplete;
    public AutocompleteCustomArrayAdapter myAdapter;
    ArrayList<Address> direccs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_evento);
        setupToolbar();

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
        fechaTV = (TextView) findViewById(R.id.fecha);
        horaTV = (TextView) findViewById(R.id.hora);
        eventos = new ArrayList<>();
        accessToken = accessToken.getCurrentAccessToken();

        fechaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            DialogFecha();
            }
        });

        horaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHora();
            }
        });
    }
    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Hide the title
        getSupportActionBar().setTitle(null);
        // Set onClickListener to customView
        TextView tvSave = (TextView) findViewById(R.id.toolbar_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarEvento();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.toolbar_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgregarEvento.this, ListarEventos.class);
                startActivity(intent);
            }
        });
    }

    public void DialogFecha() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.definir_fecha, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Definir Fecha");
        dialogBuilder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final DatePicker fechaDP = (DatePicker) dialogView.findViewById(R.id.datePicker);
                fechaDP.setMinDate(System.currentTimeMillis() - 1000);
                int año  = fechaDP.getYear();
                int mes = fechaDP.getMonth() +1;
                int dia = fechaDP.getDayOfMonth();
                String day = String.valueOf(dia);
                String month = String.valueOf(mes);
                if(dia<10){
                    day = "0"+String.valueOf(dia);
                }
                if(mes<10){
                    month = "0"+String.valueOf(mes);
                }
                String fechaa = String.valueOf(day) +"/" + String.valueOf(month) + "/" + String.valueOf(año);
                try{

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                    Date date1 = formatter.parse(fechaa);

                    String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                    Date date2 = formatter.parse(date);

                    if (date1.compareTo(date2)>0)
                    {
                        String fecha = String.valueOf(año) +"-" + String.valueOf(mes) + "-" + String.valueOf(dia);
                        fechaTV.setText(fecha);
                    }else{
                        Toast.makeText(AgregarEvento.this, "Elija una fecha válida", Toast.LENGTH_SHORT).show();
                    }
                }catch (ParseException e1){
                    e1.printStackTrace();
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

    public void DialogHora() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.definir_hora, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Definir Hora");
        dialogBuilder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final TimePicker horaTP = (TimePicker) dialogView.findViewById(R.id.timePicker);
                int hour = horaTP.getCurrentHour();
                int minute = horaTP.getCurrentMinute();
                String hora;
                if(minute <10){
                    hora = String.valueOf(hour) + ":0" + String.valueOf(minute);
                }else {
                    hora = String.valueOf(hour) + ":" + String.valueOf(minute);
                }
                horaTV.setText(hora);
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void AgregarEvento () {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        String nombre = nombreET.getText().toString();
        String lugar = myAutoComplete.getText().toString();
        String descripcion = descripcionET.getText().toString();
        String fecha = fechaTV.getText().toString();
        if (nombre.length() == 0 | descripcion.length() == 0 | lugar.length() == 0)
        {
            Toast.makeText(AgregarEvento.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
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
