package hiit.nopsa.pirate;

import java.util.Date;
import com.senseg.effect.EffectManager;
import com.senseg.effect.FeelableSurface;
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
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * 
 * @author Dinesh Wijekoon
 */
public class IslandHomeView extends SurfaceView implements SurfaceHolder.Callback{

	private final String TAG = "NOPSA-P";
	
	private Bitmap sea1 = null;
	private Bitmap ship, icons, button;
	
	private Activity islandHomeActivity;
	private Intent collectItems;
	
	private InstructionDialog id;
	private ViewControllerThread _thread;
	private boolean activityIsOnTop = true; 
	private boolean showMenuButtons = false;
	private float glowValue = 0;
	private int angle;
	
	private int selectedKey;
	private final int FOOD = 1;
	private final int SLAVE = 2;
	private final int ANIMAL = 3;
	private final int EXIT = 4;
	private final int MARKET = 5;
	int selected_x,selected_y;
	
	private Bitmap plus_icon;
	private GameStatus gameStatus;
	private Paint icon_paint;
	
	private EffectManager manager;
	private FeelableSurface mSurface_drag;
	private FeelableSurface mSurface_mainButton;
	

	public IslandHomeView(Context context, Activity activity) {
		super(context);
		islandHomeActivity = activity;
		getHolder().addCallback(this);
		gameStatus = GameStatus.getGameStatusObject();
		_thread = new ViewControllerThread(getHolder(), this);
		setFocusable(true);
		infoDialog();
	}
	
