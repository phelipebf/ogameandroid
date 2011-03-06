package com.overkill.ogame.game;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.overkill.ogame.MovementView;
import com.overkill.ogame.R;

public class NotificationSystem {
	private static final String TAG = "NotificationSystem";
	 
	private NotificationManager mNotificationManager;
	
	private Context mContext;
	private GameClient mGame;
	
	private Runnable mWorker;
	private Handler mHandler;
	
	Notification mNotification;
	
	private int mDealy = 1000;
	private boolean mRun = false;
	
	private int mHostile = 0;
	private int mNeutral = 0;
	private int mFriendly = 0;
	
	private boolean mNotifyHostile = false;
	private boolean mNotifyNeutral = false;
	private boolean mNotifyFriendly = false;
	
	private PendingIntent contentIntent;
	
	public static final int NOTIFY_FLEET = 1;
	
	public NotificationSystem(Context context, GameClient game, Uri uri){
		this.mContext = context;
		this.mGame = game;
		this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		this.mNotification = new Notification(R.drawable.bar_icon, "OGame Fleet System", System.currentTimeMillis());
		contentIntent = PendingIntent.getActivity(this.mContext, 0, new Intent(this.mContext, MovementView.class), 0);
		this.mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		this.mNotification.contentIntent = contentIntent;
		//if(uri != null)
			this.mNotification.sound = null;
		//else
			//this.mNotification.flags |= Notification.DEFAULT_SOUND;
	}	
	
	public void setDelay(int sec){
		this.mDealy = sec * 1000;
		if(mRun){
			mHandler.removeCallbacks(mWorker);
			mHandler.postDelayed(mWorker, this.mDealy );
			mRun = false;
		}		
	}
	
	public void config(boolean mNotifyHostile, boolean mNotifyNeutral, boolean mNotifyFriendly){
		this.mNotifyHostile = mNotifyHostile;
		this.mNotifyNeutral = mNotifyNeutral;
		this.mNotifyFriendly = mNotifyFriendly;
	}
	
	public void setShipsAndNotifyWhenChanged(int hostile, int neutral, int friendly, JSONObject json){
		boolean notify = false;
		
		if(hostile > 0 && mNotifyHostile == true) notify = true;
		if(neutral > 0 && mNotifyNeutral == true) notify = true;
		if(friendly > 0 && mNotifyFriendly == true) notify = true;
	
		
		mHostile = hostile;
		mNeutral = neutral;
		mFriendly = friendly;
		
		if(json != null && json.has("eventText")){
			try {
				show(notify, json.getString("eventText") + " in " + Tools.sec2str(Integer.valueOf(json.getString("eventTime"))));
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}else{
			show(notify, "No Fleet event");
		}
	}
	
	public void update(){
		try {
			String j = mGame.get("page=fetchEventbox&ajax=1");
			Log.i("Eventbox", j);
			JSONObject json = new JSONObject(j);
			setShipsAndNotifyWhenChanged(
					Integer.valueOf(json.getString("hostile")), 
					Integer.valueOf(json.getString("neutral")), 
					Integer.valueOf(json.getString("friendly")),
					json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void destroy(){
		mHandler.removeCallbacks(mWorker);
		close();
	}
	
	public void init(){
		Log.i(TAG, "init");
		this.show(false, "");
		this.mHandler = new Handler();
		mWorker = new Runnable() {
	  		 public void run() {
	  			 mRun = true;
	  			 update();
	  			 mHandler.postDelayed(this, mDealy);						
	  		 }
		};
		mWorker.run();
	}
		
	public String toString(){
		return "Hostile: " + mHostile + "\n" +
			   "Neutral: " + mNeutral + "\n" +
			   "Friendly: " + mFriendly;
		
	}
	
	public synchronized void show(boolean notify, CharSequence tickerText){
		//close();
		RemoteViews contentView = new RemoteViews(this.mContext.getPackageName(), R.layout.system_noification);
		this.mNotification.tickerText = null;
		contentView.setTextViewText(R.id.txt_info, "H: " + mHostile + " N: " + mNeutral + " F: " + mFriendly);
		contentView.setTextViewText(R.id.txt_event_text, tickerText);
		this.mNotification.number = mHostile + mNeutral + mFriendly;
		Log.i(TAG, "count " + this.mNotification.number);
		this.mNotification.when = System.currentTimeMillis();
		if(notify){
			contentView.setImageViewResource(R.id.img_warning, R.drawable.warning_on);
		}else{
			contentView.setImageViewResource(R.id.img_warning, R.drawable.warning_off);
		}
		Log.i(TAG, "settings icon state to " + String.valueOf(notify));
		this.mNotification.contentView = contentView;
		this.mNotificationManager.notify(NOTIFY_FLEET, this.mNotification);		
	}
	
	public void close(){
		Log.i("NotificationSystem", "close");
		this.mNotificationManager.cancel(NOTIFY_FLEET);
	}
}