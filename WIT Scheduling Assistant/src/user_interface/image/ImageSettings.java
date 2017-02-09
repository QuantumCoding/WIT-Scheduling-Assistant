package user_interface.image;

import java.awt.Color;
import java.util.HashMap;

import pages.ClassOption;

public class ImageSettings {
	private HashMap<ClassOption, Color> classColors;
	private boolean use3dRect;
	
	private Color textColor;
	private Color outlineColor;
	
	public ImageSettings(HashMap<ClassOption, Color> classColors) {
		this.classColors = classColors;
		
		this.use3dRect = true;
		this.textColor = Color.WHITE;
		this.outlineColor = Color.BLACK;
	}

	public HashMap<ClassOption, Color> getClassColors() {
		return classColors;
	}

	public void setClassColors(HashMap<ClassOption, Color> classColors) {
		this.classColors = classColors;
	}

	public Color getOutlineColor() { return outlineColor; }
	public Color getTextColor() { return textColor; }
	public boolean using3dRect() { return use3dRect; }

	public void setUse3dRect(boolean use3dRect) { this.use3dRect = use3dRect; }
	public void setTextColor(Color textColor) { this.textColor = textColor; }
	public void setOutlineColor(Color outlineColor) { this.outlineColor = outlineColor; }
}
