package group2.simulator.physical;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.DynamicShape;

public class BoardObject {

	private final Color color;
	private int angle;
	protected Body body;

	public BoardObject(float x, float y, String name, DynamicShape shape, float mass, Color color, int angle) {
		this.body = new Body(name, shape, mass);
		setPosition(x, y);
		this.color = color;
		this.angle = angle;
		
	}



	public void setPosition(float x, float y) {

		x = checkXPosition(x,y);
		y = checkYPosition(x,y);
		body.setPosition(x, y);
	}

	public float checkXPosition(float x, float y){
		if (x < 140) return 140;
			else if (x > 695)
				return 695;
		return x;
	}

	public float checkYPosition(float x, float y){
		if (y < 140) return 140;
		else if (y > 395)
			return 395;
	return y;
	}

	public float getX() {
		return body.getPosition().getX();
	}

	public float getY() {
		return body.getPosition().getY();
	}

	public Color getColor() {
		return color;
	}

	public double getAngle() {
		return angle;
	}

	// keeps the angle between 0 and 359 degrees
	public int convertAngle(double d) {
//		System.out.println("convertAngle received: "+d);
		while (d < 0 || d >= 360) {
			if (d < 0)
				d += 360;
			else if (d >= 360)
				d -= 360;
		}
//		System.out.println("convertAngle returned: "+d);
		return (int) d;
	}



	public void setAngle(double d){
		angle = convertAngle(d);
	}

	public Body getBody() {
		return body;
	}

	/**
	 * Draw an object into the world 
	 * @param g The graphics contact on which to draw
	 * @param world The world where the object will be drawn 
	 */
	public static void draw(Graphics2D g, World world) {
		BodyList bodies = world.getBodies();

		for (int i=0;i<bodies.size();i++) {
            Body body = bodies.get(i);
            
            if (body.getShape() instanceof Circle) {
    			drawCircleBody(g,body,(Circle) body.getShape());
    		}
            else if (body.getShape() instanceof Box) {
    			drawBoxBody(g,body,(Box) body.getShape());
            }
            else{
            	drawLineBody(g, body);
            }
		}
	}
	/**
	 * Draw a box in the world
	 * 
	 * @param g The graphics contact on which to draw
	 * @param body The body to be drawn
	 * @param shape The shape to be drawn
	 */
	private static void drawBoxBody(Graphics2D g, Body body, Box shape) {
		Vector2f[] pts = shape.getPoints(body.getPosition(), body.getRotation());

		Vector2f v1 = pts[0];
		Vector2f v2 = pts[1];
		Vector2f v3 = pts[2];
		Vector2f v4 = pts[3];

		if (body.getUserData() != null) {
			Robot r = (Robot) body.getUserData();
			BufferedImage img = r.getImage();
			if (img != null) {
				AffineTransform at = AffineTransform.getTranslateInstance(r.getX()-r.xSize/2,r.getY()-r.ySize/2);
				at.rotate(Math.toRadians(r.getAngle()), r.xSize/2,r.ySize/2);
		        g.drawImage(img, at, null);
			}
		} else {
			g.setColor(Color.BLACK);
			g.fillRect((int) v1.getX(), (int) v2.getY(),
					(int) (v3.getX() - v1.getX()),
					(int) (v4.getY() - v2.getY()));
		}

	}

	/**
	 * Draw a circle in the world
	 * 
	 * @param g The graphics contact on which to draw
	 * @param body The body to be drawn
	 * @param circle The shape to be drawn
	 */
	private static void drawCircleBody(Graphics2D g, Body body, Circle circle) {
		g.setColor(Color.RED);
		float x = body.getPosition().getX();
		float y = body.getPosition().getY();
		float r = circle.getRadius();
		g.fillOval((int) (x-r), (int) (y-r), (int) (r*2), (int) (r*2));
	}

	/**
	 * Draw a line into the simulation
	 * 
	 * @param g The graphics to draw the line onto
	 * @param body The body describing the line's position
	 */
	private static void drawLineBody(Graphics2D g, Body body) {
		Box box = (Box) body.getShape();
        Vector2f[] pts = box.getPoints(body.getPosition(), body.getRotation());
        
        Vector2f v1 = pts[0];
        Vector2f v2 = pts[1];
        Vector2f v3 = pts[2];
        Vector2f v4 = pts[3];
        
        g.setColor(Color.black);
        g.drawLine((int) v1.x,(int) v1.y,(int) v2.x,(int) v2.y);
        g.drawLine((int) v2.x,(int) v2.y,(int) v3.x,(int) v3.y);
        g.drawLine((int) v3.x,(int) v3.y,(int) v4.x,(int) v4.y);
        g.drawLine((int) v4.x,(int) v4.y,(int) v1.x,(int) v1.y);
	}
}