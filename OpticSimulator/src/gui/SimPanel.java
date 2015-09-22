package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import objects.OpticObject;

public class SimPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
	
	private int centerDistance = 200;
	private double mirrorAngle = 90;
	private JPanel objectGUI = new JPanel();
	private JCheckBox shouldSnap = new JCheckBox();
	private JCheckBox drawRays = new JCheckBox();
	private JTextField heightField = new JTextField(2);
	private JLabel snapLabel = new JLabel("Should snap to center:");
	private JLabel rayLabel = new JLabel("Draw rays:");
	private JLabel heightLabel = new JLabel("Height :");
	private JButton removeObject = new JButton("remove");
	
	private Color panelColor = Color.BLACK;
	
	ArrayList<OpticObject> objects = new ArrayList<OpticObject>();
	OpticObject selected = null;
	
	
	public SimPanel(int center){
		centerDistance = center;
		setLayout(new BorderLayout());
		shouldSnap.addActionListener(this);
		drawRays.addActionListener(this);
		heightField.addActionListener(this);
		removeObject.addActionListener(this);
		objectGUI.add(snapLabel);
		objectGUI.add(shouldSnap);
		objectGUI.add(rayLabel);
		objectGUI.add(drawRays);
		objectGUI.add(heightLabel);
		objectGUI.add(heightField);
		objectGUI.add(removeObject);
		shouldSnap.setVisible(false);
		drawRays.setVisible(false);
		heightField.setVisible(false);
		snapLabel.setVisible(false);
		rayLabel.setVisible(false);
		heightLabel.setVisible(false);
		removeObject.setVisible(false);
		shouldSnap.setEnabled(false);
		drawRays.setEnabled(false);
		heightField.setEnabled(false);
		removeObject.setEnabled(false);
		add(objectGUI);
		
	}
	
	public void addObject(OpticObject o){
		objects.add(o);
		selected = null;
		repaint();
	}
	
	/**
	 * Paints the panel. First paints the central axis, then the mirror. Then the focal and center points. Finally, it paints all of the objects
	 * and their respective image locations in the mirror.
	 */
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(panelColor);
		g2.clearRect(0, 0, getWidth(), getHeight());
		g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
		g2.drawArc(getWidth()/2-centerDistance*2, getHeight()/2-centerDistance, centerDistance*2, centerDistance*2, (int)(-1*(mirrorAngle/2)), (int)mirrorAngle);
		g2.fillOval(getWidth()/2-3-centerDistance, getHeight()/2-3, 6, 6);
		g2.setColor(Color.RED);
		g2.fillOval(getWidth()/2-3-centerDistance/2, getHeight()/2-3, 6, 6);
		for(OpticObject o: objects){
			g2.drawImage(o.getScaledImage(), getWidth()/2-o.getX(), getHeight()/2-o.getY(), null);
			if(o.shouldDrawPrincipleRays()){
				g2.setColor(Color.BLUE);
				double focus = centerDistance/2;
				double x = centerDistance-(Math.sqrt(centerDistance*centerDistance-o.getY()*o.getY()));
				
				g2.drawLine(getWidth()/2-o.getMiddleX(), getHeight()/2-o.getY(), getWidth()/2-(int)x, getHeight()/2-o.getY());
				

				double w = (double)centerDistance*(1.0-(1.0/(2.0*Math.sqrt(1.0-((double)o.getY()/centerDistance)*((double)o.getY()/centerDistance)))));
				double slp = o.getY()/w;
				double finW = getWidth()/2-x;
				double finH = -slp*finW+o.getY();
				g2.drawLine(getWidth()/2-(int)x, getHeight()/2-o.getY(), 0, getHeight()/2-(int)finH);
				g2.setColor(Color.RED);
				
				double theda = Math.atan((double)o.getY()/(double)(o.getMiddleX()-centerDistance));
				double w1 = centerDistance-centerDistance*Math.cos((theda));
				double h1 = -1*centerDistance*Math.sin((theda));
				
				Point p1 = new Point(getWidth()/2-(int)w1, getHeight()/2-(int)h1);
				Point p3 = new Point(getWidth()/2-(int)(centerDistance+centerDistance*Math.cos((theda))), (int)(getHeight()/2-(-1*h1)));
				Line2D l = new Line2D.Double(p1, p3);
				g2.draw(l);
				g2.drawLine(getWidth()/2-centerDistance, getHeight()/2, getWidth()/2-o.getMiddleX(), getHeight()/2-o.getY());
				
				g2.setColor(Color.GREEN);
				
				//double focus = centerDistance*(1-(1/(2*Math.sqrt(1-(o.getY()/centerDistance)*(o.getY()/centerDistance)))));
				theda = Math.atan((double)o.getY()/(double)(o.getMiddleX()-focus));
				double A = Math.PI-theda;
				double B = Math.asin(focus/(centerDistance/Math.sin(A)));
				double C = Math.PI-A-B;
				double hyp = Math.hypot(o.getY(), o.getMiddleX()-focus);
				double hyp1 = Math.sin(C)*(centerDistance/Math.sin(A));
				h1 = -1*(o.getY()*hyp1)/hyp;
				if(o.getMiddleX()<focus){
					h1*=-1;
				}
				w1 = Math.sqrt(hyp1*hyp1-h1*h1);
				w1 = focus-w1;
				p1 = new Point(getWidth()/2-o.getMiddleX(), getHeight()/2-o.getY());
				p3 = new Point(getWidth()/2-(int)w1, getHeight()/2-(int)h1);
				l = new Line2D.Double(p1, p3);
				g2.draw(l);
				double angB = Math.abs(Math.atan(h1/(centerDistance/2-w1)));
				theda = Math.acos(((centerDistance-w1)*(focus-w1)+(h1*h1))/((Math.hypot((centerDistance-w1), h1)*Math.hypot((focus-w1), h1))));
				double angA = Math.PI/2+angB-2*theda;
				if(angA>Math.PI/2){
					angA = Math.PI-angA;
				}
				double h = centerDistance/(Math.cos(theda));
				double w2 = h*Math.sin(angA);
				double h2 = -h*Math.cos(angA);
				double newSlope = h2/w2;
				double w3 = getWidth()/2-w1;
				g2.drawLine((int)l.getX2(), (int)l.getY2(), 0, (int)(getHeight()/2-(h1+(w3*newSlope))));
			}
			int So = o.getMiddleX();
			int F = centerDistance/2;
			if(So!=F){
				int Si = (int)(So*F/((double)(So-F)));
				double M = (-1*Si)/(double)So;
				if(M<0){
					//Sets scaling factor to a positive value.
					M = M*-1;
					
					//Scales the image by M (magnification).
					Image scaled = o.getScaledImage().getScaledInstance((int)Math.ceil(o.getBounds().width*M), (int)Math.ceil(o.getHeight()*M), Image.SCALE_DEFAULT);
					
					//Rotates the graphics context
					g2.translate(getWidth()/2, getHeight()/2);
					g2.rotate(Math.toRadians(180));
					
					//Draws the image upside down
					g2.drawImage(scaled, Si-scaled.getWidth(null)/2, (int)(o.getY()*-1*M), null);
					//Re-orients the graphics context
					g2.rotate(Math.toRadians(180));
					g2.translate(-1*getWidth()/2,-1*getHeight()/2);
				} else if (M!=0) {
					//Scales the image by M (magnification).
					Image scaled = o.getScaledImage().getScaledInstance(((int)Math.ceil(o.getBounds().width*M)), (int)Math.ceil(o.getHeight()*M), Image.SCALE_DEFAULT);
					
					//Draws the image right side up.
					
					if(o.getMiddleX()>0)
						g2.drawImage(scaled, getWidth()/2-Si, (int)(getHeight()/2-(o.getY()*M)), null);
					else {
						if(o.getY()>0)
							g2.drawImage(scaled, getWidth()/2-Si, (int)(getHeight()/2-((o.getY()*M))), null);
						else
							g2.drawImage(scaled, getWidth()/2-Si, (int)(getHeight()/2+((o.getY()*-1*M))), null);
					}
				}
			}
		}
	}

	/**
	 * Sets the height of the selected object.
	 * @param newH The new height.
	 */
	public void setNewHeightForSelected(int newH) {
		if(selected!=null){
			int change = newH-selected.getHeight();
			selected.setHeight(newH);
			if(selected.getY()>0){
				selected.setY(selected.getY()+change);
			}
			repaint();
		}
	}
	
	public void setNewCenterDistance(int newCenter) {
		centerDistance = newCenter;
		repaint();
	}
	
	public void setNewMirrorAngle(double newAngle){
		mirrorAngle = newAngle;
		repaint();
	}
	
	private void addGUIForSelected(){
		if(selected!=null){
			drawRays.setSelected(selected.shouldDrawPrincipleRays());
			shouldSnap.setSelected(selected.shouldSnapToCenter());
			heightField.setText(Integer.toString(selected.getHeight()));
			drawRays.setVisible(true);
			shouldSnap.setVisible(true);
			heightField.setVisible(true);
			snapLabel.setVisible(true);
			rayLabel.setVisible(true);
			heightLabel.setVisible(true);
			removeObject.setVisible(true);
			shouldSnap.setEnabled(true);
			drawRays.setEnabled(true);
			heightField.setEnabled(true);
			removeObject.setEnabled(true);
		}
	}
	
	private void removeGUI(){
		shouldSnap.setVisible(false);
		drawRays.setVisible(false);
		heightField.setVisible(false);
		snapLabel.setVisible(false);
		rayLabel.setVisible(false);
		heightLabel.setVisible(false);
		removeObject.setVisible(false);
		shouldSnap.setEnabled(false);
		drawRays.setEnabled(false);
		heightField.setEnabled(false);
		removeObject.setEnabled(false);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		int offset = getParent().getHeight()-getHeight();
		Point click = new Point(getWidth()/2-arg0.getX(), getHeight()/2-(arg0.getY()-offset));
		for(OpticObject o: objects){
			if(objectClicked(o.getBounds(), click)){
				selected = o;
				addGUIForSelected();
				return;
			}
		}
		selected = null;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		int offset = getParent().getHeight()-getHeight();
		Point click = new Point(getWidth()/2-arg0.getX(), getHeight()/2-(arg0.getY()-offset));
		for(OpticObject o: objects){
			if(objectClicked(o.getBounds(), click)){
				selected = o;
				addGUIForSelected();
				return;
			}
		}
	}
	
	public String getInfo(){
		String info = "Spherical Mirror Image Calculator";
		if(selected!=null){
			info = "Selected Object : "+selected.getHeight()+" pixels tall at "+selected.getMiddleX()+" pixels from the mirror and "+(selected.getY()-selected.getHeight()/2)+" pixels from the center axis.";
		}
		return info;
	}
	
	/**
	 * Gets whether the object is clicked or not based upon the rect of that OpticalObject and the position of the click in the graphics2D space.
	 * @param rect The rectangle of an OpticalObject (from OpticalObject.getRect();)
	 * @param p The point of the click (x, y).
	 * @return Returns true if the point of the click is on this object, false if otherwise.
	 */
	private boolean objectClicked(Rectangle rect, Point p){
		if(p.getX()<rect.getX() && p.getX()>rect.getX()-rect.getWidth()){
			if(p.getY()<rect.getY() && p.getY()>rect.getY()-rect.getHeight()){
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(selected!=null && !objectClicked(selected.getBounds(), new Point(arg0.getX(), arg0.getY()))){
			selected = null;
			removeGUI();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int offset = getParent().getHeight()-getHeight();
		if(selected!=null){
			selected.setX(this.getWidth()/2-e.getX());
			if(selected.shouldSnapToCenter()){
				selected.setY(selected.getHeight());
			} else {
				selected.setY(getHeight()/2-(e.getY()-offset));
			}
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(selected!=null){
			if(arg0.getSource()==shouldSnap){
				selected.setSnapToCenter(shouldSnap.isSelected());
				if(shouldSnap.isSelected()){
					selected.setY(selected.getHeight());
					drawRays.setEnabled(true);
					repaint();
				}
			} else if (arg0.getSource()==drawRays){
				selected.setDrawPrincipleRays(drawRays.isSelected());
				repaint();
			} else if (arg0.getSource()==heightField){
				try{
					int newHeight = Integer.parseInt(heightField.getText());
					if(newHeight>=30)
						setNewHeightForSelected(newHeight);
				} catch (NumberFormatException e){
					System.out.println("Could not parse height value: "+heightField.getText());
				}
			} else if (arg0.getSource()==removeObject){
				objects.remove(selected);
				selected = null;
				removeGUI();
				repaint();
			}
		} 
		validate();
	}
}
