import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

enum ShapeType { UNKNOWN, DEFAULT, LINE, RECT, CIRCLE, POLYLINE, SKETCH, ERASE, STRING, IMAGE }; // 도형
enum ActionType { UNKNOWN, ERASER, UNDO, REDO }; // 버튼

class Shape { // 도형 객체
    int x, y, w, h;
    String s;
    Font f;
    ShapeType type;
    Polygon polygon;
    Color penColor = Color.black;
    Color fillColor = Color.white;
    int thickness = 1;
    int cap = 0;
    int join = 0;
    float miterlimit = -1.0f;
    BufferedImage bImage = null;
    JPanel canvas = null;
    
    /* 생성자로 생성 */
    public Shape(BufferedImage bImage, JPanel canvas)
    {
        this.bImage = bImage;
        this.canvas = canvas;
    }
    
    public Shape(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    
    public Shape(Polygon pg, Color p, Color f, int t)
    {
    	this.polygon = pg;
    	this.penColor = p;
        this.fillColor = f;
        this.thickness = t;
    }
    
    public Shape(Polygon pg, int t)
    {
    	this.polygon = pg;
        this.thickness = t;
    }
    
    public Shape(String s, int x, int y, Font f, Color p) {
    	this.s = s;
    	this.x = x;
    	this.y = y;
    	this.f = f;
    	this.penColor = p;
    }
    
    public Shape(int x, int y, int w, int h, Color p, Color f, int t)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.penColor = p;
        this.fillColor = f;
        this.thickness = t;
    }
    
    public Shape(int x, int y, int w, int h, Color p, Color f, int t, int cap, int join, float miterlimit)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.penColor = p;
        this.fillColor = f;
        this.thickness = t;
        this.cap = cap;
        this.join = join;
        this.miterlimit = miterlimit;
    }
    
    void draw(Graphics g2, Graphics2D g) { }
}

// 사각형 Shape
class Rect extends Shape {
	public Rect(int x, int y, int w, int h, Color p, Color f, int t)
    {
    	super(x, y, w, h, p, f, t);
    }
    
    void draw(Graphics g2, Graphics2D g)
    {
    	g.setStroke(new BasicStroke(thickness));
    	g.setPaint(super.penColor);
    	if(super.fillColor == Color.white)
    		g.drawRect(x, y, w, h);
    	else {
    		g.draw(new Rectangle2D.Float(x, y, w, h));
    		g.setPaint(super.fillColor);
    		g.fill(new Rectangle2D.Float(x, y, w, h));
    	}
    }
}

// 원 Shape
class Circle extends Shape {
    public Circle(int x, int y, int w, int h, Color p, Color f, int t)
    {
    	super(x, y, w, h, p, f, t);
    }
    
    void draw(Graphics g2, Graphics2D g)
    {
    	g.setStroke(new BasicStroke(thickness));
    	g.setPaint(super.penColor);
    	if(super.fillColor == Color.white)
    		g.drawOval(x, y, w, h);
    	else {
    		g.draw(new Ellipse2D.Float(x, y, w, h));
    		g.setPaint(super.fillColor);
    		g.fill(new Ellipse2D.Float(x, y, w, h));
    		
    	}
    }
}

// 선 Shape
class Line extends Shape {
    public Line(int x, int y, int w, int h, Color p, Color f, int t, int cap, int join, float miterlimit)
    {
        super(x, y, w, h, p, f, t, cap, join, miterlimit);
    }
    
    void draw(Graphics g2, Graphics2D g)
    {
    	if(miterlimit == -1.0f) {
    		g.setStroke(new BasicStroke(thickness, cap, join));
    		g.setPaint(super.penColor);
        	g.drawLine(x, y, w, h);
    	} else {
    		g.setStroke(new BasicStroke(thickness, cap, join, miterlimit));
    		g.setPaint(super.penColor);
        	g.drawLine(x, y, w, h);
    	}
    }
}

