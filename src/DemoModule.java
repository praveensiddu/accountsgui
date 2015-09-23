/*
 *
 * Copyright (c) 2007, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.MissingResourceException;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

/**
 * A generic SwingSet2 demo module
 *
 * @author Jeff Dinkins
 */
public class DemoModule extends JApplet {

	// The preferred size of the demo
	private int PREFERRED_WIDTH = 680;
	private int PREFERRED_HEIGHT = 600;

	Border loweredBorder = new CompoundBorder(new SoftBevelBorder(
			SoftBevelBorder.LOWERED), new EmptyBorder(5, 5, 5, 5));

	// Premade convenience dimensions, for use wherever you need 'em.
	public static Dimension HGAP2 = new Dimension(2, 1);
	public static Dimension VGAP2 = new Dimension(1, 2);

	public static Dimension HGAP5 = new Dimension(5, 1);
	public static Dimension VGAP5 = new Dimension(1, 5);

	public static Dimension HGAP10 = new Dimension(10, 1);
	public static Dimension VGAP10 = new Dimension(1, 10);

	public static Dimension HGAP15 = new Dimension(15, 1);
	public static Dimension VGAP15 = new Dimension(1, 15);

	public static Dimension HGAP20 = new Dimension(20, 1);
	public static Dimension VGAP20 = new Dimension(1, 20);

	public static Dimension HGAP25 = new Dimension(25, 1);
	public static Dimension VGAP25 = new Dimension(1, 25);

	public static Dimension HGAP30 = new Dimension(30, 1);
	public static Dimension VGAP30 = new Dimension(1, 30);

	private JPanel panel = null;
	private String resourceName = null;
	private String iconPath = null;

	public DemoModule() {
		this(null, null);
	}

	public DemoModule(String resourceName,
			String iconPath) {
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());

		this.resourceName = resourceName;
		this.iconPath = iconPath;

	}

	public String getResourceName() {
		return resourceName;
	}

	public JPanel getDemoPanel() {
		return panel;
	}

	
	/**
	 * This method returns a string from the demo's resource bundle.
	 */
	public String getString(String key) {
		String value = null;
		try {
			value = TextAndMnemonicUtils.getTextAndMnemonicString(key);
		} catch (MissingResourceException e) {
			System.out
					.println("java.util.MissingResourceException: Couldn't find value for: "
							+ key);
		}
		if (value == null) {
			value = "Could not find resource: " + key + "  ";
			return "nada";
		}
		return value;
	}


	public char getMnemonic(String key) {
		return (getString(key)).charAt(0);
	}
	public ImageIcon createImageIcon(String filename, String description) {
		String path = "/resources/images/" + filename;
		return new ImageIcon(getClass().getResource(path));
	}
	/*
	public ImageIcon createImageIcon(String filename, String description) {
		if (getSwingSet2() != null) {
			return getSwingSet2().createImageIcon(filename, description);
		} else {
			String path = "/resources/images/" + filename;
			return new ImageIcon(getClass().getResource(path), description);
		}
	}
	*/

	public String getName() {
		return getString(getResourceName() + ".name");
	};

	public Icon getIcon() {
		return createImageIcon(iconPath, getResourceName() + ".name");
	};

	public String getToolTip() {
		return getString(getResourceName() + ".tooltip");
	};

	public void mainImpl() {
		JFrame frame = new JFrame(getName());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(getDemoPanel(), BorderLayout.CENTER);
		getDemoPanel().setPreferredSize(
				new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
		frame.pack();
		frame.show();
	}

	public JPanel createHorizontalPanel(boolean threeD) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setAlignmentY(TOP_ALIGNMENT);
		p.setAlignmentX(LEFT_ALIGNMENT);
		if (threeD) {
			p.setBorder(loweredBorder);
		}
		return p;
	}

	public JPanel createVerticalPanel(boolean threeD) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setAlignmentY(TOP_ALIGNMENT);
		p.setAlignmentX(LEFT_ALIGNMENT);
		if (threeD) {
			p.setBorder(loweredBorder);
		}
		return p;
	}

	public static void main(String[] args) {
		DemoModule demo = new DemoModule();
		demo.mainImpl();
	}

	public void init() {
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getDemoPanel(), BorderLayout.CENTER);
	}

	void updateDragEnabled(boolean dragEnabled) {
	}
}
