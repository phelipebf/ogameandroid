package com.overkill.ogame.game;

/*
 * File: WordsAdapter.java
 * Platform: Android 1.5
 * Last update: 24.01.2010 
 * ©2010 OV3RK1LL 
 */
import java.util.ArrayList;
import com.overkill.ogame.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BuildObjectAdapter extends ArrayAdapter<BuildObject> {

	private Context context;
	private int textViewResourceId;

 	public BuildObjectAdapter(Context context, int textViewResourceId, ArrayList<BuildObject> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
//		this.sort(new Comparator<BuildObject>() {
//		public int compare(BuildObject object1, BuildObject object2) {
//			return object1.getName().compareTo(object2.getName());		
//		}});
	}
 	
 	public int getObjectWithCountdown(){
 		for(int i = 0; i < this.getCount(); i++){
 			if(this.getItem(i).getTimeLeft() > 0)
 				return i;
 		}
 		return -1;
 	}
 	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(this.textViewResourceId, parent, false);
		}
		BuildObject b = this.getItem(position);
		if (b != null) {
			if(b.getStatus().equals("disabled"))
				((RelativeLayout)v.findViewById(R.id.root)).setBackgroundColor(Color.argb(100, 255, 230, 0));
			else if(b.getStatus().equals("off"))
				((RelativeLayout)v.findViewById(R.id.root)).setBackgroundColor(Color.argb(100, 255, 0, 0));
			else
				((RelativeLayout)v.findViewById(R.id.root)).setBackgroundColor(Color.argb(100, 0, 0, 0));
			
			if (b.getLevel() >= 0){
				((TextView) v.findViewById(R.id.name)).setText(b.getName());
				
				if(b.getDisplayType() != BuildObject.DISPLAY_TYPE_HIDE_LEVEL){
					((TextView) v.findViewById(R.id.name)).append(" (");
					
					if(Tools.getCuetypeById(b.getId()) == Item.CUETYPE_MULTIPLE)
						((TextView) v.findViewById(R.id.name)).append(this.context.getString(R.string.object_amount));
					else
						((TextView) v.findViewById(R.id.name)).append(this.context.getString(R.string.object_level));
				
					((TextView) v.findViewById(R.id.name)).append(" " + b.getLevel() + ")");
				}
			}else{
				((TextView) v.findViewById(R.id.name)).setText(b.getName());
			}
			if(b.getIcon() == 0){
				((ImageView) v.findViewById(R.id.image)).setVisibility(View.GONE);
			}else{
				((ImageView) v.findViewById(R.id.image)).setImageResource(b.getIcon());
				((ImageView) v.findViewById(R.id.image)).setVisibility(View.VISIBLE);
			}
				
			TextView metall = (TextView) v.findViewById(R.id.metall);
			TextView kristal = (TextView) v.findViewById(R.id.kristal);
			TextView deuterium = (TextView) v.findViewById(R.id.deuterium);
			TextView energy = (TextView) v.findViewById(R.id.energy);
			TextView percent = (TextView) v.findViewById(R.id.percent);
			
			if(b.getTimeLeft() > 0){
				((ImageView) v.findViewById(R.id.wrench)).setVisibility(View.VISIBLE);
			}else{
				((ImageView) v.findViewById(R.id.wrench)).setVisibility(View.GONE);
			}	
			
			if(b.getMetall() == 0){
				metall.setVisibility(View.GONE);
				((ImageView) v.findViewById(R.id.image_metall)).setVisibility(View.GONE);				
			}else{
				metall.setVisibility(View.VISIBLE);
				((ImageView) v.findViewById(R.id.image_metall)).setVisibility(View.VISIBLE);	
				metall.setText(String.valueOf(b.getMetall()));
				if (b.hasMetal())
					metall.setTextColor(Color.GREEN);
				else
					metall.setTextColor(Color.RED);	
			}	
			
			if(b.getCrystal() == 0){
				kristal.setVisibility(View.GONE);
				((ImageView) v.findViewById(R.id.image_kristal)).setVisibility(View.GONE);				
			}else{
				kristal.setVisibility(View.VISIBLE);
				((ImageView) v.findViewById(R.id.image_kristal)).setVisibility(View.VISIBLE);	
				kristal.setText(String.valueOf(b.getCrystal()));
				if (b.hasCrystal())
					kristal.setTextColor(Color.GREEN);
				else
					kristal.setTextColor(Color.RED);	
			}	
			
			if(b.getDeuterium() == 0){
				deuterium.setVisibility(View.GONE);
				((ImageView) v.findViewById(R.id.image_deuterium)).setVisibility(View.GONE);				
			}else{
				deuterium.setVisibility(View.VISIBLE);
				((ImageView) v.findViewById(R.id.image_deuterium)).setVisibility(View.VISIBLE);	
				deuterium.setText(String.valueOf(b.getDeuterium()));
				if (b.hasDeuterium())
					deuterium.setTextColor(Color.GREEN);
				else
					deuterium.setTextColor(Color.RED);
			}	
			
			if(b.getEnergy() == 0 && b.getDisplayType() == BuildObject.DISPLAY_TYPE_VALUE){
				energy.setVisibility(View.GONE);
				((ImageView) v.findViewById(R.id.image_energy)).setVisibility(View.GONE);				
			}else{
				energy.setVisibility(View.VISIBLE);
				((ImageView) v.findViewById(R.id.image_energy)).setVisibility(View.VISIBLE);	
				energy.setText(String.valueOf(b.getEnergy()));
				if (b.getEnergy() > 0)
					energy.setTextColor(Color.GREEN);
				else
					energy.setTextColor(Color.RED);
			}	
			if(b.getEnergyMax() != 0){
				energy.append("/" + String.valueOf(b.getEnergyMax()));
			}
			
			if(b.getPercent() == 0 && (b.getDisplayType() == BuildObject.DISPLAY_TYPE_VALUE || b.getDisplayType() == BuildObject.DISPLAY_TYPE_HIDE_LEVEL)){
				percent.setVisibility(View.GONE);
				((ImageView) v.findViewById(R.id.image_percent)).setVisibility(View.GONE);				
			}else{
				percent.setVisibility(View.VISIBLE);
				((ImageView) v.findViewById(R.id.image_percent)).setVisibility(View.VISIBLE);	
				percent.setText(String.valueOf(b.getPercent()));
				//percent.setTextColor(Color.RED);
			}
		}
		return v;
	}
}
