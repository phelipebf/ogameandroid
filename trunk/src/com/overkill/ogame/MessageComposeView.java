package com.overkill.ogame;

import com.overkill.ogame.game.Player;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class MessageComposeView extends Activity {
	Player player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.activity_message_compose);
		
		if(getIntent().hasExtra("to")){
			player = (Player) getIntent().getExtras().get("to");
			((EditText) findViewById(R.id.edit_to)).setText(player.getPlayerName());
			((EditText) findViewById(R.id.txt_msg)).setText(String.valueOf(player.getPlayerID()));
		}
		
		((EditText) findViewById(R.id.edit_to)).setOnFocusChangeListener(new EditText.OnFocusChangeListener() {			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus == false){
					String playerName = ((EditText) findViewById(R.id.edit_to)).getEditableText().toString();
					player = MainTabActivity.game.findPlayer(playerName);
					if(player.getPlayerID() == 0){
						Toast.makeText(MessageComposeView.this, "Unable to find Player", Toast.LENGTH_SHORT).show();
					}else{
						((EditText) findViewById(R.id.edit_to)).setText(player.getPlayerName());
					}
				}
			}
		});
		
	}
	
	public void btnSend(View view){
		String subject = ((EditText) findViewById(R.id.edit_subject)).getEditableText().toString();
		String text = ((EditText) findViewById(R.id.txt_msg)).getEditableText().toString();
		MainTabActivity.game.sendMessage(player.getPlayerID(), subject, text);
		finish();
	}
	
	public void btnDelete(View view){
		finish();
	}
	
}
