package ort.proyectofinal;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import ort.proyectofinal.Clases.CircleTransform;
import ort.proyectofinal.Clases.Participante;
import ort.proyectofinal.Clases.Usuario;

/**
 * Created by Schuster on 29/07/2016.
 */
public class FriendAdapter extends BaseAdapter {

    ArrayList<Usuario> friends;
    Context context;
    MainEvento mEvento;
    ImageButton button;

    public FriendAdapter(MainEvento mEvento, ArrayList<Usuario> friends) {
        this.context = mEvento.getApplicationContext();
        this.mEvento = mEvento;
        this.friends = friends;
    }

    @Override
    public int getCount() {
        if (friends != null && friends.size() > 0)
            return friends.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int i) {

        if (friends != null && friends.size() > 0)
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
            view = inflater.inflate(R.layout.list_item_friends, viewGroup, false);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        button = (ImageButton) view.findViewById(R.id.button);
        ImageView friendIV = (ImageView) view.findViewById(R.id.friend);
        Usuario f = friends.get(position);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean existe = false;
                Usuario f = friends.get(position);
                for(Participante p : mEvento.participantes){
                    if(p.getIdFacebook().equals(f.getIdFacebook())){
                        existe = true;
                        break;
                    }
                }
                if(existe==true){
                    Toast.makeText(mEvento, "Ese usuario ya fue agregado", Toast.LENGTH_SHORT).show();
                }else{
                    mEvento.AgregarParticipante(f);
                }
            }
        });

        name.setText(String.valueOf(f.getNombre()));
        button.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));;
        Picasso.with(context).load("https://graph.facebook.com/"+f.getIdFacebook()+"/picture?type=large").transform(new CircleTransform()).into(friendIV);

        return view;
    }


}
