package hiit.nopsa.pirate;

import hiit.nopsa.pirate.CollectItemsView.ViewControllerThread;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PopulateItemsView extends SurfaceView implements SurfaceHolder.Callback{

	private Activity populateItemsActivity;
	private final String TAG = "NOPSA-P";
	private ViewControllerThread _thread; 
	private int collectableType; //0-animal, 1-slave, 2-food
	private PopulateItemsImageManager popImageManager = null;
	private Collectable selectedColectable = null;
	private Bitmap background,chest_icon,trash_icon,back_icon;
	private Bitmap bitmap=null;
	private boolean imageDragging = false;
	private int imgDrag_x = 0;
	private Paint squarePaint = null;
	
	
	public PopulateItemsView(Context context, Activity activity) {
		super(context);
		populateItemsActivity = activity;
		getHolder().addCallback(this);
		_thread = new ViewControllerThread(getHolder(), this);
		collectableType = activity.getIntent().getExtras().getInt("type");
		infoDialog();
	}
	
	protected void onDraw(Canvas canvas){
		if (background == null){
			loadBitmaps();
		}
		//==========Draw the background
		Paint back = new Paint();
		back.setColor(Color.GREEN);
		canvas.drawRect(0, 0, getWidth(), getHeight(), back);
		
		//==========Draw Backgrund Image
		Paint back_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		back_paint.setStyle(Style.FILL);
		canvas.drawBitmap(background, 0, 0, back_paint);
		
		//==========Draw Collected Items Ribbon
		Paint text_paint = new Paint();
		text_paint.setColor(Color.WHITE);
		text_paint.setStyle(Style.FILL);
		text_paint.setAntiAlias(true);
		text_paint.setTextSize(15);
		text_paint.setTypeface(Typeface.SANS_SERIF);
		
		Paint glow_paint = new Paint();
		glow_paint.setColor(Color.WHITE);
		glow_paint.setAlpha(100);
		int count=0;
		for (Collectable collectable: GameStatus.getGameStatusObject().getCollectableFromId(collectableType)){
			// TODO Only Show some number of items
			// Becouse it will overflow the screen after 9 elements
			canvas.drawRect(15+(count*90), 485, 100+(count*90), 570, glow_paint);
			canvas.drawBitmap(collectable.getIcon_bitmap(), 20+(count*90), 490 , back_paint);
			canvas.drawText(""+collectable.getScore(),45+(count*90), 585, text_paint);
			count = count+1;
    	}
		//==========Draw Items to Be Marked Ribbon
		// NOT TODO: I dont think showing next images is nessasary. Since I commented that part
		/*
		if (popImageManager!=null){
			count = 0;
			for (Bitmap images: popImageManager.getRelatedImagesByStartPoint(selectedColectable.getLast_img_marked())){			
				canvas.drawRect(924, 15+(count*90), 1019, 100+(count*90), glow_paint);	
				canvas.drawBitmap(images, 929, 20+(count*90) , back_paint);
				count = count+1;
			}
		}
		*/
		//==========Draw Image to mark boundaries
		if (popImageManager!=null){
			bitmap = popImageManager.getImagetoMarkBonderies(selectedColectable.getLast_img_marked());
			if ((bitmap!=null)&&(!imageDragging)){
				canvas.drawRect(bitmap.getWidth()+50, 50, bitmap.getWidth()+100, bitmap.getHeight()+50, glow_paint);
				canvas.drawBitmap(bitmap, 50,50, back_paint);
			}
			if (imageDragging){
				if (squarePaint!=null){
					canvas.drawRect(imgDrag_x+20,20,imgDrag_x+bitmap.getWidth()+80,80+bitmap.getHeight(),squarePaint);
				}
				canvas.drawBitmap(bitmap,50+imgDrag_x,50, back_paint);
			}
			//=====Draw Next Image Icon
			canvas.drawCircle(1024, 300, 300, glow_paint);
			canvas.drawBitmap(chest_icon, 750, 236, back_paint);
			canvas.drawBitmap(trash_icon, 900, 450, back_paint);	
			
			text_paint.setTextSize(30);
			canvas.drawText(selectedColectable.getTag().replace("+", " "), 50, 40, text_paint);
		}
		canvas.drawBitmap(back_icon, 900, 22, back_paint);		
	}
	
	private void infoDialog(){
		InstructionDialog id = new InstructionDialog();
		String title = "Arrr !";
		String text = "Need More Food? Need to make your slaves Intelligent? OR You want to make animals worth?.." +
				"Dont Wait.. start marking them .. if you cant find any of them throw it to green Ghost. P.S: If you " +
				"cant find any items to mark, wait till you get into an Island. There you can capture them ..";
		id.popInstructionsDialog(title, text, populateItemsActivity);
	}
		
		
	//canvas.drawRect(15+(count*90), 485, 100+(count*90), 570, glow_paint);
	public boolean onTouchEvent(MotionEvent me) {
		// Rect(bitmap.getWidth()+50, 50, bitmap.getWidth()+100, bitmap.getHeight()+50,)
		
		if (me.getAction() == MotionEvent.ACTION_DOWN){
			if (bitmap!=null){
				if (((bitmap.getWidth()+50)<me.getX())&&
						(me.getX()<(bitmap.getWidth()+100))&&
						(50<me.getY())&&
						(me.getY()<(bitmap.getHeight()+50))){
						// TODO Start Moving the Image when player is dragging towards "chest" or "bin"
						Log.d(TAG,"Clicked on Correct Position !!!!");
						imageDragging = true;
				}
			}
			if (cartDist((int)me.getX(), (int)me.getY(), 900, 22)<92){
				populateItemsActivity.finish();
			}
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE){
			if (imageDragging){
				imgDrag_x = (int)me.getX()-bitmap.getWidth()-50;
				if (cartDist((int)me.getX(), (int)me.getY(), 814, 300)<92){
					//TODO Color the box around GREEN
					squarePaint = new Paint();
					squarePaint.setColor(Color.GREEN);
					//squarePaint.setAlpha(100);
				}
				if (cartDist((int)me.getX(), (int)me.getY(), 964, 514)<92){
					//TODO Color the box around RED
					squarePaint = new Paint();
					squarePaint.setColor(Color.RED);
					//squarePaint.setAlpha(100);
				}				
			}
		}
		if (me.getAction() == MotionEvent.ACTION_UP) {
			if ((485<me.getY())&&(me.getY()<570)){
				if (((int)((me.getX()-15)/90)) < GameStatus.getGameStatusObject().getCollectableFromId(collectableType).size()){
					selectedColectable = GameStatus.getGameStatusObject().getCollectableFromId(collectableType).get((int) ((me.getX()-15)/90));
					popImageManager = new PopulateItemsImageManager(
							//GameStatus.getGameStatusObject().getCollectableFromId(collectableType).get((int) ((me.getX()-15)/90)), 
							selectedColectable,
							this);
				}
			}
			if (squarePaint!=null){
				if (squarePaint.getColor() == Color.RED){
					//TODO -- Mark Image as Not useful
					selectedColectable.setLast_img_marked(selectedColectable.getLast_img_marked()+1);
					Log.d(TAG,"Image Droped on RED");
				}
				else if (squarePaint.getColor() == Color.GREEN){
					//TODO -- Upload Image Bounderies to server
					selectedColectable.setLast_img_marked(selectedColectable.getLast_img_marked()+1);
					Log.d(TAG,"Image Droped on GREEN "+selectedColectable.getLast_img_marked());
				}
			}
			imgDrag_x = 0;
			imageDragging = false;
			squarePaint = null;
		}
		return true;
	}
	
	private void loadBitmaps(){
		System.gc();
		background = BitmapFactory.decodeResource(getResources(), R.drawable.train_background);
		chest_icon = BitmapFactory.decodeResource(getResources(), R.drawable.chest_icon);
		trash_icon = BitmapFactory.decodeResource(getResources(), R.drawable.trash_icon);
		back_icon = BitmapFactory.decodeResource(getResources(), R.drawable.ship_wheel);
	}
	
	private int cartDist(int x1, int y1, int x2, int y2){
		 return (int) Math.sqrt((Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
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
		            // we will try it again and again...
		        }
		    }	
	}
	
    class ViewControllerThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private PopulateItemsView _populateItemsView;
        private boolean _run = false;
     
        public ViewControllerThread(SurfaceHolder sh, PopulateItemsView piv) {
            _surfaceHolder = sh;
            _populateItemsView = piv;
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
    	                _populateItemsView.onDraw(c);
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
