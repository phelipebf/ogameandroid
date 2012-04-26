package com.overkill.ogame;

import java.util.ArrayList;
import java.util.Arrays;

import com.overkill.ogame.game.Message;
import com.overkill.ogame.game.MessageAdapter;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MessageListView extends ListActivity {
	MessageAdapter adapter;
	int displayCategory = Message.FILTER_INBOX;
	
	int lastPosition = 0;
	
	public void setProgressVisibility(boolean visible){
		if(visible)
			((ProgressBar)findViewById(R.id.progress)).setVisibility(View.VISIBLE);
		else
			((ProgressBar)findViewById(R.id.progress)).setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);   
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_message_list);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.system_title_message_list);  
        
        if(getIntent().hasExtra("displayCategory"))
        	displayCategory = getIntent().getExtras().getInt("displayCategory");
        
        ((Button)findViewById(R.id.btn_select_all)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				adapter.setAllChecked(true);		
				adapter.notifyDataSetChanged();
			}
		});
        
        ((Button)findViewById(R.id.btn_select_none)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				adapter.setAllChecked(false);		
				adapter.notifyDataSetChanged();			
			}
		});
        
        ((Button)findViewById(R.id.btn_folder)).setText(getResources().getIdentifier("string/message_folder_" + displayCategory, null, getPackageName()));
        
        ((Button)findViewById(R.id.btn_folder)).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				final int[] folderIDs = {
					Message.FILTER_INBOX, 
					Message.FILTER_ESPIONAGE, 
					Message.FILTER_BATTLE, 
					Message.FILTER_PLAYER, 
					Message.FILTER_EXPEDITION, 
					Message.FILTER_ALLIANCE, 
					Message.FILTER_OTHER, 
					Message.FILTER_BIN
				};
				final CharSequence[] folderNames = new CharSequence[folderIDs.length];
				for(int i = 0; i < folderIDs.length; i++){
					folderNames[i] = getString(getResources().getIdentifier("string/message_folder_" + folderIDs[i], null, getPackageName()));
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(MessageListView.this);
				builder.setTitle("Pick a folder");
				builder.setItems(folderNames, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	Intent i = new Intent(MessageListView.this, MessageListView.class);
						i.putExtra("displayCategory", folderIDs[item]);
						startActivity(i);
						finish();
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
				
			}
		});
        
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
		load();
	}
	
	private void load(){
		final int lastPosition = this.lastPosition;
		
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {		
			Message[] messages = MainTabActivity.game.getMassages(displayCategory);
			adapter = new MessageAdapter(MessageListView.this, R.layout.adapter_item_message, new ArrayList<Message>(Arrays.asList(messages)));
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						setListAdapter(adapter);
						setProgressVisibility(false);
						if(adapter.isEmpty()){
							Toast.makeText(MessageListView.this, R.string.message_no_message, Toast.LENGTH_SHORT).show();
						}else{
							setSelection(lastPosition);
						}
					}
				});
			}
		});
		setProgressVisibility(true);
		t.start();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		this.lastPosition = position;
		
		final Message m = adapter.getItem(position);
		m.setRead(true);
		adapter.notifyDataSetChanged();
		Intent i = new Intent(this, MessageDetailView.class).putExtra("msg_id", m.getID());
		startActivity(i);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if(adapter.getCheckedCount() > 0) {
			menu.add(0,  R.string.message_delete_selected, 0, R.string.message_delete_selected);
			menu.add(0,  R.string.message_mark_as_read, 0, R.string.message_mark_as_read);
		}
		if(adapter.getCount() > 0)
			menu.add(0,  R.string.message_delete_all, 0, R.string.message_delete_all);
		
	    menu.add(0, R.string.message_send, 0, R.string.message_send);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.string.message_delete_selected:
	    		Thread t1 = new Thread(new Runnable() {			
	    			@Override
	    			public void run() {		
	    			int[] ids = new int[adapter.getCheckedCount()];
	    			int c = 0;
	    			for(int i = 0; i < adapter.getCount(); i++){
	    				if(adapter.getItem(i).isChecked()){
	    					ids[c] = adapter.getItem(i).getID();
	    					c++;
	    				}	    					
	    			}
	    			MainTabActivity.game.deleteMessage(ids);
	    				runOnUiThread(new Runnable() {					
	    					@Override
	    					public void run() {
	    						setProgressVisibility(false);
	    						load();
	    					}
	    				});
	    			}
	    		});
	    		setProgressVisibility(true);
	    		t1.start();
		        return true;
	    	case R.string.message_mark_as_read:
	    		Thread t2 = new Thread(new Runnable() {			
	    			@Override
	    			public void run() {		
	    			int[] ids = new int[adapter.getCheckedCount()];
	    			int c = 0;
	    			for(int i = 0; i < adapter.getCount(); i++){
	    				if(adapter.getItem(i).isChecked()){
	    					ids[c] = adapter.getItem(i).getID();
	    					c++;
	    				}	    					
	    			}
	    			MainTabActivity.game.actionMessage(ids, Message.ACTION_READ);
	    				runOnUiThread(new Runnable() {					
	    					@Override
	    					public void run() {
	    						setProgressVisibility(false);
	    						load();
	    					}
	    				});
	    			}
	    		});
	    		setProgressVisibility(true);
	    		t2.start();
		        return true;
	    	case R.string.message_delete_all:
	    		Thread t3 = new Thread(new Runnable() {			
	    			@Override
	    			public void run() {		
	    			int[] ids = new int[adapter.getCount()];
	    			for(int i = 0; i < ids.length; i++){
	    				ids[i] = adapter.getItem(i).getID();
	    			}
	    			MainTabActivity.game.deleteMessage(ids);
	    				runOnUiThread(new Runnable() {					
	    					@Override
	    					public void run() {
	    						setProgressVisibility(false);
	    						load();
	    					}
	    				});
	    			}
	    		});
	    		setProgressVisibility(true);
	    		t3.start();
		        return true;
	    	case R.string.message_send:
	    		startActivity(new Intent(this, MessageComposeView.class));
	    		return true;
	    }
        return super.onOptionsItemSelected(item);
	}
}
