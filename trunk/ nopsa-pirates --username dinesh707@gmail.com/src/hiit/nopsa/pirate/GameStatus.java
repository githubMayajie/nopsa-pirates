package hiit.nopsa.pirate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


import android.content.Context;
import android.util.Log;

public class GameStatus {
	private String TAG = "NOPSA-P";
	
	private int ship_class;
	private int weapon_class;
	private int sails_class;
	private int num_crew;
	private int coins;
	
	private int num_animals;
	private int num_slaves;
	private int num_food;
	
	private ArrayList<Collectable> foods;
	private ArrayList<Collectable> slaves;
	private ArrayList<Collectable> animals;
	
	private int total_food_score;
	
	private long lastTimeUpdated;
	
	// How much time left to goto next island. This can be vary from 3 min to 10 min.
	// This saves the time in seconds 
	private int timeOfNextIsland; 

	// If instructions is true : then system pop instructions in every screen when 
	//                           every screen is loading
	private boolean instructions;
	
	// This is set to TRUE when game is on sailing mode
	private boolean gameOn;
	

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
	public long getLastTimeUpdated() {
		return lastTimeUpdated;
	}
	public void setLastTimeUpdated(long lastTimeUpdated) {
		this.lastTimeUpdated = lastTimeUpdated;
	}
	public int getTimeOfNextIsland() {
		return timeOfNextIsland;
	}
	public void setTimeOfNextIsland(int timeOfNextIsland) {
		this.timeOfNextIsland = timeOfNextIsland;
	}	
	
	// Load Game Status
	public void loadGameData(Context context){
		// Loading other data
		loadFoodData(context);
		loadSlaveData(context);
		loadAnimalData(context);
		
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
			Log.d(TAG,strContent.toString());
			gameData = strContent.toString().split(";");
			// Load Data to GameStatus Object
			gameStatus = GameStatus.getGameStatusObject();
			gameStatus.setShip_class(Integer.parseInt(gameData[0].split(",")[1]));
			gameStatus.setWeapon_class(Integer.parseInt(gameData[1].split(",")[1]));
			gameStatus.setSails_class(Integer.parseInt(gameData[2].split(",")[1]));
			gameStatus.setNum_crew(Integer.parseInt(gameData[3].split(",")[1]));
			gameStatus.setCoins(Integer.parseInt(gameData[4].split(",")[1]));
			gameStatus.setNum_animals(Integer.parseInt(gameData[5].split(",")[1]));
			gameStatus.setNum_slaves(Integer.parseInt(gameData[6].split(",")[1]));
			gameStatus.setNum_food(Integer.parseInt(gameData[7].split(",")[1]));
			gameStatus.setTotal_food_score(Integer.parseInt(gameData[8].split(",")[1]));			
			if (Long.parseLong(gameData[9].split(",")[1])==0){
				Date d = new Date();
				gameStatus.setLastTimeUpdated(d.getTime());
			}
			gameStatus.setTimeOfNextIsland(Integer.parseInt(gameData[10].split(",")[1]));
			gameStatus.setInstructions(Boolean.parseBoolean(gameData[11].split(",")[1]));
			
		} catch (FileNotFoundException e) {
			try {
				FileOutputStream fos = context.openFileOutput("game.dat", Context.MODE_WORLD_WRITEABLE);
				Log.d(TAG,"FILE NEWLY CREATED");
				String game_data = 
						"ship_class,1;weapon_class,1;sails_class,1;num_crew,3;coins,1000;" +
						"num_animals,0;num_slaves,0;num_food,0;total_food_score,0;" +
						"lastTimeUpdated,0;timeOfNextIsland,45;instructions,true";
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
					";num_animals,"+this.getNum_animals()+
					";num_slaves,"+this.getNum_slaves()+
					";num_food,"+this.getNum_food()+
					";total_food_score,"+this.getTotal_food_score()+
					";lastTimeUpdated,"+this.getLastTimeUpdated()+
					";timeOfNextIsland,"+this.getTimeOfNextIsland()+
					";instructions,"+this.getInstructions();
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
		String[] foodData;
		StringBuffer strContent;
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
			Log.d(TAG,strContent.toString());
			// Load Data to foods arraylist
			foodData = strContent.toString().split("\n");
			Collectable food;
			foods = new ArrayList<Collectable>();
			for (int i=0;i<foodData.length;i++){
				food = new Collectable();
				food.setIcon_url(foodData[i].split(";")[0]);
				food.setScore(Integer.parseInt(foodData[i].split(";")[1]));
				food.setTag(foodData[i].split(";")[2]);
				food.setLast_img_marked(Integer.parseInt(foodData[i].split(";")[3]));
				foods.add(food);
			}
		} catch (FileNotFoundException e) {
			try {
				FileOutputStream fos = context.openFileOutput("food.dat", Context.MODE_WORLD_WRITEABLE);
				Log.d(TAG,"FOOD FILE NEWLY CREATED");
				String food_data = 
						"http://nopsa.hiit.fi/viewer/images/thumb_3233710827_34294f21b1_t.jpg;100;apple;0";
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
		String[] slaveData;
		StringBuffer strContent;
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
			Log.d(TAG,strContent.toString());
			// Load Data to foods arraylist
			slaveData = strContent.toString().split("\n");
			Collectable slave;
			slaves = new ArrayList<Collectable>();
			for (int i=0;i<slaveData.length;i++){
				slave = new Collectable();
				slave.setIcon_url(slaveData[i].split(";")[0]);
				slave.setScore(Integer.parseInt(slaveData[i].split(";")[1]));
				slave.setTag(slaveData[i].split(";")[2]);
				slave.setLast_img_marked(Integer.parseInt(slaveData[i].split(";")[3]));
				slaves.add(slave);
			}
		} catch (FileNotFoundException e) {
			try {
				FileOutputStream fos = context.openFileOutput("slave.dat", Context.MODE_WORLD_WRITEABLE);
				Log.d(TAG,"SLAVE FILE NEWLY CREATED");
				String slave_data = 
						"http://nopsa.hiit.fi/viewer/images/thumb_3802901392_9c0b3b9820_t.jpg;100;man;0";
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
		String[] animalData;
		StringBuffer strContent;
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
			Log.d(TAG,strContent.toString());
			// Load Data to foods arraylist
			animalData = strContent.toString().split("\n");
			Collectable animal;
			animals = new ArrayList<Collectable>();
			for (int i=0;i<animalData.length;i++){
				animal = new Collectable();
				animal.setIcon_url(animalData[i].split(";")[0]);
				animal.setScore(Integer.parseInt(animalData[i].split(";")[1]));
				animal.setTag(animalData[i].split(";")[2]);
				animal.setLast_img_marked(Integer.parseInt(animalData[i].split(";")[3]));
				animals.add(animal);
			}
		} catch (FileNotFoundException e) {
			try {
				FileOutputStream fos = context.openFileOutput("animal.dat", Context.MODE_WORLD_WRITEABLE);
				Log.d(TAG,"ANIMAL FILE NEWLY CREATED");
				String animal_data = 
						"http://nopsa.hiit.fi/viewer/images/thumb_132750728_8f0342f1ac_t.jpg;100;monkey;0";
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
