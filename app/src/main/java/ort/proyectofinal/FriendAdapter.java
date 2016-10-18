package ort.proyectofinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import ort.proyectofinal.Clases.CircleTransform;
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
        Usuario f = friends.get(position);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usuario f = friends.get(position);
                mEvento.AgregarParticipante(f);
            }
        });

        name.setText(String.valueOf(f.getNombre()));
        Picasso.with(context).load("https://graph.facebook.com/"+f.getIdFacebook()+"/picture?type=large").transform(new CircleTransform()).into(button);

        return view;
    }


}
