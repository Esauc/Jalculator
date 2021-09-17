import java.awt.*;
import javax.swing.*;

public class Main
{
	public static final String APP_NAME = "CALCULADORA GRAFICA - ESAU";
	public static boolean isRunning = true;
	public static Screen screen;
	public static int delay = 10;

	/**
	 *  "JALCULATOR"
	 *  MADE BY ESAU CARVALHO
	 *  
	 *  TODO
	 *  Integrals
	 *  
	 *  bugs:
	 *  (x^2x does not work properly)
	 *  (x^x does not work properly)
	 *  
	 *  
	 */
	public Main()
	{
		JFrame frame = new JFrame(APP_NAME);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		screen = new Screen();

		frame.add(screen);
		frame.setVisible(true);

		screen.updateFunction();
	}

	public void loop()
	{
		while(isRunning)
		{
			screen.update();
			try
			{
				Thread.sleep(delay);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args)
	{
		//Function f = new Function("x^2+2x-1");
		//System.out.println("f(0)= "+f.eval(1));
		Main m = new Main();
		m.loop();
	}
}