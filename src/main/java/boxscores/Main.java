package boxscores;

import java.io.IOException;
import java.util.ArrayList;

import boxscores.GameEvent.Type;

import com.dota2.proto.Demo.CDemoFileInfo;
import com.dota2.proto.DotaUsermessages.CDOTAUserMsg_ChatEvent;
import com.dota2.proto.Netmessages.CNETMsg_Tick;

import skadistats.clarity.Clarity;
import skadistats.clarity.match.ChatEventCollection;
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

			// I tried to slim this down to be something other than ALL,
			// but couldn't seem to find a Profile preset that generated
			// playerResource data. So, sticking with this. It's not that
			// slow.
			DemoInputStreamIterator iter = Clarity.iteratorForFile(args[0], Profile.ALL);

			Match match = new Match();

			int lastGameTime = 0;

			// we need to have a comprehensive data structure here.
			// for every tick, add an entry in the metadata list that contains
			// a dictionary of fields. It will be an array, with each
			// element a HashTable from string to ... ugh java is the 
			// worst. For each time stamp it needs to have:
			//  0. timestamp
			//	1. radiant_total_gold -> integer
			//	2. dire_total_gold -> integer
			//  3. some number of events
			//		events are associated with a team.
			//		we're going to attribute events positively;
			//		kills by dire go to dire
			//		tower kills by dire go to dire
			//		barrack kills by dire to go dire
			// 		so each event has a type (eg 'tower_kill', 'tower_deny', 'hero_kill', etc)
			//		a value
			//		and a team
			// annoyingly, I think I need to make an object for this.

			ArrayList<Tick> ticks = new ArrayList<Tick>();

			while (iter.hasNext()) {
				Peek p = iter.next();

				// old placeholder code for reading actual combatlog data.
				// ideally, we won't need to do this to get the information we need,
				// since it's pretty messy.
				//
				// but just for the sake of not forgetting what I know about this...
				// type==4 checks for death notices (iirc, type 1 is damage, type 2 and 3
				// related to auras being added/removed) 
				// and looking at targetname < 10 is assuming (potentially wrongly) that
				// entities 0-9 are the playerids, and we're just interested in when
				// they die. 
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

					Tick t = new Tick(match.getReplayTime());
					// once per tick, check the chat message list.
					ChatEventCollection cec = match.getChatEvents();

					for(CDOTAUserMsg_ChatEvent e : cec) {
						// we're looking for four things here:
						// CHAT_MESSAGE_HERO_KILL
						// CHAT_MESSAGE_TOWER_KILL
						// CHAT_MESSAGE_TOWER_DENY
						// CHAT_MESSAGE_BARRACKS_KILL

						// if we see any of those, log them with the timestamp
						// so it's available for the viz later.
						if(e.getType().toString().equals("CHAT_MESSAGE_HERO_KILL")) {
						} else if(e.getType().toString().equals("CHAT_MESSAGE_TOWER_KILL")) {
						} else if(e.getType().toString().equals("CHAT_MESSAGE_TOWER_DENY")) {
						} else if(e.getType().toString().equals("CHAT_MESSAGE_BARRACKS_KILL")) {
						}
					}

					//  sum up all the gold of all the players on radiant and dire.
					int snapshotRadiantGold = 0;
					int snapshotDireGold = 0;

					if(match.getPlayerResource()!=null) {
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
	
						GameEvent radiantGold = new GameEvent(Type.TOTAL_GOLD, Team.RADIANT);
						radiantGold.value = snapshotRadiantGold; 
	
						GameEvent direGold = new GameEvent(Type.TOTAL_GOLD, Team.DIRE);
						direGold.value = snapshotDireGold; 
	
						t.addEvent(radiantGold);
						t.addEvent(direGold);
					}
					
					ticks.add(t);
				}


				// AFTER we've done the analysis, apply the message.
				// if we do this before, we won't see all the messages or events or whatever from 
				// the most recent tick.
				p.apply(match);
			}

			// when we're done, dump the data
			System.out.println(ticks);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}
