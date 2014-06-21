package g0v.ly.android.legislator;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import g0v.ly.android.R;
import g0v.ly.android.rest.RESTMethods;
import g0v.ly.android.utility.FontManager;
import g0v.ly.android.utility.androidcharts.SpiderWebChart;
import g0v.ly.android.utility.androidcharts.TitleValueEntity;

public class Profile extends Fragment implements RESTMethods.RestApiCallback {
    private static final Logger logger = LoggerFactory.getLogger(Profile.class);
    private TextView tvResponse;
    private TextView tvProfileAd;
    private TextView tvProfileGender;
    private TextView tvProfileParty;
    private TextView tvProfileCounty;
    private TextView tvProfileEducation;
    private TextView tvProfileExperience;

    private ImageView imgProfile;
    private Spinner legislatorNameSpinner;

    private RESTMethods restMethods;

    private long totalSpendTime = 0;
    private String[] legislatorNameArray;
    private String[] legislatorProfileArray;
    private boolean hasNextPage = true;

    private SpiderWebChart spiderWebChart;

    private Resources resources;

	// Key => legislator's name, Value => legislator's profile
	private Map<String, String[]> legislatorListWithProfile = new HashMap<String, String[]>();

	public enum TvUpdateType {
		APPEND,
		OVERWRITE
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        resources = getResources();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		assert view != null;

        setupUiComponents(view);

		/* TODO ad selectable */
		restMethods = new RESTMethods();
		String getUrl = "https://twly.herokuapp.com/api/legislator_terms/?page=1&ad=8";
		restMethods.restGet(getUrl, Profile.this);
		setupOnclickListeners();

		return view;
	}

	@Override
	public void getDone(final String response, final long spendTime, int page) {

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
			try {
				JSONObject apiResponse = new JSONObject(response);
				if (apiResponse.getString("next").equals("null")) {
					hasNextPage = false;
				}

				JSONArray results = apiResponse.getJSONArray("results");
				legislatorNameArray = new String[results.length()];
                String[] legislatorProfileInfoApiKey = resources.getStringArray(R.array.legislator_profile_info_api_key);

				for (int i = 0 ; i < results.length() ; i++) {
					// get legislator's name
					JSONObject legislator = results.getJSONObject(i);
					legislatorNameArray[i] = legislator.getString("name");
					legislatorProfileArray = new String[7];

					// get legislator's profile
					for (int j = 0 ; j < legislatorProfileArray.length ; j++) {
						switch (j) {
							case 0:
								legislatorProfileArray[j] = legislator.getString(legislatorProfileInfoApiKey[0]);
								break;
							case 1:
								legislatorProfileArray[j] = legislator.getString(legislatorProfileInfoApiKey[1]);
								break;
							case 2:
								legislatorProfileArray[j] = legislator.getString(legislatorProfileInfoApiKey[2]);
								break;
							case 3:
								legislatorProfileArray[j] = legislator.getString(legislatorProfileInfoApiKey[3]);
								break;
							case 4:
								legislatorProfileArray[j] = legislator.getString(legislatorProfileInfoApiKey[4]);
								break;
							case 5:
								legislatorProfileArray[j] = legislator.getString(legislatorProfileInfoApiKey[5]);
								break;
							case 6:
								legislatorProfileArray[j] = legislator.getString(legislatorProfileInfoApiKey[6]);
								break;
						}
					}
					legislatorListWithProfile.put(legislatorNameArray[i], legislatorProfileArray);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			}
		});

		totalSpendTime += spendTime;
		int legislatorCount = legislatorListWithProfile.keySet().size();
		logger.debug("[Profile] getDone legislatorCount= " + legislatorCount);
		updateTextView(tvResponse, "Legislator count = " + legislatorCount, TvUpdateType.OVERWRITE);
		updateTextView(tvResponse, "Spend " + totalSpendTime/1000 + "." + totalSpendTime%1000 + "s", TvUpdateType.APPEND);

