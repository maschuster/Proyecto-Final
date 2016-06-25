package ort.proyectofinal;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Schuster on 29/05/2016.
 */
public class ObjetoAdapter extends BaseAdapter {

    ArrayList<Objeto> objetos;
    Context context;

    public ObjetoAdapter(Context context, ArrayList<Objeto> objetos) {
        this.context = context;
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
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkboxobjeto);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                objetos.get(position).setChecked(cb.isChecked());
                ModificarObjetoSQL(position);
            }
        });
        Objeto o = objetos.get(position);
        precioTV.setText(String.valueOf(o.getPrecio()));
        nombreTV.setText(String.valueOf(o.getNombre()));
        checkBox.setChecked(o.isChecked());
        return view;
    }

    public void ModificarObjetoSQL (int position) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json = new JSONObject();
            Objeto o = objetos.get(position);
            if (o.isChecked()) {
                json.put("estado", "1");
            }else{
                json.put("estado", "0");
            }
            json.put("idObjeto", o.getIdObjeto());
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
    }
}