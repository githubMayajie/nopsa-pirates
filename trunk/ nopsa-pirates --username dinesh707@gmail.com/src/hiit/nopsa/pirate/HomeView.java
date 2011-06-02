package hiit.nopsa.pirate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class HomeView extends View{
	
	private final String TAG = "NOPSA-P";
	private Bitmap home_wall;
	private Activity mainActivity;
	Intent gameHome;
	
	public HomeView(Context context, Activity activity) {
		super(context);
		mainActivity = activity;
	}
	
	protected void onDraw(Canvas canvas){
		System.gc();
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.WHITE);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		//==========Draw Background Image
		Paint home_wall_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		home_wall_paint.setStyle(Style.FILL);
		home_wall = BitmapFactory.decodeResource(getResources(), R.drawable.home_wall);
		canvas.drawBitmap(home_wall, 0, 0, home_wall_paint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			// Touched the screen
			if (cartDist(291, 88, (int) me.getX(), (int) me.getY()) < 40){
				// Get Into the game
				Log.d(TAG, "Game Start");
	    		gameHome = new Intent(mainActivity,GameHome.class);
	    		mainActivity.startActivity(gameHome);
	    		Toast.makeText(mainActivity,"Game Loading ..", Toast.LENGTH_SHORT).show();
			}
			if (cartDist(167, 283, (int) me.getX(), (int) me.getY()) < 40){
				// Open About
				Log.d(TAG, "Open ABOUT" );
			}
			if (cartDist(247, 503, (int) me.getX(), (int) me.getY()) < 40){
				//TODO
				// Open the hall of fame
				// Better to open a web browser with given like to some place
				// In that web site user will allow to do many other stuff ( MAYE BE )
				// Such as publish to FB
				Log.d(TAG, "Hall of Fame");
			}			
			if (cartDist(968, 544, (int) me.getX(), (int) me.getY()) < 40){
				Log.d(TAG, "Exit");
				mainActivity.finish();
			}				
			return true;

		}
		
		return true;
	}
	
	// This method calculates the cartesian distance between given points.
	// mainly used in finding the touch points in canvas. 
	private int cartDist(int x1, int y1, int x2, int y2){
		 return (int) Math.sqrt((Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
	}
	

}
