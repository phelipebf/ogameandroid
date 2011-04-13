package com.overkill.ogame;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class AllianceView extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Uri uri = getIntent().getData();
		if(uri != null){
			if(uri.getQueryParameter("allyid").equals("0")){
				// TODO read data from our ally
				Toast.makeText(this, "You cannot view nor edit your own ally yet", Toast.LENGTH_LONG).show();
				finish();
			}else{
				WebView web = new WebView(this);
				web.getSettings().setBuiltInZoomControls(true);
				web.getSettings().setSupportZoom(true);
				setContentView(web);
				web.loadUrl(MainTabActivity.game.getBaseUrl() + "ainfo.php?allyid=" + uri.getQueryParameter("allyid"));
			}
		}
	}	
}
