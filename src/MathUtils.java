import java.text.*;
import java.math.*;

public class MathUtils
{
	 public static float getAngle(float x, float y, float px, float py)
    {
        float angle = (float) Math.toDegrees(Math.atan2(py - y, px - x));

        if(angle < 0)
        {
            angle += 360;
        }

        return angle;
    }

    public static float roundDecimalPlaces(float value, int decimalPlaces)
    {
        return value;
        //float shift = (float) Math.pow(10 , decimalPlaces);
        //return (float) Math.round(value*shift)/shift;
    }

    public static float round(float value, int decimalPlaces)
    {
        float shift = (float) Math.pow(10 , decimalPlaces);
        return (float) Math.round(value*shift)/shift;   
    }

    
    public static float getDistance(float x, float y, float px, float py)
    {
    	float distance = (float) (Math.sqrt(Math.abs((x*x -px*px)+(y*y -py*py))));

    	return distance;
    }
}