import java.awt.Toolkit;

import acm.graphics.*;
import acm.program.*;

public class Video extends GraphicsProgram{


	private static final long serialVersionUID = 1L;
	private int PAUSE_TIME = 100;
	
	public void run(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while(true){
			Sound bounce = new Sound("BallBouncingSound.wav");
			for(int i = 0; i<12; i++){
				if(i == 5)bounce.start();
				GImage toAdd = new GImage("BouncingBall" + i + ".jpg");
				add(toAdd);
				pause(PAUSE_TIME);
				remove(toAdd);
			}
			bounce = new Sound("BallBouncingSound.wav");
			bounce.start();
			bounce = new Sound("BallBouncingSound.wav");
			for(int i = 10; i>0; i--){
				if(i == 5)bounce.start();
				GImage toAdd = new GImage("BouncingBall" + i + ".jpg");
				add(toAdd);
				pause(PAUSE_TIME);
				remove(toAdd);
			}
			bounce = new Sound("BallBouncingSound.wav");
			bounce.start();
		}
	}
	
	
}
