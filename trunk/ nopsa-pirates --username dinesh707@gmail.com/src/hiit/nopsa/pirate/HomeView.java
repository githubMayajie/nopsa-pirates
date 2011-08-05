package hiit.nopsa.pirate;

import java.io.IOException;
import java.util.Date;

//import com.senseg.effect.EffectManager;
//import com.senseg.effect.FeelableSurface;
//import com.senseg.effect.effects.DragAndDropCollection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.SlidingDrawer;
import android.widget.Toast;

public class HomeView extends SurfaceView implements SurfaceHolder.Callback{
	
	private final String TAG = "NOPSA-P";
	private Bitmap home_wall, beam;
	private Activity mainActivity;
	private Intent gameHome;
	private ViewControllerThread thread;
	private boolean redButtonPressed = false;
	private boolean greenButtonPressed = false;
	private boolean doorOpening = false;
	private int door_x = 512;
	private Bitmap day_sea = null, door_left = null, door_right = null;
	//private EffectManager manager;
	private Bitmap color_wheel = null;
	

	public HomeView(Context context, Activity activity) {
		super(context);
		mainActivity = activity;
		getHolder().addCallback(this);
        thread = new ViewControllerThread(getHolder(), this);
        setFocusable(true);
	}
	
	
	protected void onDraw(Canvas canvas){
		if (day_sea==null)
			loadBitmaps();
		//System.gc();
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.WHITE);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		//=========Draw Next Screen Sea 
		Paint sea_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		canvas.drawBitmap(day_sea, 0, 0, sea_paint);
		
