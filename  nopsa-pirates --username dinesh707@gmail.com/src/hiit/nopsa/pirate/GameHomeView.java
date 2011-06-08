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
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameHomeView extends SurfaceView implements SurfaceHolder.Callback{

	private Bitmap sea1 = null;
	private Bitmap ship, icons;
	private Activity gameHomeActivity;
	private final String TAG = "NOPSA-P";
	private GameStatus gameStatus;
	private Intent islandHome;
	private Intent populateItems;
	private InstructionDialog id;
	private ViewControllerThread _thread;
	private boolean activityIsOnTop = true; 
	private boolean showMenuButtons = false;
	private int glowValue = 0;
	private int angle;
	
	private int selectedKey;
	private final int FOOD = 1;
	private final int SLAVE = 2;
	private final int ANIMAL = 3;
	private final int EXIT = 4;
	private final int MARKET = 5;
	int selected_x,selected_y;
	
	public GameHomeView(Context context, Activity activity) {
		super(context);
		gameHomeActivity = activity;
		gameStatus = GameStatus.getGameStatusObject();
		gameStatus.setGameOn(false);
		getHolder().addCallback(this);
		_thread = new ViewControllerThread(getHolder(), this);
		setFocusable(true);
	}

	protected void onDraw(Canvas canvas){
		if (sea1 == null){
			loadBitmaps();
		}
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		try{
			//Log.d(TAG,"DRAWSKY onDraw() Called");
			//==========Draw Sea & Sky & Draw Ship
			Paint sea_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			sea_paint.setStyle(Style.FILL);
			canvas.drawBitmap(sea1, 0, 0, sea_paint);
			//Paint ship_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			//ship_paint.setStyle(Style.FILL);
			canvas.drawBitmap(ship, 0, 0, sea_paint);
		}
		catch (NullPointerException ne) {
			Log.d(TAG,"sea1 or ship Bitmaps are NULL");
		}
		
		Paint button_glow = new Paint(Paint.ANTI_ALIAS_FLAG);
		button_glow.setColor(Color.WHITE);
		button_glow.setAlpha(glowValue);
		canvas.drawCircle(501, 399, 23, button_glow);
		
		Paint menu_pop_button_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
 		menu_pop_button_paint.setColor(Color.rgb(204, 153, 51));
		canvas.drawCircle(501, 399, 18, menu_pop_button_paint);
		
		if (showMenuButtons){
			//============ Show Selected Button
			if (selectedKey>0){
				Paint button_select = new Paint(Paint.ANTI_ALIAS_FLAG);
				button_select.setColor(Color.WHITE);
				button_select.setAlpha(150);
				switch (selectedKey) {
				case ANIMAL:
					selected_x = 300;
					selected_y = 250;
					break;
				case FOOD:
					selected_x = 700;
					selected_y = 250;
					break;
				case SLAVE:
					selected_x = 500;
					selected_y = 150;
					break;
				case EXIT:
					selected_x = 954;
					selected_y = 530;
					break;
				case MARKET:
					selected_x = 70;
					selected_y = 530;
					break;
				default:
					break;
				}
				canvas.drawCircle(selected_x, selected_y, 100, button_select);
			}
			//==========Draw Animal, Slaves, and Food Icons & BACK icon
			Paint icon_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			icon_paint.setStyle(Style.FILL);			
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.animal_icon);
			canvas.drawBitmap(icons, 250, 200, icon_paint);
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.slave_icon);
			canvas.drawBitmap(icons, 450, 100, icon_paint);
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.food_icon);
			canvas.drawBitmap(icons, 650, 200, icon_paint);

			icons = BitmapFactory.decodeResource(getResources(), R.drawable.back_icon);
			canvas.drawBitmap(icons, 904, 480, icon_paint);
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.coins_icon);
			canvas.drawBitmap(icons,20,480 , icon_paint);			
		}
		
		//=========Write(Draw) Text (Ship Class & etc..)
		Paint text_paint = new Paint();
		text_paint.setColor(Color.BLACK);
		text_paint.setStyle(Style.FILL);
		text_paint.setAntiAlias(true);
		text_paint.setTextSize(20);
		text_paint.setTypeface(Typeface.SANS_SERIF);
		canvas.drawText("Ship Class :"+gameStatus.getShip_class(),800, 50, text_paint);
		canvas.drawText("Weapon Class :"+gameStatus.getWeapon_class(),800, 75, text_paint);
		canvas.drawText("Sails Class :"+gameStatus.getSails_class(),800, 100, text_paint);
		
		canvas.drawText("Crew Size :"+gameStatus.getNum_crew(),800, 140, text_paint);
		canvas.drawText("Coins :"+gameStatus.getCoins(),800, 165, text_paint);
		
		canvas.drawText("Time on Sea :"+(gameStatus.getTimeOfNextIsland()/60)+":"+(gameStatus.getTimeOfNextIsland()%60), 800, 205, text_paint);
		canvas.drawText("Food Left :"+gameStatus.getTotal_food_score(),800,230, text_paint);
	
		if ((!gameStatus.isGameOn()&&(activityIsOnTop))){
			gameStatus.setGameOn(true);	
			//Log.d(TAG,"startGameTimeElapseThread()===== Called from ON_DRAW()");
		    startGameTimeElapseThread();
		}				
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			if (cartDist(501, 399, (int)me.getX(), (int)me.getY())<24){
				showMenuButtons = true;
			}
			selectedKey = 0;
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			angle = getAngle(501, 399, (int)me.getX(), (int)me.getY());
			if ((angle<-1)&&(angle>-60))
				selectedKey = FOOD;
			else if ((angle<-60)&&(angle>-120))
				selectedKey = SLAVE;
			else if ((angle<-120)&&(angle>-179))
				selectedKey = ANIMAL;
			else if ((angle<60)&&(angle>0))
				selectedKey = EXIT;
			else if ((angle<179)&&(angle>120))
				selectedKey = MARKET;
			else
				selectedKey = 0;
		}
		if (me.getAction() == MotionEvent.ACTION_UP){
			if (showMenuButtons){
				showMenuButtons = false;
				populateItems = new Intent(gameHomeActivity,PopulateItems.class);
			
				switch (selectedKey) {
			
				case EXIT:
					// Go Back to HOME Screen
					Log.d(TAG, "Back to Home Screen");
					//Save the state in a file
					GameStatus.getGameStatusObject().saveGameData(gameHomeActivity);
					activityIsOnTop = false;
					gameStatus.setGameOn(false);
					// Stopping the SurfaceView canvas genarator
					_thread.setRunning(false);		
					//Clearing Memory
					sea1 = null;
					ship = null;
					System.gc();
					gameHomeActivity.finish();
					break;
					
				case FOOD:
					// TODO Collect Food
					populateItems.putExtra("type", 2);
		    		gameHomeActivity.startActivity(populateItems);
					break;
					
				case ANIMAL:
					// TODO Catch Animals Implementation
					populateItems.putExtra("type", 0);
		    		gameHomeActivity.startActivity(populateItems);
					break;
					
				case SLAVE:
					// TODO Catch Slaves
					populateItems.putExtra("type", 1);
		    		gameHomeActivity.startActivity(populateItems);
					break;
					
				case MARKET:
					// TODO - MArket Activity
					break;
	
				default:
					break;
				}
			}
		}
		return true;
	}
	
	private int getAngle(int x1, int y1, int x2, int y2){
		double theta = Math.atan2((y2-y1),(x2-x1));
		theta = theta*57.2957795;
		//Log.d(TAG,"THEEEEEETA-->>"+theta);
		return (int) theta;
	}
	private int cartDist(int x1, int y1, int x2, int y2){
		 return (int) Math.sqrt((Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
	}
	
	private synchronized void startGameTimeElapseThread(){
		if (gameStatus.getTimeOfNextIsland() < 1){
			activityIsOnTop = false;
			// Stop Working threads;
			_thread.setRunning(false);		// Stopping the SurfaceView canvas genarator
			gameStatus.setGameOn(false); 	// Stopping the GameElapseThread Creater
			// dismiss the alert box
			try{
				id.dissmissAlert();
				Log.d(TAG,"ALERT DISMISSED");
			}
			catch (NullPointerException ne){
				Log.d(TAG,"NO ALERT TO DISMISS");
			}
			// Clear memory
			sea1 = null;
			ship = null;
			System.gc();
			// Screen Change in to Island Mode
			Log.d(TAG,"STARTING THE INTENT 'gameHomeActivity'..");
			islandHome = new Intent(gameHomeActivity,IslandHome.class);
			gameHomeActivity.startActivityForResult(islandHome,231);															
		}
		if (gameStatus.isGameOn()){
			new Thread(new Runnable() {
				Date d = new Date();
				public void run() {
						gameStatus.setTimeOfNextIsland(gameStatus.getTimeOfNextIsland()-(int)(((d.getTime()-gameStatus.getLastTimeUpdated()))/1000));
						gameStatus.setLastTimeUpdated(d.getTime());
						for (int i=0;i<10;i++){
							glowValue = glowValue+10;
							if (glowValue>100)
								glowValue = 0;
							android.os.SystemClock.sleep(100);
						}
						//Log.d(TAG,"startGameTimeElapseThread()===== Called from THREAD");
						startGameTimeElapseThread();
			    }
			}).start();
		}
	}
	
	private void loadBitmaps(){
		System.gc();
		sea1 = BitmapFactory.decodeResource(getResources(), R.drawable.day_sea);
		ship = BitmapFactory.decodeResource(getResources(), R.drawable.ship_look);
	}
	
	public void gameResumeFromIsland(){
		Date d = new Date();
		gameStatus.setLastTimeUpdated(d.getTime());
		//TODO gameStatus.setTimeOfNextIsland(x)
		// x <- Needs to be random between 2 min to 5 min 
		gameStatus.setTimeOfNextIsland(60);
		_thread = new ViewControllerThread(getHolder(), this);
		_thread.setRunning(true);
		activityIsOnTop = true; // Child Intent terminated and currently this Activity is alive
	}
	
	public void gameResumeFromPopulateItems(){
		_thread = new ViewControllerThread(getHolder(), this);
		_thread.setRunning(true);
		activityIsOnTop = true; // Child Intent terminated and currently this Activity is alive
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		_thread.setRunning(true);
		_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		 boolean retry = true;
		    _thread.setRunning(false);
		    while (retry) {
		        try {
		            _thread.join();
		            retry = false;
		        } catch (InterruptedException e) {
		            // we will try it again and again...
		        }
		    }	
	}
	
    class ViewControllerThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private GameHomeView _gameHomeView;
        private boolean _run = false;
     
        public ViewControllerThread(SurfaceHolder sh, GameHomeView ghv) {
            _surfaceHolder = sh;
            _gameHomeView = ghv;
        }
     
        public void setRunning(boolean run) {
            _run = run;
        }
        
        public SurfaceHolder getSurfaceHolder(){
        	return _surfaceHolder;
        }
     
        @Override
        public void run() {
    	   Canvas c;
    	    while (_run) {
    	        c = null;
    	        try {
    	            c = _surfaceHolder.lockCanvas(null);
    	            synchronized (_surfaceHolder) {
    	                _gameHomeView.onDraw(c);
    	            }
    	        } finally {
    	            // do this in a finally so that if an exception is thrown
    	            // during the above, we don't leave the Surface in an
    	            // inconsistent state
    	            if (c != null) {
    	                _surfaceHolder.unlockCanvasAndPost(c);
    	            }
    	        }
    	    }     
        }
    }
	
}
