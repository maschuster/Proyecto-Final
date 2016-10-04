package ort.proyectofinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ort.proyectofinal.Clases.CircleTransform;
import ort.proyectofinal.Clases.Participante;

/**
 * Created by Schuster on 30/07/2016.
 */
public class ParticipanteAdapter extends BaseAdapter {

    ArrayList<Participante> participantes;
    Context context;
    MainEvento mEvento;

    public ParticipanteAdapter(MainEvento mEvento, ArrayList<Participante> participantes) {
        this.context = mEvento.getApplicationContext();
        this.mEvento = mEvento;
        this.participantes=participantes;
    }

    @Override
    public int getCount() {
        return participantes.size();
    }

    @Override
    public Object getItem(int i) {
        return participantes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_participantes, viewGroup, false);
        }

        TextView nombreTV = (TextView)view.findViewById(R.id.nombre);
        ImageView fotoIV = (ImageView)view.findViewById(R.id.image_participante);
        Participante p = participantes.get(position);
        nombreTV.setText(String.valueOf(p.getNombre()));
        if(p.getIdFacebook() == "personavirtual"){
            Picasso.with(context).load(R.drawable.participante_default).transform(new CircleTransform()).into(fotoIV);
        }else{
            Picasso.with(context).load(R.drawable.participante_default).transform(new CircleTransform()).into(fotoIV);
        }
        return view;
    }
}