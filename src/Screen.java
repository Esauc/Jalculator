import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.Vector;
import java.awt.image.BufferedImage;

public class Screen extends JPanel
{
	private Color dashboardColor = Color.GRAY;
	private Color backgroundColor = Color.WHITE;
	private Color axisColor = Color.BLACK;
	private Color fColor = Color.RED;
	private Color dfColor = Color.BLUE;

	private int xCamera = -300;
	private int yCamera = -200;
	public static int xMouse = 0;
	public static int yMouse = 0;
	public boolean isMousePressed = false;

	private Point mousePressedPoint = new Point();
	private float navigationSpeed = 4;
	private int pixelsPerUnit = 20;

	private float mouseSpeed = 0;
	private int lastxMouse = 0, lastyMouse = 0;

	public static Function f;

	private String[] buttonText;
	private Rectangle[] buttons;
	private Rectangle dashboard;

	private static final Color buttonColorDark = new Color(190, 190, 190);

	private static long lastTime = 0;
	private static int fps = 0;

	private static final int FUNCTION_GRAPH_OFFSET = 50;

	private static Vector<Point> points;
	private static Vector<Point> derivative1Points;

	public static int iterator = 0;

	private boolean renderDerivative = false;
	private boolean renderIntegral = false;

	public Screen()
	{	
		super();

		setupInputConfiguration();

		setDoubleBuffered(true);

		dashboard = new Rectangle();
		buttons = new Rectangle[5];
		buttonText = new String[5];


		for(int i = 0; i < buttons.length; i++)
		{
			buttons[i] = new Rectangle();

			buttons[i].x = 10;
			buttons[i].y = 10 +i*30;
			buttons[i].width = 80;
			buttons[i].height = 20;
		}


		buttonText[0] = "SET f(x)";
		buttonText[1] = "df / dx [  ]";
		buttonText[2] = "Integral [  ]";
		buttonText[3] = "lim a = ";
		buttonText[4] = "lim b = ";


		//f = new Function("2*x+x+1");
		
		f = new Function("2x^2-4x+1");

		points = new Vector<Point>();
		derivative1Points = new Vector<Point>();
	}

