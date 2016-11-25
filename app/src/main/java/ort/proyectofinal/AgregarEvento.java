package ort.proyectofinal;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ort.proyectofinal.Clases.AutocompleteCustomArrayAdapter;
import ort.proyectofinal.Clases.CircleTransform;
import ort.proyectofinal.Clases.CustomAutoCompleteTextChangedListener;
import ort.proyectofinal.Clases.CustomAutoCompleteView;
import ort.proyectofinal.Clases.Evento;


public class AgregarEvento extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static public int REQUEST_IMAGE_GET = 2;
    //static final int REQUEST_TAKE_PHOTO = 3;
    ImageButton ibfoto;
    EditText nombreET, descripcionET;
    ArrayList<Evento> eventos;
    AccessToken accessToken;
    TextView horaTV, fechaTV, uriTV;
    String nombre,lugar,descripcion,fecha,hora;
    String url ="http://eventospf2016.azurewebsites.net/agregarevento.php";

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

        uriTV = (TextView) findViewById(R.id.uriTV);
        ibfoto = (ImageButton) findViewById(R.id.ibfoto);
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

        Picasso.with(this).load(R.drawable.camara).into(ibfoto);
        ibfoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
    });
    }

    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Hide the title
        getSupportActionBar().setTitle(null);
        // Set onClickListener to customView
        final TextView tvSave = (TextView) findViewById(R.id.toolbar_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSave.setEnabled(false);
                nombre = nombreET.getText().toString();
                lugar = myAutoComplete.getText().toString();
                descripcion = descripcionET.getText().toString();
                fecha = fechaTV.getText().toString();
                hora = horaTV.getText().toString();
                if (nombre.length() > 0 | descripcion.length() > 0 | lugar.length() > 0)
                {
                    new AgregarEventoTask().execute();
                }else{
                    Toast.makeText(AgregarEvento.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.toolbar_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgregarEvento.this, ListEventos.class);
                startActivity(intent);
            }
        });
    }

    public void DialogFecha() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.definir_fecha, null);
        dialogBuilder.setView(dialogView);

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

                    if (date1.compareTo(date2)>=0)
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
            try {
                OkHttpClient client = new OkHttpClient();
                String url ="http://eventospf2016.azurewebsites.net/agregarevento.php";
                JSONObject json = new JSONObject();
                json.put("idAdmin", String.valueOf(accessToken.getUserId()));
                json.put("nombre", nombre);
                json.put("fecha", fecha+" "+hora+":00");
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
                Intent intent = new Intent(this, ListEventos.class);
                startActivity(intent);
            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());
            }
        }


    private class AgregarEventoTask extends AsyncTask<String, Void, String> {
        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);
            //progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(getApplicationContext(), ListEventos.class);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... params) {
            RequestBody body = null;
            try {
                body = generarJSON();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Request request = new Request.Builder()
                    .addHeader("X-USER-ID",accessToken.getUserId())
                    .url("http://eventospf2016.azurewebsites.net/agregarevento.php")
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return parsearRespuesta(response.body().string());
            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());
                return "";
            }
        }


    RequestBody generarJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("idAdmin", String.valueOf(accessToken.getUserId()));
        json.put("nombre", nombre);
        json.put("fecha", fecha+" "+hora+":00");
        json.put("lugar", lugar);
        json.put("descripcion", descripcion);

        if (uriTV.getText() != null && !uriTV.getText().toString().isEmpty()) {
            Bitmap finalImage;
            if (Uri.parse(uriTV.getText().toString()).getScheme().startsWith("http")) {
                finalImage = getBitmapFromURL(uriTV.getText().toString());
            } else {
                // Get Bitmap image from Uri
                try {

                    ParcelFileDescriptor parcelFileDescriptor =
                            getApplicationContext().getContentResolver().openFileDescriptor(Uri.parse(uriTV.getText().toString()), "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();


/* esto reduce tamaño del bitmap */
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeFileDescriptor(fileDescriptor, null, o);

                    // The new size we want to scale to
                    final int REQUIRED_SIZE=200;

                    // Find the correct scale value. It should be the power of 2.
                    int scale = 1;
                    while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                            o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                        scale *= 2;
                    }

                    // Decode with inSampleSize
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize = scale;

                    Bitmap originalImage =  BitmapFactory.decodeFileDescriptor(fileDescriptor, null, o2);
                    parcelFileDescriptor.close();
                    finalImage = originalImage;
                } catch (java.io.IOException e){
                    e.getMessage();
                    return null;
                }

            }
            // Convert bitmap to output string
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            finalImage.compress(Bitmap.CompressFormat.PNG, 100, stream);   // Compress to PNG lossless
            byte[] byteArray = stream.toByteArray();

            String fileName = UUID.randomUUID().toString() + ".png";


            String base64pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
            json.put("foto",base64pic);
            //mpb.addPart(Headers.of("Content-Disposition", "form-data; name=\"image\"; filename=\"" + fileName + "\""),
            //  RequestBody.create(MEDIA_TYPE_PNG, byteArray));

        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

        return body;
    }

    String parsearRespuesta(String JSONstr) throws JSONException {
        org.json.JSONObject respuesta = new org.json.JSONObject(JSONstr);
        if (respuesta.has("id")) {
            String id = respuesta.getString("id");
            return id;
        } else {
            String error = respuesta.getString("Error");
            return error;
        }
    }

    private void addMultipartField(MultipartBuilder mpb, String value, String fieldname){
        if (!value.isEmpty())
            mpb.addPart(Headers.of("Content-Disposition", "form-data; name=\""+fieldname+"\""),
                    RequestBody.create(null, value));

    }

    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == this.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                ibfoto.setImageBitmap(bitmap);
                uriTV.setText(uri.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == this.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ibfoto.setImageBitmap(imageBitmap);
            //setPic();
            //galleryAddPic();
        }
    }



}

