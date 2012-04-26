package com.overkill.ogame;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.overkill.ogame.game.Message;
import com.overkill.ogame.game.Player;
import com.overkill.ogame.game.Tools;

public class MessageDetailView extends Activity {
	int msg_id;
	Message msg;
	Player from;
	boolean canReply = false;
	boolean canReport = false;
	String replySubject = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.activity_message_detail);
		
		if(getIntent().getExtras().containsKey("msg_id")==false)
			finish();
		
		msg_id = getIntent().getExtras().getInt("msg_id");
		
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {		
				Document html = Jsoup.parse(MainTabActivity.game.get("page=showmessage&msg_id=" + msg_id));
				
				if(html.select("li.reply").size() > 0)
					canReply = true;
				
				/*if(html.select("li.notify").size() > 0)
					canReport = true;*/			
				
				Element info = html.select("div.infohead").first();

				Elements tr = info.select("tr");
				
				msg = new Message(msg_id);

				String playerID = html.select("form").attr("action"); //
				if(playerID.contains("&to="))
					playerID = Tools.between(playerID, "&to=", "&");

				String playerName = html.select("span.playerName").html();
				if(playerName.contains("<")) //Not from system (no link after name)
					playerName = playerName.substring(0, playerName.indexOf("<")).trim();
				
				msg.setFrom(playerName);
				msg.setTo(tr.get(1).select("td").text());
				msg.setSubject(tr.get(2).select("td").text());
				msg.setDate(tr.get(3).select("td").text());
				
				String content = html.select("div.note").html();
					
				content = content.replaceAll("index.php\\?page=fleet1([^/]*)&amp;galaxy=([^/]*)&amp;system=([^/]*)&amp;position=([^/]*)&amp;type=([^/]*)&amp;mission=([^/]*)\"",
						"ogame://fleet?galaxy=$2&system=$3&position=$4&type=$5&mission=$6\"");
				
				content = content.replaceAll("index.php\\?page=galaxy&amp;galaxy=([^/]*)&amp;system=([^/]*)&amp;position=([^/]*)&amp;session=([^/]*)\"",
						"ogame://galaxy?galaxy=$1&system=$2&position=$3\"");

				content = content.replaceAll("javascript:showGalaxy\\(([^/]*),([^/]*),([^/]*)\\)", 
						"ogame://galaxy?galaxy=$1&system=$2&position=$3");
						
				content = content.replaceAll("<a href=\\\"#\\\" onclick=\\\"popupWindow\\('index.php\\?page=combatreport&amp;session=([^/]*)&amp;nID=([^/]*)','CombatReport','auto','no','0','0','no','620','600','yes'\\);\\\">",
						"<a href=\"ogame://combat?nID=$2\">");
				
				msg.setContent(content);
					
				msg.setHtml(html.head() + "<body id=\"showmessage\"> <div id=\"messagebox\" class=\"read\"> <div id=\"wrapper\"><div class=\"note\"> " + content + "</div></div></div> </body>");
				
				//Get subject and messageID
				if(canReply){
					replySubject = html.select("input[name=betreff]").attr("value");
					from = new Player(Integer.valueOf(playerID), msg.getFrom());
				}else{
					from = new Player(0, msg.getFrom());
				}
				
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						
						((TextView)findViewById(R.id.txt_from)).append(" " + Tools.htmlconvert(msg.getFrom()));
						((TextView)findViewById(R.id.txt_to)).append(" " + Tools.htmlconvert(msg.getTo()));
						((TextView)findViewById(R.id.txt_subject)).append(" " + Tools.htmlconvert(msg.getSubject()));
						((TextView)findViewById(R.id.txt_date)).append(" " + msg.getDate());
						if(getSharedPreferences("ogame", 0).getBoolean("message_html", true)){
							((WebView)findViewById(R.id.web_msg)).setVisibility(View.VISIBLE);
							((WebView)findViewById(R.id.web_msg)).loadData(msg.getHtml().replace("%", "&#37"), "text/html", "utf-8");
						}else{
							((ScrollView)findViewById(R.id.txt_scroll)).setVisibility(View.VISIBLE);
							((TextView)findViewById(R.id.txt_msg)).setMovementMethod(LinkMovementMethod.getInstance());
							((TextView)findViewById(R.id.txt_msg)).setText(Html.fromHtml(Tools.htmlconvert(msg.getContent())));
						}
						
						
						((Button)findViewById(R.id.btn_reply)).setEnabled(canReply);
						((Button)findViewById(R.id.btn_report)).setEnabled(canReport);
						setProgressBarIndeterminateVisibility(false);
					}
				});
			}
		});
		setProgressBarIndeterminateVisibility(true);
		t.start();
	}

	public void btnDelete(View view){
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {		
				MainTabActivity.game.deleteMessage(msg_id);
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						setProgressBarIndeterminateVisibility(false);
						finish();
					}
				});
			}
		});
		setProgressBarIndeterminateVisibility(true);
		t.start();	
	}
	
	public void btnRepley(View view){
		Intent newMessage = new Intent(this, MessageComposeView.class);
		newMessage.putExtra("to", from);
		newMessage.putExtra("replySubject", replySubject);
		newMessage.putExtra("relationMessageId", msg_id);
		startActivity(newMessage);
	}
	
}
