package boxscores;

import java.io.IOException;

import com.dota2.proto.Demo.CDemoFileInfo;

import skadistats.clarity.Clarity;

public class Main {
	public static void main(String[] args) {
		try {
			CDemoFileInfo i = Clarity.infoForFile(args[0]);
			
			System.out.println(i.getAllFields());
			System.out.println(i.getPlaybackFrames());
			System.out.println(i.getPlaybackTicks());
			System.out.println(i.getPlaybackTime());
			System.out.println(i.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
