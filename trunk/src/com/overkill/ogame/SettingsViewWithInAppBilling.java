/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.overkill.ogame;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.android.vending.billing.BillingService;
import com.android.vending.billing.BillingService.RequestPurchase;
import com.android.vending.billing.BillingService.RestoreTransactions;
import com.android.vending.billing.Consts;
import com.android.vending.billing.Consts.PurchaseState;
import com.android.vending.billing.Consts.ResponseCode;
import com.android.vending.billing.PurchaseDatabase;
import com.android.vending.billing.PurchaseObserver;
import com.android.vending.billing.ResponseHandler;

/**
 * A sample application that demonstrates in-app billing.
 */
public class SettingsViewWithInAppBilling extends PreferenceActivity{
    private static final String TAG = "ogame-donate";

    private static final String DB_INITIALIZED = "db_initialized";

    private static final String paypalurl = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=JBA3WQ9LAFH8C&lc=US&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted";
    private static final String abouturl = "http://code.google.com/p/ogameandroid/people/list";
    private DonationObserver mDonationObserver;
    private Handler mHandler;

    private BillingService mBillingService;
    private CheckBoxPreference mBuyButton;
    private Preference mDonateButton;
    //private Button mEditPayloadButton;
    //private TextView mLogTextView;
    //private Spinner mSelectItemSpinner;
    //private ListView mOwnedItemsTable;
    //private SimpleCursorAdapter mOwnedItemsAdapter;
    private PurchaseDatabase mPurchaseDatabase;
    private Cursor mOwnedItemsCursor;
    private Set<String> mOwnedItems = new HashSet<String>();

    private static final int DIALOG_CANNOT_CONNECT_ID = 1;
    private static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 2;
        
    /**
     * Each product in the catalog is either MANAGED or UNMANAGED.  MANAGED
     * means that the product can be purchased only once per user (such as a new
     * level in a game). The purchase is remembered by Android Market and
     * can be restored if this application is uninstalled and then
     * re-installed. UNMANAGED is used for products that can be used up and
     * purchased multiple times (such as poker chips). It is up to the
     * application to keep track of UNMANAGED products for the user.
     */
    private enum Managed { MANAGED, UNMANAGED }

    /**
     * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
     * messages to this application so that we can update the UI.
     */
    private class DonationObserver extends PurchaseObserver {
        public DonationObserver(Handler handler) {
            super(SettingsViewWithInAppBilling.this, handler);
        }

        @Override
        public void onBillingSupported(boolean supported) {
            if (Consts.DEBUG) {
                Log.i(TAG, "supported: " + supported);
            }
            if (supported) {
                restoreDatabase();
                mBuyButton.setEnabled(true);
            } else {
                showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
                ((Preference)findPreference("donate")).setEnabled(false);
            }
        }

        @Override
        public void onPurchaseStateChange(PurchaseState purchaseState, String itemId,
                int quantity, long purchaseTime, String developerPayload) {
            if (Consts.DEBUG) {
                Log.i(TAG, "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);
            }

            if (developerPayload == null) {
                logProductActivity(itemId, purchaseState.toString());
            } else {
                logProductActivity(itemId, purchaseState + "\n\t" + developerPayload);
            }

            if (purchaseState == PurchaseState.PURCHASED) {
                mOwnedItems.add(itemId);
            }
            mCatalogAdapter.setOwnedItems(mOwnedItems);
            mOwnedItemsCursor.requery();
        }

        @Override
        public void onRequestPurchaseResponse(RequestPurchase request,
                ResponseCode responseCode) {
            if (Consts.DEBUG) {
                Log.d(TAG, request.mProductId + ": " + responseCode);
            }
            if (responseCode == ResponseCode.RESULT_OK) {
                if (Consts.DEBUG) {
                    Log.i(TAG, "purchase was successfully sent to server");
                }
                logProductActivity(request.mProductId, "sending purchase request");
            } else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
                if (Consts.DEBUG) {
                    Log.i(TAG, "user canceled purchase");
                }
                logProductActivity(request.mProductId, "dismissed purchase dialog");
            } else {
                if (Consts.DEBUG) {
                    Log.i(TAG, "purchase failed");
                }
                logProductActivity(request.mProductId, "request purchase returned " + responseCode);
            }
        }

        @Override
        public void onRestoreTransactionsResponse(RestoreTransactions request,
                ResponseCode responseCode) {
            if (responseCode == ResponseCode.RESULT_OK) {
                if (Consts.DEBUG) {
                    Log.d(TAG, "completed RestoreTransactions request");
                }
                // Update the shared preferences so that we don't perform
                // a RestoreTransactions again.
                SharedPreferences prefs = getSharedPreferences("ogame", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(DB_INITIALIZED, true);
                edit.commit();
            } else {
                if (Consts.DEBUG) {
                    Log.d(TAG, "RestoreTransactions error: " + responseCode);
                }
            }
        }
    }

