package app.rasendriya.cintamasjid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.model.ModelKajian;
import app.rasendriya.cintamasjid.model.ModelMasjid;

/**
 * Created by muhammadagungrizkyana on 1/16/15.
 */
public class ListKajianAdapter extends ArrayAdapter<ArrayList> {

    private Context context ;
    private ArrayList values;

    ModelMasjid m;

    public ListKajianAdapter(Context context, ArrayList values) {
        super(context, R.layout.adapter_list, values);
        this.context = context;
        this.values = values;

        System.out.println("MASUK LIST KAJIAN REKOMENDASI ADAPTER");
    }

    public View getView(int position, View containView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        containView = inflater.inflate(R.layout.adapter_list_kajian, parent, false);

        TextView judulKajian = (TextView) containView.findViewById(R.id.judulKajian);
        TextView alamat = (TextView) containView.findViewById(R.id.lokasi);
        TextView jamKajian = (TextView) containView.findViewById(R.id.jamKajian);
        ModelKajian modelKajian = (ModelKajian) values.get(position);

        judulKajian.setText(modelKajian.getJudul());
        alamat.setText(modelKajian.getModelMasjid().getAlamat());
        jamKajian.setText(modelKajian.getJam_mulai());

        System.out.println("judul kajian : " + modelKajian.getJudul());
//
//        textView.setText(modelKajian.getJenis());

        return containView;
    }



}
