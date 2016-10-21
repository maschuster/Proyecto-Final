package ort.proyectofinal;

import android.content.Context;
import android.content.DialogInterface;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by Schuster on 29/05/2016.
 */
public class ObjetoAdapter extends BaseAdapter {

    ArrayList<Objeto> objetos;
    Context context;
    MainEvento mEvento;
    Spinner spinner;

    public ObjetoAdapter(MainEvento mEvento, ArrayList<Objeto> objetos) {
        this.context = mEvento.getApplicationContext();
        this.mEvento = mEvento;
        this.objetos=objetos;
    }

    @Override
    public int getCount() {
        return objetos.size();
    }

    @Override
    public Object getItem(int i) {
        return objetos.get(i);
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
            view = inflater.inflate(R.layout.list_item_objetos, viewGroup, false);
        }

        TextView nombreTV = (TextView)view.findViewById(R.id.nombre);
        TextView precioTV = (TextView)view.findViewById(R.id.precio);
        TextView participanteTV = (TextView)view.findViewById(R.id.participante);
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkboxobjeto);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                objetos.get(position).setChecked(cb.isChecked());
                ModificarObjetoSQL(position,0,objetos.get(position).getIdParticipante());
            }
        });
        Objeto o = objetos.get(position);
        String precio = "$";
        precioTV.setText(precio + String.valueOf(o.getPrecio()));
        nombreTV.setText(String.valueOf(o.getNombre()));
        ImageView fotoIV = (ImageView)view.findViewById(R.id.image_participante_obj);
        Picasso.with(context).load(R.drawable.participante_default).transform(new CircleTransform()).into(fotoIV);
        for(Participante p : mEvento.participantes){
            if(p.getIdParticipante() == o.getIdParticipante()){
                String nombre = p.getNombre();
                if(nombre.indexOf(" ") == -1){
                    participanteTV.setText(nombre);
                }else {
                    int posicion = nombre.indexOf(" ");
                    nombre = nombre.substring(0, posicion);
                    participanteTV.setText(nombre);
                }
                break;
            }else{
                participanteTV.setText(String.valueOf("(Sin Asignar)"));
            }
        }
        checkBox.setChecked(o.isChecked());
        participanteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mEvento);
                LayoutInflater inflater = mEvento.getLayoutInflater();

                final View dialogView = inflater.inflate(R.layout.asignar_participante, null);
                dialogBuilder.setView(dialogView);
                spinner = (Spinner) dialogView.findViewById(R.id.spinner);
                final ArrayList<String> participantesAL = new ArrayList<>();
                for (Participante item : mEvento.participantes) {
                    participantesAL.add(item.getNombre());
                }
                participantesAL.add("Sin Asignar");
                ArrayAdapter<String> partAdapter;
                partAdapter = new ArrayAdapter<>(mEvento, android.R.layout.simple_spinner_item, participantesAL);
                partAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                spinner.setAdapter(partAdapter);

                dialogBuilder.setTitle("Asignar Participante");
                dialogBuilder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(spinner.getSelectedItem().toString() == "Sin Asignar"){
                            ModificarObjetoSQL(position,1,0);
                        }else{
                            final int idParticipantePosition = spinner.getSelectedItemPosition();
                            Participante p = mEvento.participantes.get(idParticipantePosition);
                            final int idParticipante = p.getIdParticipante();
                            ModificarObjetoSQL(position,1,idParticipante);
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
        });

        precioTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mEvento);
                LayoutInflater inflater = mEvento.getLayoutInflater();

                final View dialogView = inflater.inflate(R.layout.cambiar_precio, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("Cambiar Precio");
                dialogBuilder.setPositiveButton("Listo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText precioET = (EditText) dialogView.findViewById(R.id.precioET);
                        int precio = Integer.parseInt(precioET.getText().toString());
                        if(String.valueOf(precio).equals("")){
                            Toast.makeText(mEvento, "Debes completar todos los campos", Toast.LENGTH_SHORT).show();
                        }else {
                            ModificarObjetoSQL(position, 2, precio);
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
        });

        return view;
    }

    public void ModificarObjetoSQL (int position, int tipomod, int idParticipanteoPrecio) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            Objeto o = objetos.get(position);
            json.put("tipomod", tipomod);
            json.put("idObjeto", o.getIdObjeto());

            if(tipomod == 0){
                if (o.isChecked()) {
                    json.put("estado", "1");
                }else{
                    json.put("estado", "0");
                }
            }if(tipomod==1){
                json.put("idParticipante", idParticipanteoPrecio);
            }
            if (tipomod ==2){
                json.put("precio", idParticipanteoPrecio);
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .url("http://eventospf2016.azurewebsites.net/actualizarobjeto.php")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            Log.d("Response", response.body().string());
        } catch (IOException | JSONException e) {
            Log.d("Error", e.getMessage());
        }
        if(tipomod ==1  || tipomod==2){
            mEvento.ActualizarObjetoTask();
        }
    }
}