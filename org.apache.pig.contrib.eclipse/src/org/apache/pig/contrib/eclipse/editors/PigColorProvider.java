package org.apache.pig.contrib.eclipse.editors;

import java.util.Hashtable;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author zhanje
 *
 */
public class PigColorProvider {
	
	
	public static final RGB RAWSTRING = new RGB(135,206,235);

	public static RGB COMMENT=new RGB(0,0,192);
	
	/**
	 * purple
	 */
	public static RGB KEYWORD=new RGB(127, 0, 85);
	
	public static RGB BUILTIN_FUN=new RGB(237,145,33);

	public static RGB DATA_TYPE=new RGB(217,45,33);

	public static final RGB DEFAULT= new RGB(0, 0, 0);
	
	private static Hashtable<RGB,Color> map=new Hashtable<RGB,Color>();
	
	public Color getColor(RGB rgb){
		Color color=map.get(rgb);
		if (color==null){
			color=new Color(Display.getCurrent(),rgb);
			map.put(rgb, color);
		}
		
		return color;
	}
	
	
}