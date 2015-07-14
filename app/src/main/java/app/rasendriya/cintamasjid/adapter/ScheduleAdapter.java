package app.rasendriya.cintamasjid.adapter;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.alarm.Alarm;
import app.rasendriya.cintamasjid.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.ParseException;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ScheduleAdapter extends BaseAdapter {
    boolean[] listStateSpeaker = new boolean[7];
    List<String> listKeySpeaker = new ArrayList<String>();
    static List<Map<String, String>> listSchedule = new ArrayList<Map<String, String>>();

    SharedPreferences pref;
    private Context ctx;
    Alarm alarm;

    public ScheduleAdapter(Context context, List<? extends Map<String, ?>> data) {
//        super(context, data, R.layout.row_schedule, new String[] { "title",
//                "time" }, new int[] { R.id.tv_title, R.id.tv_time });
        ctx = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        alarm = new Alarm();
        initListStateSpeaker();
        listSchedule = (List<Map<String, String>>) data;
//		initListStateNotif();
    }

    private void initListStateSpeaker() {
        String keySpeaker = "";
        for (int i = 0; i < 7; i++) {
            switch (i) {
                case 0:
                    keySpeaker = Constant.Pref.ALARM_IMSAK;
                    break;
                case 1:
                    keySpeaker = Constant.Pref.ALARM_SUBUH;
                    break;
                case 2:
                    keySpeaker = Constant.Pref.ALARM_SYURUQ;
                    break;
                case 3:
                    keySpeaker = Constant.Pref.ALARM_DZUHUR;
                    break;
                case 4:
                    keySpeaker = Constant.Pref.ALARM_ASAR;
                    break;
                case 5:
                    keySpeaker = Constant.Pref.ALARM_MAGHRIB;
                    break;
                case 6:
                    keySpeaker = Constant.Pref.ALARM_ISYA;
                    break;
                default:

                    break;

            }
            listStateSpeaker[i] = pref.getBoolean(keySpeaker, false);
            listKeySpeaker.add(keySpeaker);
        }
    }

    @Override
    public int getCount() {
        return listSchedule.size();
    }

    @Override
    public Object getItem(int position) {
        return listSchedule.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = ((Activity) ctx).getLayoutInflater().inflate(
                    R.layout.row_schedule, null);
        }
        TextView tvTitle = (TextView) vi.findViewById(R.id.tv_title);
        TextView tvTime = (TextView) vi.findViewById(R.id.tv_time);
        LinearLayout linRow = (LinearLayout) vi.findViewById(R.id.linRow);
        final ToggleButton btnSpeaker = (ToggleButton) vi
                .findViewById(R.id.btn_speaker);

        Map<String, String> map = (Map<String, String>) listSchedule.get(position);

        btnSpeaker.setChecked(listStateSpeaker[position]);

        vi.findViewById(R.id.layout_speaker).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        listStateSpeaker[position] = !listStateSpeaker[position];
                        btnSpeaker.setChecked(listStateSpeaker[position]);
                        updateSpeakerData(listKeySpeaker.get(position),
                                listStateSpeaker[position]);
                    }
                });

        tvTime.setText(map.get("time"));
        tvTitle.setText(map.get("title"));

        try {
            int posLast;
            if (position == 0)
                posLast = getCount() - 1;
            else
                posLast = position - 1;

            Map<String, String> mapLast = (Map<String, String>) getItem(posLast);
            SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
            Date dateCurr = sdf.parse(map.get("time").toUpperCase());
            Date dateLast = sdf.parse(mapLast.get("time").toUpperCase());
            Date tmpDateNow = new Date();
            Date dateNow = sdf.parse(sdf.format(tmpDateNow));
            // cek dengan nilai posisi lama
            // jika > last && < curr

            if ((dateNow.after(dateLast) && dateNow.before(dateCurr))
                    || (dateNow.before(dateCurr) && position == 0)) {
                tvTime.setTextColor(Color.parseColor("#FFFFFF"));
                linRow.setBackgroundResource(R.color.primary_pressed);
            } else {
                tvTime.setTextColor(Color.parseColor("#f73f08"));
                linRow.setBackgroundResource(R.color.backgroundList);
            }
        } catch (ParseException e) {

        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return vi;
    }

    private void updateSpeakerData(String key, boolean isChecked) {
        Editor edit = pref.edit();
        edit.putBoolean(key, isChecked);
        edit.commit();
        alarm.SetAlarm(ctx);
    }

}