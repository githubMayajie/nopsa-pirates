package hiit.nopsa.pirate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.content.Context;
import android.util.Log;

/**
 * A singleton class which holds all the data related to the game.
 * Values are loaded at the game start and saves at the end of the game
 * 
 * @author Dinesh Wijekoon
 */
public class GameStatus {
	private String TAG = "NOPSA-P";
	
	private int ship_class;
	private int weapon_class;
	private int sails_class;
	private int num_crew;
	private int coins;
	private int total_food_score;
	
	private int num_animals;
	private int num_slaves;
	private int num_food;
	
	private ArrayList<Collectable> foods = new ArrayList<Collectable>();
	private ArrayList<Collectable> slaves = new ArrayList<Collectable>();
	private ArrayList<Collectable> animals = new ArrayList<Collectable>();
		
	private long lastTimeUpdated;
	
	private int user_id;
	private String user_name;
	
	// How much time left to goto next island. This can be vary from 3 min to 10 min.
	// This saves the time in seconds 
	private int timeOfNextIsland; 

	// If instructions is true : then system pop instructions in every screen when 
	//                           every screen is loading
	private boolean instructions;
	private boolean sounds;
	private boolean haptics;
	
	// This is set to TRUE when game is on sailing mode
	private boolean gameOn;
	
	// When game is running. This set to false when player exit game
	public boolean gameIsRunning = true;
	

	// ================ Made this a SINGLETON ===============================
	private static GameStatus gameStatus;
	private GameStatus(){}
	public static GameStatus getGameStatusObject(){
		if (gameStatus == null)
			gameStatus = new GameStatus();
	      	return gameStatus;
	}
	// ================ END of SINGLETON ====================================

