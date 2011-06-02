package hiit.nopsa.pirate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class PopulateItemsView extends View{

	private Activity populateItemsActivity;
	private final String TAG = "NOPSA-P";
	
	public PopulateItemsView(Context context, Activity activity) {
		super(context);
		populateItemsActivity = activity;
	}
	
	protected void onDraw(Canvas canvas){
		//==========Draw the background
		Paint background = new Paint();
		background.setColor(Color.GREEN);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
	}

}