    private static class CatalogEntry {
        public String sku;
        public String name;
        public Managed managed;

        public CatalogEntry(String sku, String name, Managed managed) {
            this.sku = sku;
            this.name = name;
            this.managed = managed;
        }
    }

    /** An array of product list entries for the products that can be purchased. */
    private static final CatalogEntry[] CATALOG = new CatalogEntry[] {
        new CatalogEntry("ogame.inapp.donate.1", "Donate 1 嚙�", Managed.UNMANAGED),
        new CatalogEntry("ogame.inapp.donate.2", "Donate 2 嚙�", Managed.UNMANAGED),
        new CatalogEntry("ogame.inapp.donate.5", "Donate 5 嚙�", Managed.UNMANAGED),
        new CatalogEntry("ogame.inapp.donate.10", "Donate 10 嚙�", Managed.UNMANAGED)
    };

    private String mItemName;
    private String mSku;
    private CatalogAdapter mCatalogAdapter;

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	getPreferenceManager().setSharedPreferencesName("ogame");
        addPreferencesFromResource(R.xml.preferences);

        mHandler = new Handler();
        mDonationObserver = new DonationObserver(mHandler);
        mBillingService = new BillingService();
        mBillingService.setContext(this);

        mPurchaseDatabase = new PurchaseDatabase(this);
        setupWidgets();

        // Check if billing is supported.
        ResponseHandler.register(mDonationObserver);
        if (!mBillingService.checkBillingSupported()) {
            showDialog(DIALOG_CANNOT_CONNECT_ID);
        }
        
    }

    /**
     * Called when this activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        ResponseHandler.register(mDonationObserver);
        initializeOwnedItems();
    }

    /**
     * Called when this activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
        ResponseHandler.unregister(mDonationObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPurchaseDatabase.close();
        mBillingService.unbind();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_CANNOT_CONNECT_ID:
            return createDialog(R.string.cannot_connect_title, R.string.cannot_connect_message);
        case DIALOG_BILLING_NOT_SUPPORTED_ID:
            return createDialog(R.string.billing_not_supported_title, R.string.billing_not_supported_message);
        default:
            return null;
        }
    }

    private Dialog createDialog(int titleId, int messageId) {
        String helpUrl = replaceLanguageAndRegion(getString(R.string.help_url));
        if (Consts.DEBUG) {
            Log.i(TAG, helpUrl);
        }
        final Uri helpUri = Uri.parse(helpUrl);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleId)
            .setIcon(android.R.drawable.stat_sys_warning)
            .setMessage(messageId)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(R.string.learn_more, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, helpUri);
                    startActivity(intent);
                }
            });
        return builder.create();
    }

    /**
     * Replaces the language and/or country of the device into the given string.
     * The pattern "%lang%" will be replaced by the device's language code and
     * the pattern "%region%" will be replaced with the device's country code.
     *
     * @param str the string to replace the language/country within
     * @return a string containing the local language and region codes
     */
    private String replaceLanguageAndRegion(String str) {
        // Substitute language and or region if present in string
        if (str.contains("%lang%") || str.contains("%region%")) {
            Locale locale = Locale.getDefault();
            str = str.replace("%lang%", locale.getLanguage().toLowerCase());
            str = str.replace("%region%", locale.getCountry().toLowerCase());
        }
        return str;
    }

