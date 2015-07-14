package app.rasendriya.cintamasjid.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by muhammadagungrizkyana on 3/5/15.
 */
public class MasjidHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "masjid";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAMA = "nama";
    public static final String COLUMN_ALAMAT = "alamat";
    public static final String COLUMN_KOTA = "kota";
    public static final String COLUMN_PROVINSI = "provinsi";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LON = "lon";
    public static final String COLUMN_FOTO = "foto";
    public static final String COLUMN_JENIS = "jenis";
    public static final String COLUMN_KAPASITAS = "kapasitas";
    public static final String COLUMN_STATUS = "status";

    private static final String DATABASE_NAME = "cintamasjid.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NAMA
            + " varchar(30) not null, "+ COLUMN_ALAMAT +" text not null, " + COLUMN_KOTA + " varchar(30) not null, " + COLUMN_PROVINSI
            + " varchar(30) not null, "+ COLUMN_LAT +" double not null, "+ COLUMN_LON
            + " double not null, "+ COLUMN_FOTO +" text null, "+ COLUMN_JENIS
            + " varchar(30) null, "+ COLUMN_KAPASITAS +" varchar(30) null, "+ COLUMN_STATUS
            + " int(1) not null )";

    public MasjidHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MasjidHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
