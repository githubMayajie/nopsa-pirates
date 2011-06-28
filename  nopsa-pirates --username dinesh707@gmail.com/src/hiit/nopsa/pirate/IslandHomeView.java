package hiit.nopsa.pirate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class IslandHomeView extends View {

	private Bitmap sea = null;
	private Bitmap ship = null;
	private Bitmap icons,plus_icon;
	private final String TAG = "NOPSA-P";
	private Activity islandHomeActivity;
	private Intent collectItems;
	
	public IslandHomeView(Context context, Activity activity) {
		super(context);
		islandHomeActivity = activity;
		infoDialog();
		// TODO Auto-generated constructor stub
	}
	
	protected void onDraw(Canvas canvas){
		if (sea==null)
			loadBitmaps();
		//==========Draw Sea & Draw Ship
		Paint sea_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		sea_paint.setStyle(Style.FILL);
		canvas.drawBitmap(sea, 0, 0, sea_paint);
		canvas.drawBitmap(ship, 0, 0, sea_paint);		
		
		//==========Draw Animal, Slaves, and Food Icons & BACK icon
		Paint icon_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		icon_paint.setStyle(Style.FILL);
		plus_icon = BitmapFactory.decodeResource(getResources(), R.drawable.collect_icon);
		
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.ship_wheel);
		canvas.drawBitmap(icons, 904, 480, icon_paint);
		
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.animal_icon);
		canvas.drawBitmap(icons, 20, 20, icon_paint);
		canvas.drawBitmap(plus_icon, 80, 80, icon_paint);		
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.slave_icon);
		canvas.drawBitmap(icons, 20, 140, icon_paint);
		canvas.drawBitmap(plus_icon, 80, 200, icon_paint);
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.food_icon);
		canvas.drawBitmap(icons, 20, 260, icon_paint);
		canvas.drawBitmap(plus_icon, 80, 320, icon_paint);	
	}
	
	private void loadBitmaps(){
		System.gc();
		sea = BitmapFactory.decodeResource(getResources(), R.drawable.sea_island);
		ship = BitmapFactory.decodeResource(getResources(), R.drawable.ship_look);
	}
	
	private void infoDialog(){
		InstructionDialog id = new InstructionDialog();
		String title = "Avast! You found a deserted island...";
		String text = "Now its time to catch some animals, collect some food and capture slaves. Remember that" +
				" when you have more animals and more slaves they need more food.. ";
		id.popInstructionsDialog(title, text, islandHomeActivity);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			// Touched the screen
			if ((480<me.getY())&&(580>me.getY())&&(1004>me.getX())&&(904<me.getX())){
				// Go Back to HOME Screen
				Log.d(TAG, "Back to Home Screen");
				sea = null;
				ship = null;
				System.gc();
				islandHomeActivity.setResult(1);
				islandHomeActivity.finish();
			}
			
			collectItems = new Intent(islandHomeActivity,CollectItems.class);
			if ((20<me.getY())&&(120>me.getY())&&(120>me.getX())&&(20<me.getX())){
				// TODO Catch Animals Implementation
				collectItems.putExtra("type", 0);
	    		islandHomeActivity.startActivity(collectItems);
			}
			if ((140<me.getY())&&(240>me.getY())&&(120>me.getX())&&(20<me.getX())){
				// TODO Catch Slaves
				collectItems.putExtra("type", 1);
	    		islandHomeActivity.startActivity(collectItems);
			}
			if ((260<me.getY())&&(360>me.getY())&&(120>me.getX())&&(20<me.getX())){
				// TODO Collect Food
				collectItems.putExtra("type", 2);
	    		islandHomeActivity.startActivity(collectItems);
			}
			
		}
		return true;
	}
	

}
