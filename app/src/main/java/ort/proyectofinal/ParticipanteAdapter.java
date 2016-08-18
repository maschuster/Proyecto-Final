package ort.proyectofinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Schuster on 30/07/2016.
 */
public class ParticipanteAdapter extends BaseAdapter {

    ArrayList<Participante> participantes;
    Context context;

    public ParticipanteAdapter(Context context, ArrayList<Participante> participantes) {
        this.context = context;
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
        Participante p = participantes.get(position);
        nombreTV.setText(String.valueOf(p.getNombre()));
        return view;
    }
}