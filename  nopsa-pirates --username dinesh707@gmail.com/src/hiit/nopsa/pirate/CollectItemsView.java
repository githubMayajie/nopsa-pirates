package hiit.nopsa.pirate;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import hiit.nopsa.pirate.GameHomeView.ViewControllerThread;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class CollectItemsView extends SurfaceView implements SurfaceHolder.Callback{

	private Activity collectItemsActivity;
	private final String TAG = "NOPSA-P";
	private Bitmap background;
	private Bitmap ribbonIcon;
	private ViewControllerThread _thread;
	private int collectableType;
	private CollectItemsImageGenarator imageGenarator=null;
	private int currentLoaction = 0;
	private int img_id = -1;
	private Bitmap moving_img;
	private int moving_imgX, moving_imgY;
	private URL moving_url = null;
	private Bitmap icons;
	private int moving_imgId;
	
	public CollectItemsView(Context context, Activity activity) {
		super(context);
		collectItemsActivity = activity;
		getHolder().addCallback(this);
		_thread = new ViewControllerThread(getHolder(), this);
		collectableType = activity.getIntent().getExtras().getInt("type");
		infoDialog();
	}
	
	protected void onDraw(Canvas canvas){
		if (background == null){
			loadBitmaps();
		}
		if (imageGenarator==null){
			imageGenarator = new CollectItemsImageGenarator(this,collectableType);
		}
		
		//==========Draw Background Color
		Paint back_color = new Paint();
		back_color.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), back_color);
		
		//==========Draw Backgrund Image
		Paint back_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		back_paint.setStyle(Style.FILL);
		canvas.drawBitmap(background, 0, 0, back_paint);
		
		//=========OK Icon
		icons = BitmapFactory.decodeResource(getResources(), R.drawable.ok_icon);
		canvas.drawBitmap(icons, 904, 480, back_paint);
		
		//==========Draw Collected Items Ribbon
		Paint text_paint = new Paint();
		text_paint.setColor(Color.WHITE);
		text_paint.setStyle(Style.FILL);
		text_paint.setAntiAlias(true);
		text_paint.setTextSize(15);
		text_paint.setTypeface(Typeface.SANS_SERIF);
		int count=0;
		for (Collectable collectable: GameStatus.getGameStatusObject().getCollectableFromId(collectableType)){
			// TODO Only Show some number of items
			// Becouse it will overflow the screen after 9 elements
			canvas.drawBitmap(collectable.getIcon_bitmap(), 20+(count*90), 490 , back_paint);
			canvas.drawText(""+collectable.getScore(),45+(count*90), 585, text_paint);
			count = count+1;
    	}
		
		//==========Draw Image Wheel
		imageGenarator.setCurrentLocation(currentLoaction);
		canvas.drawBitmap(imageGenarator.images.get(0), 680, 200, back_paint);
		canvas.drawBitmap(imageGenarator.images.get(1), 637, 285, back_paint);//1
		canvas.drawBitmap(imageGenarator.images.get(2), 722, 285, back_paint);//2
		canvas.drawBitmap(imageGenarator.images.get(3), 765, 200, back_paint);//3
		canvas.drawBitmap(imageGenarator.images.get(4), 722, 115, back_paint);//4
		canvas.drawBitmap(imageGenarator.images.get(5), 637, 115, back_paint);//5
		canvas.drawBitmap(imageGenarator.images.get(6), 595, 200, back_paint);//6
		canvas.drawBitmap(imageGenarator.images.get(7), 552, 285, back_paint);//7
		canvas.drawBitmap(imageGenarator.images.get(8), 594, 370, back_paint);//8
		canvas.drawBitmap(imageGenarator.images.get(9), 679, 370, back_paint);//9
		canvas.drawBitmap(imageGenarator.images.get(10), 765, 370, back_paint);//10
		canvas.drawBitmap(imageGenarator.images.get(11), 807, 285, back_paint);//11
		canvas.drawBitmap(imageGenarator.images.get(12), 850, 200, back_paint);//12
		canvas.drawBitmap(imageGenarator.images.get(13), 807, 115, back_paint);//13
		canvas.drawBitmap(imageGenarator.images.get(14), 765, 30, back_paint);//14
		canvas.drawBitmap(imageGenarator.images.get(15), 679, 30, back_paint);//15
		canvas.drawBitmap(imageGenarator.images.get(16), 594, 30, back_paint);//16
		canvas.drawBitmap(imageGenarator.images.get(17), 509, 30, back_paint);//17
		canvas.drawBitmap(imageGenarator.images.get(18), 424, 30, back_paint);//18
		canvas.drawBitmap(imageGenarator.images.get(19), 339, 30, back_paint);//19
		canvas.drawBitmap(imageGenarator.images.get(20), 254, 30, back_paint);//20
		canvas.drawBitmap(imageGenarator.images.get(21), 169, 30, back_paint);//21
		canvas.drawBitmap(imageGenarator.images.get(22), 84, 30, back_paint);//22
		canvas.drawBitmap(imageGenarator.images.get(23), 0, 30, back_paint);//23
		
		// Draw Dragging Image
		if (img_id>=0){
			Log.d(TAG,"IMAGE is MOVING");
			canvas.drawBitmap(moving_img, moving_imgX-37, moving_imgY-37, back_paint);//23	
		}
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			if ((904<me.getX())&&(me.getX()<1004)&&(480<me.getY())&&(me.getY()<580)){
				collectItemsActivity.finish();
			}
			
			if ( 680< me.getX() && me.getX()<755 && 200<me.getY() && me.getY()<275)
				img_id = 0;
			if ( 637< me.getX() && me.getX()<712 && 285<me.getY() && me.getY()<360)
				img_id = 1;
			if ( 722< me.getX() && me.getX()<797 && 285<me.getY() && me.getY()<360)
				img_id = 2;
			if ( 765< me.getX() && me.getX()<840 && 200<me.getY() && me.getY()<275)
				img_id = 3;
			if ( 722< me.getX() && me.getX()<797 && 115<me.getY() && me.getY()<200)
				img_id = 4;
			if ( 637< me.getX() && me.getX()<712 && 115<me.getY() && me.getY()<200)
				img_id = 5;
			if ( 595< me.getX() && me.getX()<670 && 200<me.getY() && me.getY()<275)
				img_id = 6;
			if ( 552< me.getX() && me.getX()<627 && 285<me.getY() && me.getY()<360)
				img_id = 7;
			if ( 595< me.getX() && me.getX()<670 && 370<me.getY() && me.getY()<445)
				img_id = 8;
			if ( 680< me.getX() && me.getX()<755 && 370<me.getY() && me.getY()<445)
				img_id = 9;
			if ( 765< me.getX() && me.getX()<840 && 370<me.getY() && me.getY()<445)
				img_id = 10;
			if ( 807< me.getX() && me.getX()<882 && 285<me.getY() && me.getY()<360)
				img_id = 11;
			if ( 850< me.getX() && me.getX()<925 && 200<me.getY() && me.getY()<275)
				img_id = 12;
			if ( 807< me.getX() && me.getX()<882 && 115<me.getY() && me.getY()<200)
				img_id = 13;
			if ( 765< me.getX() && me.getX()<840 && 30<me.getY() && me.getY()<105)
				img_id = 14;
			if ( 680< me.getX() && me.getX()<755 && 30<me.getY() && me.getY()<105)
				img_id = 15;
			if ( 595< me.getX() && me.getX()<670 && 30<me.getY() && me.getY()<105)
				img_id = 16;
			if ( 509< me.getX() && me.getX()<584 && 30<me.getY() && me.getY()<105)
				img_id = 17;
			if ( 424< me.getX() && me.getX()<499 && 30<me.getY() && me.getY()<105)
				img_id = 18;
			if ( 339< me.getX() && me.getX()<414 && 30<me.getY() && me.getY()<105)
				img_id = 19;
			if ( 254< me.getX() && me.getX()<329 && 30<me.getY() && me.getY()<105)
				img_id = 20;
			if ( 169< me.getX() && me.getX()<244 && 30<me.getY() && me.getY()<105)
				img_id = 21;
			if ( 84< me.getX() && me.getX()<159 && 30<me.getY() && me.getY()<105)
				img_id = 22;
			if ( 0< me.getX() && me.getX()<74 && 30<me.getY() && me.getY()<105)
				img_id = 23;
			
			if (img_id>=0){
				moving_img = imageGenarator.getImageById(img_id);
				moving_url = imageGenarator.getImageUrlById(img_id);
				moving_imgId = imageGenarator.getImageFileId_ById(img_id);
			}
			moving_imgX = (int) me.getX();
			moving_imgY = (int) me.getY();
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			moving_imgX = (int) me.getX();
			moving_imgY = (int) me.getY();
		}
		if (me.getAction() == MotionEvent.ACTION_UP) {
			if ((me.getY()>470)&&(img_id>=0)&&(moving_url!=null)){
				synchronized (this) {
					_thread.setRunning(false);
					img_id = -1;	
					Intent keyboardHome = new Intent(collectItemsActivity, KeyboardHome.class);
					keyboardHome.putExtra("img_url", moving_url.toString());
					moving_url = null;
					keyboardHome.putExtra("img_fileid", moving_imgId);
					keyboardHome.putExtra("type", collectableType);
					System.gc();
					collectItemsActivity.startActivityForResult(keyboardHome, 511);					
				}
				//collectItemsActivity.getWindow()
				//	.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				//TODO pop the keyboard to enter tag
				// After the tag is enterd create Collectable Object "c"
				//GameStatus.getGameStatusObject().addCollectableFromId(collectableType, c)
			}
			img_id = -1;
			moving_url = null;
		}
		return true;
	}
	

	private void loadBitmaps(){
		System.gc();
		background = BitmapFactory.decodeResource(getResources(), R.drawable.blackship_background);
	}
	
	private void infoDialog(){
		InstructionDialog id = new InstructionDialog();
		String title = "Yo! Sailer..";
		String text = "Drop the items you want in to the drak sea down. Later you can use them as food, " +
				"you can train them an make them your crew, OR you can sell them at the harbor";
		id.popInstructionsDialog(title, text, collectItemsActivity);
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
	            // we will try it again and again...
	        }
	    }	
	}
	
    class ViewControllerThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private CollectItemsView _collectItemsView;
        private boolean _run = false;
     
        public ViewControllerThread(SurfaceHolder sh, CollectItemsView civ) {
            _surfaceHolder = sh;
            _collectItemsView = civ;
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
    	                _collectItemsView.onDraw(c);
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
