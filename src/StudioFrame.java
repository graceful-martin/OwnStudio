/* 
 * OwnStudio
 * StudioFrame.java
 * Version 6.2
 * 2019. 11. 23 ~
 * 20160658 ����� 
 */


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.io.File;
import java.io.IOException;

class MouseListen implements MouseMotionListener {
	JLabel positionLabel;
	public MouseListen(JLabel positionLabel) {
		this.positionLabel = positionLabel;
	}
    
	@Override
    public void mouseMoved(MouseEvent e) {
    	Point mousePoint = new Point(0,0);
    	mousePoint = e.getPoint();
		positionLabel.setText("X: " + mousePoint.getX() + " / Y: " + mousePoint.getY());
    }

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}

public class StudioFrame extends JFrame implements WindowListener{
	// �� ���¸� ������ ActionListener�� ���� CanvasPanel ��ü�� �Ѱ��ֱ� ���� ���� 
	private static Color penColor = Color.black;
	private static Color fillColor = Color.white;
	/* int temp = 0; */
	public StudioFrame() 
	{
		addWindowListener(this); // ������ â ���� �̺�Ʈ �ޱ� ���� Listener �߰�
		setSize(800,600); // ������ ������ ũ�� ����
		setLayout(null); // ������ǥ�� �����ϱ� ���� ���̾ƿ� null�� ����
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // JFrame 
    	setTitle("Own Studio"); // ������ ������ Ÿ��Ʋ ����
		setIcon(); // ������ ����
    	this.setFocusable(true); // Ű �̺�Ʈ ��Ŀ���� ���� �� �ְ� �켱������ �Է¹ޱ� ���� ����
    	
		/* ���콺 ��ǥ ����� ���� �� */
		JLabel positionLabel = new JLabel("Position");
		positionLabel.setLocation(670,5);
		positionLabel.setSize(300,15);
		add(positionLabel);
		
		/* �׷����� �׸��� ���� �г� */
		CanvasPanel canvasPanel = new CanvasPanel();
		canvasPanel.setLocation(10, 25);
		canvasPanel.setSize(new Dimension(766, 400));
		canvasPanel.setBackground(Color.white);
		canvasPanel.setBorder(new EtchedBorder());
		canvasPanel.setLayout(new FlowLayout());
		canvasPanel.addMouseMotionListener(new MouseListen(positionLabel));
        canvasPanel.setShapeType(ShapeType.DEFAULT); // ShapeType �ʱ�ȭ 
		
		/* Canvas�� ǥ���ϱ� ���� �� */
		JLabel canvasLabel = new JLabel("Canvas");
		canvasLabel.setLocation(10,7);
		canvasLabel.setSize(70,10);
		add(canvasLabel);
        
		/* Drawing Type �г� */
        JPanel drawingTypePanel = new JPanel();
        drawingTypePanel.setLocation(10, 430);
        drawingTypePanel.setSize(new Dimension(300, 100));
        drawingTypePanel.setBorder(new TitledBorder(new EtchedBorder(),"Drawing Type"));
        add(drawingTypePanel);
          
        /* Drawing Type ������ư */
        JRadioButton drawingTypeRadio[] = new JRadioButton[7];
        String drawingTypeString[] = {"Default", "Line", "Rect", "Circle", "Sketch", "String", "Erase"}; 
        for(int i=0; i< drawingTypeRadio.length; i++){
        	drawingTypeRadio[i] = new JRadioButton(drawingTypeString[i]);
        	drawingTypeRadio[i].addItemListener(new CanvasPanel());
            drawingTypePanel.add(drawingTypeRadio[i]);
        }
        
        /* Drawing Type ������ư �̺�Ʈ */
        drawingTypeRadio[0].setSelected(true); // ���� ��ư ���� �ʱ�ȭ
        drawingTypeRadio[0].addActionListener(e->canvasPanel.setShapeType(ShapeType.DEFAULT));
        drawingTypeRadio[1].addActionListener(e->canvasPanel.setShapeType(ShapeType.LINE));
        drawingTypeRadio[2].addActionListener(e->canvasPanel.setShapeType(ShapeType.RECT));
        drawingTypeRadio[3].addActionListener(e->canvasPanel.setShapeType(ShapeType.CIRCLE));
        drawingTypeRadio[4].addActionListener(e->canvasPanel.setShapeType(ShapeType.SKETCH));
        drawingTypeRadio[5].addActionListener(e->canvasPanel.setShapeType(ShapeType.STRING));
        drawingTypeRadio[6].addActionListener(e->canvasPanel.setShapeType(ShapeType.ERASE));
		
        
        /* Border Type �г� */
        JPanel borderTypePanel = new JPanel();
        borderTypePanel.setLayout(null);
        borderTypePanel.setLocation(310, 430);
        borderTypePanel.setSize(new Dimension(150, 100));
        borderTypePanel.setBorder(new TitledBorder(new EtchedBorder(),"Border Type"));
        add(borderTypePanel);
        
        /* Border Color �� */
        JLabel borderColorLabel = new JLabel("Color");
		borderColorLabel.setLocation(35,25);
		borderColorLabel.setSize(60,30);
		borderTypePanel.add(borderColorLabel);
		
		/* Border Thick �� */
		JLabel borderThickLabel = new JLabel("Thick");
		borderThickLabel.setLocation(35,55);
		borderThickLabel.setSize(60,30);
		borderTypePanel.add(borderThickLabel);
		
		/* Border Color �г� */
		JPanel borderColorPanel = new JPanel();
		borderColorPanel.setLocation(80, 30);
		borderColorPanel.setSize(new Dimension(40, 20));
		borderColorPanel.setBackground(penColor);
		borderColorPanel.setBorder(new TitledBorder(new EtchedBorder()));
		borderTypePanel.add(borderColorPanel);
		borderColorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent click) {
            	penColor = JColorChooser.showDialog(null, "Color", penColor);
            	borderColorPanel.setBackground(penColor);
            	canvasPanel.setPenColor(penColor);
            }
        });
		
		/* Border Thick �޺��ڽ� */
        String[] thickString = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40"};
        JComboBox thickComboBox = new JComboBox(thickString); 
        thickComboBox.setLocation(80,60);
        thickComboBox.setSize(40,20);
        borderTypePanel.add(thickComboBox);
        thickComboBox.addItemListener(new ItemListener() {
        	@Override 
        	public void itemStateChanged(ItemEvent e) { 
        		if (e.getStateChange() == ItemEvent.SELECTED) { 
        			JComboBox cb = (JComboBox) e.getSource(); // �޺��ڽ� �˾Ƴ���
        			int i = cb.getSelectedIndex();// ���õ� �������� �ε���
        			canvasPanel.setPenThick(i);
        		} 
        	} 
        }); 

        /* Fill Type �г� */
        JPanel fillTypePanel = new JPanel();
        fillTypePanel.setLayout(null);
        fillTypePanel.setLocation(460, 430);
        fillTypePanel.setSize(new Dimension(150, 100));
        fillTypePanel.setBorder(new TitledBorder(new EtchedBorder(),"Fill Type"));
        add(fillTypePanel);
        
        /* Fill Color �� */
        JLabel fillColorLabel = new JLabel("Color");
        fillColorLabel.setLocation(35,40);
        fillColorLabel.setSize(60,30);
		fillTypePanel.add(fillColorLabel);
		
		/* Fill Color �г� */
        JPanel fillColorPanel = new JPanel();
        fillColorPanel.setLocation(80, 45);
        fillColorPanel.setSize(new Dimension(40, 22));
        fillColorPanel.setBackground(fillColor);
        fillColorPanel.setBorder(new TitledBorder(new EtchedBorder()));
		fillTypePanel.add(fillColorPanel);
		fillColorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent click) { 
            	fillColor = JColorChooser.showDialog(null, "Color", fillColor);
            	fillColorPanel.setBackground(fillColor);
            	canvasPanel.setFillColor(fillColor);
            }
        });

        /* And So On �г� */
        JPanel andPanel = new JPanel();
        andPanel.setLayout(null);
        andPanel.setLocation(610, 430);
        andPanel.setSize(new Dimension(166, 100));
        andPanel.setBorder(new TitledBorder(new EtchedBorder(),"And so on"));
        add(andPanel);
        
        /* Redo ��ư */
        JButton redoButton = new JButton("Redo");
        redoButton.setBorder(new TitledBorder(new EtchedBorder()));
        redoButton.setMnemonic(KeyEvent.VK_R);
        redoButton.setSize(45, 30);
        redoButton.setLocation(10, 25);
        redoButton.addActionListener(e->canvasPanel.setActionType(ActionType.REDO));
        andPanel.add(redoButton);
        
        /* Reset ��ư */
        JButton resetButton = new JButton("Reset");
        resetButton.setBorder(new TitledBorder(new EtchedBorder()));
        resetButton.setMnemonic(KeyEvent.VK_S);
        resetButton.setSize(45, 30);
        resetButton.setLocation(60, 25);
        resetButton.addActionListener(e->canvasPanel.setActionType(ActionType.ERASER));
        andPanel.add(resetButton);
        
        /* Undo ��ư */
        JButton undoButton = new JButton("Undo");
        undoButton.setBorder(new TitledBorder(new EtchedBorder()));
        undoButton.setMnemonic(KeyEvent.VK_U);
        undoButton.setSize(45, 30);
        undoButton.setLocation(110, 25);
        undoButton.addActionListener(e->canvasPanel.setActionType(ActionType.UNDO));
        andPanel.add(undoButton);
        
        /* Zoom �� */
		JLabel zoomLabel = new JLabel("Zoom");
		zoomLabel.setLocation(25,60);
		zoomLabel.setSize(60,30);
		andPanel.add(zoomLabel);
		
		/* Zoom �޺��ڽ� */
        String[] zoomString = new String[51];
        for(int zoomCount = 0; zoomCount < 51; zoomCount++) 
        	zoomString[zoomCount] = (10 * zoomCount) + "%";
        JComboBox zoomComboBox = new JComboBox(zoomString); 
        zoomComboBox.setSelectedItem(zoomString[10]);
        zoomComboBox.setLocation(65,65);
        zoomComboBox.setSize(70,20);
        andPanel.add(zoomComboBox);
        zoomComboBox.addItemListener(new ItemListener() { 
        	@Override 
        	public void itemStateChanged(ItemEvent e) { 
        		if (e.getStateChange() == ItemEvent.SELECTED) { 
        			JComboBox cb = (JComboBox) e.getSource(); // �޺��ڽ� �˾Ƴ���
        			double i = cb.getSelectedIndex();// ���õ� �������� �ε���
        			if((i/10) != 1.0f) {
        				canvasPanel.setZoom(i/10);
        			} else {
        				canvasPanel.setZoom(i/10);
        			}
        		} 
         	} 
        });

		/* �޴��� ���� */
		JMenuBar menuBar = new JMenuBar(); 
		setJMenuBar(menuBar);
		
		/* File �޴� ���� */
		JMenu fileMenu = new JMenu("File");
		JMenuItem menuSave = new JMenuItem("Save",KeyEvent.VK_S);
		fileMenu.add(menuSave);
        menuSave.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		canvasPanel.saveImage();
        	}
        });
		JMenuItem menuOpen = new JMenuItem("Open", KeyEvent.VK_O);
		fileMenu.add(menuOpen);
        menuOpen.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		canvasPanel.openImage();
        	}
        });
		fileMenu.addSeparator();
		JMenuItem menuExit = new JMenuItem("Exit",KeyEvent.VK_E);
		fileMenu.add(menuExit);
        menuExit.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		int result = JOptionPane.showConfirmDialog(null, "���� �����Ͻðڽ��ϱ�?", "Own Studio", JOptionPane.YES_NO_OPTION);
        		if(result == JOptionPane.CLOSED_OPTION) {
        			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        		} else if (result == JOptionPane.YES_OPTION) {
        			System.exit(0);
        		} else {
        			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        		}
        	}
        });
		menuBar.add(fileMenu);
		
		/* Edit �޴� ���� */
		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
		JMenuItem strokeItem = new JMenuItem("Stroke", KeyEvent.VK_S);
		editMenu.add(strokeItem);
		strokeItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        			int nCap = 0;
        			int nJoin = 0;
        			float fMiterLimit = -1.0f;
        		    Object obj = JOptionPane.showInputDialog(null, "Choose Stroke", "Own Studio",
        		        JOptionPane.QUESTION_MESSAGE, null, new Object[] {
        		            "Butt Cap(default)", "Round Cap", "Square Cap"}, "Cap");
        		    if(obj == null)
        		    	return;
        		    /*
        		    JOptionPane optionPane = new JOptionPane();
        			JSlider slider = new JSlider();
        			slider.setMajorTickSpacing(10);
        			slider.setPaintTicks(true);
        			slider.setPaintLabels(true);
        			slider.setValue(0);
        			ChangeListener changeListener = new ChangeListener() {
        				public void stateChanged(ChangeEvent changeEvent) {
        					JSlider theSlider = (JSlider) changeEvent.getSource();
        			        if (!theSlider.getValueIsAdjusting()) {
        			        	temp = (int) theSlider.getValue();
        			        }
        			    }
        			};
        			slider.addChangeListener(changeListener);
        		    optionPane.setMessage(new Object[] { "Select miterlimit", slider });
        		    optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        		    optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        		    */
        		    if(obj.equals("Butt Cap(default)") == true)
        		    	nCap = BasicStroke.CAP_BUTT;
        		    else if(obj.equals("Round Cap") == true)
        		    	nCap = BasicStroke.CAP_ROUND;
        		    else if(obj.equals("Square Cap") == true)
        		    	nCap = BasicStroke.CAP_SQUARE; 
        		    /*
        		    else if(obj.equals("Bevel Join(default)") == true) {
            		    JDialog dialog = optionPane.createDialog(null, "Own Studio");
            		    dialog.setVisible(true);
            		    fMiterLimit = temp;
        		    }
        		    else if(obj.equals("Miter Join") == true) {
            		    JDialog dialog = optionPane.createDialog(null, "Own Studio");
            		    dialog.setVisible(true);
            		    fMiterLimit = temp;
        		    }
        		    else if(obj.equals("Round Join") == true) {
            		    JDialog dialog = optionPane.createDialog(null, "Own Studio");
            		    dialog.setVisible(true);
            		    fMiterLimit = temp;
            		    System.out.println(temp);
        		    }
        		    */
        		    canvasPanel.setDrawStroke(nCap, nJoin, fMiterLimit);
        		}
        });
		
		/* Made �޴� ���� */
		JMenu madeMenu = new JMenu("Made");
		menuBar.add(madeMenu);
		JMenuItem byItem = new JMenuItem("By");
		madeMenu.add(byItem);
		byItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		showInfoBox("Java Programming(01)\n20160658 Kang Sang Woo","Own Studio");
        	}
        });

		/* ��ũ�� �г� (�߰���) */
        JScrollPane scrollPane = new JScrollPane(canvasPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(10, 25, 766, 400);
        add(scrollPane);
        
    	setVisible(true); // ������ �����ֱ�
	}

	ImageIcon setImageSize(String imgPath) { 
		ImageIcon originIcon = new ImageIcon(imgPath);  
		Image originImg = originIcon.getImage(); 
		Image changedImg= originImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH );
		ImageIcon Icon = new ImageIcon(changedImg);
		return Icon;
	}
	
 	void setIcon() { 
		ImageIcon img = new ImageIcon(System.getProperty("user.dir") + "\\zubat.png");
		this.setIconImage(img.getImage());
	}
	
    public void showInfoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e) {
		int result = JOptionPane.showConfirmDialog(null, "���� �����Ͻðڽ��ϱ�?", "Own Studio", JOptionPane.YES_NO_OPTION);
		if(result == JOptionPane.CLOSED_OPTION) {
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		} else if (result == JOptionPane.YES_OPTION) {
			System.exit(0);
		} else {
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}
}
