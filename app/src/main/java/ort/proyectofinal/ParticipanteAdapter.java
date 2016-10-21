package ort.proyectofinal;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ort.proyectofinal.Clases.CircleTransform;
import ort.proyectofinal.Clases.Objeto;
import ort.proyectofinal.Clases.Participante;

/**
 * Created by Schuster on 30/07/2016.
 */
public class ParticipanteAdapter extends BaseAdapter {

    ArrayList<Participante> participantes;
    ArrayList<Objeto> objetos;
    Context context;
    MainEvento mEvento;

    public ParticipanteAdapter(MainEvento mEvento, ArrayList<Participante> participantes, ArrayList<Objeto> objetos) {
        this.context = mEvento.getApplicationContext();
        this.mEvento = mEvento;
        this.participantes=participantes;
        this.objetos=objetos;
    }

    public void setItem(ArrayList<Objeto> objetos){
        this.objetos=objetos;
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
        TextView gastoTV = (TextView)view.findViewById(R.id.gasto);
        TextView debeoledebenTV = (TextView)view.findViewById(R.id.debeoledeben);
        TextView debeoledebenvalue = (TextView)view.findViewById(R.id.debeoledebenvalue);
        Participante p = participantes.get(position);
        nombreTV.setText(String.valueOf(p.getNombre()));
        if(p.getIdFacebook().equals("personavirtual")){
            Picasso.with(context).load(R.drawable.participante_default).transform(new CircleTransform()).into(fotoIV);
        }else{
            Picasso.with(context).load("https://graph.facebook.com/"+p.getIdFacebook()+"/picture?type=large").transform(new CircleTransform()).into(fotoIV);
        }

        int gastos = 0;
        int total = 0;
        int debeoledeben = 0;
        for (Objeto o :objetos){
            if(o.isChecked()){
                total += o.getPrecio();
                if(o.getIdParticipante()==p.getIdParticipante()){
                    gastos += o.getPrecio();
                }
            }
        }
        String nombre = p.getNombre();
        if(nombre.indexOf(" ") == -1){
            nombreTV.setText(nombre);
        }else {
            int posicion = nombre.indexOf(" ");
            nombre = nombre.substring(0, posicion);
            nombreTV.setText(nombre);
        }

        int totaldividido = total/participantes.size();
        debeoledeben = gastos - totaldividido;
        gastoTV.setText(String.valueOf(gastos));
        if(gastos>totaldividido){
            debeoledebenTV.setText("Debe");
            debeoledebenvalue.setText(String.valueOf(debeoledeben));
            debeoledebenvalue.setTextColor(Color.RED);
        }
        if(gastos<totaldividido){
            debeoledebenTV.setText("Debe");
            debeoledebenvalue.setText(String.valueOf(debeoledeben));
            debeoledebenvalue.setTextColor(Color.GREEN);
        }



        return view;
    }
}