	protected void onDraw(Canvas canvas){
		if (sea1 == null){
			loadBitmaps();
		    startGameTimeElapseThread();
		}
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		Paint button_glow = new Paint(Paint.ANTI_ALIAS_FLAG);
		button_glow.setAlpha((int) glowValue);
		Bitmap glow_center = BitmapFactory.decodeResource(getResources(), R.drawable.center_button_glow);
		
		try{
			//==========Draw Sea & Sky & Draw Ship
			Paint sea_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			sea_paint.setStyle(Style.FILL);
			canvas.drawBitmap(sea1, 0, 0, sea_paint);
			canvas.drawBitmap(ship, 0, 0, sea_paint);
			canvas.drawBitmap(button, 416,314, sea_paint);
			canvas.drawBitmap(glow_center, 402,302, button_glow);
		}
		catch (NullPointerException ne) {
			Log.d(TAG,"sea1 or ship Bitmaps are NULL");
		}
		Paint icon_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
			icon_paint.setStyle(Style.FILL);			
			plus_icon = BitmapFactory.decodeResource(getResources(), R.drawable.collect_icon);
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.animal_icon);
			canvas.drawBitmap(icons, 250, 200, icon_paint);
			canvas.drawBitmap(plus_icon, 310, 260, icon_paint);	
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.slave_icon);
			canvas.drawBitmap(icons, 450, 100, icon_paint);
			canvas.drawBitmap(plus_icon, 510, 160, icon_paint);	
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.food_icon);
			canvas.drawBitmap(icons, 650, 200, icon_paint);
			canvas.drawBitmap(plus_icon, 710, 260, icon_paint);	
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.back_icon);
			canvas.drawBitmap(icons, 904, 480, icon_paint);
			icons = BitmapFactory.decodeResource(getResources(), R.drawable.coins_icon);
			canvas.drawBitmap(icons,20,480 , icon_paint);			
		}
		
		//============Draw Game Status BOX========
		int food_t;
		Paint text_paint = new Paint();
		text_paint.setColor(Color.BLACK);
		text_paint.setStyle(Style.FILL);
		text_paint.setAntiAlias(true);
		text_paint.setTypeface(Typeface.SANS_SERIF);		
		Paint glow_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		text_paint.setTextSize(15);
		text_paint.setColor(Color.BLACK);
		//canvas.drawRect(600,480,880,580, glow_paint);
		Bitmap backPaper = BitmapFactory.decodeResource(getResources(), R.drawable.game_status_back);
		canvas.drawBitmap(backPaper, 680, 5, icon_paint);
		//glow_paint.setColor(Color.YELLOW);//glow_paint.setColor(Color.rgb(255-(temp_t/600)*255, (temp_t/600)*255, 0));
		canvas.drawText("Hurry up Sailor! Get back to sea ..", 710, 55, text_paint);
		if ((gameStatus.getNum_animals()+gameStatus.getNum_slaves()*2+gameStatus.getNum_crew()*5)==0)
			food_t = 25;
		else{
			food_t = gameStatus.getTotal_food_score() / (gameStatus.getNum_animals()+gameStatus.getNum_slaves()*2+gameStatus.getNum_crew()*5);
			food_t = Math.min(food_t, 25);
		}
		glow_paint.setColor(Color.rgb(255-(food_t*255/25), (food_t*255/25), 0));
		canvas.drawRect(705,60,705+(food_t*270/25),85, glow_paint);
		canvas.drawText("Food",710,80,text_paint);
		text_paint.setTextSize(25);
		Bitmap small = BitmapFactory.decodeResource(getResources(), R.drawable.crew_small);
		canvas.drawBitmap(small, 710, 95 , icon_paint);
		canvas.drawText(""+gameStatus.getNum_crew(),740,120,text_paint);
		small = BitmapFactory.decodeResource(getResources(), R.drawable.slave_small);
		canvas.drawBitmap(small, 790, 95 , icon_paint);
		canvas.drawText(""+gameStatus.getNum_slaves(),820,120,text_paint);
		small = BitmapFactory.decodeResource(getResources(), R.drawable.animal_small);
		canvas.drawBitmap(small, 870, 95 , icon_paint);
		canvas.drawText(""+gameStatus.getNum_animals(),900,120,text_paint);
		//=== End of game status box
		
	}
	
	private void playSelectMenuItem(){
		if (GameStatus.getGameStatusObject().isSounds()){
			//==Start Plaing sound when player takes finger to menu items
			new Thread(new Runnable() {
				public void run() {
					try{
						MediaPlayer mPlayer2 = MediaPlayer.create(islandHomeActivity, R.raw.redgreen_butn_blip);
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
	
	private void playSkullClickSound(){
		if (GameStatus.getGameStatusObject().isSounds()){
			new Thread(new Runnable() {
				public void run() {
					try{
						MediaPlayer mPlayer2 = MediaPlayer.create(islandHomeActivity, R.raw.skull_butn);
						mPlayer2.start();
						while(mPlayer2.isPlaying()){
							android.os.SystemClock.sleep(100);
						}
					}catch(Exception e){
						Log.d(TAG,"ERROR PLAYING");
						e.printStackTrace();
					}
				}}).start();
		}
	}
	
	private void playButtonSelectedSound(){
		if (GameStatus.getGameStatusObject().isSounds()){
			new Thread(new Runnable() {
				public void run() {
					try{
						MediaPlayer mPlayer2 = MediaPlayer.create(islandHomeActivity, R.raw.next_back);
						mPlayer2.start();
						while(mPlayer2.isPlaying()){
							android.os.SystemClock.sleep(100);
						}
					}catch(Exception e){
						Log.d(TAG,"ERROR PLAYING");
						e.printStackTrace();
					}
				}}).start();	
		}
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			if (cartDist(501, 399, (int)me.getX(), (int)me.getY())<90){
				showMenuButtons = true;
				playSkullClickSound();
			}
			selectedKey = 0;
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			if (cartDist(501, 399, (int)me.getX(), (int)me.getY())<90){
				if (GameStatus.getGameStatusObject().isHaptics()){
					// feeling of main button
					mSurface_mainButton.setActive(true);
					mSurface_mainButton.onTouchEvent(me);
				}
			}
			if (showMenuButtons){
				if (GameStatus.getGameStatusObject().isHaptics()){
					// If the main button is clicked you start feeling drag
					mSurface_drag.setActive(true);
					mSurface_drag.onTouchEvent(me);
				}
			}
			int oldSelectedKey = selectedKey;
			if (cartDist(501, 399, (int)me.getX(), (int)me.getY())>70){
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
				if ((selectedKey>0)&&(oldSelectedKey!=selectedKey)&&(showMenuButtons)){
					playSelectMenuItem();
				}
			}
			else
				selectedKey = 0;
		}
		if (me.getAction() == MotionEvent.ACTION_UP){
			if (showMenuButtons){
				showMenuButtons = false;
				collectItems = new Intent(islandHomeActivity,CollectItems.class);
			
				switch (selectedKey) {
			
				case EXIT:
					playButtonSelectedSound();
					Log.d(TAG, "Back to Home Screen");
					sea1 = null;
					ship = null;
					System.gc();
					islandHomeActivity.setResult(1);
					islandHomeActivity.finish();
					break;
					
				case FOOD:
					playButtonSelectedSound();
					collectItems.putExtra("type", 2);
		    		islandHomeActivity.startActivity(collectItems);
					break;
					
				case ANIMAL:
					playButtonSelectedSound();
					collectItems.putExtra("type", 0);
		    		islandHomeActivity.startActivity(collectItems);
					break;
					
				case SLAVE:
					playButtonSelectedSound();
					collectItems.putExtra("type", 1);
		    		islandHomeActivity.startActivity(collectItems);
					break;
					
				case MARKET:
					playButtonSelectedSound();
					Intent market = new Intent(islandHomeActivity,MarketHome.class);
					islandHomeActivity.startActivityForResult(market, 233);
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
		return (int) theta;
	}
	private int cartDist(int x1, int y1, int x2, int y2){
		 return (int) Math.sqrt((Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
	}
	
	private synchronized void startGameTimeElapseThread(){
		if (activityIsOnTop){
			new Thread(new Runnable() {
				public void run() {
						for (int i=0;i<10;i++){
							Date d = new Date();
							glowValue = (((float) Math.sin((d.getTime()/10)*0.0174532925))*120)+120;
							android.os.SystemClock.sleep(100);
						}
						startGameTimeElapseThread();
			    }
			}).start();
		}
	}

	private void loadBitmaps(){
		System.gc();
		sea1 = BitmapFactory.decodeResource(getResources(), R.drawable.sea_island);
		ship = BitmapFactory.decodeResource(getResources(), R.drawable.ship_look);
		button = BitmapFactory.decodeResource(getResources(), R.drawable.center_button);
		
		manager = (EffectManager) islandHomeActivity.getSystemService(islandHomeActivity.EFFECT_SERVICE);
		mSurface_drag = new FeelableSurface(this.getContext(), manager, R.xml.screen2_drag);
		mSurface_mainButton = new FeelableSurface(this.getContext(), manager, R.xml.screen2_mainpiratebutton);
	}
	
	private void infoDialog(){
		InstructionDialog id = new InstructionDialog();
		String title = "Avast! You found a deserted island...";
		String text = "Now its time to catch some animals, collect some food and capture slaves. Remember that" +
				" when you have more animals and more slaves they need more food.. ";
		id.popInstructionsDialog(title, text, islandHomeActivity);
	}
	
	public void gameResumeFromCollectItems(){
		_thread = new ViewControllerThread(getHolder(), this);
		_thread.setRunning(true);
		activityIsOnTop = true; // Child Intent terminated and currently this Activity is alive
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		_thread = new ViewControllerThread(getHolder(), this);
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
		        }
		    }	
	}
	
    class ViewControllerThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private IslandHomeView _islandHomeView;
        private boolean _run = false;
     
        public ViewControllerThread(SurfaceHolder sh, IslandHomeView ihv) {
            _surfaceHolder = sh;
            _islandHomeView = ihv;
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
    	                _islandHomeView.onDraw(c);
    	            }
    	        } finally {
    	            if (c != null) {
    	                _surfaceHolder.unlockCanvasAndPost(c);
    	            }
    	        }
    	    }     
        }
    }
	
}

