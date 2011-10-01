package com.overkill.ogame;

import java.util.ArrayList;
import java.util.Arrays;

import com.overkill.ogame.game.Player;
import com.overkill.ogame.game.PlayerAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class BuddyView extends ListActivity {

	private PlayerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		adapter = new PlayerAdapter(this, R.layout.adapter_item_player, new ArrayList<Player>(Arrays.asList(MainTabActivity.game.getBuddyList())));
		setListAdapter(adapter);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent();
		i.putExtra("to", (Player)l.getAdapter().getItem(position));
		setResult(RESULT_OK, i);
		finish();
	}
	
}
