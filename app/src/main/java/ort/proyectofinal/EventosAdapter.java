package ort.proyectofinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Schuster on 25/05/2016.
 */

public class EventosAdapter extends BaseAdapter {

    ArrayList<Evento> eventos;
    Context context;

    public EventosAdapter(Context context, ArrayList<Evento> eventos) {
        this.context = context;
        this.eventos=eventos;
    }

    @Override
    public int getCount() {
        return eventos.size();
    }

    @Override
    public Object getItem(int i) {
        return eventos.get(i);
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
            view = inflater.inflate(R.layout.list_item, viewGroup, false);
        }

        TextView nombreTV = (TextView)view.findViewById(R.id.nombre);
        TextView descripcionTV = (TextView)view.findViewById(R.id.descripcion);
        TextView fechaTV = (TextView)view.findViewById(R.id.fecha);
        TextView lugarTV = (TextView)view.findViewById(R.id.lugar);

        Evento e = eventos.get(position);

        descripcionTV.setText(e.getDescripcion());
        nombreTV.setText(String.valueOf(e.getNombre()));
        fechaTV.setText(String.valueOf(e.getFecha()));
        lugarTV.setText(String.valueOf(e.getLugar()));
        return view;
    }
}
