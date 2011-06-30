package hiit.nopsa.pirate;

import java.util.Date;

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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class HomeView extends SurfaceView implements SurfaceHolder.Callback{
	
	private final String TAG = "NOPSA-P";
	private Bitmap home_wall;
	private Activity mainActivity;
	private Intent gameHome;
	private ViewControllerThread thread;
	private float glowAlpha;
	private boolean buttonsOnDrag;
	private int _x,_y,_r, _st_x, _st_y, _button_id;
	private boolean screenAlive = false;
	
	public HomeView(Context context, Activity activity) {
		super(context);
		mainActivity = activity;
		getHolder().addCallback(this);
        thread = new ViewControllerThread(getHolder(), this);
        setFocusable(true);
        glowAlpha = 0;
        buttonsOnDrag = false;
        screenAlive = true;
	}
	
	protected void onDraw(Canvas canvas){
		//System.gc();
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.WHITE);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		//==========Draw Background Image
		Paint home_wall_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		home_wall_paint.setStyle(Style.FILL);
		home_wall = BitmapFactory.decodeResource(getResources(), R.drawable.home_wall);
		canvas.drawBitmap(home_wall, 0, 0, home_wall_paint);
		
		
		//==========Draw Glowing Buttons
		Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
		glow.setColor(Color.WHITE);
		//glow.setAlpha((int) Math.abs(((Math.sin((glowAlpha*Math.PI)/180))*50)));
		glow.setAlpha((int)glowAlpha);
		if (buttonsOnDrag){
			canvas.drawCircle(291, 88, 36, glow);
			canvas.drawCircle(168, 285, 36, glow);
			canvas.drawCircle(247, 503, 36, glow);
			canvas.drawCircle(968, 547, 36, glow);
			canvas.drawCircle(_x, _y, _r, glow);	
		}
		//==========Draw Dragging Buttons
		if (!buttonsOnDrag){
			Paint skull_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			//skull_paint.setStyle(Style.FILL);
			skull_paint.setAlpha((int)glowAlpha);
			Bitmap skull = BitmapFactory.decodeResource(getResources(), R.drawable.glow_skel);
			canvas.drawBitmap(skull, 284,73, skull_paint);
		}
	}
	
	//TODO
	private void buttonGlower(){
		new Thread(new Runnable() {
			public void run() {
				while(screenAlive){
					android.os.SystemClock.sleep(40); 
						Date d = new Date();
						glowAlpha = (((float) Math.sin((d.getTime()/10)*0.0174532925))*120)+120;
				}
			}
		}).start();
	}
	
		
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			if (cartDist(291, 88, (int) me.getX(), (int) me.getY()) < 40){
				// Play game button
				_st_x = 291;
				_st_y = 88;
				_button_id = 1;
				//glowAlpha = 100;
				buttonsOnDrag = true;
			}
			if (cartDist(167, 283, (int) me.getX(), (int) me.getY()) < 40){
				// About Button
				_st_x = 167;
				_st_y = 283;
				_button_id = 2;
				//glowAlpha = 100;
				buttonsOnDrag = true;
			}
			if (cartDist(247, 503, (int) me.getX(), (int) me.getY()) < 40){
				// Hall of Fame Button
				_st_x = 247;
				_st_y = 503;
				_button_id = 3;
				//glowAlpha = 100;
				buttonsOnDrag = true;
			}			
			if (cartDist(968, 544, (int) me.getX(), (int) me.getY()) < 40){
				_st_x = 968;
				_st_y = 544;
				_button_id = 4;
				//glowAlpha = 100;
				buttonsOnDrag = true;				
			}				
		}
		if (me.getAction() == MotionEvent.ACTION_UP){
			buttonsOnDrag = false;
			synchronized (this) {
			if (cartDist(515, 303, (int) me.getX(), (int) me.getY()) < 200){
				if (_button_id==1){
					screenAlive = false;
					Log.d(TAG, "Game Start");
					thread.setRunning(false);
		    		gameHome = new Intent(mainActivity,GameHome.class);
		    		mainActivity.startActivity(gameHome);
		    		Toast.makeText(mainActivity,"Game Loading ..", Toast.LENGTH_SHORT).show();					
				}
				if (_button_id==2){
					Log.d(TAG, "Open About Box");
					//TODO
					// Open About Box
				}
				if (_button_id==3){
					Log.d(TAG, "Open Hall of Fame");
					//TODO
					// Open the hall of fame
					// Better to open a web browser with given like to some place
					// In that web site user will allow to do many other stuff ( MAYE BE )
					// Such as publish to FB
				}
				if (_button_id==4){
					screenAlive = false;
					Log.d(TAG, "Exit Game");
					mainActivity.finish();
				}
			}
			}
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE){
			//glowAlpha = 100;
			_x = (int) me.getX();
			_y = (int) me.getY();
			_r = Math.max(36, 200-((cartDist(_x, _y, 515, 303)*200)/cartDist(_st_x, _st_y, 515, 303)));
		}
		return true;
	}
	
	// This method calculates the cartesian distance between given points.
	// mainly used in finding the touch points in canvas. 
	private int cartDist(int x1, int y1, int x2, int y2){
		 return (int) Math.sqrt((Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new ViewControllerThread(getHolder(), this);
		thread.setRunning(true);
		Log.d(TAG,"Thread is Aline ==>>"+thread.isAlive());
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG,"surface distroyed called");
		boolean retry = true;
		    thread.setRunning(false);
		    while (retry) {
		        try {
		            thread.join();
		            retry = false;
		        } catch (InterruptedException e) {}
		    }
		    Log.d(TAG,"surface distroyed #####");
	}
	
    class ViewControllerThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private HomeView homeView;
        private boolean run = false;
     
        public ViewControllerThread(SurfaceHolder sh, HomeView hv) {
            surfaceHolder = sh;
            homeView = hv;
        }
     
        public void setRunning(boolean r) {
        	if (r){
        		Log.d(TAG,"Button Glower Called");
        		buttonGlower();
        	}
            run = r;
        }
        
        public SurfaceHolder getSurfaceHolder(){
        	return surfaceHolder;
        }
     
        @Override
        public void run() {
        	Log.d(TAG,"THREAD STARTED");
        	Canvas c;
    	    while (run) {
    	        c = null;
    	        try {
    	            c = surfaceHolder.lockCanvas(null);
    	            synchronized (surfaceHolder) {
    	            	//homeView.do_What_Ever_Changers_To_Parameters()
    	                homeView.onDraw(c);
    	            }
    	        } finally {
    	            if (c != null) {
    	                surfaceHolder.unlockCanvasAndPost(c);
    	            }
    	        }
    	    }     
    	    Log.d(TAG,"THREAD TERMINATED !!");
        }
    }// End of Inner Class "ViewControllerThread"

}
