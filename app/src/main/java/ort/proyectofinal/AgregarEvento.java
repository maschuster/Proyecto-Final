package ort.proyectofinal;


import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.util.ArrayList;


public class AgregarEvento extends AppCompatActivity {

    EditText nombreET, lugarET, descripcionET;
    ArrayList<Evento> eventos;
    DatePicker fechaDT;
    String mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_evento);
        nombreET = (EditText) findViewById(R.id.nombre);
        lugarET = (EditText) findViewById(R.id.lugar);
        descripcionET = (EditText) findViewById(R.id.descripcion);
        fechaDT = (DatePicker) findViewById(R.id.datePicker);
        eventos = new ArrayList<>();
    }


    public void popup (String mensaje)
    {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(mensaje)
                .setPositiveButton("Ok", null)
                .create();
        dialog.show();
    }


    public void btnCrear (View view)
    {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        String nombre = nombreET.getText().toString();
        String lugar = lugarET.getText().toString();
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
            OkHttpClient client = new OkHttpClient();
            String url ="http://10.152.2.5/agregarevento.php" + "?Nombre="+ nombre + "&" + "Fecha="+ fecha + "&" + "Lugar="+ lugar + "&" + "escripcion="+ descripcion;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                Log.d("Error", e.getMessage());
            }
        }
    }
}
