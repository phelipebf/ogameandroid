package com.overkill.ogame;

import com.overkill.ogame.game.Player;
import com.overkill.ogame.game.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class MessageComposeView extends Activity {
	Player player;
	int isAnswerMessage = 0;
	int relationMessageId = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.activity_message_compose);
		
		if(getIntent().hasExtra("to")){
			player = (Player) getIntent().getExtras().get("to");
			((EditText) findViewById(R.id.edit_to)).setText(player.getPlayerName());
			((EditText) findViewById(R.id.edit_subject)).setText(getIntent().getExtras().getString("replySubject"));
			isAnswerMessage = 1;
			relationMessageId = getIntent().getExtras().getInt("relationMessageId");
			((EditText) findViewById(R.id.txt_msg)).requestFocus();
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.player = (Player) data.getExtras().get("to");
		((EditText) findViewById(R.id.edit_to)).setText(player.getPlayerName());
		((EditText) findViewById(R.id.edit_subject)).requestFocus();
	}
	
	public void btnLookup(View view){
		Intent i = new Intent(this, BuddyView.class);
		startActivityForResult(i, 0);
	}
	
	public void btnSend(View view){
		String subject = ((EditText) findViewById(R.id.edit_subject)).getEditableText().toString();
		String text = ((EditText) findViewById(R.id.txt_msg)).getEditableText().toString();
		
		String html = MainTabActivity.game.sendMessage(player.getPlayerID(), subject, text, isAnswerMessage, relationMessageId);
		
		html = Tools.between(html, "$(document).ready(function() {", "}");
		String result = "Error sending Message";
		if(html.contains("fadeBox("))
			result = Tools.between(html, "fadeBox(\"", "\"");
		
		Toast.makeText(MessageComposeView.this, result, Toast.LENGTH_LONG).show();		
		finish();
	}
	
	public void btnDelete(View view){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.message_delete);
		dialog.setMessage("Sure?");
		dialog.setPositiveButton(android.R.string.yes, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				finish();				
			}
		});
		dialog.setNegativeButton(android.R.string.no, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();				
			}
		});
		dialog.show();
	}
	
}
