package com.overkill.ogame.game;

import java.text.DateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.overkill.ogame.MainTabActivity;
import com.overkill.ogame.R;

public class NotificationSystem {
	private static final String TAG = "NotificationSystem";
	 
	private NotificationManager mNotificationManager;
	
	private Activity mActivity;
	private GameClient mGame;
	
	private Thread mThread;
	
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
	
	public static final int NOTIFY_FLEET = 1;
	
	/**
	 * 
	 * 
	 * @param activity
	 * @param game
	 * @param sound
	 */
	public NotificationSystem(Activity activity, GameClient game, String sound){
		this.mActivity = activity;
		this.mGame = game;
		
		if(sound != null)
			this.mSound = Uri.parse(sound);
		else
			this.mSound = null;
			
		Intent notifyIntent = new Intent(this.mActivity, MainTabActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		this.mNotificationManager = (NotificationManager) this.mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
		
		this.mNotification = new Notification(R.drawable.bar_icon, activity.getString(R.string.app_name), System.currentTimeMillis());
		this.mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		this.mNotification.contentIntent = PendingIntent.getActivity(this.mActivity, 0, notifyIntent, NOTIFY_FLEET);
		this.mNotification.sound = null;
		this.mNotification.number = '1';
	}	
	
	/**
	 * 
	 * 
	 * @param sec
	 */
	public void setDelay(int sec){
		this.mDealy = sec * 1000;
	}
	
	/**
	 * 
	 * 
	 * @param mNotifyHostile
	 * @param mNotifyNeutral
	 * @param mNotifyFriendly
	 * @param mNotifyMessages
	 */
	public void config(boolean mNotifyHostile, boolean mNotifyNeutral, boolean mNotifyFriendly, boolean mNotifyMessages){
		this.mNotifyHostile = mNotifyHostile;
		this.mNotifyNeutral = mNotifyNeutral;
		this.mNotifyFriendly = mNotifyFriendly;
		this.mNotifyMessages = mNotifyMessages;
	}
	
	/**
	 * 
	 * 
	 * @param hostile
	 * @param neutral
	 * @param friendly
	 */
	public void setFleetEvents(int hostile, int neutral, int friendly){		
		mHostileLast = mHostile;
		mHostile = hostile;
		
		mNeutralLast = mNeutral;
		mNeutral = neutral;
		
		mFriendlyLast = mFriendly;
		mFriendly = friendly;
		
		if(mJson != null && mJson.has("eventText")){
			try {
				String text = mJson.getString("eventText");
				
				Calendar now = Calendar.getInstance();
				DateFormat sdf = DateFormat.getDateTimeInstance();
				now.add(Calendar.SECOND, Integer.valueOf(mJson.getString("eventTime")));
				text += " (" + sdf.format(now.getTime()) + ")";
				
				show(text);
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}else{
			show("No Fleet event");
		}
	}
	
	/**
	 * 
	 * 
	 * @param messages
	 */
	public void setMessages(int messages){	
		mMessagesLast = mMessages;
		mMessages = messages;				
		show("No Fleet event");
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean getFleetState(){	
		if(mHostile > 0) return true;
		if(mNeutral > 0) return true;
		if(mFriendly > 0) return true;
		
		return false;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean doNotify(){
		if(mHostile > 0 && mNotifyHostile == true && mHostile > mHostileLast){mHostileLast = mHostile; return true;}
		if(mNeutral > 0 && mNotifyNeutral == true && mNeutral > mNeutralLast){mNeutralLast = mNeutral; return true;}
		if(mFriendly > 0 && mNotifyFriendly == true && mFriendly > mFriendlyLast){mFriendlyLast = mFriendly; return true;}

		if(mMessages > 0 && mNotifyMessages == true && mMessages > mMessagesLast){mMessagesLast = mMessages; return true;}
		
		return false;
	}
	
	/**
	 * 
	 */
	public void update(){
		if(mRun==false)
			return;
		
		Log.i(TAG, "Update notification -> fetchEventbox");
		final String data = mGame.get("page=fetchEventbox&ajax=1");
		if (data != null) {
			Log.d(TAG, "fetchEventbox data: " + data);
			this.mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						mJson = new JSONObject(data);
						setFleetEvents(
							Integer.valueOf(mJson.getString("hostile")), 
							Integer.valueOf(mJson.getString("neutral")), 
							Integer.valueOf(mJson.getString("friendly"))
						);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
		else {
			Log.e(TAG, "No data fetched");
		}
	}
	
	/**
	 * 
	 */
	public void destroy(){
		if(mThread != null) mThread.interrupt();
		mRun = false;
		close();
	}
	
	/**
	 * 
	 */
	public void init(){
		Log.i(TAG, "init");
		Runnable mRunnable = new Runnable() {
	  		 public void run() {
	  			 while(mRun) {
	  				 try {
	  					 update();
	  					 Thread.sleep(mDealy);
					} catch (InterruptedException e) {
						mRun = false;
					}
	  			 }
	  		 }
		};
		mThread = new Thread(mRunnable);
		mRun = true;
		mThread.start();
	}
	
	/**
	 * 
	 */
	public String toString(){
		return "Hostile: " + mHostile + "\n" +
			   "Neutral: " + mNeutral + "\n" +
			   "Friendly: " + mFriendly;		
	}
	
	/**
	 * 
	 * 
	 * @param tickerText
	 */
	public synchronized void show(CharSequence tickerText){
		this.mNotification.tickerText = null;
		this.mNotification.number = mHostile + mNeutral + mFriendly + mMessages;
		this.mNotification.when = System.currentTimeMillis();
		
		Log.d(TAG, "Notification number: " + this.mNotification.number);
		
		RemoteViews contentView = new RemoteViews(this.mActivity.getPackageName(), R.layout.system_noification);
		
		contentView.setTextViewText(R.id.txt_info, "H: " + mHostile + " N: " + mNeutral + " F: " + mFriendly);
		contentView.setTextViewText(R.id.txt_event_text, tickerText);
		contentView.setTextViewText(R.id.txt_messages, mMessages + " unread Messages");
		
		if(getFleetState()){
			Log.d(TAG, "Fleet info present: warning on");
			contentView.setImageViewResource(R.id.img_warning, R.drawable.warning_on);
		}else{
			Log.d(TAG, "Fleet info NOT present: warning off");
			contentView.setImageViewResource(R.id.img_warning, R.drawable.warning_off);
		}
		
		if(mMessages > 0){
			Log.d(TAG, "Unread messages present: post unread");
			contentView.setImageViewResource(R.id.img_message, R.drawable.post_unread);
		}else{
			Log.d(TAG, "Unread messages NOT present: post read");
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
	
	/**
	 * 
	 */
	public void close(){
		Log.i("NotificationSystem", "close");
		this.mNotificationManager.cancel(NOTIFY_FLEET);
	}
	
	/**
	 * 
	 * 
	 * @param context
	 */
	public static void remove(Context context){
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFY_FLEET);
	}
}