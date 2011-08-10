package hiit.nopsa.pirate;

import java.util.Date;
import java.util.Random;
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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameHomeView extends SurfaceView implements SurfaceHolder.Callback{

	private Bitmap sea1 = null;
	private Bitmap ship, icons, button;
	private Activity gameHomeActivity;
	private final String TAG = "NOPSA-P";
	private GameStatus gameStatus;
	private Intent islandHome = null;
	private Intent populateItems;
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
	
	private long animal_timer = 0;
	private long slave_timer = 0;
	private long crew_timer = 0;
	private Paint icon_paint;
	
	
	public GameHomeView(Context context, Activity activity) {
		super(context);
		gameHomeActivity = activity;
		gameStatus = GameStatus.getGameStatusObject();
		gameStatus.setGameOn(false);
		getHolder().addCallback(this);
		_thread = new ViewControllerThread(getHolder(), this);
		setFocusable(true);
		InfoDialog();
	}

	protected void onDraw(Canvas canvas){
		if (sea1 == null){
			loadBitmaps();
		}
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		Paint button_glow = new Paint(Paint.ANTI_ALIAS_FLAG);
		button_glow.setAlpha((int) glowValue);
		Bitmap glow_center = BitmapFactory.decodeResource(getResources(), R.drawable.center_button_glow);
		
		try{
			//Log.d(TAG,"DRAWSKY onDraw() Called");
			//==========Draw Sea & Sky & Draw Ship
			Paint sea_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			sea_paint.setStyle(Style.FILL);
			canvas.drawBitmap(sea1, 0, 0, sea_paint);
			//Paint ship_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			//ship_paint.setStyle(Style.FILL);
			canvas.drawBitmap(ship, 0, 0, sea_paint);
			//canvas.drawCircle(501, 399, 100, button_glow);
			canvas.drawBitmap(button, 416,314, sea_paint);
			canvas.drawBitmap(glow_center, 402,302, button_glow);
		}
		catch (NullPointerException ne) {
			Log.d(TAG,"sea1 or ship Bitmaps are NULL");
		}
		
		
		//Paint menu_pop_button_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
 		//menu_pop_button_paint.setColor(Color.rgb(204, 153, 51));
		//canvas.drawCircle(501, 399, 18, menu_pop_button_paint);
		

		
		if (showMenuButtons){
			//button_glow.setStyle(Style.STROKE);
			//button_glow.setStrokeWidth(20);
			//canvas.drawCircle(501, 399, 90, button_glow);
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
			icon_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			icon_paint.setStyle(Style.FILL);		
			
			try{
				if (gameStatus.getCollectableFromId(0).isEmpty())
					icons = BitmapFactory.decodeResource(getResources(), R.drawable.animal_icon_gr);
				else
					icons = BitmapFactory.decodeResource(getResources(), R.drawable.animal_icon);
			}catch(NullPointerException ne){
				icons = BitmapFactory.decodeResource(getResources(), R.drawable.animal_icon_gr);
			}
			canvas.drawBitmap(icons, 250, 200, icon_paint);
			
			try{
				if (gameStatus.getCollectableFromId(1).isEmpty())
					icons = BitmapFactory.decodeResource(getResources(), R.drawable.slave_icon_gr);
				else
					icons = BitmapFactory.decodeResource(getResources(), R.drawable.slave_icon);
			}catch(NullPointerException ne){
				icons = BitmapFactory.decodeResource(getResources(), R.drawable.slave_icon_gr);
			}
			canvas.drawBitmap(icons, 450, 100, icon_paint);
			
			try{
				if (gameStatus.getCollectableFromId(2).isEmpty())
					icons = BitmapFactory.decodeResource(getResources(), R.drawable.food_icon_gr);
				else
					icons = BitmapFactory.decodeResource(getResources(), R.drawable.food_icon);
			}catch(NullPointerException ne){
				icons = BitmapFactory.decodeResource(getResources(), R.drawable.food_icon_gr);
			}
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
		/*
		canvas.drawText("Ship Class :"+gameStatus.getShip_class(),800, 50, text_paint);
		canvas.drawText("Weapon Class :"+gameStatus.getWeapon_class(),800, 75, text_paint);
		canvas.drawText("Sails Class :"+gameStatus.getSails_class(),800, 100, text_paint);
		
		canvas.drawText("Crew Size :"+gameStatus.getNum_crew(),800, 140, text_paint);
		canvas.drawText("Coins :"+gameStatus.getCoins(),800, 165, text_paint);
		
		//canvas.drawText("Time on Sea :"+(gameStatus.getTimeOfNextIsland()/60)+":"+(gameStatus.getTimeOfNextIsland()%60), 800, 205, text_paint);
		canvas.drawText("Food Left :"+gameStatus.getTotal_food_score(),800,230, text_paint);
		//==========NEW
		int food_t;
		if ((gameStatus.getNum_animals()+gameStatus.getNum_slaves()*2+gameStatus.getNum_crew()*5)==0)
			food_t = 25;
		else{
			food_t = gameStatus.getTotal_food_score() / (gameStatus.getNum_animals()+gameStatus.getNum_slaves()*2+gameStatus.getNum_crew()*5);
			food_t = Math.min(food_t, 25);
		}
		
		glow_paint.setColor(Color.rgb(255-((food_t/25)*255), (food_t/25)*255, 0));
		canvas.drawRect(800,240,800+(food_t*220/25),265, glow_paint);
		//END OF NEW
		 */
		int food_t;
		Paint glow_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		canvas.drawText("Time on Sea :",50,50,text_paint);
		text_paint.setTextSize(80);
		canvas.drawText(
				String.format("%d:%02d", 
						(gameStatus.getTimeOfNextIsland()/60),
						(gameStatus.getTimeOfNextIsland()%60)), 50, 120, text_paint);
		
		if ((!gameStatus.isGameOn()&&(activityIsOnTop))){
			gameStatus.setGameOn(true);	
			//Log.d(TAG,"startGameTimeElapseThread()===== Called from ON_DRAW()");
		    startGameTimeElapseThread();
		}
		
		//============Draw Game Status BOX========
		text_paint.setTextSize(15);
		int temp_t = gameStatus.getTimeOfNextIsland();
		text_paint.setColor(Color.BLACK);
		//canvas.drawRect(600,480,880,580, glow_paint);
		Bitmap backPaper = BitmapFactory.decodeResource(getResources(), R.drawable.game_status_back_ext);
		canvas.drawBitmap(backPaper, 710, 120, icon_paint);
		backPaper = BitmapFactory.decodeResource(getResources(), R.drawable.game_status_back);
		canvas.drawBitmap(backPaper, 680, 5, icon_paint);
		glow_paint.setColor(Color.YELLOW);//glow_paint.setColor(Color.rgb(255-(temp_t/600)*255, (temp_t/600)*255, 0));
		canvas.drawRect(705,35,705+(temp_t*270/600),60, glow_paint);
		canvas.drawText("Time On Sea", 710, 55, text_paint);
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
		text_paint.setTextSize(20);
		canvas.drawText("Ship Class : "+gameStatus.getShip_class(),740,170,text_paint);
		canvas.drawText("Coins : "+gameStatus.getCoins(), 740, 200, text_paint);
		//=== End of game status box
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			if (cartDist(501, 399, (int)me.getX(), (int)me.getY())<90){
				showMenuButtons = true;
			}
			selectedKey = 0;
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE) {
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
			}
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
					populateItems.putExtra("type", 2);
					gameHomeActivity.startActivityForResult(populateItems, 972);
					break;
					
				case ANIMAL:
					populateItems.putExtra("type", 0);
		    		gameHomeActivity.startActivity(populateItems);
					break;
					
				case SLAVE:
					populateItems.putExtra("type", 1);
		    		gameHomeActivity.startActivity(populateItems);
					break;
					
				case MARKET:
					Intent market = new Intent(gameHomeActivity,MarketHome.class);
					gameHomeActivity.startActivityForResult(market, 233);
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
	
	private void alertDeath(final String s){
		Handler mHandler = new Handler(Looper.getMainLooper());
		mHandler.post(new Runnable() {
	          public void run() {
		        	InstructionDialog id = new InstructionDialog();
		      		String title = "Arrr Sailor !!";
		      		String text = s;
		      		if (gameStatus.getInstructions())
		      			id.popInstructionsDialog(title, text, gameHomeActivity);
		      		else{
		      			gameStatus.setInstructions(true);
		      			id.popInstructionsDialog(title, text, gameHomeActivity);
		      			gameStatus.setInstructions(false);
		      		}
	          }
	       });
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
			//populateItems.
			gameHomeActivity.finishActivity(972); // Finishing PopulateItems Activity
			//TODO BUG : When it returns from Populate items it some times creates two island home activities.
			Log.d(TAG, "ISLAND HOME T OR F --->>"+(islandHome==null));
			islandHome = new Intent(gameHomeActivity,IslandHome.class);
			gameHomeActivity.startActivityForResult(islandHome,231);
			
		}
		if (gameStatus.isGameOn()){
			new Thread(new Runnable() {
				Date d = new Date();
				public void run() {
						//=== Update Food Usage Every Second =======
						long time_spent = d.getTime()-gameStatus.getLastTimeUpdated();
						animal_timer = animal_timer + time_spent;
						crew_timer = crew_timer + time_spent;
						slave_timer = slave_timer + time_spent;
						
						if ((crew_timer*gameStatus.getNum_crew())>((60*1000))){
							for(int i=0;i<(crew_timer*gameStatus.getNum_crew()/(60*1000));i++){
								gameStatus.eat_one_food();
								Log.d(TAG,"Crew Ate Food !!");
							}
							crew_timer = 0;
							if (gameStatus.getTotal_food_score()<1){
								if (gameStatus.getNum_crew()>0)
									alertDeath("Crew Member died cause there is NO FOOD !!");
								gameStatus.setNum_crew(Math.max(gameStatus.getNum_crew()-1,0));
							}
						}
						if ((slave_timer*gameStatus.getNum_slaves())>((180*1000))){
							for(int i=0;i<(slave_timer*gameStatus.getNum_slaves()/(180*1000));i++){
								gameStatus.eat_one_food();
								Log.d(TAG,"Slave Ate Food !!");
							}
							slave_timer = 0;
							if (gameStatus.getTotal_food_score()<1){
								if (gameStatus.getNum_slaves()>0)
									alertDeath("Slave died cause there is NO FOOD !!");
								gameStatus.setNum_slaves(Math.max(gameStatus.getNum_slaves()-1,0));
								gameStatus.getSlaves().remove(0); // Killing Slave Due to Hunger
							}
						}
						if ((animal_timer*gameStatus.getNum_animals())>((300*1000))){
							for(int i=0;i<(animal_timer*gameStatus.getNum_animals()/(300*1000));i++){
								gameStatus.eat_one_food();
								Log.d(TAG,"Animal Ate Food !!");
							}
							animal_timer = 0;
							if (gameStatus.getTotal_food_score()<1){
								if (gameStatus.getNum_animals()>0)
									alertDeath("Animal died cause ther is NO FOOD !!");
								gameStatus.setNum_animals(Math.max(gameStatus.getNum_animals()-1,0));
								gameStatus.getAnimals().remove(0); // Kill Animal Due to Hunger
							}
						}
						//=== Update Time Every Second===============
						gameStatus.setTimeOfNextIsland(gameStatus.getTimeOfNextIsland()-(int)(((d.getTime()-gameStatus.getLastTimeUpdated()))/1000));
						gameStatus.setLastTimeUpdated(d.getTime());
						//============================================
						for (int i=0;i<10;i++){
							//glowValue = glowValue+10;
							//if (glowValue>100)
							//	glowValue = 0;
							Date d = new Date();
							glowValue = (((float) Math.sin((d.getTime()/10)*0.0174532925))*120)+120;
							android.os.SystemClock.sleep(100);
						}
						//========
						startGameTimeElapseThread();
			    }
			}).start();
		}
	}

	
	private void loadBitmaps(){
		System.gc();
		sea1 = BitmapFactory.decodeResource(getResources(), R.drawable.day_sea);
		ship = BitmapFactory.decodeResource(getResources(), R.drawable.ship_look);
		button = BitmapFactory.decodeResource(getResources(), R.drawable.center_button);
	}
	
	private void InfoDialog(){
		InstructionDialog id = new InstructionDialog();
		String title = "Ahoy, Matey!";
		String text = "Wanna be the Pirate Lord of NOPSA Sea ? Let's get ready to battle!" +
				"Collect Coins, Hire Crew, Upgrade your ship. Keep in mind that you need food to survive...";
		id.popInstructionsDialog(title, text, gameHomeActivity);
	}
	
	public void gameResumeFromIsland(){
		Date d = new Date();
		gameStatus.setLastTimeUpdated(d.getTime());
		Random r = new Random(d.getTime());
		gameStatus.setTimeOfNextIsland(r.nextInt(300)+300);
		_thread = new ViewControllerThread(getHolder(), this);
		_thread.setRunning(true);
		activityIsOnTop = true; // Child Intent terminated and currently this Activity is alive
	}
	
	public void gameResumeFromPort(){
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
