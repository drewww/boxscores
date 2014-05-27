package boxscores;

import java.io.IOException;
import java.util.ArrayList;

import com.dota2.proto.Demo.CDemoFileInfo;
import com.dota2.proto.Netmessages.CNETMsg_Tick;

import skadistats.clarity.Clarity;
import skadistats.clarity.match.ChatEventCollection;
import skadistats.clarity.match.GameEventCollection;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.GameEvent;
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

			// I tried to slim this down to be something other than ALL,
			// but couldn't seem to find a Profile preset that generated
			// playerResource data. So, sticking with this. It's not that
			// slow.
			DemoInputStreamIterator iter = Clarity.iteratorForFile(args[0], Profile.ALL);

			Match match = new Match();

			int lastGameTime = 0;

			ArrayList<Integer> radiantGold = new ArrayList<Integer>();
			ArrayList<Integer> direGold = new ArrayList<Integer>();

			while (iter.hasNext()) {
				Peek p = iter.next();
					//
					//		            GameEventCollection ec = match.getGameEvents();
					//		            for(GameEvent e : ec) {
					//	            		if(e.getName().equals("dota_combatlog")) {
					//	            			if(((Integer)e.getProperty("type"))==4) {
					//	            				if(((Integer)e.getProperty("targetname")<10)) {
					////	            					System.out.println(e);
					//	            				}
					//	            			}
					//	            		}
					//		            }

				// CNETMsg_Tick messages trigger once per simulation tick.
				// the Match object accumulates activities within that tick,
				// so we don't want to ask it what happened until the tick 
				// is over, otherwise we see multiple instances of every event.
				// eg, if there's a chat message, every MESSAGE after that
				// chat message comes in will turn up a note about that message,
				// generating lots of duplicates. Instead, we'll wait for the
				// tick to end and just look once for actions we care about
				// that took place during the tick.
				if (p.getMessage() instanceof CNETMsg_Tick) {
					// once per tick, check the chat message list.
					ChatEventCollection cec = match.getChatEvents();

					for(Object e : cec) {
						System.out.println(e);
					}

					// check and see if we're at least a second beyond the last time we grabbed data.
					// we could do this every tick, but that's still more often than we really 
					// need to be checking. Even once a second is probably overkill.
					if(Math.floor(match.getGameTime()) > lastGameTime) {	
						lastGameTime = (int) Math.floor(match.getGameTime());
						// if yes, sum up all the gold of all the players on radiant and dire.
						int snapshotRadiantGold = 0;
						int snapshotDireGold = 0;

						for(int i=0; i<10; i++) {
							// property name missing the last two digits, which we'll
							// add on manually.
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

				// AFTER we've done the analysis, apply the message.
				// if we do this before, we won't see all the messages or events or whatever from 
				// the most recent tick.
				p.apply(match);
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}
