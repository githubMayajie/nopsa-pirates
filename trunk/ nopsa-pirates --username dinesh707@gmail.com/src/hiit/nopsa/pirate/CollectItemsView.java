package hiit.nopsa.pirate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class CollectItemsView extends View{

	private Activity collectItemsActivity;
	private final String TAG = "NOPSA-P";
	
	public CollectItemsView(Context context, Activity activity) {
		super(context);
		collectItemsActivity = activity;
	}
	
	protected void onDraw(Canvas canvas){
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.WHITE);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
	}

}