	public boolean isGameOn() {
		return gameOn;
	}
	public void setGameOn(boolean gameOn) {
		this.gameOn = gameOn;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public boolean isSounds() {
		return sounds;
	}
	public void setSounds(boolean sounds) {
		this.sounds = sounds;
	}
	public boolean isHaptics() {
		return haptics;
	}
	public void setHaptics(boolean haptics) {
		this.haptics = haptics;
	}
	public ArrayList<Collectable> getCollectableFromId(int id){
		switch (id) {
		case 0:
			return animals;
		case 1:
			return slaves;
		case 2:
			return foods;
		}
		return null;
	}
	public boolean removeItemFromTypeAndId(int type, int id){
		synchronized (gameStatus) {
			switch (type) {
			case 0:
				//return animals;
				for (Iterator<Collectable> iter = animals.iterator(); iter.hasNext();) {
				      Collectable s = iter.next();
				      if (s.equals(animals.get(id))){
				    	  iter.remove();
				    	  return true;
				      }
				}
				break;
			case 1:
				//return slaves;
				for (Iterator<Collectable> iter = slaves.iterator(); iter.hasNext();) {
				      Collectable s = iter.next();
				      if (s.equals(slaves.get(id))){
				    	  iter.remove();
				    	  return true;
				      }
				}
				break;
			case 2:
				//return foods;
				for (Iterator<Collectable> iter = foods.iterator(); iter.hasNext();) {
				      Collectable s = iter.next();
				      if (s.equals(foods.get(id))){
				    	  iter.remove();
				    	  return true;
				      }
				}
				break;
			}
		}
		return false;
	}
	
	
	public void addCollectableFromId(int id, Collectable c){
		switch (id) {
		case 0:
			animals.add(c);
			num_animals = num_animals+1;
			break;
		case 1:
			slaves.add(c);
			num_slaves = num_slaves+1;
			break;
		case 2:
			foods.add(c);
			num_food = num_food+1;
			break;
		}
	}
	public ArrayList<Collectable> getFoods() {
		return foods;
	}
	public void setFoods(ArrayList<Collectable> foods) {
		this.foods = foods;
	}
	public ArrayList<Collectable> getSlaves() {
		return slaves;
	}
	public void setSlaves(ArrayList<Collectable> slaves) {
		this.slaves = slaves;
	}
	public ArrayList<Collectable> getAnimals() {
		return animals;
	}
	public void setAnimals(ArrayList<Collectable> animals) {
		this.animals = animals;
	}
	public boolean getInstructions() {
		return instructions;
	}
	public void setInstructions(boolean instructions) {
		Log.d(TAG,"SET Instruction :"+instructions);
		this.instructions = instructions;
	}
	public int getShip_class() {
		return ship_class;
	}
	public void setShip_class(int ship_class) {
		this.ship_class = ship_class;
	}
	public int getWeapon_class() {
		return weapon_class;
	}
	public void setWeapon_class(int weapon_class) {
		this.weapon_class = weapon_class;
	}
	public int getSails_class() {
		return sails_class;
	}
	public void setSails_class(int sails_class) {
		this.sails_class = sails_class;
	}
	public int getNum_crew() {
		return num_crew;
	}
	public void setNum_crew(int num_crew) {
		this.num_crew = num_crew;
	}
	public int getCoins() {
		return coins;
	}
	public void setCoins(int coins) {
		this.coins = coins;
	}
	public int getNum_animals() {
		return num_animals;
	}
	public void setNum_animals(int num_animals) {
		this.num_animals = num_animals;
	}
	public int getNum_slaves() {
		return num_slaves;
	}
	public void setNum_slaves(int num_slaves) {
		this.num_slaves = num_slaves;
	}
	public int getNum_food() {
		return num_food;
	}
	public void setNum_food(int num_food) {
		this.num_food = num_food;
	}
	public int getTotal_food_score() {
		return total_food_score;
	}
	public void setTotal_food_score(int total_food_score) {
		this.total_food_score = total_food_score;
	}
	public void eat_one_food(){
		if (foods.size()<1)
			return;
		this.setTotal_food_score(this.getTotal_food_score()-1);
		foods.get(0).setScore(Math.max(foods.get(0).getScore()-1,0));
		if (foods.get(0).getScore()<=0)
			foods.remove(0);
	}
	public long getLastTimeUpdated() {
		return lastTimeUpdated;
	}
	public void setLastTimeUpdated(long lastTimeUpdated) {
		this.lastTimeUpdated = lastTimeUpdated;
	}
	public int getTimeOfNextIsland() {
		if (timeOfNextIsland<0)
			return 0;
		return timeOfNextIsland;
	}
	public void setTimeOfNextIsland(int timeOfNextIsland) {
		this.timeOfNextIsland = timeOfNextIsland;
	}	
	
	private void startAutoSaving(final Context context){
	//TODO
	
	}
	
	// Load Game Status
	public void loadGameData(Context context){	
		String[] gameData;
		StringBuffer strContent;
		try {
			FileInputStream fis = context.openFileInput("game.dat");
			Log.d(TAG,"FILE OPEN FOR READ");
			int ch;
		    strContent = new StringBuffer("");
		    try {
				while((ch=fis.read()) != -1)
				    strContent.append((char)ch);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d(TAG,"THE GAME DATA STRING :==:==:"+strContent.toString());
			gameData = strContent.toString().split(";");
			// Load Data to GameStatus Object
			gameStatus = GameStatus.getGameStatusObject();
			gameStatus.setShip_class(Integer.parseInt(gameData[0].split(",")[1]));
			gameStatus.setWeapon_class(Integer.parseInt(gameData[1].split(",")[1]));
			gameStatus.setSails_class(Integer.parseInt(gameData[2].split(",")[1]));
			gameStatus.setNum_crew(Integer.parseInt(gameData[3].split(",")[1]));
			gameStatus.setCoins(Integer.parseInt(gameData[4].split(",")[1]));	
			if (Long.parseLong(gameData[5].split(",")[1])==0){
				Date d = new Date();
				gameStatus.setLastTimeUpdated(d.getTime());
			}
			else {
				gameStatus.setLastTimeUpdated(Long.parseLong((gameData[5].split(",")[1])));
			}
			Log.d(TAG,"Last UPDATED TIME :==:==:"+gameStatus.getLastTimeUpdated());
			gameStatus.setTimeOfNextIsland(Integer.parseInt(gameData[6].split(",")[1]));
			gameStatus.setInstructions(Boolean.parseBoolean(gameData[7].split(",")[1]));
			// instructions,true;sounds,true;haptics,true;" +"user_id,0,user_name, ";
			gameStatus.setSounds(Boolean.parseBoolean(gameData[8].split(",")[1]));
			gameStatus.setHaptics(Boolean.parseBoolean(gameData[9].split(",")[1]));
			gameStatus.setUser_id(Integer.parseInt(gameData[10].split(",")[1]));
			gameStatus.setUser_name(gameData[11].split(",")[1]);
			Log.d(TAG,"BOOOO NEW STUFF---Z"+gameStatus.getUser_id());
			//============================= Loading other data
			gameStatus.setNum_animals(0);
			gameStatus.setNum_food(0);
			gameStatus.setNum_slaves(0);
			gameStatus.setTotal_food_score(0);
			loadFoodData(context);
			loadSlaveData(context);
			loadAnimalData(context);
			startAutoSaving(context);
		} catch (FileNotFoundException e) {
			try {
				FileOutputStream fos = context.openFileOutput("game.dat", Context.MODE_WORLD_WRITEABLE);
				Log.d(TAG,"FILE NEWLY CREATED");
				String game_data = 
						"ship_class,1;weapon_class,1;sails_class,1;num_crew,3;coins,1000;" +
						"lastTimeUpdated,0;timeOfNextIsland,300;instructions,true;sounds,true;haptics,true;" +
						"user_id,0;user_name,new_pirate";
				try {
					fos.write(game_data.getBytes());
					fos.flush();
					fos.close();
					this.loadGameData(context);	// Method is called again in order to load Data
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	public void saveGameData(Context context){
		// ===================== SAVE GAME DATA ==================================================
		try{
			FileOutputStream fos = context.openFileOutput("game.dat", Context.MODE_WORLD_WRITEABLE);
			Log.d(TAG,"FILE OPEN FOR UPDATE");
			String game_data = 
					"ship_class,"+this.getShip_class()+ 
					";weapon_class,"+this.getWeapon_class()+
					";sails_class,"+this.getSails_class()+
					";num_crew,"+this.getNum_crew()+
					";coins,"+this.getCoins()+
					";lastTimeUpdated,"+this.getLastTimeUpdated()+
					";timeOfNextIsland,"+this.getTimeOfNextIsland()+
					";instructions,"+this.getInstructions()+
					";sounds,"+this.isSounds()+
					";haptics,"+this.isHaptics()+
					";user_id,"+this.getUser_id()+
					";user_name,"+this.getUser_name();
			fos.write(game_data.getBytes());
			fos.flush();
			fos.close();
			Log.d(TAG,"Gave Status Saved");
		}catch (Exception e) {
			Log.d(TAG, "GAME FILE SAVING FAILED");
		}
		// ===================== SAVE FOOD DATA ==================================================
		try{
			FileOutputStream fos = context.openFileOutput("food.dat", Context.MODE_WORLD_WRITEABLE);
			Log.d(TAG,"FOOD FILE OPEN FOR UPDATE");
			String food_data="";
			for (int i=0;i<foods.size();i++){
				food_data = food_data + 
					foods.get(i).getIcon_url()+";"+
					foods.get(i).getScore()+";"+
					foods.get(i).getTag()+";"+
					foods.get(i).getLast_img_marked()+"\n";
			}
				fos.write(food_data.getBytes());
				fos.flush();
				fos.close();
				Log.d(TAG,"Food Status Saved");
		}catch (Exception e) {
			Log.d(TAG, "FOOD FILE SAVING FAILED");
		}
		//===================== SAVE ANIMAL DATA ==================================================
		try{
			FileOutputStream fos = context.openFileOutput("animal.dat", Context.MODE_WORLD_WRITEABLE);
			Log.d(TAG,"ANIMAL FILE OPEN FOR UPDATE");
			String animal_data="";
			for (int i=0;i<animals.size();i++){
				animal_data = animal_data + 
					animals.get(i).getIcon_url()+";"+
					animals.get(i).getScore()+";"+
					animals.get(i).getTag()+";"+
					animals.get(i).getLast_img_marked()+"\n";
			}
				fos.write(animal_data.getBytes());
				fos.flush();
				fos.close();
				Log.d(TAG,"Animal Status Saved");
		}catch (Exception e) {
			Log.d(TAG, "ANIMAL FILE SAVING FAILED");
		}
		//===================== SAVE SLAVES DATA ==================================================
		try{
			FileOutputStream fos = context.openFileOutput("slave.dat", Context.MODE_WORLD_WRITEABLE);
			Log.d(TAG,"SLAVE FILE OPEN FOR UPDATE");
			String slave_data="";
			for (int i=0;i<slaves.size();i++){
				slave_data = slave_data + 
					slaves.get(i).getIcon_url()+";"+
					slaves.get(i).getScore()+";"+
					slaves.get(i).getTag()+";"+
					slaves.get(i).getLast_img_marked()+"\n";
			}
				fos.write(slave_data.getBytes());
				fos.flush();
				fos.close();
				Log.d(TAG,"Slave Status Saved");
		}catch (Exception e) {
			Log.d(TAG, "SLAVE FILE SAVING FAILED");
		}
	}
	
	public void loadFoodData(Context context){
		gameStatus = GameStatus.getGameStatusObject();
		String[] foodData;
		StringBuffer strContent;
		foods = new ArrayList<Collectable>();
		try {
			FileInputStream fis = context.openFileInput("food.dat");
			Log.d(TAG,"FOOD FILE OPEN FOR READ");
			int ch;
		    strContent = new StringBuffer("");
		    try {
				while((ch=fis.read()) != -1)
				    strContent.append((char)ch);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (strContent.length()>0){	
				Log.d(TAG,strContent.toString());
				// Load Data to foods arraylist
				foodData = strContent.toString().split("\n");
				Collectable food;
				for (int i=0;i<foodData.length;i++){
					food = new Collectable();
					food.setIcon_url(foodData[i].split(";")[0]);
					food.setScore(Integer.parseInt(foodData[i].split(";")[1]));
					food.setTag(foodData[i].split(";")[2]);
					food.setLast_img_marked(Integer.parseInt(foodData[i].split(";")[3]));
					foods.add(food);
					// Set Number of Food Items 
					gameStatus.setNum_food(gameStatus.getNum_food()+1);
					gameStatus.setTotal_food_score((gameStatus.getTotal_food_score()+food.getScore()));
				}
				Log.d(TAG,"Game Score"+gameStatus.getTotal_food_score());
			}
			else {
				Log.d(TAG,"NO FOOD DATA TO LOAD");
				gameStatus.setNum_food(0);
				gameStatus.setTotal_food_score(0);
			}
		} catch (FileNotFoundException e) {
			try {
				FileOutputStream fos = context.openFileOutput("food.dat", Context.MODE_WORLD_WRITEABLE);
				Log.d(TAG,"FOOD FILE NEWLY CREATED");
				String food_data = 
						"http://128.214.112.107/pmg/viewer/images/square_3233710827_34294f21b1_t.jpg;100;+apple;0";
				try {
					fos.write(food_data.getBytes());
					fos.flush();
					fos.close();
					this.loadFoodData(context);	// Method is called again in order to load Data
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}	
	}

	public void loadSlaveData(Context context){
		gameStatus = GameStatus.getGameStatusObject();
		String[] slaveData;
		StringBuffer strContent;
		slaves = new ArrayList<Collectable>();
		try {
			FileInputStream fis = context.openFileInput("slave.dat");
			Log.d(TAG,"SLAVE FILE OPEN FOR READ");
			int ch;
		    strContent = new StringBuffer("");
		    try {
				while((ch=fis.read()) != -1)
				    strContent.append((char)ch);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (strContent.length()>0){	
				Log.d(TAG,strContent.toString());
				// Load Data to foods arraylist
				slaveData = strContent.toString().split("\n");
				Collectable slave;
				for (int i=0;i<slaveData.length;i++){
					slave = new Collectable();
					slave.setIcon_url(slaveData[i].split(";")[0]);
					slave.setScore(Integer.parseInt(slaveData[i].split(";")[1]));
					slave.setTag(slaveData[i].split(";")[2]);
					slave.setLast_img_marked(Integer.parseInt(slaveData[i].split(";")[3]));
					slaves.add(slave);
					gameStatus.setNum_slaves(gameStatus.getNum_slaves()+1);
				}
			}else{
				Log.d(TAG,"NO SLAVE DATA TO LOAD");
				gameStatus.setNum_slaves(0);
			}
		} catch (FileNotFoundException e) {
			try {
				FileOutputStream fos = context.openFileOutput("slave.dat", Context.MODE_WORLD_WRITEABLE);
				Log.d(TAG,"SLAVE FILE NEWLY CREATED");
				String slave_data = 
						"http://128.214.112.107/pmg/viewer/images/square_3802901392_9c0b3b9820_t.jpg;100;+man;0";
				try {
					fos.write(slave_data.getBytes());
					fos.flush();
					fos.close();
					this.loadSlaveData(context);	// Method is called again in order to load Data
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}	
	}

	public void loadAnimalData(Context context){
		gameStatus = GameStatus.getGameStatusObject();
		String[] animalData;
		StringBuffer strContent;
		animals = new ArrayList<Collectable>();
		try {
			FileInputStream fis = context.openFileInput("animal.dat");
			Log.d(TAG,"ANIMAL FILE OPEN FOR READ");
			int ch;
		    strContent = new StringBuffer("");
		    try {
				while((ch=fis.read()) != -1)
				    strContent.append((char)ch);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (strContent.length()>0){	
				Log.d(TAG,strContent.toString());
				// Load Data to foods arraylist
				animalData = strContent.toString().split("\n");
				Collectable animal;
				for (int i=0;i<animalData.length;i++){
					animal = new Collectable();
					animal.setIcon_url(animalData[i].split(";")[0]);
					animal.setScore(Integer.parseInt(animalData[i].split(";")[1]));
					animal.setTag(animalData[i].split(";")[2]);
					animal.setLast_img_marked(Integer.parseInt(animalData[i].split(";")[3]));
					animals.add(animal);
					gameStatus.setNum_animals(gameStatus.getNum_animals()+1);
				}
			}else{
				Log.d(TAG,"NO ANIMAL DATA TO LOAD");
				gameStatus.setNum_animals(0);
			}
		} catch (FileNotFoundException e) {
			try {
				FileOutputStream fos = context.openFileOutput("animal.dat", Context.MODE_WORLD_WRITEABLE);
				Log.d(TAG,"ANIMAL FILE NEWLY CREATED");
				String animal_data = 
						"http://128.214.112.107/pmg/viewer/images/square_132750728_8f0342f1ac_t.jpg;100;+monkey;0";
				try {
					fos.write(animal_data.getBytes());
					fos.flush();
					fos.close();
					this.loadAnimalData(context);	// Method is called again in order to load Data
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}	
	}
}
