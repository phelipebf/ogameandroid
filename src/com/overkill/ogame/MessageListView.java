package com.overkill.ogame;

import java.util.ArrayList;
import java.util.Arrays;

import com.overkill.ogame.game.Message;
import com.overkill.ogame.game.MessageAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

public class MessageListView extends ListActivity {
	MessageAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {		
			Message[] messages = MainTabActivity.game.getMassages();
			adapter = new MessageAdapter(MessageListView.this, R.layout.adapter_item_message, new ArrayList<Message>(Arrays.asList(messages)));
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						setListAdapter(adapter);
						setProgressBarIndeterminateVisibility(false);
						if(adapter.isEmpty())
							Toast.makeText(MessageListView.this, "No message to display", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		setProgressBarIndeterminateVisibility(true);
		t.start();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);
		final Message m = adapter.getItem(position);
		m.setRead(true);
		adapter.notifyDataSetChanged();
		Intent i = new Intent(this, MessageDetailView.class).putExtra("msg_id", m.getID());
		startActivity(i);
		/*final Message m = adapter.getItem(position);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle(m.getSubject() + " from " + m.getFrom());
    	alert.setMessage(Html.fromHtml(m.getContent(MainTabActivity.game)));
    	alert.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	    	  public void onClick(DialogInterface dialog, int whichButton) {
	    		  m.setRead(true);
	    		  adapter.notifyDataSetChanged();
	    		  dialog.cancel();
	    	  }
	    	});
    	alert.show();    	*/
	}
}