// 스케치 Shape
class Sketch extends Shape {
	Polygon polygon = null;
    public Sketch(Polygon pg, Color p, Color f, int t)
    {
        super(pg, p, f, t);
        polygon = pg;
    }
    
    void draw(Graphics g2, Graphics2D g)
    {
    	g.setStroke(new BasicStroke(thickness));
    	g.setPaint(super.penColor);
    	g.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
    }
}

// 지우개 Shape
class Erase extends Shape {
	Polygon polygon = null;
    public Erase(Polygon pg, int t)
    {
        super(pg, t);
        polygon = pg;
    }
    
    void draw(Graphics g2, Graphics2D g)
    {
    	g.setStroke(new BasicStroke(thickness));
    	g.setPaint(Color.white);
    	g.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
    }
}

// 초기화 Shape
class Eraser extends Shape {
	public Eraser(int x, int y, int w, int h) 
	{
		super(x, y, w, h);
	}
	
	void draw(Graphics g2, Graphics2D g) { 
		g.clearRect(0,0,1000,1000);
	}
}

// String Shape
class forString extends Shape {
	public forString(String s, int x, int y, Font f, Color p) 
	{
		super(s, x, y, f, p);
	}
	
	void draw(Graphics g2, Graphics2D g) { 
		g.setColor(super.penColor);
		g.setFont(f);
		g.drawString(s, x, y+10);
	}
}

// 이미지 Shape
class Image extends Shape {
	public Image(BufferedImage bImage, JPanel canvas) 
	{
		super(bImage, canvas);
	}
	
	void draw(Graphics g2, Graphics2D g) { 
		g.drawImage(bImage, 0, 0, canvas);
	}
}

public class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener, ItemListener {
	static int Index = 0;
	BufferedImage bImage = null;
	Color penColor = Color.black;
	Color fillColor = Color.white;
	int thickness = 1;
	int x, y, w, h;
    int [] tempX = new int [40];
	int [] tempY = new int [40];
    int index = 0;
    double zoom = 1.0f;
    float miterlimit = -1.0f;
    String check;
    Font font = null;
	String inputString = null;
	
	int cap, join;
	int ox, oy;
	Rectangle tr=null;
    Rectangle rt=null;
    Line2D ld=null;
    Line2D tl=null;
    Ellipse2D.Float ed=null;
    Ellipse2D.Float te=null;
    String st=null;
    private boolean selected=false;
    private boolean focus=false;
    private boolean erase=false;
    
    
    ArrayList<Shape> shapeList;
    ArrayList<Shape> tempList;
    ArrayList<Action> actionList;
    ShapeType shapeType = ShapeType.UNKNOWN;
    ActionType actionType = ActionType.UNKNOWN;
    static CanvasPanel ownPanel;
    
    Polygon polygon;
    ArrayList<Polygon> polygonArrayList;
    
    void setShapeType(ShapeType type)
    {
        shapeType = type; // Drawing Type 패널의 라디오버튼이 호출 되면 CanvasPanel의 현재 선택된 라디오버튼의 내용과 같은 도형으로 지정해줌
        if(type == ShapeType.STRING ) // DrawingType에서 String 라디오 버튼이 눌렸을 시
        {
        	/* 폰트, 글자 크기 등을 지정해줄 수 있는 Dialog를 띄움 */
        	inputString = JOptionPane.showInputDialog(
        	this, 
        	"Input String", 
        	"Own Studio", 
        	JOptionPane.QUESTION_MESSAGE
        	);
        	
        	JFontChooser fc = new JFontChooser();
        	if(fc.showDialog(this) == JFontChooser.OK_OPTION)
        		font = fc.getSelectedFont();
        }   
    }
    
