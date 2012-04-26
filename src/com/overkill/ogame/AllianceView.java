package com.overkill.ogame;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
			if(uri.getQueryParameter("allianceId").equals("0")){
				Toast.makeText(this, "You cannot view nor edit your own ally yet", Toast.LENGTH_LONG).show();	
				finish();
				//Document html = Jsoup.parse(MainTabActivity.game.get("page=allianceOverview"));
				// Section Title
				//Elements tabs = html.select("div.section");
				// Section Content
				//Elements content = html.select("div.sectioncontent");
				/*
				 * 0	Short info
				 * 1	Member list
				 * 2	Internal Text
				 * 3	External Text
				 */
				// TODO read data from our ally
				
			}else{
				WebView web = new WebView(this);
				web.getSettings().setBuiltInZoomControls(true);
				web.getSettings().setSupportZoom(true);
				web.getSettings().setLoadsImagesAutomatically(true);
				setContentView(web);
				web.loadUrl(MainTabActivity.game.getBaseUrl() + "allianceInfo.php?allianceId=" + uri.getQueryParameter("allianceId"));
			}
		}
	}	
	
	private void transferLeadership()	{
	    String url = "page=allianceManagement&action=17";
	    //$.post(url, $("#form_setNewLeader").serialize())
	}
	 
	private void dissolve(){
		String url = "page=allianceManagement&action=2";
		MainTabActivity.game.get(url);
	}	 
	 
	private void leaveAlly(){
	    String url = "page=allianceOverview&action=18";
		MainTabActivity.game.get(url);
	}
	 
	private void takeoverLeadership(){
		String url = "page=allianceManagement&action=11";
		MainTabActivity.game.get(url);
	}
	 
	private void kickMember(String memberId){
		String url = "page=allianceOverview&action=10";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("memberId", memberId));		
		MainTabActivity.game.execute(url, nameValuePairs);
	}
	 
	private void deleteRank(String rankId){
	    String url = "index.php?page=allianceManagement&session=37697abb2f6d&action=8";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("rankId", rankId));		
		MainTabActivity.game.execute(url, nameValuePairs);
	}
}