		//==========Draw Background Image
		Paint home_wall_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		home_wall_paint.setStyle(Style.FILL);
		canvas.drawBitmap(door_left, 0-door_x, 0, home_wall_paint);
		canvas.drawBitmap(door_right, 0+door_x, 0, home_wall_paint);
	
		
		//=========Draw Laser Beams When button Clicks
		Paint beam_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Paint text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		text_paint.setTextSize(20);
		text_paint.setColor(Color.BLACK);
		if (greenButtonPressed){
			beam = BitmapFactory.decodeResource(getResources(), R.drawable.right_green_beam);
			canvas.drawBitmap(beam, 728, 0, beam_paint);
			beam = BitmapFactory.decodeResource(getResources(), R.drawable.right_bg_mask);
			canvas.drawBitmap(beam, 758, 0, beam_paint);	
		}
		if (redButtonPressed){
			beam = BitmapFactory.decodeResource(getResources(), R.drawable.left_red_beam);
			canvas.drawBitmap(beam, 0, 0, beam_paint);
			beam = BitmapFactory.decodeResource(getResources(), R.drawable.left_bg_mask);
			canvas.drawBitmap(beam, 0, 0, beam_paint);
			//canvas.drawText("          Exit              About", 0, 305, text_paint);
		}
	}
	
	private void loadBitmaps(){
		door_x = 512;
		day_sea = BitmapFactory.decodeResource(getResources(), R.drawable.day_sea);
		door_left = BitmapFactory.decodeResource(getResources(), R.drawable.maindoor_left);
		door_right = BitmapFactory.decodeResource(getResources(), R.drawable.maindoor_right);
		color_wheel  = BitmapFactory.decodeResource(getResources(), R.drawable.color_wheel);
		Log.d(TAG,"AAAAA  IM Insde thread !!!!!!!!!!!!!!!!!");
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG,"CCCC IM Insde thread !!!!!!!!!!!!!!!!!");
				while (door_x>0) {
					Log.d(TAG,"BBB IM Insde thread !!!!!!!!!!!!!!!!!"+door_x);
					door_x = door_x-1;
					android.os.SystemClock.sleep(2);
				}
			}
		}).start();
	}
	
	private Bitmap rotateImage(Bitmap inputImg, int angle){
	     Matrix mat = new Matrix();
	     mat.postRotate(angle);
	     Bitmap bMapRotate = Bitmap.createBitmap(inputImg, 0, 0, inputImg.getWidth(), inputImg.getHeight(), mat, true);
	     return bMapRotate;
	}
	
		
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		Log.d(TAG,"Ontouch Called !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		if (me.getPointerCount()>1){
			PointerCoords pc1 = new PointerCoords();
			me.getPointerCoords(0, pc1);
			PointerCoords pc2 = new PointerCoords();
			me.getPointerCoords(1, pc2);
			if (((pc1.x-512)*(pc2.x-512))<0){
				door_x = cartDist((int)pc1.x, (int)pc1.y, (int)pc2.x, (int)pc2.y)/2;
				//===========HAPTICS=======
				//manager = (EffectManager) mainActivity.getSystemService(mainActivity.EFFECT_SERVICE);
				//DragAndDropCollection mDrag = DragAndDropCollection.load(mainActivity, manager);
				//mDrag.tick.play();	
				//==========END OF HAPTICS
			}
		}
		else{
			// Test Haptics Load from XML
			//manager = (EffectManager) mainActivity.getSystemService(mainActivity.EFFECT_SERVICE);
			//FeelableSurface mSurface = new FeelableSurface(this.getContext(), manager, R.xml.unlock_surface);
			//mSurface.setActive(true);
		    //mSurface.onTouchEvent(me);
			//========== End of test haptics
		}
		
		if ((door_x>150)&&(me.getAction()==MotionEvent.ACTION_UP)){
			while (door_x<512){
				door_x = door_x+1;
				android.os.SystemClock.sleep(5);
			}
			Log.d(TAG, "Game Start");
			thread.setRunning(false);
    		gameHome = new Intent(mainActivity,GameHome.class);
    		mainActivity.startActivity(gameHome);
    		Toast.makeText(mainActivity,"Game Loading ..", Toast.LENGTH_SHORT).show();	
		}
		if (me.getAction() == MotionEvent.ACTION_DOWN){
			if (cartDist(277, 300, (int) me.getX(), (int) me.getY()) < 50){
				redButtonPressed = true;
			}
			if (cartDist(744, 300, (int) me.getX(), (int) me.getY()) < 50){
				greenButtonPressed = true;
			}
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE){
			
			
		}
		if (me.getAction() == MotionEvent.ACTION_UP){
			if (door_x<500)
				door_x = 0;
			if ((greenButtonPressed)&&(cartDist(830, 300, (int) me.getX(), (int)me.getY())<50)){
				Log.d(TAG,"============ HELP PRESSED !! ==================");
				HelpDialog hd = new HelpDialog();
	      	    hd.popInstructionsDialog(mainActivity,0);
			}
			if ((greenButtonPressed)&&(cartDist(940, 300, (int) me.getX(), (int)me.getY())<50)){
				Log.d(TAG,"============ START PRESSED !! ==================");
				greenButtonPressed = false;
				while (door_x<512){
					door_x = door_x+1;
					android.os.SystemClock.sleep(5);
				}
				thread.setRunning(false);
	    		gameHome = new Intent(mainActivity,GameHome.class);
	    		mainActivity.startActivity(gameHome);
	    		Toast.makeText(mainActivity,"Game Loading ..", Toast.LENGTH_SHORT).show();		
			}
			if ((redButtonPressed)&&(cartDist(80, 300, (int) me.getX(), (int)me.getY())<50)){
				Log.d(TAG,"============ EXIT PRESSED !! ==================");
				Log.d(TAG, "Exit Game");
				GameStatus.getGameStatusObject().saveGameData(mainActivity);
				mainActivity.finish();
			}
			if ((redButtonPressed)&&(cartDist(190, 300, (int) me.getX(), (int)me.getY())<50)){
				Log.d(TAG,"============ About PRESSED !! ==================");
			}

			redButtonPressed = false;
			greenButtonPressed = false;							
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
		door_x = 0;
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
	    day_sea = null;
		door_left = null;
		door_right = null;
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
