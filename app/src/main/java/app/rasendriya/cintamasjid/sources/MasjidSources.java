package app.rasendriya.cintamasjid.sources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import app.rasendriya.cintamasjid.helper.MasjidHelper;
import app.rasendriya.cintamasjid.model.ModelMasjid;


/**
 * Created by muhammadagungrizkyana on 3/5/15.
 */
public class MasjidSources {

    private SQLiteDatabase database;
    private MasjidHelper masjidHelper;
    private String[] allColumns = {
            masjidHelper.COLUMN_ID,
            masjidHelper.COLUMN_NAMA,
            masjidHelper.COLUMN_ALAMAT,
            masjidHelper.COLUMN_KOTA,
            masjidHelper.COLUMN_PROVINSI,
            masjidHelper.COLUMN_FOTO,
            masjidHelper.COLUMN_JENIS,
            masjidHelper.COLUMN_LAT,
            masjidHelper.COLUMN_LON,
            masjidHelper.COLUMN_KAPASITAS,
            masjidHelper.COLUMN_STATUS
    };

    public MasjidSources(Context context){
        masjidHelper = new MasjidHelper(context);
    }

    public void open() throws SQLException {
        database = masjidHelper.getWritableDatabase();
    }

    public void close(){
        masjidHelper.close();
    }

    //create
    public ModelMasjid createMasjid(ModelMasjid modelMasjid){
        System.out.println("nama masjid : " + modelMasjid.getNama());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MasjidHelper.COLUMN_NAMA, modelMasjid.getNama());
        contentValues.put(MasjidHelper.COLUMN_ALAMAT, modelMasjid.getAlamat());
        contentValues.put(MasjidHelper.COLUMN_KOTA, modelMasjid.getKota());
        contentValues.put(MasjidHelper.COLUMN_PROVINSI, modelMasjid.getProvinsi());
        contentValues.put(MasjidHelper.COLUMN_FOTO, modelMasjid.getFotoCover());
        contentValues.put(MasjidHelper.COLUMN_JENIS, modelMasjid.getJenis());
        contentValues.put(MasjidHelper.COLUMN_LAT,modelMasjid.getLatitude());
        contentValues.put(MasjidHelper.COLUMN_LON, modelMasjid.getLongitude());
        contentValues.put(MasjidHelper.COLUMN_KAPASITAS, modelMasjid.getKapasitas());
        contentValues.put(MasjidHelper.COLUMN_STATUS, modelMasjid.getStatus());

        System.out.println("content values : " + contentValues.toString());
        long insertId = database.insert(MasjidHelper.TABLE_NAME, null, contentValues);
//
        Cursor cursor = database.query(MasjidHelper.TABLE_NAME,
                allColumns, MasjidHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        ModelMasjid newModelMasjid = cursorToModelMasjid(cursor);
        cursor.close();
        return null;
    }

    public void deleteMasjid(ModelMasjid modelMasjid) {
        String id = modelMasjid.getIdMasjid();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MasjidHelper.TABLE_NAME, MasjidHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<ModelMasjid> getAllMasjid() {
        List<ModelMasjid> modelMasjids = new ArrayList<ModelMasjid>();

        Cursor cursor = database.query(MasjidHelper.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ModelMasjid comment = cursorToModelMasjid(cursor);
            modelMasjids.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return modelMasjids;
    }

    private ModelMasjid cursorToModelMasjid(Cursor cursor){
        ModelMasjid modelMasjid = new ModelMasjid();
        System.out.println("cursor : " + cursor.toString());
        modelMasjid.setIdMasjid(cursor.getString(0));
        modelMasjid.setNama(cursor.getString(1));
        modelMasjid.setAlamat(cursor.getString(2));
        modelMasjid.setKota(cursor.getString(3));
        modelMasjid.setProvinsi(cursor.getString(4));
        modelMasjid.setFotoCover(cursor.getString(5));
        modelMasjid.setJenis(cursor.getString(6));
        modelMasjid.setLatitude(cursor.getDouble(7));
        modelMasjid.setLongitude(cursor.getDouble(8));
        modelMasjid.setKapasitas(cursor.getString(9));
        modelMasjid.setStatus(cursor.getString(10));
        return modelMasjid;
    }


}

