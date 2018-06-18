package xute.steemconnectexample;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.RequestOptions;
import com.hapramp.steemconnect4j.SteemConnect;
import com.hapramp.steemconnect4j.SteemConnectCallback;
import com.hapramp.steemconnect4j.SteemConnectException;
import com.hapramp.steemconnect4j.SteemConnectOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

	ImageView imageView;
	TextView usernameTv;
	TextView fullname;
	TextView sbd;
	TextView about;
	Handler handler;
	ProgressBar progressBar;

	private SteemConnect steemConnect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		handler = new Handler();
		usernameTv = findViewById(R.id.username);
		imageView = findViewById(R.id.userImage);
		fullname = findViewById(R.id.fullname);
		about = findViewById(R.id.about);
		sbd = findViewById(R.id.sbd);
		progressBar = findViewById(R.id.progressbar);

		String username = getIntent().getStringExtra(Constants.EXTRA_USERNAME);
		String token = getIntent().getStringExtra(Constants.EXTRA_ACCESS_TOKEN);

		prepareSteemConnect(token);
		fetchResponse();
		usernameTv.setText(username);
		loadImage(username);
	}

	private void loadImage(String username) {
		String url = "https://steemitimages.com/u/" + username + "/avatar";
		RequestOptions requestOptions = new RequestOptions();
		requestOptions.circleCrop();
		Glide
			.with(this)
			.applyDefaultRequestOptions(requestOptions)
			.load(url)
			.into(imageView);
	}

	private void prepareSteemConnect(String token) {
		SteemConnectOptions steemConnectOptions = new SteemConnectOptions();
		steemConnectOptions.setAccessToken(token);
		steemConnect = new SteemConnect(steemConnectOptions);
	}

	private void fetchResponse() {
		new Thread() {
			@Override
			public void run() {
				steemConnect.me(steemConnectCallback);
			}
		}.start();
	}

	SteemConnectCallback steemConnectCallback = new SteemConnectCallback() {
		@Override
		public void onResponse(String json) {
			try {
				final JSONObject accountObject = new JSONObject(json).getJSONObject("account");
				String jsonMetaDataString = accountObject.getString("json_metadata");
				String jsonFormattedString = jsonMetaDataString.replaceAll("\\\\", "");
				final JSONObject profileObject = new JSONObject(jsonFormattedString).getJSONObject("profile");
				handler.post(new Runnable() {
					@Override
					public void run() {
						progressBar.setVisibility(View.GONE);
						sbd.setText(String.format("Balance : %s", accountObject.optString("sbd_balance", "0 SBD")));
						fullname.setText(profileObject.optString("name", "--name--"));
						about.setText(profileObject.optString("about", "--about--"));
					}
				});
			}
			catch (JSONException e) {
				Log.d("WebData", "Error " + e.toString());
				e.printStackTrace();
			}
		}

		@Override
		public void onError(SteemConnectException e) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					progressBar.setVisibility(View.GONE);
					Toast.makeText(HomeActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
				}
			});
		}
	};

}