    /**
     * Sets up the UI.
     */
    private void setupWidgets() {        
        EditTextPreference fleetsystem_intervall = (EditTextPreference)findPreference("fleetsystem_intervall");
        EditText editText = (EditText)fleetsystem_intervall.getEditText();
        editText.setKeyListener(DigitsKeyListener.getInstance(false,true));
        

        mBuyButton = (CheckBoxPreference)findPreference("show_ads");
        mBuyButton.setEnabled(false);
        mBuyButton.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
        	@Override
        	public boolean onPreferenceChange(Preference preference, Object newValue) {
        		boolean newState = (Boolean)newValue;
        		if(newState == true){
        			return true;
        		}
        		if(!hasAdsFree()){
        			createAdsFreeDialog();
        			return false;
        		}else{
        			return true;
        		}
        	}
		});
        
        ((Preference)findPreference("donate")).setOnPreferenceClickListener(new OnPreferenceClickListener() {			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				createDonationDialog();
				return false;
			}
		});
        
        ((Preference)findPreference("donate_paypal")).setOnPreferenceClickListener(new OnPreferenceClickListener() {			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paypalurl));
				startActivity(myIntent);
				return false;
			}
		});
        
        ((Preference)findPreference("about")).setOnPreferenceClickListener(new OnPreferenceClickListener() {			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(abouturl)));
				return false;
			}
		});        
        
        mCatalogAdapter = new CatalogAdapter(this, CATALOG);
        mOwnedItemsCursor = mPurchaseDatabase.queryAllPurchasedItems();
        startManagingCursor(mOwnedItemsCursor);
        
        if(getIntent().hasExtra("donateOnly")){
        	runOnUiThread(new Runnable() {				
				@Override
				public void run() {
		        	getListView().setSelection(10);
		        	getListView().setSelected(true);
					
				}
			});
        	// scroll down
        }
    }

    private void prependLogEntry(CharSequence cs) {
//        SpannableStringBuilder contents = new SpannableStringBuilder(cs);
//        contents.append('\n');
//        contents.append(mLogTextView.getText());
//        mLogTextView.setText(contents);
    }

    private void logProductActivity(String product, String activity) {
        SpannableStringBuilder contents = new SpannableStringBuilder();
        contents.append(Html.fromHtml("<b>" + product + "</b>: "));
        contents.append(activity);
        prependLogEntry(contents);
    }

    /**
     * If the database has not been initialized, we send a
     * RESTORE_TRANSACTIONS request to Android Market to get the list of purchased items
     * for this user. This happens if the application has just been installed
     * or the user wiped data. We do not want to do this on every startup, rather, we want to do
     * only when the database needs to be initialized.
     */
    private void restoreDatabase() {
        SharedPreferences prefs = getSharedPreferences("ogame", MODE_PRIVATE);
        boolean initialized = prefs.getBoolean(DB_INITIALIZED, false);
        if (!initialized) {
            mBillingService.restoreTransactions();
            Toast.makeText(this, R.string.restoring_transactions, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Creates a background thread that reads the database and initializes the
     * set of owned items.
     */
    private void initializeOwnedItems() {
        new Thread(new Runnable() {
            public void run() {
                doInitializeOwnedItems();
            }
        }).start();
    }

    /**
     * Reads the set of purchased items from the database in a background thread
     * and then adds those items to the set of owned items in the main UI
     * thread.
     */
    private void doInitializeOwnedItems() {
        Cursor cursor = mPurchaseDatabase.queryAllPurchasedItems();
        if (cursor == null) {
            return;
        }

        final Set<String> ownedItems = new HashSet<String>();
        try {
            int productIdCol = cursor.getColumnIndexOrThrow(PurchaseDatabase.PURCHASED_PRODUCT_ID_COL);
            while (cursor.moveToNext()) {
                String productId = cursor.getString(productIdCol);
                ownedItems.add(productId);
            }
        } finally {
            cursor.close();
        }

        // We will add the set of owned items in a new Runnable that runs on
        // the UI thread so that we don't need to synchronize access to
        // mOwnedItems.
        mHandler.post(new Runnable() {
            public void run() {
                mOwnedItems.addAll(ownedItems);
                mCatalogAdapter.setOwnedItems(mOwnedItems);
            }
        });
    }

	
    
	public void createDonationDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("How much do you want to donate ?");
		builder.setAdapter(mCatalogAdapter, new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int position) {
				mItemName = CATALOG[position].name;
		        mSku = CATALOG[position].sku;
		        if (Consts.DEBUG) { Log.d(TAG, "buying: " + mItemName + " sku: " + mSku); }
		        if (!mBillingService.requestPurchase(mSku, null)) {
		            showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
		        }				
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void createAdsFreeDialog(){
		mItemName = "Ads Free";
        mSku = "ogame.inapp.adsfree";
        if (Consts.DEBUG) { Log.d(TAG, "buying: " + mItemName + " sku: " + mSku); }
        if (!mBillingService.requestPurchase(mSku, null)) {
            showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
        }
	}
	
	public boolean hasAdsFree(){	
		return mOwnedItems.size() > 0;
	}
    
    public void onClick(DialogInterface dialog, int position) {
        
    }

    /**
     * An adapter used for displaying a catalog of products.  If a product is
     * managed by Android Market and already purchased, then it will be "grayed-out" in
     * the list and not selectable.
     */
    private static class CatalogAdapter extends ArrayAdapter<String> {
        private CatalogEntry[] mCatalog;
        private Set<String> mOwnedItems = new HashSet<String>();

        public CatalogAdapter(Context context, CatalogEntry[] catalog) {
            super(context, R.layout.system_item_donation); 
            mCatalog = catalog;
            for (CatalogEntry element : catalog) {
                add(element.name);
            }
            //setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        public void setOwnedItems(Set<String> ownedItems) {
            mOwnedItems = ownedItems;
            notifyDataSetChanged();
        }
        
        @Override
        public boolean areAllItemsEnabled() {
        	return true;
        }
        
        @Override
        public boolean isEnabled(int position) {
        	return true;
        }
    }
}
