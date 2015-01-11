import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import acm.graphics.GLine;
import acm.graphics.GOval;
import acm.graphics.GPoint;
import acm.graphics.GRect;
import acm.program.*;
import acm.util.RandomGenerator;


public class OtherBounce extends GraphicsProgram{

	private static final long serialVersionUID = 1L;
	
	private static final int BALL_RADIUS = 10;
	private static final Color BALL_COLOR = Color.RED;
	private static final int FRAME_X = 0;
	private static final int FRAME_Y = -100;
	
	private static final double GRAVITY = 1.1;
	private static final double YBOUNCE_RATIO = 0.75;
	private static final double XBOUNCE_RATIO = 0.75;
	private static final int YSMALL_BOUNCE = 3;
	private static final int XSMALL_VELOCITY = 1;
	private static final double XFRICTION = .95;
	private static final int PAUSE_TIME = 50;
	
	private static final int SPEED_RATIO = 50;
	private GPoint lastPoint;
	private long lastTime;
	
	private LinkedList<Ball> balls;
	private GRect frame;
	private Ball lastBall, newBall;
	
	public void run(){
		balls = new LinkedList<Ball>();
		waitForClick();
		addMouseListeners();
		frame = addFrame(FRAME_X, FRAME_Y, getWidth() - FRAME_X - 1, getHeight() - FRAME_Y - 1);
		lastBall = newBall = null;
		
		while(true){
			try{
				for(Ball ball : balls){
					ball.move();
					
					// Check Bottom For Bounce
					if((ball.getY() + ball.getHeight()) > (frame.getY() + frame.getHeight())){
						double extra = (ball.getY()+ball.getHeight()) - (frame.getY() + frame.getHeight());
						ball.move(0, -2*extra*YBOUNCE_RATIO);
						
						ball.setVelocity(ball.getXVel(), -ball.getYVel()*YBOUNCE_RATIO);
						if(Math.abs(ball.getYVel())<YSMALL_BOUNCE){//Insignificant bounce
							ball.setLocation(ball.getX(), (frame.getY() + frame.getHeight()) - ball.getHeight());
							ball.setVelocity(ball.getXVel(), 0);
							break;
						}
					}
					//Check Top For Bounce
					else if(ball.getY() < frame.getY()){
						double extra = ball.getY() - frame.getY();
						ball.move(0, -2*extra*YBOUNCE_RATIO);
						ball.setVelocity(ball.getXVel()*XFRICTION, -ball.getYVel()*YBOUNCE_RATIO);
					}
					
					// Check Left Side For Bounce
					if(ball.getX() < frame.getX()){
						double extra = ball.getX() - frame.getX();
						ball.move(-2*extra*XBOUNCE_RATIO, 0);
						ball.setVelocity(-ball.getXVel()*XBOUNCE_RATIO, ball.getYVel());
					}
					// Check Right Side For Bounce
					else if((ball.getX() + ball.getWidth()) > (frame.getX() + frame.getWidth())){
						double extra = (ball.getX()+ball.getWidth()) - (frame.getX() + frame.getWidth());
						ball.move(-2*extra*XBOUNCE_RATIO, 0);
						ball.setVelocity(-ball.getXVel()*XBOUNCE_RATIO, ball.getYVel());
					}
					
					if (!isGrounded(ball))
						ball.accelerate(0, GRAVITY);
					else if (ball.getXVel() > XSMALL_VELOCITY)
						ball.setVelocity(ball.getXVel()*XFRICTION, 0);
					else{
						ball.setVelocity(0, 0);
						balls.remove(ball);
					}
				}
				pause(PAUSE_TIME);
			}
			catch(Exception e){}
		}
	}
	
	public void mousePressed(MouseEvent e){
		newBall = addBall(e.getX()-BALL_RADIUS, e.getY()-BALL_RADIUS);
		lastPoint = new GPoint(e.getPoint());
		lastTime = System.currentTimeMillis();
	}
	
	public void mouseDragged(MouseEvent e){
		newBall.setLocation(e.getX() - BALL_RADIUS, e.getY() - BALL_RADIUS);
		long currTime = System.currentTimeMillis();
		double xSpeed = (e.getX() - lastPoint.getX()) / (currTime - lastTime);
		double ySpeed = (e.getY() - lastPoint.getY()) / (currTime - lastTime);
		newBall.setVelocity(xSpeed*SPEED_RATIO, ySpeed*SPEED_RATIO);
		
		lastPoint = new GPoint(e.getPoint());
		lastTime = currTime;
	}
	
	public void mouseReleased(MouseEvent e){
		balls.add(newBall);
		
		if (lastBall != null){
			GLine line = new GLine(lastBall.getX()+BALL_RADIUS, lastBall.getY()+BALL_RADIUS, e.getX(), e.getY());
			lastBall.setRight(line);
			newBall.setLeft(line);
			add(line);
		}
		lastBall = newBall;
		newBall = null;
	}
	
	private Ball addBall(int x, int y){
		Ball ball = new Ball(x, y, 2*BALL_RADIUS, 2*BALL_RADIUS);
		ball.setFilled(true);
		ball.setFillColor(BALL_COLOR);
		add(ball);
		return ball;
	}
	
	private GRect addFrame(int x, int y, int width, int height){
		GRect floor = new GRect(x, y, width, height);
		add(floor);
		return floor;
	}
	
	private boolean isGrounded(Ball ball){
		if (ball.getYVel() > 0) return false;
		return ball.getY() + ball.getHeight() == (frame.getY() + frame.getHeight());
	}
	
	RandomGenerator rgen = new RandomGenerator();
	
}

class Ball extends GOval{
	
	private double xVel, yVel;
	private GLine left, right;
	
	public Ball(int x, int y, int width, int height){
		super(x, y, width, height);
		xVel = yVel = 0;
		left = right = null;
	}
	
	public void setVelocity(double x, double y){
		xVel = x;
		yVel = y;
	}
	
	public void setLeft(GLine left){
		this.left = left;
	}
	
	public void setRight(GLine right){
		this.right = right;
	}
	
	public double getXVel(){
		return xVel;
	}
	
	public double getYVel(){
		return yVel;
	}
	
	public GLine getLeft(){
		return this.left;
	}
	
	public GLine getRight(){
		return this.right;
	}
	
	public void move(){
		super.move(xVel, yVel);
		if (left != null)
			left.setEndPoint(this.getX() + (this.getWidth()/2), this.getY() + (this.getHeight()/2));
		if (right != null)
			right.setLocation(this.getX() + (this.getWidth()/2), this.getY() + (this.getHeight()/2));
	}
	
	public void accelerate(double x, double y){
		xVel += x;
		yVel += y;
	}
	
}