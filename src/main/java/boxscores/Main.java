package boxscores;

import java.io.IOException;
import java.util.ArrayList;

import com.dota2.proto.Demo.CDemoFileInfo;

import skadistats.clarity.Clarity;
import skadistats.clarity.match.Match;
import skadistats.clarity.parser.DemoInputStreamIterator;
import skadistats.clarity.parser.Peek;
import skadistats.clarity.parser.Profile;

public class Main {
	public static void main(String[] args) {
		try {
			CDemoFileInfo info = Clarity.infoForFile(args[0]);
			
			System.out.println(info.getAllFields());
			System.out.println(info.getPlaybackFrames());
			System.out.println(info.getPlaybackTicks());
			System.out.println(info.getPlaybackTime());
			System.out.println(info.toString());
			
			DemoInputStreamIterator iter = Clarity.iteratorForFile(args[0], Profile.ALL);
			
			Match match = new Match();
			
			int lastGameTime = 0;
			
			ArrayList<Integer> radiantGold = new ArrayList<Integer>();
			ArrayList<Integer> direGold = new ArrayList<Integer>();
			
	        while (iter.hasNext()) {
	            Peek p = iter.next();
	            p.apply(match);
	            
	            if(match.getPlayerResource()!=null) {
	            	
	            	// check and see if we're at least a second beyond the last time we grabbed data.
	            	if(Math.floor(match.getGameTime()) > lastGameTime) {
	            		lastGameTime = (int) Math.floor(match.getGameTime());
	            		// if yes, sum up all the gold of all the players on radiant and dire.
	            		int snapshotRadiantGold = 0;
	            		int snapshotDireGold = 0;
	            		
	            		for(int i=0; i<10; i++) {
	            			// propety name missing the last two digits
	            			String property = "EndScoreAndSpectatorStats.m_iTotalEarnedGold.00";
	            			
	            			// now 
	            			property = property + String.format("%02d", i);
	            			
	            			int gold = match.getPlayerResource().getProperty(property);
	            			
	            			if(i >= 5) {
	            				snapshotDireGold += gold;
	            			} else {
	            				snapshotRadiantGold += gold;
	            			}	            			
	            		}
	            		
            			System.out.println("R " + snapshotRadiantGold + " - " + snapshotDireGold + " D");
	            	}
	            }
	        }
	        
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
