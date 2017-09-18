import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * 
 */

// Shaket Chaudhary and Juan Castano
// October 7, 2016
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		
		// checking to see if within first quadrant
		if ((int)point.getX() < (int)p2.getX() && (int)p2.getX() < x2 && y1 < (int)p2.getY() && (int)p2.getY() < (int)point.getY()) {
			//If it has a child we will need to insert the point within that quadrant
			if (hasChild(1)) {
				c1.insert(p2);		//recursive call
			}
			//since there is no child make a new point
			else c1 = new PointQuadtree<E>(p2,(int)point.getX(),y1,x2,(int)point.getY());
		}
		
		// second quadrant
		if (x1 < (int)p2.getX() && (int)p2.getX() < (int)point.getX() && y1 < (int)p2.getY() && (int)p2.getY() < (int)point.getY()) {
			if (hasChild(2)) {
				c2.insert(p2);
			}
			else c2 = new PointQuadtree<E>(p2,x1,y1,(int)point.getX(),(int)point.getY());
		}
		
		// third quadrant
		if (x1 < (int)p2.getX() && (int)p2.getX() < (int)point.getX() && (int)point.getY() < (int)p2.getY() && (int)p2.getY() < y2) {
			if (hasChild(3)) {
				c3.insert(p2);
			}
			else c3 = new PointQuadtree<E>(p2,x1,(int)point.getY(),(int)point.getX(),y2);
		}
		
		// fourth quadrant
		if ((int)point.getX() < (int)p2.getX() && (int)p2.getX() < x2 && (int)point.getY() < (int)p2.getY() && (int)p2.getY() < y2) {
			if (hasChild(4)) {
				c4.insert(p2);
			}
			else c4 = new PointQuadtree<E>(p2,(int)point.getX(),(int)point.getY(),x2,y2);
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE
		int num = 1;		//recursive calls don't include parents so we account for that here
		if (c1 !=null) num += c1.size();
		if (c2 !=null) num += c2.size();
		if (c3 != null) num += c3.size();
		if (c4 != null) num += c4.size();
		return num;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
			ArrayList<E> points = new ArrayList<E>();		//create the list we want to return
			addToAllPoints(points); 						//we use a helper method since the recursive calls would mess up the final list
			return points;
	}	
	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		ArrayList<E> allPoints = new ArrayList<E>();		//The list we want to return
		findCircleHelper(cx, cy, cr, allPoints);			//we use a helper method since the recursive calls would mess up the final list
		return allPoints;
	}

	// TODO: YOUR CODE HERE for any helper methods
	private void addToAllPoints(List<E> allPoints) {
		if (c1 != null) { 									//since it has a child we want to export it's children to find more points
			c1.addToAllPoints(allPoints);
		}
		if (c2 != null) {
			c2.addToAllPoints(allPoints);
		}
		if (c3 != null) {
			c3.addToAllPoints(allPoints);
		}
		if (c4 != null) {
			c4.addToAllPoints(allPoints);
		}
	}
	private void findCircleHelper(double cx, double cy, double cr, List<E> hitPoints) {
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) { // find the quadrant the circle is in
			double px = point.getX();
			double py = point.getY();
			if (Geometry.pointInCircle(px, py, cx, cy, cr)) {
				hitPoints.add(point);
			}
			if (hasChild(1)) { // recursive calls and adds them to list if it is a hit
				c1.findCircleHelper (cx, cy, cr, hitPoints);
			}
			if (hasChild(2)) {
				c2.findCircleHelper (cx, cy, cr, hitPoints);
			}
			if (hasChild(3)) {
				c3.findCircleHelper (cx, cy, cr, hitPoints);
			}
			if (hasChild(4)) {
				c4.findCircleHelper (cx, cy, cr, hitPoints);
			}
		}
	}
}
