package app.rasendriya.cintamasjid.model;

/**
 * Created by muhammadagungrizkyana on 1/31/15.
 */
public class ModelSholat {

    String tanggal, bulan, imsyak, fajr, syuruq, dzuhr, ashr, maghrib, isha, tahun;

    public ModelSholat(String ashr, String tanggal, String bulan, String imsyak, String fajr, String syuruq, String dzuhr, String maghrib, String isha) {
        this.ashr = ashr;
        this.tanggal = tanggal;
        this.bulan = bulan;
        this.imsyak = imsyak;
        this.fajr = fajr;
        this.syuruq = syuruq;
        this.dzuhr = dzuhr;
        this.maghrib = maghrib;
        this.isha = isha;
    }

    public ModelSholat() {
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getBulan() {
        return bulan;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public String getImsyak() {
        return imsyak;
    }

    public void setImsyak(String imsyak) {
        this.imsyak = imsyak;
    }

    public String getFajr() {
        return fajr;
    }

    public void setFajr(String fajr) {
        this.fajr = fajr;
    }

    public String getSyuruq() {
        return syuruq;
    }

    public void setSyuruq(String syuruq) {
        this.syuruq = syuruq;
    }

    public String getDzuhr() {
        return dzuhr;
    }

    public void setDzuhr(String dzuhr) {
        this.dzuhr = dzuhr;
    }

    public String getAshr() {
        return ashr;
    }

    public void setAshr(String ashr) {
        this.ashr = ashr;
    }

    public String getMaghrib() {
        return maghrib;
    }

    public void setMaghrib(String maghrib) {
        this.maghrib = maghrib;
    }

    public String getIsha() {
        return isha;
    }

    public void setIsha(String isha) {
        this.isha = isha;
    }
}
