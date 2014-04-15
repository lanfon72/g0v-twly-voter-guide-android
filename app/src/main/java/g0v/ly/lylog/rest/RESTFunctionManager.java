package g0v.ly.lylog.rest;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RESTFunctionManager {

	public void restGet(String getUrl, RestApiCallback restApiCallback) {
		Log.e("RESTFunctionManager :: restGet", "(0) in restGet");
		RESTGetAsyncTask restGetAsyncTask  = new RESTGetAsyncTask(getUrl, restApiCallback);
		restGetAsyncTask.execute();
	}

	private class RESTGetAsyncTask extends AsyncTask<Void, Integer, Void> {
		long 			startTime;
		String 			getUrl;
		String 			responseStr;
		RestApiCallback restApiCallback;

		public RESTGetAsyncTask(String url, RestApiCallback callback) {
			getUrl 			= url;
			restApiCallback = callback;
		}

		@Override
		protected Void doInBackground(Void... voids) {
			startTime = System.currentTimeMillis();

			DefaultHttpClient 	client 	= new DefaultHttpClient();
			HttpGet 			request = new HttpGet(getUrl);
			Log.e("RESTFunctionManager :: ThreadRESTGet", "(1) getUrl: " + getUrl);
			try {
				HttpResponse response 	= client.execute(request);
				responseStr 			= EntityUtils.toString(response.getEntity(), "UTF-8");
				restApiCallback.getDone(responseStr);
				Log.e("RESTFunctionManager :: ThreadRESTGet", "(2) get responseStr");
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.e("RESTFunctionManager :: ThreadRESTGet", "(3) spend " + (System.currentTimeMillis() - startTime) / 1000 +
					"." + (System.currentTimeMillis() - startTime) + " sec to get response.");
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			restApiCallback.getDone(responseStr);
		}
	}
}