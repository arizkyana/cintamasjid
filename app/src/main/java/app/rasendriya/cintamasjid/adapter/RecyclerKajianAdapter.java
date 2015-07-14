package app.rasendriya.cintamasjid.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

import app.rasendriya.cintamasjid.R;

//import app.rasendriya.cintamasjid.activity.DetailMasjidActivity;
import app.rasendriya.cintamasjid.activity.DetailMasjidActivity;
import app.rasendriya.cintamasjid.model.ModelKajian;
import app.rasendriya.cintamasjid.model.ModelMasjid;

/**
 * Created by muhammadagungrizkyana on 2/12/15.
 */

public class RecyclerKajianAdapter extends RecyclerView.Adapter<RecyclerKajianAdapter.KajianViewHolder> {

    private List<ModelKajian> modelKajians;
    private Context context;
    private Activity activity;

    public RecyclerKajianAdapter(Activity activity,Context context, List<ModelKajian> modelKajians) {
        this.modelKajians = modelKajians;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return modelKajians.size();
    }

    @Override
    public void onBindViewHolder(final KajianViewHolder kajianViewHolder, final int i) {
//        ContactInfo ci = modelKajians.get(i);
//        kajianViewHolder.vName.setText(ci.name);
//        kajianViewHolder.vSurname.setText(ci.surname);
//        kajianViewHolder.vEmail.setText(ci.email);
//        kajianViewHolder.vTitle.setText(ci.name + " " + ci.surname);

        ModelKajian modelKajian = modelKajians.get(i);
        kajianViewHolder.judul.setText(modelKajian.getJudul());

        kajianViewHolder.lokasi.setText(modelKajian.getModelMasjid().getAlamat());
        kajianViewHolder.pemateri.setText(modelKajian.getDeskripsi());
        kajianViewHolder.pukul.setText(modelKajian.getJam_mulai() + " - " + modelKajian.getJam_akhir());
        kajianViewHolder.lokasiMasjid.setText(modelKajian.getModelMasjid().getNama());

//
        kajianViewHolder.kajianContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // perform ur click here
//                Toast.makeText(v.getContext(), "position : " + i, Toast.LENGTH_LONG).show();
//                Intent inte = new Intent(v.getContext(), DetailMasjidActivity.class);
                ModelMasjid m = modelKajians.get(i).getModelMasjid();
////                Toast t = Toast.makeText(getActivity(), "latitude : " + m.getLatitude(), Toast.LENGTH_LONG);
////                t.show();
//                inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                inte.putExtra("nama", m.getNama());
//                inte.putExtra("tujuan", m.getLatitude() + "," + m.getLongitude());
//                inte.putExtra("tujuanLatitude", m.getLatitude());
//                inte.putExtra("tujuanLongitude", m.getLongitude());
//                inte.putExtra("alamat", m.getAlamat());
//
////                kajianViewHolder.mainView.start
//
//                context.startActivity(inte);
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+(m.getLatitude() + "," + m.getLongitude())+"&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                activity.startActivity(mapIntent);
            }
        });
    }

    @Override
    public KajianViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.activity_kajian, viewGroup, false);
        ModelKajian modelKajian = modelKajians.get(i);
//        Toast.makeText(itemView.getContext(), modelKajian.getDeskripsi(), Toast.LENGTH_LONG).show();
        return new KajianViewHolder(itemView);
    }

    public static class KajianViewHolder extends RecyclerView.ViewHolder {
        protected TextView judul;
        protected TextView tanggal;
        protected TextView pemateri;
        protected TextView lokasi;
        protected CardView kajianContent;
        protected TextView pukul;
        protected TextView lokasiMasjid;

        protected View mainView;

        public KajianViewHolder(View v) {
            super(v);
            mainView = v;
//            vName = (TextView) v.findViewById(R.id.txtName);
//            vSurname = (TextView) v.findViewById(R.id.txtSurname);
//            vEmail = (TextView) v.findViewById(R.id.txtEmail);
//            vTitle = (TextView) v.findViewById(R.id.title);
            judul = (TextView) v.findViewById(R.id.judulKajian);
            tanggal = (TextView) v.findViewById(R.id.tanggalKajian);
            lokasi = (TextView) v.findViewById(R.id.lokasi);
            kajianContent = (CardView) v.findViewById(R.id.card_view);

            pemateri = (TextView) v.findViewById(R.id.pemateri);
            pukul = (TextView) v.findViewById(R.id.pukul);

            lokasiMasjid = (TextView) v.findViewById(R.id.lokasiMasjid);
        }


    }
}
