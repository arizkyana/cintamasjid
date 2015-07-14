package app.rasendriya.cintamasjid.fragment;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.adapter.ScheduleAdapter;
import app.rasendriya.cintamasjid.prays.Location;
import app.rasendriya.cintamasjid.prays.Method;
import app.rasendriya.cintamasjid.prays.PrayTimes;
import app.rasendriya.cintamasjid.prays.PrayTimes.Time;
import app.rasendriya.cintamasjid.prays.Util;
import app.rasendriya.cintamasjid.service.HijriCalendar;
import app.rasendriya.cintamasjid.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.drive.internal.ad;

public class ScheduleFragment extends Fragment implements
        OnClickListener {
    private static final String KEY_MODE = "Fragment:Mode";

    private ListView listView;
    static TextView tvLocation;
    static TextView tvDate;
    static TextView tvSholat;
    static TextView tvNext;

    static List<Map<String, String>> listSchedule = new ArrayList<Map<String, String>>();

    private int mode;

    public static ScheduleAdapter adapter;

    static ScheduleFragment fragment;
    static SharedPreferences sharedPreferences;

    public static ScheduleFragment newInstance(int mode) {
        if (fragment == null)
            fragment = new ScheduleFragment();
        fragment.mode = mode;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null)
                && savedInstanceState.containsKey(KEY_MODE)) {
            mode = savedInstanceState.getInt(KEY_MODE);
        }

        setPrayTime(getActivity(), listSchedule);
        adapter = new ScheduleAdapter(getActivity(), listSchedule);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, null);
        listView = (ListView) view.findViewById(R.id.list);

        View viewHeader = inflater.inflate(R.layout.header_jadwal_sholat, null);
        tvLocation = (TextView) viewHeader.findViewById(R.id.tvLocation);
        tvDate = (TextView) viewHeader.findViewById(R.id.tvDate);
        tvSholat = (TextView) viewHeader.findViewById(R.id.tvSholat);
        tvNext = (TextView) viewHeader.findViewById(R.id.tvNext);

        listView.addHeaderView(viewHeader);
        listView.setAdapter(adapter);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_MODE, mode);
    }

    public void initData(Context ctx) {
        setPrayTime(ctx, listSchedule);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public static void initNextTime() {
        tvLocation.setText(sharedPreferences.getString(Constant.Pref.LOC_NAME, ""));
        tvDate.setText(HijriCalendar.getSimpleDate(Calendar.getInstance()));

        try {
            for (int position = 0; position < listSchedule.size(); position++) {
                Map<String, String> map = listSchedule.get(position);
                int posLast;
                if (position == 0)
                    posLast = listSchedule.size() - 1;
                else
                    posLast = position - 1;

                Map<String, String> mapLast = listSchedule.get(posLast);
                SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
                Date dateCurr = sdf.parse(map.get("time").toUpperCase());
                Date dateLast = sdf.parse(mapLast.get("time").toUpperCase());
                Date tmpDateNow = new Date();
                Date dateNow = sdf.parse(sdf.format(tmpDateNow));
                // cek dengan nilai posisi lama
                // jika > last && < curr

                if ((dateNow.after(dateLast) && dateNow.before(dateCurr))
                        || (dateNow.before(dateCurr) && position == 0)) {
                    tvNext.setText("NEXT " + map.get("title") + " " + map.get("time"));
                    break;
                } else {
                    tvSholat.setText(map.get("title"));
                }
            }
        } catch (ParseException e) {

        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void setPrayTime(Context ctx, List<Map<String, String>> list) {
        setPrayTime(ctx, list, false);
    }

    public static void setPrayTime(Context ctx, List<Map<String, String>> list, boolean isTomorrow) {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        // TODO kasih lokasi default
        double latitude = Double.parseDouble(pref.getString(
                Constant.Pref.LOC_LAT, "0"));
        double longitude = Double.parseDouble(pref.getString(
                Constant.Pref.LOC_LNG, "0"));
        double altitude = Double.parseDouble(pref.getString(
                Constant.Pref.LOC_ALT, "0"));

        PrayTimes pt = new PrayTimes(Method.ISNA);
        pt.adjustAngle(Time.FAJR, 20);
        pt.adjustMinutes(Time.DHUHR, 2);
        pt.adjustMinutes(Time.MAGHRIB, 1);
        pt.adjustAngle(Time.ISHA, 18);

        pt.tuneOffset(Time.IMSAK, 2);
        pt.tuneOffset(Time.FAJR, 2);
        pt.tuneOffset(Time.SUNRISE, -2);
        pt.tuneOffset(Time.ASR, 2);
        pt.tuneOffset(Time.MAGHRIB, 2);
        pt.tuneOffset(Time.ISHA, 2);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        if (isTomorrow)
            cal.add(Calendar.DAY_OF_MONTH, 1);
        Map<Time, Double> times = pt.getTimes(cal, new Location(latitude,
                longitude, altitude));

        list.clear();
        Map<String, String> map = new HashMap<String, String>();
        map.put("title", "IMSAK");
        map.put("time", Util.toTime24(times.get(Time.IMSAK)));
        map.put("notif", Util.toTime24Minus15m(times.get(Time.IMSAK), 15));
        list.add(map);

        map = new HashMap<String, String>();
        map.put("title", "SUBUH");
        map.put("time", Util.toTime24(times.get(Time.FAJR)));
        map.put("notif", Util.toTime24Minus15m(times.get(Time.FAJR), 15));
        list.add(map);

//        map = new HashMap<String, String>();
//        map.put("title", "SYURUK");
//        map.put("time", Util.toTime24(times.get(Time.SUNRISE)));
//        map.put("notif", Util.toTime24Minus15m(times.get(Time.SUNRISE), 15));
//        list.add(map);

        map = new HashMap<String, String>();
        map.put("title", "DZUHUR");
        map.put("time", Util.toTime24(times.get(Time.DHUHR)));
        map.put("notif", Util.toTime24Minus15m(times.get(Time.DHUHR), 15));
        list.add(map);

        map = new HashMap<String, String>();
        map.put("title", "ASHAR");
        map.put("time", Util.toTime24(times.get(Time.ASR)));
        map.put("notif", Util.toTime24Minus15m(times.get(Time.ASR), 15));
        list.add(map);

        map = new HashMap<String, String>();
        map.put("title", "MAGHRIB");
        map.put("time", Util.toTime24(times.get(Time.MAGHRIB)));
        map.put("notif", Util.toTime24Minus15m(times.get(Time.MAGHRIB), 15));
        list.add(map);

        map = new HashMap<String, String>();
        map.put("title", "ISYA");
        map.put("time", Util.toTime24(times.get(Time.ISHA)));
        map.put("notif", Util.toTime24Minus15m(times.get(Time.ISHA), 15));
        list.add(map);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            if (sharedPreferences != null) {
                if (sharedPreferences.getString(Constant.Pref.LOC_NAME, "").equals(""))
                    initNextTime();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }

    }

}