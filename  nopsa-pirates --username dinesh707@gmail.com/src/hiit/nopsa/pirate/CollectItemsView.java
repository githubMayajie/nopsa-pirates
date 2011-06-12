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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class CollectItemsView extends SurfaceView implements SurfaceHolder.Callback{

	private Activity collectItemsActivity;
	private final String TAG = "NOPSA-P";
	private Bitmap background;
	private Bitmap ribbonIcon;
	private ViewControllerThread _thread;
	private int collectableType;
	
	public CollectItemsView(Context context, Activity activity) {
		super(context);
		collectItemsActivity = activity;
		getHolder().addCallback(this);
		_thread = new ViewControllerThread(getHolder(), this);
		collectableType = activity.getIntent().getExtras().getInt("type");
	}
	
	protected void onDraw(Canvas canvas){
		if (background == null){
			loadBitmaps();
		}
		
		//==========Draw Background Color
		Paint back_color = new Paint();
		back_color.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), getHeight(), back_color);
		
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
		int count=0;
		Bitmap tempBitmap=null;
		for (Collectable collectable: GameStatus.getGameStatusObject().getCollectableFromId(collectableType)){
			canvas.drawBitmap(collectable.getIcon_bitmap(), 20+(count*90), 490 , back_paint);
			canvas.drawText(""+collectable.getScore(),45+(count*90), 585, text_paint);
			count = count+1;
			tempBitmap = collectable.getIcon_bitmap();
    	}
		
		//==========Draw Image Wheel
		//TODO
		//Create chaning image wheel using a queue
		// change it by rotate guesture
		canvas.drawBitmap(tempBitmap, 680, 200, back_paint);
		
		canvas.drawBitmap(tempBitmap, 637, 285, back_paint);//1
		canvas.drawBitmap(tempBitmap, 722, 285, back_paint);//2
		canvas.drawBitmap(tempBitmap, 765, 200, back_paint);//3
		canvas.drawBitmap(tempBitmap, 722, 115, back_paint);//4
		canvas.drawBitmap(tempBitmap, 637, 115, back_paint);//5
		canvas.drawBitmap(tempBitmap, 595, 200, back_paint);//6
		
		canvas.drawBitmap(tempBitmap, 552, 285, back_paint);//7
		canvas.drawBitmap(tempBitmap, 594, 370, back_paint);//8
		canvas.drawBitmap(tempBitmap, 679, 370, back_paint);//9
		canvas.drawBitmap(tempBitmap, 765, 370, back_paint);//10
		canvas.drawBitmap(tempBitmap, 807, 285, back_paint);//11
		canvas.drawBitmap(tempBitmap, 850, 200, back_paint);//12
		canvas.drawBitmap(tempBitmap, 807, 115, back_paint);//13

		canvas.drawBitmap(tempBitmap, 765, 30, back_paint);//14
		canvas.drawBitmap(tempBitmap, 679, 30, back_paint);//15
		
		canvas.drawBitmap(tempBitmap, 594, 30, back_paint);//16
		canvas.drawBitmap(tempBitmap, 509, 30, back_paint);//17
		canvas.drawBitmap(tempBitmap, 424, 30, back_paint);//18
		canvas.drawBitmap(tempBitmap, 339, 30, back_paint);//19
		canvas.drawBitmap(tempBitmap, 254, 30, back_paint);//20
		canvas.drawBitmap(tempBitmap, 169, 30, back_paint);//21
		canvas.drawBitmap(tempBitmap, 84, 30, back_paint);//22
		canvas.drawBitmap(tempBitmap, 0, 30, back_paint);//23
	}
	
	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
	
	private void loadBitmaps(){
		System.gc();
		background = BitmapFactory.decodeResource(getResources(), R.drawable.blackship_background);
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
