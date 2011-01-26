package com.overkill.ogame;

import com.overkill.ogame.game.Tools;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Overview extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        final TextView txt_info = (TextView)findViewById(R.id.txt_info);
        txt_info.setText(R.string.loading);
        Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {
				final String info =  MainTabActivity.game.get("page=overview");	
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						txt_info.setText("");
						txt_info.append(Tools.between(info, "textContent[0] = \"", "\"") + " ");
						txt_info.append(Tools.between(info, "textContent[1] = \"", "\"").replace("<span>", "").replace("</span>", "") + "\n");
						
						txt_info.append(Tools.between(info, "textContent[2] = \"", "\"") + " ");
						txt_info.append(Tools.between(info, "textContent[3] = \"", "\"") + "\n");
						
						txt_info.append(Tools.between(info, "textContent[4] = \"", "\"") + " ");
						txt_info.append(MainTabActivity.game.getCurrentPlanet().getCoordinates() + "\n");
						
						txt_info.append(Tools.between(info, "textContent[6] = \"", "\"") + " ");
						int start = info.indexOf("textContent[7] = \"") + "textContent[7] = \"".length();
						start = info.indexOf(">", start) + 1;
						int end = info.indexOf("<", start);
						txt_info.append(info.substring(start, end));
						
						//txt_info.append("\n\nFlottenbewegung:\n");
						//txt_info.append(MainTabActivity.notify.toString());
					}
				});
			}
        });
        t.start();
    }
}
