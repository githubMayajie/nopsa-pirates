package hiit.nopsa.pirate;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

public class CollectItemsImageGenarator {

	public ArrayList<Bitmap> images;
	public ArrayList<URL> image_urls;
	public ArrayList<Integer> image_fileId; 
	
	private int currentLocation;
	public int oldLoaction = -1;
	private final String TAG = "NOPSA-P";
	private String search_tags;
	private Bitmap no_img;
	
	private Boolean methodLock = new Boolean(false);
	
	public CollectItemsImageGenarator(View parentView, int type){
		images = new ArrayList<Bitmap>();
		image_urls = new ArrayList<URL>();
		image_fileId = new ArrayList<Integer>();
		
		no_img = BitmapFactory.decodeResource(parentView.getResources(), R.drawable.no_img);
		for (int i=0;i<25;i++){
			images.add(no_img);
			image_urls.add(null);
			image_fileId.add(null);
		}
		switch (type) {
		case 0:
			search_tags = "%2Banimal%2fish%2Bmonkey%2Bparrot";
			break;
		case 1:
			search_tags = "%2Bpeople%2Bperson%2Bman%2Bwoman%2Bboy%2Bgirl";
			break;
		case 2:
			search_tags = "%2Bfood%2Bdrink%2Bfruit";
			break;
		default:
			break;
		}
	}
	// This set the current location of first image shows in the screen
	// Screen shows 24 images starting from currentLocation
	public void setCurrentLocation(int i){
		currentLocation = i;
		Log.d(TAG,"---> Num Img In Array -->"+images.size());
		if (!methodLock){
			methodLock = true;
			Log.d(TAG,"Times it called!");
			loadImagesToArray();
		}
	}
	
	public Bitmap getImageById(int id){
		return images.get(id);
	}
	
	public URL getImageUrlById(int id) {
		return image_urls.get(id);
	}
	
	public int getImageFileId_ById(int id) {
		return image_fileId.get(id);
	}
	
	private void loadImagesToArray(){	
		if (oldLoaction<0){
			// Code Runs for FIrst time
			oldLoaction = 0;
			new Thread(new Runnable() {
				public void run() {
					Bitmap icon_bitmap = null;
					URL url = null;
					int fileId=0;
					for (int i=0;i<25;i++){
						images.remove(0);
						image_urls.remove(0);
						image_fileId.remove(0);
						images.add(0,no_img);
						image_urls.add(0,null);
						image_fileId.add(0,null);
						
						String url_str = "http://128.214.112.107/pmg/index.php/api/search?" +
							"apikey=1A4ECEAF1A942425&tags="+search_tags+"&order_attr=rank&mode=any" +
							"&per_page=1&page="+(i+1)+"&encoding=html&output_type=xml&yt1=Query";
						try {
							url = new URL(url_str);
							HttpURLConnection connection = (HttpURLConnection) url.openConnection();
							connection.setRequestMethod("GET");
							connection.connect();
							InputStream stream = connection.getInputStream();
							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							DocumentBuilder db = dbf.newDocumentBuilder();
							Document doc = db.parse(stream);
							doc.getDocumentElement().normalize();
							Node node = doc.getElementsByTagName("baseName").item(0);
							url = new URL("http://128.214.112.107/pmg/viewer/images/"+node.getTextContent().replace("photo", "square"));
							connection  = (HttpURLConnection) url.openConnection();
							InputStream is = connection.getInputStream();
							icon_bitmap = BitmapFactory.decodeStream(is);  			
							node = doc.getElementsByTagName("fileId").item(0);
							fileId = Integer.parseInt(node.getTextContent());
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (icon_bitmap!=null){
							images.remove(0);
							image_urls.remove(0);
							image_fileId.remove(0);
							images.add(icon_bitmap);
							image_urls.add(url);
							image_fileId.add(fileId);
						}
					}
					methodLock = false;
				}
			}
			).start();
		}// End of IF
		else{
			// code runs when it loads other times
			//TODO if (currentLocation-oldLoaction>0)
				//Log.d(TAG,"Current IIIIIIIIIIIIIIIIIIII"+i);
				//==============================================
				new Thread(new Runnable() {
					public void run() {
						Bitmap icon_bitmap = null;
						URL url = null;
						int fileId=0;
						for (int i=oldLoaction+25;i<currentLocation+25;i++){
							images.remove(0);
							image_urls.remove(0);
							image_fileId.remove(0);
							images.add(0,no_img);
							image_urls.add(0,null);
							image_fileId.add(0,null);
							Log.d(TAG,"CC "+i);
							String url_str = "http://128.214.112.107/pmg/index.php/api/search?" +
								"apikey=1A4ECEAF1A942425&tags="+search_tags+"&order_attr=rank&mode=any" +
								"&per_page=1&page="+(i+1)+"&encoding=html&output_type=xml&yt1=Query";
							try {
								url = new URL(url_str);
								HttpURLConnection connection = (HttpURLConnection) url.openConnection();
								connection.setRequestMethod("GET");
								connection.connect();
								InputStream stream = connection.getInputStream();
								DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
								DocumentBuilder db = dbf.newDocumentBuilder();
								Document doc = db.parse(stream);
								doc.getDocumentElement().normalize();
								Node node = doc.getElementsByTagName("baseName").item(0);
								url = new URL("http://128.214.112.107/pmg/viewer/images/"+node.getTextContent().replace("photo", "square"));
								connection  = (HttpURLConnection) url.openConnection();
								InputStream is = connection.getInputStream();
								icon_bitmap = BitmapFactory.decodeStream(is);  			
								node = doc.getElementsByTagName("fileId").item(0);
								fileId = Integer.parseInt(node.getTextContent());
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (icon_bitmap!=null){
								images.remove(0);
								image_urls.remove(0);
								image_fileId.remove(0);
								images.add(icon_bitmap);
								image_urls.add(url);
								image_fileId.add(fileId);
							}
						}
						oldLoaction = currentLocation;
						methodLock = false;
					}
				}
				).start();
				//==============================================
		}
	}
}