	public void update()
	{
		dashboard.x = 0;
		dashboard.y = 0;
		dashboard.width = 100;
		dashboard.height = getHeight();

		mouseSpeed = (Math.abs(lastxMouse - xMouse) + Math.abs(lastyMouse - yMouse)) / 2; //Heuristic speed

		lastxMouse = xMouse;
		lastyMouse = yMouse;


		if(dashboard.contains(xMouse, yMouse))
		{
			boolean handCursor = false;

			for(int i = 0; i < buttons.length; i++)
			{
				if(buttons[i].contains(xMouse, yMouse))
				{
					handCursor = true;
					break;
				}
			}

			if(handCursor)
			{
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			else
			{
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
		else
		{
			if(isMousePressed)
			{
				Main.delay = 10; //Acelera a taxa de renderizaçao
									//Quando o mouse está pressionado

				setCursor(new Cursor(Cursor.MOVE_CURSOR));

				xCamera -= (xMouse - mousePressedPoint.x);
				yCamera -= (yMouse - mousePressedPoint.y);
				
				mousePressedPoint = new Point(xMouse, yMouse);
				
				/*
				navigationSpeed = mouseSpeed *2F;

				if(navigationSpeed > 30)
				{
					navigationSpeed = 30;
				}

				if(mouseSpeed == 0)
				{
					mousePressedPoint = new Point(xMouse, yMouse);
				}

				double sin = Math.sin(Math.toRadians(MathUtils.getAngle(xMouse, yMouse, mousePressedPoint.x, mousePressedPoint.y)));
				double cos = Math.cos(Math.toRadians(MathUtils.getAngle(xMouse, yMouse, mousePressedPoint.x, mousePressedPoint.y)));

				if(Math.abs(xMouse - mousePressedPoint.x) > 2)
				{
					xCamera += (int) (navigationSpeed *cos);
				}

				if(Math.abs(yMouse - mousePressedPoint.y) > 2)
				{
					yCamera += (int) (navigationSpeed *sin);
				}*/
			}
			else
			{
				Main.delay = 30; //Diminui renderizaçao

				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}


		fps++;
		long time = System.currentTimeMillis();

		if(time -lastTime >= 2000)
		{
			lastTime = time;
			fps = 0;
			//updateDerivative();


		    	new Thread()
		    	{
		    		@Override
		    		public synchronized void run()
		    		{
				    	updateFunction();
				    	updateDerivative();
		    		}
		    	}.start();
		}

		repaint();
	}

	public synchronized void paint(Graphics g1)
	{

		Graphics2D g = (Graphics2D) g1;

		g.setColor(backgroundColor);
		g.fillRect(0, 0, getWidth(), getHeight());


		g.translate(-xCamera, -yCamera);


			//plotFunctionGraph(g1);


			int size = points.size();

			int[] xPoints = new int[size];
			int[] yPoints = new int[size];

			for(int i = 0; i < size; i++)
			{
				xPoints[i] = points.get(i).x;
				yPoints[i] = -points.get(i).y; //Invert Y

				//debug
				//g.fillRect(xPoints[i]-2, yPoints[i]-2, 4, 4);

			}

			g.setColor(fColor);

			//ANTI_ALIAS
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g.drawPolyline(xPoints, yPoints, size);



		if(renderDerivative)
		{

			size = derivative1Points.size();

			xPoints = new int[size];
			yPoints = new int[size];

			for(int i = 0; i < size; i++)
			{
				xPoints[i] = derivative1Points.get(i).x;
				yPoints[i] = -derivative1Points.get(i).y; //Invert Y
			}

			g.setColor(dfColor);

			g.drawPolyline(xPoints, yPoints, size);

		}

		g.setColor(axisColor);


		//----Y-AXIS----
		int x1 = 0;
		int x2 = 0;
		int y1 = yCamera +getHeight();
		int y2 = yCamera;

		g.drawLine(x1, y1, x2, y2); // Y-Axis

		//Draw Y-Axis Arrowhead
		int arrayXa[] = {x2-5, x2+5, x2};
		int arrayYa[] = {y2+5, y2+5, y2};
		g.fillPolygon(arrayXa, arrayYa, 3);
		g.drawString("Y", x2+5, y2+15);

		//----X-AXIS----
		x1 = xCamera;
		y1 = 0;
		x2 = xCamera +getWidth();
		y2 = 0;

		g.drawLine(x1, y1, x2, y2); //X-Axis
		g.drawString("X", x2-10, y2-10);

		//Draw X-Axis Arrowhead
		int arrayXb[] = {x2-5, x2-5, x2};
		int arrayYb[] = {y2-5, y2+5, y2};
		g.fillPolygon(arrayXb, arrayYb, 3);


		int xCoord = (xCamera + xMouse) / pixelsPerUnit;
		int yCoord = (yCamera + yMouse) / pixelsPerUnit;


		//Draw mouse coord
		if(!dashboard.contains(xMouse, yMouse))
		{

			g.setColor(backgroundColor);
			g.fillRect(xCamera+xMouse+20, yCamera+yMouse +20, 50, 10);
			g.setColor(axisColor);
			g.drawString("( "+xCoord+" , "+(-yCoord)+" )", xCamera+xMouse+20, yCamera+yMouse+30);
		}

		//Draw function

		if(f != null)
		{
			g.setColor(backgroundColor);
			g.fillRect(xCamera+getWidth()-f.getFunction().length()*8-52, yCamera, f.getFunction().length()*8+20, 14);
			g.setColor(axisColor);
			g.drawRect(xCamera+getWidth()-f.getFunction().length()*8-52, yCamera-2, f.getFunction().length()*8+20, 14);
			g.drawString("f(x) = "+f.getFunction(), xCamera+getWidth()-f.getFunction().length()*8-50, yCamera+10);
		}


		//-----Draw dashboard------
		g.translate(xCamera, yCamera);
		g.setColor(dashboardColor);
		g.fillRect(dashboard.x, dashboard.y, dashboard.width, dashboard.height);

		for(int i = 0; i < buttons.length; i++)
		{		
			g.setColor(buttonColorDark);
			g.fillRect(buttons[i].x, buttons[i].y, buttons[i].width, buttons[i].height);
			g.setColor(Color.BLACK);
			g.drawRect(buttons[i].x, buttons[i].y, buttons[i].width, buttons[i].height);
			g.drawString(buttonText[i], buttons[i].x+5, buttons[i].y+15);
		}
	}

	public void updateFunction()
	{
		Vector<Point> p = new Vector<Point>(); // Cria outro vetor temporario
		//para nao congestionar o outro, devido ao multi-thread

		int h2 = getHeight() / 2;


		for(float i = xCamera -FUNCTION_GRAPH_OFFSET; i < xCamera +getWidth() +FUNCTION_GRAPH_OFFSET; i++)
		{
			if(Screen.f == null)
			{
				points.clear();
				return;
			}

			float yy = (float) ((f.eval(i / pixelsPerUnit) *pixelsPerUnit));
			//float yy = (float) f.eval(i);

			boolean isNaN = (yy != yy);


			if(!isNaN && Math.abs(yy) < 100000000)
			{
				int y = (int) yy;
				p.add(new Point((int) (i), y));
			}
		}

		points = p;
	}

	public void updateDerivative()
	{

		Vector<Point> p = new Vector<Point>(); // Cria outro vetor temporario
		//para nao congestionar o outro, devido ao multi-thread

		int h2 = getHeight() / 2;

		for(float i = xCamera -FUNCTION_GRAPH_OFFSET; i < xCamera +getWidth() +FUNCTION_GRAPH_OFFSET; i++)
		{
			if(Screen.f == null)
			{
				derivative1Points.clear();
				return;
			}

			float yy = (f.derivative1((i / pixelsPerUnit)) * pixelsPerUnit);

			boolean isNaN = (yy != yy);
			float dist = Math.abs(yy +(yCamera+h2));

			if(!isNaN && Math.abs(yy) < 100000000)
			{
				int y = (int) yy;
				p.add(new Point((int) i, y));
			}
		}
		derivative1Points = p;
	}

/*
	public void plotFunctionGraph(Graphics g)
	{
		if(Screen.f == null)
		{
			return;
		}

		Vector<Point> points = new Vector<Point>();

		for(float i = xCamera; i < xCamera +getWidth(); i++)
		{
			if(Screen.f == null)
			{
				return;
			}

			float yy = ((f.calculate1(i / pixelsPerUnit) *pixelsPerUnit));

			boolean isNaN = (yy != yy);

			if(!isNaN)
			{
				int y = (int) yy;
				points.add(new Point((int) (i), y));
			}
		}

		int[] xPoints = new int[points.size()];
		int[] yPoints = new int[points.size()];

		for(int i = 0; i < points.size(); i++)
		{
			xPoints[i] = points.get(i).x;
			yPoints[i] = -points.get(i).y; //Invert Y
		}

		g.setColor(Color.RED);
		g.drawPolyline(xPoints, yPoints, points.size());
	}
	*/

	public void setupInputConfiguration()
	{

		this.addMouseMotionListener(new MouseMotionListener() 
		{
		    @Override
		    public void mouseMoved(MouseEvent e) 
		    {
		        Screen.xMouse = e.getX();
		        Screen.yMouse = e.getY();
		    }

		    @Override
		    public void mouseDragged(MouseEvent e) 
		    {
		    	//mouseSpeedX = Math.abs(Screen.xMouse -e.getX());
		    	//mouseSpeedY = Math.abs(Screen.yMouse -e.getY());

		        Screen.xMouse = e.getX();
		        Screen.yMouse = e.getY();
		    }
		});

		this.addMouseListener(new MouseListener() 
		{
		    @Override
		    public void mouseClicked(MouseEvent e) 
		    {
		    }

		    @Override
		    public void mouseEntered(MouseEvent e) 
		    {
		    }

		    @Override
		    public void mouseExited(MouseEvent e) 
		    {
		    	isMousePressed = false;
		    }

		    @Override
		    public void mousePressed(MouseEvent e) 
		    {
		    	//points.clear();
		    	isMousePressed = true;
		    	mousePressedPoint = new Point(e.getX(), e.getY());

		    	if(buttons[0].contains(xMouse, yMouse))
		    	{
		    		String s = JOptionPane.showInputDialog("Entre com a funcao");

		    		if(s == null)
		    		{
		    			f = null;
		    		}
		    		else if(s.length() == 0)
		    		{
		    			f = null;
		    		}
		    		else
		    		{
		    			f = new Function(s);
		    		}
		    	}
		    	else if(buttons[1].contains(xMouse, yMouse))
		    	{
		    		renderDerivative = !renderDerivative;
		    		buttonText[1] = "df / dx ["+(renderDerivative ? "X" : "  ")+"]";
		    	}
		    	else if(buttons[2].contains(xMouse, yMouse))
		    	{
		    		renderIntegral = !renderIntegral;
		    		buttonText[2] = "Integral ["+(renderIntegral ? "X" : "  ")+"]";
		    	}
		    }

		    @Override
		    public void mouseReleased(MouseEvent e) 
		    {
		    	isMousePressed = false;
		    }
		});
	}
}