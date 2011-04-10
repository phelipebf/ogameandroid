package com.overkill.ogame;

import java.util.ArrayList;
import java.util.Arrays;

import com.overkill.ogame.game.Message;
import com.overkill.ogame.game.MessageAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
		load();
	}
	
	private void load(){
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
							Toast.makeText(MessageListView.this, R.string.message_no_message, Toast.LENGTH_SHORT).show();
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
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0,  R.string.message_delete_all, 0, R.string.message_delete_all);
	    menu.add(0, R.string.message_send, 0, R.string.message_send);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.string.message_delete_all:
	    		Thread t = new Thread(new Runnable() {			
	    			@Override
	    			public void run() {		
	    			int[] ids = new int[adapter.getCount()];
	    			for(int i = 0; i < ids.length; i++)
	    				ids[i] = adapter.getItem(i).getID();
	    			MainTabActivity.game.deleteMessage(ids);
	    				runOnUiThread(new Runnable() {					
	    					@Override
	    					public void run() {
	    						setProgressBarIndeterminateVisibility(false);
	    						load();
	    					}
	    				});
	    			}
	    		});
	    		setProgressBarIndeterminateVisibility(true);
	    		t.start();
		        return true;
	    	case R.string.message_send:
	    		startActivity(new Intent(this, MessageComposeView.class));
	    		return true;
	    }
        return super.onOptionsItemSelected(item);
	}
}
