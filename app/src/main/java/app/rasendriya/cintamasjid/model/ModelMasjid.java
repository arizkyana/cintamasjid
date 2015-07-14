package app.rasendriya.cintamasjid.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by muhammadagungrizkyana on 1/28/15.
 */
public class ModelMasjid {

    private String idMasjid;
    private String nama;
    private String alamat;
    private String kota;
    private String provinsi;
    private double latitude;
    private double longitude;
    private String fotoCover;
    private String jenis;
    private String kapasitas;
    private String status;
    private String jarak;

    private Bitmap cover;

    private ArrayList<ModelMasjid> modelMasjids;

    public ModelMasjid(String idMasjid, String nama, String alamat, String kota, String provinsi, double latitude, double longitude, String fotoCover, String jenis, String kapasitas, String status, String jarak) {
        this.idMasjid = idMasjid;
        this.nama = nama;
        this.alamat = alamat;
        this.kota = kota;
        this.provinsi = provinsi;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fotoCover = fotoCover;
        this.jenis = jenis;
        this.kapasitas = kapasitas;
        this.status = status;
        this.jarak = jarak;
    }

    public ModelMasjid() {

    }



    public ArrayList<ModelMasjid> getModelMasjids() {
        return modelMasjids;
    }

    public void setModelMasjids(ArrayList<ModelMasjid> modelMasjids) {
        this.modelMasjids = modelMasjids;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public String getJarak() {
        return jarak;
    }

    public void setJarak(String jarak) {
        this.jarak = jarak;
    }

    public String getIdMasjid() {
        return idMasjid;
    }

    public void setIdMasjid(String idMasjid) {
        this.idMasjid = idMasjid;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFotoCover() {
        return fotoCover;
    }

    public void setFotoCover(String fotoCover) {
        this.fotoCover = fotoCover;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(String kapasitas) {
        this.kapasitas = kapasitas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<ModelMasjid> getListMasjid(){

        ArrayList<ModelMasjid> modelMasjids = new ArrayList<>();
        ModelMasjid m1 = new ModelMasjid();
        m1.setNama("Masjid Istiqomah");
        m1.setLatitude((float) -6.905283);
        m1.setLongitude((float) 107.621250);

        ModelMasjid m2 = new ModelMasjid();
        m2.setNama("Masjid Al-Muhajirin");
        m2.setLatitude((float) -6.911496);
        m2.setLongitude((float) 107.606113);

        ModelMasjid m3 = new ModelMasjid();
        m3.setNama("Masjid Al-Ukhuwah");
        m3.setLatitude((float) -6.910468);
        m3.setLongitude((float) 107.608747);

        ModelMasjid m4 = new ModelMasjid();
        m4.setNama("Masjid Istiqomah");
        m4.setLatitude((float) -6.905283);
        m4.setLongitude((float) 107.621250);

        ModelMasjid m5 = new ModelMasjid();
        m5.setNama("Masjid Al-Muhajirin");
        m5.setLatitude((float) -6.911496);
        m5.setLongitude((float) 107.606113);

        ModelMasjid m6 = new ModelMasjid();
        m6.setNama("Masjid Al-Ukhuwah");
        m6.setLatitude((float) -6.910468);
        m6.setLongitude((float) 107.608747);

        ModelMasjid m7 = new ModelMasjid();
        m7.setNama("Masjid Istiqomah");
        m7.setLatitude((float) -6.905283);
        m7.setLongitude((float) 107.621250);

        ModelMasjid m8 = new ModelMasjid();
        m8.setNama("Masjid Al-Muhajirin");
        m8.setLatitude((float) -6.911496);
        m8.setLongitude((float) 107.606113);

        ModelMasjid m9 = new ModelMasjid();
        m9.setNama("Masjid Al-Ukhuwah");
        m9.setLatitude((float) -6.910468);
        m9.setLongitude((float) 107.608747);

        modelMasjids.add(m1);
        modelMasjids.add(m2);
        modelMasjids.add(m3);

        modelMasjids.add(m4);
        modelMasjids.add(m5);
        modelMasjids.add(m6);

        modelMasjids.add(m7);
        modelMasjids.add(m8);
        modelMasjids.add(m9);


        return modelMasjids;
    }


}

