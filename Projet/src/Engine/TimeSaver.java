package Engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public final class TimeSaver implements Serializable {
	
	private static TimeSaver save;
	
	private HashMap<Integer, Integer> times;
	
	public TimeSaver() {
		times = new HashMap<Integer, Integer>();
		times.put(0, 0);
	}
	
	public void setTime(int level, int time) {
		if(times.containsKey(level-1)) {
			if(times.get(level-1) == 0)
				times.replace(level-1, time);
			else
				times.replace(level-1, Math.min(times.get(level-1), time));
		}
		else times.put(level-1, time);
	}
	
	public int getTime(int level) {
		if(!times.containsKey(level-1)) return -1;
		return times.get(level-1);
	}
	
	public HashMap<Integer, Integer> getTimes() {
		return times;
	}

	public static TimeSaver load() {
		if(save != null) return save;
		
		try {
			FileInputStream fileIn = new FileInputStream("save/time.save");
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			
			save = (TimeSaver)objIn.readObject();
			
			objIn.close();
			fileIn.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Creating new file");
			save = new TimeSaver();
			TimeSaver.save();
			return save;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return save;
	}
	
	public static void save() {
		try {
			File dir = new File("save");
			if(!dir.exists() || !dir.isDirectory()) {
				dir.mkdir();
			}
			
			FileOutputStream fileOut = new FileOutputStream("save/time.save");
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(save);
			objOut.close();
			fileOut.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static long startTime; 
	public static void startTimer() {
		startTime = System.currentTimeMillis();
	}
	
	public static int stopTimer() {
		return (int)(System.currentTimeMillis() - startTime);
	}
	
	public static String convertTimeToString(int time) {
		
		int min = (int)(time / 60000);
		int sec = (int)((time - min*60000)/1000);
		int milli = (int)(time - min*60000 - sec*1000);
		
		//System.out.println(min + ":" + sec + ":" + milli);
		
		return String.format("%02d", min) + ":" + String.format("%02d", sec) + ":" + String.format("%03d", milli);
	}
	
}
