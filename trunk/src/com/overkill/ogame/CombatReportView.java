package com.overkill.ogame;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

public class CombatReportView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Uri uri = getIntent().getData();
		if(uri != null){
			WebView web = new WebView(this);
			setContentView(web);
			String html = MainTabActivity.game.get("page=combatreport&nID=" + uri.getQueryParameter("nID"));
			html = html.replace("%", "&#37");
			web.loadData(html, "text/html", "utf-8");
		}
	}
}
