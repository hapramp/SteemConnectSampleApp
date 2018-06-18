package xute.steemconnectexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hapramp.steemconnect4j.SteemConnect;
import com.hapramp.steemconnect4j.SteemConnectException;
import com.hapramp.steemconnect4j.SteemConnectOptions;

public class MainActivity extends AppCompatActivity {

	SteemConnect steemConnect;
	String loginUrl;
	public static final int REQUEST_LOGIN_CODE = 119;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupSteemConnect();
	}

	private void setupSteemConnect() {
		SteemConnect.InstanceBuilder instanceBuilder = new SteemConnect.InstanceBuilder();
		instanceBuilder.setApp("hapramp.app")
			.setCallbackUrl("https://alpha.hapramp.com/_oauth/")
			.setScope(new String[]{"comment", "vote"});
		steemConnect = instanceBuilder.build();
	}

	private void navigateToWebLogin() {
		try {
			loginUrl = steemConnect.getLoginUrl();
			Intent i = new Intent(this, WebActivity.class);
			i.putExtra(Constants.EXTRA_LOGIN_URL, loginUrl);
			startActivityForResult(i, REQUEST_LOGIN_CODE);
		}
		catch (SteemConnectException e) {
			e.printStackTrace();
		}
	}

	public void loginUsingSteemConnect(View view) {
		navigateToWebLogin();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_LOGIN_CODE) {
			if (resultCode == RESULT_OK) {
				navigateToHomePage(data);
			} else {
				Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void navigateToHomePage(Intent intent) {
		intent.setClass(this, HomeActivity.class);
		startActivity(intent);
	}

}
