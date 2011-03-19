package com.overkill.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Sprite {
	private Bitmap sprite;
	private Rect sRectangle = new Rect();
	private int count;
	private int rows;
	private String[] row_names;
	
	private int spriteWidth;
	private int spriteHeight;
	
	
	public Sprite(Drawable image, int count){
		this.count = count - 1;
		this.rows = 1;
		this.sprite = ((BitmapDrawable)image).getBitmap();
		this.spriteWidth = this.sprite.getWidth() / this.count;
		this.spriteHeight = this.sprite.getHeight() / this.rows;
		sRectangle.top = 0;
		sRectangle.bottom = this.spriteHeight;
		sRectangle.left = 0;
		sRectangle.right = this.spriteWidth;
	}
	
	public Sprite(Drawable image, int count, String[] row_names){
		this.count = count - 1;
		this.rows = row_names.length;
		this.sprite = ((BitmapDrawable)image).getBitmap();
		this.spriteWidth = this.sprite.getWidth() / this.count;
		this.spriteHeight = this.sprite.getHeight() / this.rows;
		//Log.i(TAG, "new: " + count + "x" + rows + " - " + spriteWidth + "x" + spriteHeight);
		sRectangle.top = 0;
		sRectangle.bottom = this.spriteHeight;
		sRectangle.left = 0;
		sRectangle.right = this.spriteWidth;
		this.row_names = row_names;
	}
	
	private int indexOfState(String state){
		for(int i=0; i<this.row_names.length; i++){
			if(this.row_names[i].equals(state))
				return i;
		}
		return 0;
	}
	
	private void moveRect(int x, int y){
		sRectangle.left = x * spriteWidth;
		sRectangle.right = sRectangle.left + spriteWidth;
		sRectangle.top = y * spriteHeight;
		sRectangle.bottom = sRectangle.top + spriteHeight;
	}
	
	public Drawable get(int index){
		if(index > count){
			throw new IndexOutOfBoundsException("The given sprite only contains " + (count + 1) + " Elements (0-" + count + ")");
		}
		Bitmap myImage = Bitmap.createBitmap(this.spriteWidth, this.spriteHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(myImage);
		moveRect(index, 0);
		Rect dest = new Rect(0, 0, spriteWidth, spriteHeight);
		canvas.drawBitmap(sprite, sRectangle, dest, null);
		return (new BitmapDrawable(myImage));		
	}
	
	public Drawable get(int index, String state){
		if(index > count){
			throw new IndexOutOfBoundsException("The given sprite only contains " + (count + 1) + " Elements (0-" + count + ")");
		}
		Bitmap myImage = Bitmap.createBitmap(this.spriteWidth, this.spriteHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(myImage);
		moveRect(index, indexOfState(state));
		Rect dest = new Rect(0, 0, spriteWidth, spriteHeight);
		canvas.drawBitmap(sprite, sRectangle, dest, null);
		return (new BitmapDrawable(myImage));			
	}
}
