package ort.proyectofinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Schuster on 22/06/2016.
 */
public class PersonaAdapter extends BaseAdapter {

    ArrayList<Persona> personas;
    Context context;

    public PersonaAdapter(Context context, ArrayList<Persona> personas) {
        this.context = context;
        this.personas=personas;
    }

    @Override
    public int getCount() {
        return personas.size();
    }

    @Override
    public Object getItem(int i) {
        return personas.get(i);
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
            view = inflater.inflate(R.layout.list_item_objetos, viewGroup, false);
        }

        TextView nombreTV = (TextView)view.findViewById(R.id.nombre);
        Persona p = personas.get(position);
        nombreTV.setText(String.valueOf(p.getNombre()));
        return view;
    }
}