    void setActionType(ActionType type)
    {
    	Shape s;
    	actionType = type;
        if( actionType == ActionType.ERASER ) { // Reset 버튼 액션이 호출 될 시
        	s = new Eraser(x, y, w, h);
            shapeList.add(s);
            this.revalidate();
            this.repaint();
            actionType = null;
        } else if( actionType == ActionType.REDO ) { // Redo 버튼 액션이 호출 될 시
            if(shapeList.size() != 0) {
            	tempList.add(shapeList.get(shapeList.size()-1)); // shapeList의 도형들 중 가장 최근에 생성된 것을 tempList로 백업
            	shapeList.remove(shapeList.get(shapeList.size()-1)); // shapeList의 도형들 중 가장 최근 것을 지움
            	this.revalidate();
                this.repaint();
            }
        } else if( actionType == ActionType.UNDO ) { // Undo 버튼 액션이 호출 될 시
        	if(tempList.size() != 0) {
            	shapeList.add(tempList.get(tempList.size()-1)); // tempList의 도형들 중 가장 최근에 생성된 것을 ShapeList로 백업
            	tempList.remove(tempList.get(tempList.size()-1)); // tempList의 도형들 중 가장 최근 것을 지움
            	this.revalidate();
                this.repaint();
            }
        }
    }
    
    void setDrawStroke(int cap, int join, float miterlimit) { // Line Stroke 지정
    	this.cap = cap;
    	this.join = join;
    	this.miterlimit = miterlimit;
    }
    
    public void setZoom(double d) { // 확대/축소 지정
    	zoom = d;
    	this.revalidate();
        this.repaint();
    }
    
    public void setPenColor(Color c) { // 선 색 및 도형 테두리 색 지정
    	penColor = c;
    }
    
    public void setFillColor(Color c) { // 채우기 지정
    	fillColor = c;
    }
    
    public void setPenThick(int t) { // 굵기 지정
    	thickness = t;
    }
    
	public BufferedImage mergeDrawings() // 이미지 저장할 때 그래픽을 이용해 새로운 BufferedImage 생성
	{
		BufferedImage bImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bImage.createGraphics();
		g.setColor(getForeground());
		g.setBackground(getBackground());
		paintComponent(g);
		return bImage;
	}

	public void openImage()
	{
		JFileChooser fc = new JFileChooser(); 
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG", "jpg"); 
		fc.setFileFilter(filter);
		fc.setCurrentDirectory(new File(System.getProperty("user.dir"))); // 현재 디렉토리 User로 이동
		if ( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
			File file = fc.getSelectedFile();
			//String ext = FilenamesUtil.getExtension(file.getName()); 
			try {
				Shape s;
				bImage = ImageIO.read(file); // 이미지 불러오기
		        s = new Image(bImage, this); // 이미지 위에 그림을 그릴 수 있게 이미지 자체를 Shape로 지정하여 shapeList에 추가
		        shapeList.add(s);
		        s = null;
		        bImage = null;
		        // 이미지 불러온 후 그래픽 다시 그리기
		        this.revalidate();
		        this.repaint();
			}
			catch (IOException e){}
		}
	}
	
	public void saveImage() // BufferedImage를 이용하여 파일 저장 위치
	{
		BufferedImage backImage = mergeDrawings();	
		JFileChooser fc = new JFileChooser(); 
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG", "jpg"); 
		fc.setFileFilter(filter);
		fc.setCurrentDirectory(new File(System.getProperty("user.dir"))); // 현재 디렉토리 User로 이동
		if ( fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION ) {
			File file = new File(fc.getSelectedFile()+".jpg"); // 확장명에 jpg 추가
			//String ext = FilenamesUtil.getExtension(file.getName()); 
			try {
				ImageIO.write(backImage, "jpg", file); // jpg 형식으로 저장
			}
			catch (IOException e){}
		}
	}
    
