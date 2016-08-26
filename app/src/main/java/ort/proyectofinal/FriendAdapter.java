package ort.proyectofinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Schuster on 29/07/2016.
 */
public class FriendAdapter extends BaseAdapter {

    ArrayList<Usuario> friends;
    Context context;

    public FriendAdapter(Context context, ArrayList<Usuario> friends) {
        this.context = context;
        this.friends=friends;
    }

    @Override
    public int getCount() {
        if (friends != null && friends.size() >0)
            return friends.size();
        else
            return  0;
    }

    @Override
    public Object getItem(int i) {

        if (friends != null && friends.size() >0)
            return friends.get(i);
        else
            return null;
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
            view = inflater.inflate(R.layout.list_item_participantes, viewGroup, false);
        }

        TextView name = (TextView)view.findViewById(R.id.nombre);
        ImageButton button = (ImageButton)view.findViewById(R.id.boton_friends);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usuario f = friends.get(position);
                MainEvento mEvento= new MainEvento();
                mEvento.AgregarParticipante(f);
            }
        });
        Usuario f = friends.get(position);
        button.setImageResource(R.drawable.ic_person_add_black_24dp);
        name.setText(String.valueOf(f.getNombre()));
        return view;
    }
}
