package app.rasendriya.cintamasjid.model;

import java.util.Date;

/**
 * Created by muhammadagungrizkyana on 2/16/15.
 */
public class ModelKajian {

    private int idKajian;
    private int fkMasjid;
    private String jenis;
    private String deskripsi;
    private Date tanggal;
    private int status;
    private String foto;


    private String judul;
    private String mulai;
    private String akhir;
    private String jam_mulai;
    private String jam_akhir;
    private String ulangi;

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getMulai() {
        return mulai;
    }

    public void setMulai(String mulai) {
        this.mulai = mulai;
    }

    public String getAkhir() {
        return akhir;
    }

    public void setAkhir(String akhir) {
        this.akhir = akhir;
    }

    public String getJam_mulai() {
        return jam_mulai;
    }

    public void setJam_mulai(String jam_mulai) {
        this.jam_mulai = jam_mulai;
    }

    public String getJam_akhir() {
        return jam_akhir;
    }

    public void setJam_akhir(String jam_akhir) {
        this.jam_akhir = jam_akhir;
    }

    public String getUlangi() {
        return ulangi;
    }

    public void setUlangi(String ulangi) {
        this.ulangi = ulangi;
    }

    private ModelMasjid modelMasjid;

    public ModelMasjid getModelMasjid() {
        return modelMasjid;
    }

    public void setModelMasjid(ModelMasjid modelMasjid) {
        this.modelMasjid = modelMasjid;
    }
    //    private String masjid;

    public ModelKajian(){}

    public ModelKajian(int idKajian, int fkMasjid, String jenis, String deskripsi, Date tanggal, int status) {
        this.idKajian = idKajian;
        this.fkMasjid = fkMasjid;
        this.jenis = jenis;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
        this.status = status;
    }

    public int getIdKajian() {
        return idKajian;
    }

    public void setIdKajian(int idKajian) {
        this.idKajian = idKajian;
    }

    public int getFkMasjid() {
        return fkMasjid;
    }

    public void setFkMasjid(int fkMasjid) {
        this.fkMasjid = fkMasjid;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