		Object[] NameObjArray = legislatorListWithProfile.keySet().toArray();
		legislatorNameArray = new String[legislatorListWithProfile.size()]; // XXX, new 12 times
		int nameArraySize = NameObjArray.length;
		for (int i = 0 ; i < nameArraySize ; i++) {
			legislatorNameArray[i] = NameObjArray[i].toString();
		}
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, legislatorNameArray);
		legislatorNameSpinner.setAdapter(arrayAdapter);

		// get rest profiles
		if (hasNextPage) {
			restMethods.restGet("https://twly.herokuapp.com/api/legislator_terms/?page=" + (page+1) + "&ad=8", Profile.this);
		}
		else {
			logger.debug("[Profile] getDone hasNextPage = false");
		}
	}

	private void setupOnclickListeners() {

		legislatorNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				Toast.makeText(getActivity(), "你選的是 " + legislatorNameArray[position], Toast.LENGTH_SHORT).show();

				if (legislatorListWithProfile.containsKey(legislatorNameArray[position])) {
                    updateTextView(tvProfileAd, legislatorListWithProfile.get(legislatorNameArray[position])[0], TvUpdateType.OVERWRITE);
                    updateTextView(tvProfileGender, legislatorListWithProfile.get(legislatorNameArray[position])[1], TvUpdateType.OVERWRITE);
                    updateTextView(tvProfileParty, legislatorListWithProfile.get(legislatorNameArray[position])[2], TvUpdateType.OVERWRITE);
                    updateTextView(tvProfileCounty, legislatorListWithProfile.get(legislatorNameArray[position])[3], TvUpdateType.OVERWRITE);
                    updateTextView(tvProfileEducation, legislatorListWithProfile.get(legislatorNameArray[position])[4], TvUpdateType.OVERWRITE);
                    updateTextView(tvProfileExperience, legislatorListWithProfile.get(legislatorNameArray[position])[5], TvUpdateType.OVERWRITE);
                    /*
					updateTextView(tvProfile, legislatorNameArray[position] + "\n"
							+ legislatorProfileInfo[0] + "：" + legislatorListWithProfile.get(legislatorNameArray[position])[0] + "\n"
                            + legislatorProfileInfo[1] + "：" + legislatorListWithProfile.get(legislatorNameArray[position])[1] + "\n"
                            + legislatorProfileInfo[2] + "：" + legislatorListWithProfile.get(legislatorNameArray[position])[2] + "\n"
                            + legislatorProfileInfo[3] + "：" + legislatorListWithProfile.get(legislatorNameArray[position])[3] + "\n"
                            + legislatorProfileInfo[4] + "：" + legislatorListWithProfile.get(legislatorNameArray[position])[4] + "\n"
                            + legislatorProfileInfo[5] + "：" + legislatorListWithProfile.get(legislatorNameArray[position])[5], TvUpdateType.OVERWRITE);
                    */
					GetImageFromUrl getImageFromUrl = new GetImageFromUrl();
					getImageFromUrl.execute(legislatorListWithProfile.get(legislatorNameArray[position])[6]);
				}
				else {
					logger.warn("[onItemSelected] legislator profile not found");
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});
	}

	private void updateTextView(TextView tv, String msg, TvUpdateType updateType) {
		switch (updateType) {
			case OVERWRITE:
				tv.setText(msg);
				break;
			case APPEND:
				CharSequence tempMsg = tv.getText();
				tv.setText(msg + "\n");
				if (tempMsg != null){
					tv.append(tempMsg);
				}
				break;
		}
	}

	// =============================================================================================

	private void initSpiderWebChart() {

        String[] webChartTitle = resources.getStringArray(R.array.legislator_profile_radar_chart_title);

		// TODO create with class
		List<TitleValueEntity> data1 = new ArrayList<TitleValueEntity>();
		data1.add(new TitleValueEntity(webChartTitle[0], 3));
		data1.add(new TitleValueEntity(webChartTitle[1], 4));
		data1.add(new TitleValueEntity(webChartTitle[2], 9));
		data1.add(new TitleValueEntity(webChartTitle[3], 8));
		data1.add(new TitleValueEntity(webChartTitle[4], 10));
		data1.add(new TitleValueEntity(webChartTitle[4], 2));

		List<TitleValueEntity> data2 = new ArrayList<TitleValueEntity>();
		data2.add(new TitleValueEntity(webChartTitle[0], 3));
		data2.add(new TitleValueEntity(webChartTitle[1], 4));
		data2.add(new TitleValueEntity(webChartTitle[2], 5));
		data2.add(new TitleValueEntity(webChartTitle[3], 6));
		data2.add(new TitleValueEntity(webChartTitle[4], 7));
		data2.add(new TitleValueEntity(webChartTitle[5], 1));

		List<List<TitleValueEntity>> data = new ArrayList<List<TitleValueEntity>>();
		data.add(data1);
		data.add(data2);

		addRadarChartData(data);
		spiderWebChart.setLatitudeNum(5);//TODO method useless, check lib
	}

	//TODO check is data added before init chart
	private void addRadarChartData(List<List<TitleValueEntity>> newData) {
		List<List<TitleValueEntity>> radarChartData = new ArrayList<List<TitleValueEntity>>();
		for (List<TitleValueEntity> aData : newData) {
			radarChartData.add(aData);
		}
		spiderWebChart.setData(radarChartData);
	}

	private class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... urls) {
			Bitmap map = null;
			for (String url : urls) {
				map = downloadImage(url);
			}
			return map;
		}

		// Sets the Bitmap returned by doInBackground
		@Override
		protected void onPostExecute(Bitmap result) {
			imgProfile.setImageBitmap(result);
		}

		// Creates Bitmap from InputStream and returns it
		private Bitmap downloadImage(String url) {
			Bitmap bitmap = null;
			InputStream stream;
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;

			try {
				stream = getHttpConnection(url);
				bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
				stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return bitmap;
		}

		// Makes HttpURLConnection and returns InputStream
		private InputStream getHttpConnection(String urlString)
				throws IOException {
			InputStream stream = null;
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();

			try {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("GET");
				httpConnection.connect();

				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					stream = httpConnection.getInputStream();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return stream;
		}
	}

    private void setupUiComponents(View view) {
        TextView tvProfileAdTitle = (TextView) view.findViewById(R.id.legislator_profile_info_ad_title);
        tvProfileAd = (TextView) view.findViewById(R.id.legislator_profile_info_ad);
        TextView tvProfileGenderTitle = (TextView) view.findViewById(R.id.legislator_profile_info_gender_title);
        tvProfileGender = (TextView) view.findViewById(R.id.legislator_profile_info_gender);
        TextView tvProfilePartyTitle = (TextView) view.findViewById(R.id.legislator_profile_info_party_title);
        tvProfileParty = (TextView) view.findViewById(R.id.legislator_profile_info_party);
        TextView tvProfileCountyTitle = (TextView) view.findViewById(R.id.legislator_profile_info_county_title);
        tvProfileCounty = (TextView) view.findViewById(R.id.legislator_profile_info_county);
        TextView tvProfileEducationTitle = (TextView) view.findViewById(R.id.legislator_profile_info_education_title);
        tvProfileEducation = (TextView) view.findViewById(R.id.legislator_profile_info_education);
        TextView tvProfileExperienceTitle = (TextView) view.findViewById(R.id.legislator_profile_info_experience_title);
        tvProfileExperience = (TextView) view.findViewById(R.id.legislator_profile_info_experience);

        tvResponse = (TextView) view.findViewById(R.id.tv_response);
        imgProfile = (ImageView) view.findViewById(R.id.profile_img);
        legislatorNameSpinner = (Spinner) view.findViewById(R.id.spinner_legislator_name);
        spiderWebChart = (SpiderWebChart) view.findViewById(R.id.profile_radar_chart);
        initSpiderWebChart();

        // setup text view fonts
        FontManager fontManager = FontManager.getInstance();
        fontManager.setContext(getActivity());
        Typeface robotoLight = fontManager.getRobotoLight();
        Typeface droidSansFallback = fontManager.getDroidSansFallback();
        tvResponse.setTypeface(robotoLight);
        tvProfileAdTitle.setTypeface(droidSansFallback);
        tvProfileAd.setTypeface(droidSansFallback);
        tvProfileGenderTitle.setTypeface(droidSansFallback);
        tvProfileGender.setTypeface(droidSansFallback);
        tvProfilePartyTitle.setTypeface(droidSansFallback);
        tvProfileParty.setTypeface(droidSansFallback);
        tvProfileCountyTitle.setTypeface(droidSansFallback);
        tvProfileCounty.setTypeface(droidSansFallback);
        tvProfileEducationTitle.setTypeface(droidSansFallback);
        tvProfileEducation.setTypeface(droidSansFallback);
        tvProfileExperienceTitle.setTypeface(droidSansFallback);
        tvProfileExperience.setTypeface(droidSansFallback);

        // setup titles
        String[] legislatorProfileInfo = resources.getStringArray(R.array.legislator_profile_info);
        tvProfileAdTitle.setText(legislatorProfileInfo[0]);
        tvProfileGenderTitle.setText(legislatorProfileInfo[1]);
        tvProfilePartyTitle.setText(legislatorProfileInfo[2]);
        tvProfileEducationTitle.setText(legislatorProfileInfo[3]);
        tvProfileExperienceTitle.setText(legislatorProfileInfo[4]);
    }
}
