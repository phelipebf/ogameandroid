package com.overkill.ogame.game;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.overkill.ogame.MainTabActivity;
import com.overkill.ogame.MovementView;
import com.overkill.ogame.R;

public class NotificationSystem {
	private static final String TAG = "NotificationSystem";
	 
	private NotificationManager mNotificationManager;
	
	private Context mContext;
	private GameClient mGame;
	
	private Runnable mWorker;
	private Handler mHandler;
	
	private JSONObject mJson;
	
	Notification mNotification;
	
	private int mDealy = 1000;
	private boolean mRun = false;
	
	private int mHostile = 0;
	private int mHostileLast = 0;
	
	private int mNeutral = 0;
	private int mNeutralLast = 0;
	
	private int mFriendly = 0;
	private int mFriendlyLast = 0;
	
	private int mMessages = 0;
	private int mMessagesLast = 0;
	
	private boolean mNotifyHostile = false;
	private boolean mNotifyNeutral = false;
	private boolean mNotifyFriendly = false;
	private boolean mNotifyMessages = false;
	
	private Uri mSound = null;
	
	private PendingIntent contentIntent;
	
	public static final int NOTIFY_FLEET = 1;
	
	public NotificationSystem(Context context, GameClient game, String sound){
		this.mContext = context;
		this.mGame = game;
		if(sound != null)
			this.mSound = Uri.parse(sound);
		else
			this.mSound = null;
			
		Intent notifyIntent = new Intent(this.mContext, MainTabActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		this.contentIntent = PendingIntent.getActivity(this.mContext, 0, notifyIntent, NOTIFY_FLEET);
				
		this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		this.mNotification = new Notification(R.drawable.bar_icon, context.getString(R.string.app_name), System.currentTimeMillis());
		this.mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		this.mNotification.contentIntent = this.contentIntent;
		this.mNotification.sound = null;
	}	
	
	public void setDelay(int sec){
		this.mDealy = sec * 1000;
		if(mRun){
			mHandler.removeCallbacks(mWorker);
			mHandler.postDelayed(mWorker, this.mDealy);
			mRun = false;
		}		
	}
	
	public void config(boolean mNotifyHostile, boolean mNotifyNeutral, boolean mNotifyFriendly, boolean mNotifyMessages){
		this.mNotifyHostile = mNotifyHostile;
		this.mNotifyNeutral = mNotifyNeutral;
		this.mNotifyFriendly = mNotifyFriendly;
		this.mNotifyMessages = mNotifyMessages;
	}
	
	public void setFleetEvents(int hostile, int neutral, int friendly){		
		mHostileLast = mHostile;
		mHostile = hostile;
		
		mNeutralLast = mNeutral;
		mNeutral = neutral;
		
		mFriendlyLast = mFriendly;
		mFriendly = friendly;
		
		if(mJson != null && mJson.has("eventText")){
			try {
				show(mJson.getString("eventText") + " in " + Tools.sec2str(Integer.valueOf(mJson.getString("eventTime"))));
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}else{
			show("No Fleet event");
		}
	}
	
	public void setMessages(int messages){	
		mMessagesLast = mMessages;
		mMessages = messages;				
		show("No Fleet event");
	}
	
	public boolean getFleetState(){	
		if(mHostile > 0) return true;
		if(mNeutral > 0) return true;
		if(mFriendly > 0) return true;
		return false;
	}
	
	public boolean doNotify(){
		if(mHostile > 0 && mNotifyHostile == true && mHostile > mHostileLast){mHostileLast = mHostile; return true;}
		if(mNeutral > 0 && mNotifyNeutral == true && mNeutral > mNeutralLast){mNeutralLast = mNeutral; return true;}
		if(mFriendly > 0 && mNotifyFriendly == true && mFriendly > mFriendlyLast){mFriendlyLast = mFriendly; return true;}

		if(mMessages > 0 && mNotifyMessages == true && mMessages > mMessagesLast){mMessagesLast = mMessages; return true;}
		
		return false;
	}
	
	public void update(){
		if(mRun==false)
			return;
		try {
			mJson = new JSONObject(mGame.get("page=fetchEventbox&ajax=1"));
			setFleetEvents(
					Integer.valueOf(mJson.getString("hostile")), 
					Integer.valueOf(mJson.getString("neutral")), 
					Integer.valueOf(mJson.getString("friendly")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void destroy(){
		mHandler.removeCallbacks(mWorker);
		mRun = false;
		close();
	}
	
	public void init(){
		Log.i(TAG, "init");
		this.show("");
		this.mHandler = new Handler();
		mWorker = new Runnable() {
	  		 public void run() {
	  			 mRun = true;
	  			 update();
	  			 mHandler.postDelayed(mWorker, mDealy);						
	  		 }
		};
		mWorker.run();
	}
		
	public String toString(){
		return "Hostile: " + mHostile + "\n" +
			   "Neutral: " + mNeutral + "\n" +
			   "Friendly: " + mFriendly;		
	}
	
	public synchronized void show(CharSequence tickerText){
		this.mNotification.tickerText = null;
		this.mNotification.number = mHostile + mNeutral + mFriendly + mMessages;
		this.mNotification.when = System.currentTimeMillis();
		
		RemoteViews contentView = new RemoteViews(this.mContext.getPackageName(), R.layout.system_noification);
		
		contentView.setTextViewText(R.id.txt_info, "H: " + mHostile + " N: " + mNeutral + " F: " + mFriendly);
		contentView.setTextViewText(R.id.txt_event_text, tickerText);
		contentView.setTextViewText(R.id.txt_messages, mMessages + " unread Messages");
		
		if(getFleetState()){
			contentView.setImageViewResource(R.id.img_warning, R.drawable.warning_on);
		}else{
			contentView.setImageViewResource(R.id.img_warning, R.drawable.warning_off);
		}
		
		if(mMessages > 0){
			contentView.setImageViewResource(R.id.img_message, R.drawable.post_unread);
		}else{
			contentView.setImageViewResource(R.id.img_message, R.drawable.post_read);
		}		
		
		this.mNotification.contentView = contentView;
		
		// check if we want a real Notification
		if(doNotify() == true){
			this.mNotification.sound = this.mSound;			
			Log.i(TAG, "sound=" + this.mNotification.sound.toString());
		}else{
			this.mNotification.sound = null;
		}
		
		this.mNotificationManager.notify(NOTIFY_FLEET, this.mNotification);		
	}
	
	public void close(){
		Log.i("NotificationSystem", "close");
		this.mNotificationManager.cancel(NOTIFY_FLEET);
	}
	
	public static void remove(Context context){
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFY_FLEET);
	}
}