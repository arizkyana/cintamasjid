package app.rasendriya.cintamasjid.helper;

import org.json.JSONArray;

import java.util.ArrayList;

import app.rasendriya.cintamasjid.model.ModelMasjid;

/**
 * Created by muhammadagungrizkyana on 3/25/15.
 */
public class SingletonHelper {

    private ArrayList<ModelMasjid> modelMasjids;

    private double lat, lon;

    private static SingletonHelper singleton = new SingletonHelper( );

    private JSONArray jsonArray;

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private SingletonHelper(){ }

    /* Static 'instance' method */
    public static SingletonHelper getInstance( ) {
        return singleton;
    }
    /* Other methods protected by singleton-ness */
    protected static void demoMethod( ) {
        System.out.println("demoMethod for singleton");
    }

    public ArrayList<ModelMasjid> getModelMasjids() {
        return modelMasjids;
    }

    public void setModelMasjids(ArrayList<ModelMasjid> modelMasjids) {
        this.modelMasjids = modelMasjids;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public static SingletonHelper getSingleton() {
        return singleton;
    }

    public static void setSingleton(SingletonHelper singleton) {
        SingletonHelper.singleton = singleton;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }
}
