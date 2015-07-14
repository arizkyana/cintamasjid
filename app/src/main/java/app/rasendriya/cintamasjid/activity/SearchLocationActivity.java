package app.rasendriya.cintamasjid.activity;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.utils.Constant;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchLocationActivity extends ListActivity implements OnClickListener {
	private Context ctx = this;
	private EditText inpAddress;
	private List<Address> listAddresses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_location);

		inpAddress = (EditText) findViewById(R.id.inp_address);

		findViewById(R.id.btn_search).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			new SearchAddress().execute();
			break;

		default:
			break;
		}

	}

	private class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater = null;

		public MyAdapter() {
			super();
			inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return listAddresses.size();
		}

		@Override
		public Object getItem(int position) {
			return listAddresses.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			final Address item = (Address) getItem(position);

			TextView tv;
			if (convertView == null) {
				vi = inflater
						.inflate(R.layout.row_location, null);
			}
			tv = (TextView) vi.findViewById(android.R.id.text1);

			int maxAddressLineIndex = item.getMaxAddressLineIndex();
			String addressLine = "";
			for (int j = 0; j <= maxAddressLineIndex; j++) {
				addressLine += ", " + item.getAddressLine(j) ;
			}
			
			tv.setText(addressLine.replaceFirst(", ", ""));
			return vi;
		}
	}

	private class SearchAddress extends AsyncTask<Void, Void, String> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ctx, null, "Searching");
		}

		@Override
		protected String doInBackground(Void... params) {
			String error = null;
			try {
				Geocoder geocoder = new Geocoder(ctx);
				listAddresses = geocoder.getFromLocationName(inpAddress
						.getText().toString(), 10);
			} catch (IOException e) {
				error = getString(R.string.error_internet);
			} catch (IllegalArgumentException e) {
				error = "Please input address in the box";
			}
			return error;
		}

		@Override
		protected void onPostExecute(String error) {
			dialog.cancel();
			if (error == null) {
				// masukkan ke listview
				setListAdapter(new MyAdapter());
			} else {
				Toast.makeText(ctx, error, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Editor prefsPrivateEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		prefsPrivateEditor.putString(Constant.Pref.LOC_LAT, listAddresses.get(position).getLatitude()+"");
		prefsPrivateEditor.putString(Constant.Pref.LOC_LNG, listAddresses.get(position).getLongitude()+"");
		TextView tv = (TextView) v.findViewById(android.R.id.text1);
		prefsPrivateEditor.putString(Constant.Pref.LOC_NAME, tv.getText().toString());
		prefsPrivateEditor.commit();

		setResult(RESULT_OK);
		finish();
	}
	
	public static void startThisActivity(Context ctx){
		Intent intent = new Intent(ctx, SearchLocationActivity.class);
		((Activity) ctx).startActivityForResult(intent, 0);
	}
}
