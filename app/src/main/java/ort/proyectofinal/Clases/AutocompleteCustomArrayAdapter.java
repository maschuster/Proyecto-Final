package ort.proyectofinal.Clases;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ort.proyectofinal.AgregarEvento;
import ort.proyectofinal.MainEvento;
import ort.proyectofinal.R;

/**
 * Created by 41400475 on 7/10/2016.
 */
public class AutocompleteCustomArrayAdapter extends ArrayAdapter<Address> {

    final String TAG = "AutocompleteCustomArrayAdapter.java";

    Context mContext;
    int layoutResourceId;
    ArrayList<Address> data= new ArrayList<>();

    public AutocompleteCustomArrayAdapter(Context mContext, int layoutResourceId, ArrayList<Address> data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = ((AgregarEvento) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            // object item based on the position
            Address objectItem = data.get(position);

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
            textViewItem.setText(objectItem.getAddressLine(0) + ", " + objectItem.getAdminArea());

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }
}
