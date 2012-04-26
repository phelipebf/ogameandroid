package com.overkill.ogame;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.overkill.gui.HtmlSelect;
import com.overkill.ogame.game.NotificationSystem;
import com.overkill.ogame.game.Tools;


public class LoginView extends Activity {
    private static final String TAG = "ogame";
	
    private static final boolean marketRelease = false;
    private Class<?> settingsClass = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		NotificationSystem.remove(this);		
		
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(this.getClass().getPackage().getName(),0);
			TextView info = (TextView)findViewById(R.id.txt_info);
			info.setText("Current Version of OGame for Android: " + pinfo.versionName + "\n" + info.getText());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		if(marketRelease){
			settingsClass = SettingsViewWithInAppBilling.class;
		}else{
			settingsClass = SettingsView.class;
		}
		
		//load settings
		final SharedPreferences settings = getSharedPreferences(TAG, Context.MODE_PRIVATE);
		
		//Do we need to show the TOS
		if(settings.getBoolean("tos_" + getString(R.string.tos_version), false) == false){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Terms of Service");
			builder.setMessage(R.string.tos)
			       .setCancelable(false)
			       .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   SharedPreferences.Editor editor = settings.edit();
			        	   editor.putBoolean("tos_" + getString(R.string.tos_version), true);
			        	   editor.commit();
			        	   dialog.cancel();
			           }
			       })
			       .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   LoginView.this.finish();
			           }
				});
			AlertDialog alert = builder.create();
			alert.show();
		}
		
		//if there is no username stored -> uncheck the save username box
		if(settings.getString("username","").equals("") == false)
			((CheckBox)LoginView.this.findViewById(R.id.check_save)).setChecked(true);
		
		//Write settings to TextBox
		((EditText)findViewById(R.id.txt_login_user)).setText(settings.getString("username",""));
		((EditText)findViewById(R.id.txt_login_password)).setText(settings.getString("password",""));
		
		//Set selected universe
		((Spinner) findViewById(R.id.sel_domain)).setSelection(settings.getInt("domain", 0));
		
		((Spinner) findViewById(R.id.sel_domain)).setOnItemSelectedListener(new ListView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, final int position, long arg3) {			
				//Read country array
				String[] contries = getResources().getStringArray(R.array.countries);
				final String domain = getResources().getStringArray(R.array.domainlist)[position];
				//Prepare loading screen
				final ProgressDialog loaderDialog = new ProgressDialog(LoginView.this);				
				loaderDialog.setMessage("Loading universe list...");
				//Check if the universe list exsits or if we need to download it
				final File file = new File(getFilesDir().getAbsolutePath() + "/" + contries[position]);				
				if(file.exists()){
					final HtmlSelect select = new HtmlSelect(file);
					Spinner spinner = (Spinner) findViewById(R.id.sel_uni);
					spinner.setAdapter(select.toAdapter(LoginView.this));	
					if(position == settings.getInt("domain", 0)){
						spinner.setSelection(settings.getInt("universum", 0));
					}
				}else{
					Thread load_universe_list = new Thread(new Runnable() {					
						@Override
						public void run() {
							String html = get(domain);
							if(html.equals("")){
								runOnUiThread(new Runnable() {							
									@Override
									public void run() {
										loaderDialog.cancel();
										Toast.makeText(LoginView.this, R.string.error_load, Toast.LENGTH_LONG).show();
									}
								});		
								LoginView.this.finish();
								return;
							}
							String data = Tools.between(html, "<div class=\"black-border\">","</div>");					
							save(file, data);
							final HtmlSelect select = new HtmlSelect(data);
							runOnUiThread(new Runnable() {							
								@Override
								public void run() {
									Spinner spinner = (Spinner) findViewById(R.id.sel_uni);
									spinner.setAdapter(select.toAdapter(LoginView.this));	
									if(position == settings.getInt("domain", 0)){
										spinner.setSelection(settings.getInt("universum", 0));
									}
									loaderDialog.cancel();
								}
							});					
						}
					});
					loaderDialog.show();
					load_universe_list.start();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {			
			}			
		});
		
		Button btn_login = (Button)findViewById(R.id.btn_login);
		btn_login.setOnClickListener(new Button.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				EditText txt_user = (EditText)LoginView.this.findViewById(R.id.txt_login_user);
				final String username = txt_user.getText().toString();	
				
				EditText txt_pass = (EditText)LoginView.this.findViewById(R.id.txt_login_password);
				final String password = txt_pass.getText().toString();	
				
				final Spinner spinner = (Spinner) findViewById(R.id.sel_uni);	
				final Spinner country = (Spinner) findViewById(R.id.sel_domain);
				
				Intent i = new Intent(LoginView.this, MainTabActivity.class)
	    			.putExtra("username", username)
	    			.putExtra("password", password)
	    			.putExtra("universe", spinner.getSelectedItemPosition())
	    			.putExtra("country", country.getSelectedItemPosition())
	    			.putExtra("save", ((CheckBox)LoginView.this.findViewById(R.id.check_save)).isChecked());
				
				startActivity(i);								
				finish();
			}
		});
		ImageButton btn_donate = (ImageButton)findViewById(R.id.btn_donate);
		btn_donate.setOnClickListener(new Button.OnClickListener() {	
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginView.this, settingsClass).putExtra("donateOnly", true));
				//PLEASE DO NOT CHANGE THIS PAYPAL LINK!!
				//Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=JBA3WQ9LAFH8C&lc=US&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted"));
				//startActivity(myIntent);
			}		
		});		
	}
		
	public ListView.OnItemSelectedListener onChangeDomain(int position){
		return null;	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public String get(String url){
		try{
			DefaultHttpClient http = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = http.execute(httpget);
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            response.getEntity().writeTo(ostream);
			return ostream.toString();
		}catch(Exception ex){
			return "";
		}
	}	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.settings: startActivity(new Intent(this, settingsClass)); return true;
        	case R.id.reloadunivers: 
        		Spinner spinner = ((Spinner) findViewById(R.id.sel_domain));
        		int position = spinner.getSelectedItemPosition();
				String[] contries = getResources().getStringArray(R.array.countries);
				File file = new File(getFilesDir().getAbsolutePath() + "/" + contries[position]);
				Log.i(TAG,"Deleting " + file.getAbsolutePath());
				if(file.exists()){
					file.delete();
					spinner.setSelection(0, true);
					spinner.setSelection(position, true);
					
				}
        		return true;
        }
        return false;
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	FlurryAgent.onEndSession(this);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
		FlurryAgent.onStartSession(this, "WW6QN8MMFHGV82QKVEM4");
    }
	
	private void save(File file, String content){
		byte[] buffer = content.getBytes();
	    BufferedOutputStream f = null;
	    try {
	        f = new BufferedOutputStream(new FileOutputStream(file));
	        f.write(buffer);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        if (f != null) try { f.close();	} catch (IOException e) { }
	    }
	}
    
}
