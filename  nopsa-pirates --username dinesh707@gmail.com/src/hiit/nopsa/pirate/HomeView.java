package hiit.nopsa.pirate;


import com.senseg.effect.EffectManager;
import com.senseg.effect.FeelableSurface;

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
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * 
 * 
 * @author Dinesh Wijekoon
 */
public class HomeView extends SurfaceView implements SurfaceHolder.Callback{
	
	private final String TAG = "NOPSA-P";
	private Activity mainActivity;
	private MainActivity mActivity;
	private Intent gameHome;
	private ViewControllerThread thread;
	private boolean redButtonPressed = false;
	private boolean greenButtonPressed = false;
	private int door_x = 512;
	private Bitmap day_sea = null, door_left = null, door_right = null, glow_red=null, glow_green=null, ship, button, menu, beam;
	private EffectManager manager;
	private FeelableSurface mSurface_1,mSurface_2,mSurface_3,mSurface_4,mSurface_5;
	private boolean red_a=false,red_b=false,green_a=false,green_b=false;

	public HomeView(Context context, Activity activity) {
		super(context);
		mainActivity = activity;
		mActivity = (MainActivity) activity;
		getHolder().addCallback(this);
        thread = new ViewControllerThread(getHolder(), this);
        setFocusable(true);
	}
	
	
	protected void onDraw(Canvas canvas){
		if (day_sea==null)
			loadBitmaps();
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.WHITE);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		//=========Draw Next Screen Sea 
		Paint sea_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		canvas.drawBitmap(day_sea, 0, 0, sea_paint);
		canvas.drawBitmap(ship, 0, 0, sea_paint);
		canvas.drawBitmap(button, 416,314, sea_paint);
		
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
		}
		
		//=========Draw Selection When finger goes on top of them
		if (red_a)
			canvas.drawBitmap(glow_red, -20, 200, beam_paint);
		if (red_b)
			canvas.drawBitmap(glow_red, 90, 200, beam_paint);
		if (green_a)
			canvas.drawBitmap(glow_green, 730, 200, beam_paint);
		if (green_b)
			canvas.drawBitmap(glow_green, 840, 200, beam_paint);
		
		//=========Draw Settings Icon
		canvas.drawBitmap(menu, 10, 550,sea_paint);
	}
	
	private void loadBitmaps(){
		door_x = 512;
		day_sea = BitmapFactory.decodeResource(getResources(), R.drawable.day_sea);
		door_left = BitmapFactory.decodeResource(getResources(), R.drawable.maindoor_left);
		door_right = BitmapFactory.decodeResource(getResources(), R.drawable.maindoor_right);
		glow_green = BitmapFactory.decodeResource(getResources(), R.drawable.glow_green);
		glow_red = BitmapFactory.decodeResource(getResources(), R.drawable.glow_red);
		ship = BitmapFactory.decodeResource(getResources(), R.drawable.ship_look);
		button = BitmapFactory.decodeResource(getResources(), R.drawable.center_button);
		menu = BitmapFactory.decodeResource(getResources(), R.drawable.menu_icon);
		playDoorCloseSound();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (door_x>0) {
					door_x = door_x-1;
					android.os.SystemClock.sleep(2);
				}
			}
		}).start();
		//===Loading Effect 
		manager = (EffectManager) mainActivity.getSystemService(mainActivity.EFFECT_SERVICE);
		mSurface_1 = new FeelableSurface(this.getContext(), manager, R.xml.a_circular_menu_buttons_green_red);
		mSurface_2 = new FeelableSurface(this.getContext(), manager, R.xml.b_door_open_stretch);
		mSurface_3 = new FeelableSurface(this.getContext(), manager, R.xml.c_door_line);
		mSurface_4 = new FeelableSurface(this.getContext(), manager, R.xml.d_menu_bar_slide);
		mSurface_5 = new FeelableSurface(this.getContext(), manager, R.xml.e_menu_buttons_finger);
	}
	
	/*
	private Bitmap rotateImage(Bitmap inputImg, int angle){
	     Matrix mat = new Matrix();
	     mat.postRotate(angle);
	     Bitmap bMapRotate = Bitmap.createBitmap(inputImg, 0, 0, inputImg.getWidth(), inputImg.getHeight(), mat, true);
	     return bMapRotate;
	}*/
	
	private void playSoundsOnBar(){
		/*
		if (GameStatus.getGameStatusObject().isSounds()){
			//==Start Plaing sound when player takes finger to menu items
			new Thread(new Runnable() {
				public void run() {
					try{
						MediaPlayer mPlayer2 = MediaPlayer.create(mainActivity, R.raw.screen1_barelements_menuover);
						mPlayer2.start();
						while(mPlayer2.isPlaying()){
							android.os.SystemClock.sleep(100);
						}
					}catch(Exception e){
						Log.d(TAG,"ERROR PLAYING");
						e.printStackTrace();
					}
				}}).start();				
			//== End of playing sound
		}
		*/
	}
	
	private void playDoorOpenSound(){
		if (GameStatus.getGameStatusObject().isSounds()){
			//==Start Plaing sound for door opening
			new Thread(new Runnable() {
				public void run() {
					try{
						MediaPlayer mPlayer =  MediaPlayer.create(mainActivity, R.raw.door_opens_ample);
						//mActivity.mPlayer.setLooping(true);
						mPlayer.start();
						while(mPlayer.isPlaying()){
							android.os.SystemClock.sleep(100);
						}
					}catch(Exception e){
						Log.d(TAG,"ERROR PLAYING");
						e.printStackTrace();
					}
				}}).start();				
			//== End of playing sound			
		}
	}
	
	private void playButtonClickSound(){
		if (GameStatus.getGameStatusObject().isSounds()){
			//==Start Plaing sound red button click
			new Thread(new Runnable() {
				public void run() {
					try{
						MediaPlayer mPlayer2 = MediaPlayer.create(mainActivity, R.raw.redgreen_butn_blip);
						mPlayer2.start();
						while(mPlayer2.isPlaying()){
							android.os.SystemClock.sleep(100);
						}
					}catch(Exception e){
						Log.d(TAG,"ERROR PLAYING");
						e.printStackTrace();
					}
				}}).start();				
			//== End of playing sound
		}
	}
	
	private void playDoorCloseSound(){
		if (GameStatus.getGameStatusObject().isSounds()){
			//==Start Plaing sound for door opening
			new Thread(new Runnable() {
				public void run() {
					try{
						MediaPlayer mPlayer = MediaPlayer.create(mainActivity, R.raw.door_close_sample);
						//mActivity.mPlayer.setLooping(true);
						mPlayer.start();
						while(mPlayer.isPlaying()){
							android.os.SystemClock.sleep(100);
						}
					}catch(Exception e){
						Log.d(TAG,"ERROR PLAYING");
						e.printStackTrace();
					}
				}}).start();				
			//== End of playing sound
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if ((me.getAction()==MotionEvent.ACTION_DOWN)&((me.getX()<60)&&(540<me.getY()))){
			//Open Options menu
			mainActivity.openOptionsMenu();
		}
		
		if((500<(me.getX())&&(me.getX()<524))){
			//3. door line..this is the line between the 2 doors when its shut..a feeling as you pass finger over it
			if (GameStatus.getGameStatusObject().isHaptics()){
				Log.d(TAG,"Haptic Feeling Type 3");
				mSurface_3.setActive(true);
			    mSurface_3.onTouchEvent(me);
			}
		}
					
		if (redButtonPressed||greenButtonPressed)
			if((me.getY()<330)&&(270<me.getY())){
				try{
					//4. menu bar slide�as finger slides over the menu bar
					if (GameStatus.getGameStatusObject().isHaptics()){
						Log.d(TAG,"Haptic Feeling Type 4");
						mSurface_4.setActive(true);
					    mSurface_4.onTouchEvent(me);
					}
				}catch(NullPointerException ne){}
			}
		if (me.getPointerCount()>1){
			PointerCoords pc1 = new PointerCoords();
			me.getPointerCoords(0, pc1);
			PointerCoords pc2 = new PointerCoords();
			me.getPointerCoords(1, pc2);
			if (((pc1.x-512)*(pc2.x-512))<0){
				door_x = cartDist((int)pc1.x, (int)pc1.y, (int)pc2.x, (int)pc2.y)/2;
				if (door_x<125){
					//2. door open stretch..as you opn the door, the stretchy feeling
					if (GameStatus.getGameStatusObject().isHaptics()){
						Log.d(TAG,"Haptic Feeling Type 2");
						mSurface_2.setActive(true);
					    mSurface_2.onTouchEvent(me);
					}
				}
			}
		}
		
		if ((door_x>120)&&(me.getAction()==MotionEvent.ACTION_UP)){
			playDoorOpenSound();		
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
				playButtonClickSound();
			}
			if (cartDist(744, 300, (int) me.getX(), (int) me.getY()) < 50){
				greenButtonPressed = true;
				playButtonClickSound();
			}
		}
		
		if (me.getAction() == MotionEvent.ACTION_MOVE){
			red_a = false;
			red_b = false;
			green_a = false;
			green_b = false;
			if (cartDist(277, 300, (int) me.getX(), (int) me.getY()) < 50){
				// Reached Red Button Area
				//1. circular menu, just when finger moves over those red and green buttons.
				if (GameStatus.getGameStatusObject().isHaptics()){
					Log.d(TAG,"Haptic Feeling Type 1");
					mSurface_1.setActive(true);
				    mSurface_1.onTouchEvent(me);
				}
			}
			if (cartDist(744, 300, (int) me.getX(), (int) me.getY()) < 50){
				// Reached Green Button Area
				//1. circular menu, just when finger moves over those red and green buttons.
				if (GameStatus.getGameStatusObject().isHaptics()){
					Log.d(TAG,"Haptic Feeling Type 1");
					mSurface_1.setActive(true);
					mSurface_1.onTouchEvent(me);				
				}
			}
			if ((greenButtonPressed)&&(cartDist(830, 300, (int) me.getX(), (int)me.getY())<50)){
				// 5. menu bar button finger..as your finger goes over info / about etc..
				playSoundsOnBar();
				if (GameStatus.getGameStatusObject().isHaptics()){
					Log.d(TAG,"Haptic Feeling Type 5");
					mSurface_5.setActive(true);
				    mSurface_5.onTouchEvent(me);	
				}
			    green_a = true;
			}
			if ((greenButtonPressed)&&(cartDist(940, 300, (int) me.getX(), (int)me.getY())<50)){
				// 5. menu bar button finger..as your finger goes over info / about etc..
				playSoundsOnBar();
				if (GameStatus.getGameStatusObject().isHaptics()){
					Log.d(TAG,"Haptic Feeling Type 5");
					mSurface_5.setActive(true);
				    mSurface_5.onTouchEvent(me);	
				}
			    green_b = true;
			}
			if ((redButtonPressed)&&(cartDist(80, 300, (int) me.getX(), (int)me.getY())<50)){
				// 5. menu bar button finger..as your finger goes over info / about etc..
				playSoundsOnBar();
				if (GameStatus.getGameStatusObject().isHaptics()){
					Log.d(TAG,"Haptic Feeling Type 5");
					mSurface_5.setActive(true);
				    mSurface_5.onTouchEvent(me);
				}
				red_a = true;
			}
			if ((redButtonPressed)&&(cartDist(190, 300, (int) me.getX(), (int)me.getY())<50)){
				// 5. menu bar button finger..as your finger goes over info / about etc..
				playSoundsOnBar();
				if (GameStatus.getGameStatusObject().isHaptics()){
					Log.d(TAG,"Haptic Feeling Type 5");
					mSurface_5.setActive(true);
				    mSurface_5.onTouchEvent(me);
				}
				red_b=true;
			}
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
				green_b = false;
				greenButtonPressed = false;
				playDoorOpenSound();
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
				About about = new About();
				about.popAboutDialog(mainActivity);
			}
			red_a = false;
			red_b = false;
			green_a = false;
			green_b = false;
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
