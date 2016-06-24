package ort.proyectofinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

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
            }
        });

        Objeto o = objetos.get(position);
        precioTV.setText(String.valueOf(o.getPrecio()));
        nombreTV.setText(String.valueOf(o.getNombre()));
        checkBox.setChecked(o.isChecked());
        return view;
    }
}