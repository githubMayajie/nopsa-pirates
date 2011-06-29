package hiit.nopsa.pirate;

import hiit.nopsa.pirate.GameHomeView.ViewControllerThread;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MarketHomeView extends SurfaceView implements SurfaceHolder.Callback{
	
	private Activity marketHomeActivity;
	private ViewControllerThread _thread;
	
	public MarketHomeView(Context context, Activity activity) {
		super(context);
		marketHomeActivity = activity;
		getHolder().addCallback(this);
		_thread = new ViewControllerThread(getHolder(), this);
		setFocusable(true);
		//InfoDialog();
	}
	
	protected void onDraw(Canvas canvas){
		/*if (sea1 == null){
			loadBitmaps();
		}
		*/
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.YELLOW);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
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
	        private MarketHomeView _marketHomeView;
	        private boolean _run = false;
	     
	        public ViewControllerThread(SurfaceHolder sh, MarketHomeView mhv) {
	            _surfaceHolder = sh;
	            _marketHomeView = mhv;
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
	    	                _marketHomeView.onDraw(c);
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