    public CanvasPanel() // 캔버스 패널 객체가 생성될 시 호출
    {
    	// 초기화
    	polygon = null;
        polygonArrayList = new ArrayList<>();
        shapeList = new ArrayList<>();
        tempList = new ArrayList<>();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void paintComponent(Graphics g2) // Swing Component가 다시 그릴 필요가 있을 시 호출
    {
        Graphics2D g =(Graphics2D)g2; // Graphics를 상속하는 JDK 1.2 이후 추가된 2D 그래픽 환경에 적합한 Graphics2D를 사용
        super.paintComponent(g); // 부모 클래스도 그릴 수 있게 paintComponent 메소드 재정의
        
       if(zoom != 1.0f) { // 확대 기능이 default값이 아닐 때
    	   /* AffineTransform을 이용해 그래픽 scale 조정 */
    	   Rectangle rec = new Rectangle(5,5,getWidth(),getHeight());
    	   AffineTransform at= g.getTransform();
    	   AffineTransform tr= new AffineTransform(at);
    	   tr.translate(
        	    (this.getWidth()/2) - (rec.getWidth()*(zoom+0.1))/2,
        	    (this.getHeight()/2) - (rec.getHeight()*(zoom+0.1))/2
        	);
    	   tr.scale(zoom+0.1,zoom+0.1);
    	   g.setTransform(tr);
       }
        
        
    	if( shapeType == ShapeType.DEFAULT )
    		return;
    	
        for( Shape r:shapeList) { // shapeList에 있는 모든 Shape들 그리기
        	r.draw(g2, g);
        }
        
        if(shapeList.size() != 0) {
        	check = shapeList.get(shapeList.size()-1).toString();
        	// 가장 최근에 생성된 shapeList의 객체가 Reset버튼의 Eraser일 경우 그림을 그리지 않고 메소드 종료
        	if(check.contains("Eraser@") && rt == null && ed == null)
        		return;
        }
        
        if(actionType == ActionType.ERASER || actionType == ActionType.REDO || actionType == ActionType.UNDO) {
        	actionType = null; // actionType 초기화
        }
        
        if( shapeType == ShapeType.RECT ) { // 선택된 도형이 사각형인 경우
            if(rt != null ) {
            	g.setStroke(new BasicStroke(thickness));
            	g.setPaint(penColor);
            	/* Resize 가능한 사각형 그리기 */
                g.draw(new Rectangle2D.Float(rt.x, rt.y, rt.width, rt.height));
                if(fillColor != Color.white) {
                	g.setPaint(fillColor);
                	g.fill(new Rectangle2D.Float(rt.x, rt.y, rt.width, rt.height));
                }
                int ex, ey;
                ex = rt.x + rt.width;
                ey = rt.y + rt.height;
                g.setColor(Color.BLUE);
                g.fillRect(ex-4, ey-4, 8, 8); // Resize를 위한 사각형 그리기
            }
    	}
        
        else if( shapeType == ShapeType.CIRCLE ) { // 선택된 도형이 원인 경우
            if(ed != null ) {
            	g.setStroke(new BasicStroke(thickness));
            	g.setPaint(penColor);
            	/* Resize 가능한 원 그리기 */
                g.draw(new Ellipse2D.Float((float) ed.getX(), (float) ed.getY(), (float) ed.getWidth(), (float) ed.getHeight()));
                if(fillColor != Color.white) {
                	 g.setPaint(fillColor);
                	 g.fill(new Ellipse2D.Float((float) ed.getX(), (float) ed.getY(), (float) ed.getWidth(), (float) ed.getHeight()));
                }
                int ex, ey;
                ex = (int) ed.getX() + (int) ed.getWidth();
                ey = (int) ed.getY() + (int) ed.getHeight();
                g.setColor(Color.BLUE);
                g.fillRect(ex-4, ey-4, 8, 8); // Resize를 위한 사각형 그리기
            }
        }
        
        else if( shapeType == ShapeType.LINE ) { // 선택된 도형이 선인 경우
        	if(miterlimit == -1.0f) {
        		g.setStroke(new BasicStroke(thickness, cap, join));
        		g.setPaint(penColor);
        		g.drawLine(x, y, w, h);
        	} else {
        		g.setStroke(new BasicStroke(thickness, cap, join, miterlimit));
        		g.setPaint(penColor);
        		g.drawLine(x, y, w, h);
        	}
        }
        
        else if( shapeType == ShapeType.SKETCH ) { // 선택된 기능이 스케치 기능인 경우
        	if( polygon != null ) {
        		g.setStroke(new BasicStroke(thickness));
        		g.setPaint(penColor);
                g.drawPolyline( polygon.xpoints, polygon.ypoints, polygon.npoints );
        	}
        }
        
        else if( shapeType == ShapeType.ERASE ) { // 선택된 기능이 지우개인 경우
        	if( polygon != null ) {
        		g.setStroke(new BasicStroke(thickness));
        		g.setPaint(Color.white);
                g.drawPolyline( polygon.xpoints, polygon.ypoints, polygon.npoints );
        	}
        	if(erase == true) {
        		g.setPaint(Color.black);
        		g.setStroke(new BasicStroke());
        		g.drawOval(x - thickness/2, y - thickness/2, thickness, thickness);
        	} else {
        		g.clearRect(x - thickness/2, y - thickness/2, thickness, thickness);
        	}
        }
        

    }

    @Override
    public void mouseClicked(MouseEvent e) { // 마우스 클릭 후

    }

    @Override
    public void mousePressed(MouseEvent e) { // 마우스 눌러 졌을 때
    	Shape s;
    	if( shapeType == ShapeType.DEFAULT ) // ShapeType이 Default인 경우 아무것도 하지 않고 함수 밖으로 나가줌
    		return;
    	
    	if(shapeType != ShapeType.ERASE) { // 지우개 기능이 아닌 다른 모든 곳에서 마우스 좌표 받기
    		x = e.getX();
    		y = e.getY();
    	} else {
    		erase = true;
    	}
    	
        if(shapeType == ShapeType.SKETCH || shapeType == ShapeType.ERASE) { // 스케치 기능 및 지우개 기능에서는 polygon 객체를 생성해 마우스 좌표 입력
        	polygon = new Polygon();
        	polygon.addPoint(x, y);
        }
        
        else if( shapeType == ShapeType.RECT ) { //
        	if( rt == null )
        		rt = new Rectangle();
        	int ex = rt.x + rt.width;
        	int ey = rt.y + rt.height;
        	tr = new Rectangle(ex - 4, ey - 4, 8, 8);
        	if (tr.contains(e.getX(), e.getY())) { // 마우스 포인터가 Resize를 위한 사각형 위에 위치한 경우
        		selected = true;
        	} else {
        		selected = false;
        		ox = rt.x = e.getX();
        		oy = rt.y = e.getY();
        	}
        } 
        
        else if( shapeType == ShapeType.CIRCLE ) { 
        	if( ed == null )
        		ed = new Ellipse2D.Float();
        	int ex = (int) (ed.getX() + ed.getWidth());
        	int ey = (int) (ed.getY() + ed.getHeight());
        	te = new Ellipse2D.Float(ex - 4, ey - 4, 8, 8);
        	if (te.contains(e.getX(), e.getY())) { // 마우스 포인터가 Resize를 위한 사각형 위에 위치한 경우
        		selected = true;
        	} else {
        		selected = false;
        		ox = (int) (ed.x = e.getX());
        		oy = (int) (ed.y = e.getY());
        	}
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) { // 마우스 클릭 종료 후(때질 때) 호출
        Shape s;
    	if( shapeType == ShapeType.DEFAULT )
    		return;
        else if (shapeType == ShapeType.LINE ){
            s = new Line(x, y, w, h, penColor, fillColor, thickness, cap, join, miterlimit);
            shapeList.add(s);
            s = null;
        } 
        else if(shapeType == ShapeType.SKETCH ) {
        	s = new Sketch(polygon, penColor, fillColor, thickness);
            shapeList.add(s);
        	polygon = null;
        	s = null;
        }
        else if (shapeType == ShapeType.STRING ){
            s = new forString(inputString, x, y, font, penColor);
            shapeList.add(s);
            s = null;
        } 
        else if(shapeType == ShapeType.ERASE && erase == true ) { erase = false; x = e.getX(); y = e.getY(); }

    	/* 그래픽 다시 그리기 */
        this.revalidate();
        this.repaint();

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    	if(shapeType == ShapeType.DEFAULT)
    		setCursor(new Cursor(Cursor.HAND_CURSOR)); 
    	else if (shapeType == ShapeType.ERASE) {
    		setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
    	}
    	else if (shapeType == ShapeType.STRING) 
    		setCursor(new Cursor(Cursor.TEXT_CURSOR)); 
    	else
    		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR)); 
    }

