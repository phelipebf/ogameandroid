package com.overkill.ogame;

import com.overkill.ogame.game.Message;
import com.overkill.ogame.game.Player;
import com.overkill.ogame.game.Tools;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MessageDetailView extends Activity {
	int msg_id;
	Message msg;
	Player from;
	boolean canRepley = false;
	boolean canReport = false;
	
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
				String html = MainTabActivity.game.get("page=showmessage&ajax=1&msg_id=" + msg_id);
				String info = Tools.between(html, "<div class=\"infohead\">", "</div>");

				String tr[] = info.split("</tr>");
				Log.i("tr[0]", tr[0]);
				Log.i("tr", "len " + tr.length);
				msg = new Message(msg_id);
				msg.setFrom(Tools.between(tr[0], "<span class=\"playerName\">", " <").trim());
				msg.setTo(Tools.between(tr[1], "<td>", "</td>"));
				msg.setSubject(Tools.between(tr[2], "<td>", "</td>"));
				msg.setDate(Tools.between(tr[3], "<td>", "</td>"));
				
				String content = html.substring(html.indexOf("<div class=\"note\">"), html.lastIndexOf("</div>"));
				
				msg.setContent(Html.fromHtml(content).toString());
				
				from = new Player(0, msg.getFrom());
				
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						((TextView)findViewById(R.id.txt_from)).append(" " + msg.getFrom());
						((TextView)findViewById(R.id.txt_to)).append(" " + msg.getTo());
						((TextView)findViewById(R.id.txt_subject)).append(" " + msg.getSubject());
						((TextView)findViewById(R.id.txt_date)).append(" " + msg.getDate());
						((TextView)findViewById(R.id.txt_msg)).setText(msg.getContent());
						((Button)findViewById(R.id.btn_repley)).setEnabled(canRepley);
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
		startActivity(new Intent(this, MessageComposeView.class).putExtra("to", from));
	}
	
}
