package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import objects.OpticObject;

public class SimFrame extends JFrame implements ActionListener {
	
	
	private int WIDTH = 600;
	private int HEIGHT = 400;
	
	private JTextField centerField = new JTextField(2);
	private JTextField angleField = new JTextField(2);
	private JButton addButton = new JButton("add object");
	private JButton pathButton = new JButton("set image folder");
	private JPanel gui = new JPanel();
	
	private static String folderPath = "";
	
	private static SimPanel panel = new SimPanel(200);
		
	public SimFrame(){
		super();
		setSize(WIDTH, HEIGHT);
		int w = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int h = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
		setBackground(Color.WHITE);
		setLocation((w-WIDTH)/2, (h-HEIGHT)/2);
		setUpGUI();
		add(panel, BorderLayout.CENTER);
		addObject();
		addMouseListener(panel);
		addMouseMotionListener(panel);
		String sep = System.getProperty("file.separator");
		folderPath = System.getProperty("user.home")+sep+"Desktop"+sep+"object images"+sep;
		setUpImageFolder();
		MyThread thread = new MyThread();
		thread.start();
		setVisible(true);
	}
	
	/**
	 * Adds the GUI to the frame.
	 */
	private void setUpGUI(){
		centerField.addActionListener(this);
		angleField.addActionListener(this);
		centerField.setText("200");
		angleField.setText("90");
		addButton.addActionListener(this);
		pathButton.addActionListener(this);
		gui.add(addButton);
		gui.add(new JLabel(" Center distance:"));
		gui.add(centerField);
		gui.add(new JLabel(" Mirror angle:"));
		gui.add(angleField);
		gui.add(pathButton);
		add(gui, BorderLayout.NORTH);
	}
	
	private static void setUpImageFolder(){
		
		File f = new File(folderPath);
		f.mkdir();
		System.out.println("Image folder:"+ folderPath);
		File arrowFile = new File(SimFrame.class.getResource("/objects/arrow.png").getFile());
		try {
			BufferedImage arrow = ImageIO.read(arrowFile);
			File newArrow = new File(folderPath+System.getProperty("file.separator")+arrowFile.getName());
			if(newArrow.createNewFile()){
				ImageIO.write(arrow, "png", newArrow);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addObject(){
		BufferedImage img;
		
		try {
			File arrowFile = new File(getClass().getResource("/objects/arrow.png").getFile());
			img = ImageIO.read(arrowFile);
			
			OpticObject obj = new OpticObject(img, 200, 64, 64);
			panel.addObject(obj);
		} catch (IOException e) {
			System.out.println("Couln't read file: "+System.getProperty("user.dir")+"/src/objects/arrow.png");
		}
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource()==centerField){
			try{
				int newCenter = Integer.parseInt(centerField.getText());
				panel.setNewCenterDistance(newCenter);
			} catch(NumberFormatException exeption){
				System.out.println("could not parse center distance value: "+centerField.getText());
			}
		} else if (e.getSource()==angleField){
			try{
				double newAngle = Math.abs(Double.parseDouble(angleField.getText()));
				panel.setNewMirrorAngle(newAngle);
			} catch(NumberFormatException exeption){
				System.out.println("could not parse angle value: "+centerField.getText());
			}
		} else if (e.getSource()==addButton){
			AddObjectFrame addFrame = new SimFrame.AddObjectFrame();
			
		} else if (e.getSource()==pathButton){
			ChangePath chngpth = new SimFrame.ChangePath();
		}
	}
	
	public class MyThread extends Thread{
		public void run(){
			while(true){
				try {
					Thread.sleep(10);
					setTitle(panel.getInfo());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static class AddObjectFrame extends JFrame implements ActionListener{
		
		JComboBox imgPick = new JComboBox();
		public AddObjectFrame(){
			setSize(200, 100);
			int w = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
			int h = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
			setBackground(Color.WHITE);
			setLocation((w-this.getWidth())/2, (h-this.getHeight())/2);
			File dir = new File(folderPath);
			for(File file: dir.listFiles()){
				if(file!=null && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg"))){
					imgPick.addItem(file.getName());
				}
			}
			imgPick.addActionListener(this);
			this.add(imgPick);
			setVisible(true);
		}
		
		public void actionPerformed(ActionEvent e){
			if(e.getSource()==imgPick){
				File imgFile = new File(folderPath+System.getProperty("file.separator")+imgPick.getSelectedItem().toString());
				BufferedImage img;
				try{
					img = ImageIO.read(imgFile);
					OpticObject obj = new OpticObject(img, 200, img.getHeight(), img.getHeight());
					panel.addObject(obj);
				} catch (IOException ex){
					System.out.println("Could not read image file: "+imgFile.getAbsolutePath());
				}
			}
			dispose();
		}
	}
	
	public static class ChangePath extends JFrame implements ActionListener{

		JTextField inputField = new JTextField(10);
		
		public ChangePath(){
			setSize(400, 50);
			int w = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
			int h = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
			setBackground(Color.WHITE);
			setLocation((w-this.getWidth())/2, (h-this.getHeight())/2);
			inputField.addActionListener(this);
			inputField.setText(folderPath);
			add(inputField);
			setVisible(true);
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource()==inputField){
				folderPath = inputField.getText();
				setUpImageFolder();
			}
			dispose();
		}
	}
}