    @Override
    public void mouseExited(MouseEvent e) { // 마우스가 CanvasPanel 밖으로 나간 경우
    	setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 마우스 커서 기본으로 변경
    	Shape s;
    	if (shapeType == ShapeType.RECT ) {
    		if(tr != null) {
    			if (!tr.contains(e.getX(), e.getY()) && rt != null) {
        			s = new Rect(rt.x, rt.y, rt.width, rt.height, penColor, fillColor, thickness); // 새로운 Rect 객체 만들기
            		shapeList.add(s);
            		s = null;
            		rt = null;
    			}
    		}
    	} else if (shapeType == ShapeType.CIRCLE ) {
    		if(te != null) {
    			if (!te.contains(e.getX(), e.getY()) && ed != null) {
        			s = new Circle((int)ed.x, (int)ed.y, (int)ed.width, (int)ed.height, penColor, fillColor, thickness); // 새로운 Rect 객체 만들기
            		shapeList.add(s);
            		s = null;
            		ed = null;
    			}
    		}
    	}
    	/* 그래픽 다시 그리기 */
    	this.revalidate();
    	this.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) { // 마우스 드래그 될 경우
    	Shape s;
    	if( shapeType == ShapeType.DEFAULT )
    		return;
    	if (shapeType == ShapeType.LINE) {
    		w = e.getX();
    		h = e.getY(); 
    	} else if (shapeType == ShapeType.RECT  && rt != null) {
            rt.width = e.getX() - ox;
            rt.height = e.getY() - oy;
            if( selected ) {
                if( rt.width < 10 )
                    rt.width = 10;
                if( rt.height < 10 )
                    rt.height = 10;
            }
            else {
                if (rt.width < 0) {
                    rt.x = e.getX();
                    rt.width = -rt.width;
                }
                if (rt.height < 0) {
                    rt.y = e.getY();
                    rt.height = -rt.height;
                }
            }
    		w = e.getX() - x;
    		h = e.getY() - y;
    	} else if (shapeType == ShapeType.CIRCLE && ed != null) {
            ed.width = e.getX() - ox;
            ed.height = e.getY() - oy;
            if( selected ) {
                if( ed.width < 10 )
                    ed.width = 10;
                if( ed.height < 10 )
                    ed.height = 10;
            }
            else {
                if (ed.width < 0) {
                    ed.x = e.getX();
                    ed.width = -ed.width;
                }
                if (ed.height < 0) {
                    ed.y = e.getY();
                    ed.height = -ed.height;
                }
            }
    		w = e.getX() - x;
    		h = e.getY() - y;
    	} else if(shapeType == ShapeType.SKETCH ) {
    		polygon.addPoint(e.getX(), e.getY());
    	} else if(shapeType == ShapeType.ERASE ) {
    		if(erase == true) {
    			x = e.getX();
    			y = e.getY();
    		}
        	s = new Erase(polygon, thickness);
            shapeList.add(s);
    		polygon.addPoint(e.getX(), e.getY());
    	}
    	/* 그래픽 다시 그리기 */
    	this.revalidate();
        this.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getStateChange() == ItemEvent.DESELECTED) // 라디오 버튼 하나만 선택할 수 있게
			return; 
	}
}
