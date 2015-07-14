package app.rasendriya.cintamasjid.service;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import app.rasendriya.cintamasjid.model.ModelMasjid;

/**
 * Created by muhammadagungrizkyana on 1/29/15.
 */
public class ServiceDistance {

    public static double countDistance(double lat1, double lng1, double lat2,
                                       double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        double km = dist / 0.62137;

        return km;
    }

    public static ArrayList<ModelMasjid> sortDistance (ArrayList<ModelMasjid> modelMasjids){
        System.out.println("besar model masjid sebelum : " + modelMasjids.size());

//        for(int x=0; x<modelMasjids.size(); x++){
////            double jarak = SphericalUtil.computeDistanceBetween(new LatLng(currentLat, currentLon), new LatLng(modelMasjids.get(x).getLatitude(), modelMasjids.get(x).getLongitude()));
////            System.out.println("double jarak : " + jarak);
////            if(jarak > 2000){
////                System.out.println("ini jarakknya : " + jarak + " index : " + x);
////                modelMasjids.remove(modelMasjids.get(x));
////            }
//            modelMasjids.get(x).setJarak(String.valueOf(jarak));
//////            System.out.println("jarak : " + x + " : " + (int)(jarak / 1000));
////            if((int)(jarak / 1000) == 0){
//////                System.out.println("masjid  : " + x + " : " + jarak / 1000);
////
////            }else{
//////                System.out.println("masjid terjauh lebih dari 2 KM : " + x + " : " + jarak / 1000);
////
////            }
//
//
//
//        }





        for(int i = 1; i < modelMasjids.size(); i++){
            

                if(modelMasjids.get(i).getJarak() != (null)) {

                    double tempJarak = Double.parseDouble(modelMasjids.get(i).getJarak());
                    ModelMasjid mTemp = modelMasjids.get(i);
                    int j;
                    for (j = i - 1; j >= 0 && tempJarak < Double.parseDouble(modelMasjids.get(j).getJarak()); j--) {
                        modelMasjids.set(j + 1, modelMasjids.get(j));

                    }
                    modelMasjids.set(j + 1, mTemp);
                }

        }

//
        System.out.println("besar model masjid : " + modelMasjids.size());

        return modelMasjids;
    }


}
