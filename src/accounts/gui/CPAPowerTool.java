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
package accounts.gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SingleSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import accounts.data.RealProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

/**
 * A demo that shows all of the Swing components.
 *
 * @author Jeff Dinkins
 */
public class CPAPowerTool extends JPanel {

	String[] demos = { "TabbedPaneDemo", "ToolTipDemo", "TreeDemo" };

	/*
	void loadDemos() {
		for (int i = 0; i < demos.length;) {
			loadDemo(demos[i]);
			i++;
		}
	}
	*/

	// Possible Look & Feels
	private static final String windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	// The current Look & Feel
	private static String currentLookAndFeel = windows;

	// List of demos
	private ArrayList<DemoModule> demosList = new ArrayList<DemoModule>();

	// The preferred size of the demo
	private static final int PREFERRED_WIDTH = 720;
	private static final int PREFERRED_HEIGHT = 640;

	// Box spacers
	private Dimension HGAP = new Dimension(1, 5);
	private Dimension VGAP = new Dimension(5, 1);

	// A place to hold on to the visible demo
	private DemoModule currentDemo = null;
	private JPanel demoPanel = null;

	// About Box
	private JDialog aboutBox = null;

	// Status Bar
	private JTextField statusField = null;

	// Tool Bar
	private ToggleButtonToolBar toolbar = null;
	private ButtonGroup toolbarGroup = new ButtonGroup();

	// Menus
	private JMenuBar menuBar = null;
	private JMenu lafMenu = null;
	private JMenu optionsMenu = null;
	private ButtonGroup lafMenuGroup = new ButtonGroup();
	private ButtonGroup themesMenuGroup = new ButtonGroup();
	private ButtonGroup audioMenuGroup = new ButtonGroup();

	// Popup menu
	private JPopupMenu popupMenu = null;
	private ButtonGroup popupMenuGroup = new ButtonGroup();

	// Used only if swingset is an application
	private JFrame frame = null;

	// To debug or not to debug, that is the question
	private boolean DEBUG = true;
	private int debugCounter = 0;

	// The tab pane that holds the demo
	private JTabbedPane tabbedPane = null;

	// contentPane cache, saved from the applet or application frame
	Container contentPane = null;

	// number of swingsets - for multiscreen
	// keep track of the number of SwingSets created - we only want to exit
	// the program when the last one has been closed.
	private static int numSSs = 0;
	private static Vector<CPAPowerTool> swingSets = new Vector<CPAPowerTool>();

	private boolean dragEnabled = true;

	/**
	 * SwingSet2 Constructor
	 */
	public CPAPowerTool(GraphicsConfiguration gc) {

		currentLookAndFeel = UIManager.getLookAndFeel().getClass().getName();

		frame = createFrame(gc);

		frame.setIconImage(createImage("toolbar/JTable.gif", null));

		// set the layout
		setLayout(new BorderLayout());

		// set the preferred size of the demo
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

		initializeDemo();
		preloadFirstDemo();

		// Show the demo. Must do this on the GUI thread using invokeLater.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				showSwingSet2();
			}
		});

		// Start loading the rest of the demo in the background
		DemoLoadThread demoLoader = new DemoLoadThread(this);
		demoLoader.start();
	}

	private static List<RealProperty> rpL;
	/**
	 * SwingSet2 Main. Called only if we're an application, not an applet.
	 */
	public static void main(String[] args) {
		
		if (args.length == 0) {
			System.out.println("requires properties.csv as argument");
			System.exit(-1);
		}
		try {
			rpL = RealPropertiesSwingTable.parsePropFile(args[0]);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Create SwingSet on the default monitor
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		CPAPowerTool swingset = new CPAPowerTool(GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration());

	}

	// *******************************************************
	// *************** Demo Loading Methods ******************
	// *******************************************************

	public void initializeDemo() {
		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		//add(top, BorderLayout.NORTH);

		menuBar = createMenus();

		frame.setJMenuBar(menuBar);

		// creates popup menu accessible via keyboard
		popupMenu = createPopupMenu();

		ToolBarPanel toolbarPanel = new ToolBarPanel();
		toolbarPanel.setLayout(new BorderLayout());
		toolbar = new ToggleButtonToolBar();
		toolbarPanel.add(toolbar, BorderLayout.CENTER);
		top.add(toolbarPanel, BorderLayout.SOUTH);
		toolbarPanel.addContainerListener(toolbarPanel);

		tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.getModel().addChangeListener(new TabListener());

		statusField = new JTextField("");
		statusField.setEditable(false);
		add(statusField, BorderLayout.SOUTH);

		demoPanel = new JPanel();
		demoPanel.setLayout(new BorderLayout());
		demoPanel.setBorder(new EtchedBorder());
		tabbedPane.addTab("Hi There!", demoPanel);
		
		JFXPanel propertiesJfxPanel = new JFXPanel();
		TableViewSample tvs = new TableViewSample();
		Scene sceneProperties = tvs.createScene(rpL);
		propertiesJfxPanel.setScene(sceneProperties);

		tabbedPane.addTab(getString("TabbedPane.properties_label"), null,
				propertiesJfxPanel, getString("TabbedPane.properties_tooltip"));
		

		JFXPanel statementsJfxPanel = new JFXPanel();
		TableViewSample tvs1 = new TableViewSample();
		Scene sceneStatements = tvs1.createScene(rpL);
		statementsJfxPanel.setScene(sceneStatements);

		tabbedPane.addTab(getString("TabbedPane.statements_label"), null,
				statementsJfxPanel, getString("TabbedPane.statements_tooltip"));
		

		JFXPanel taxReportJfxPanel = new JFXPanel();

		tabbedPane.addTab(getString("TabbedPane.taxreport_label"), null,
				taxReportJfxPanel, getString("TabbedPane.taxreport_tooltip"));

	}

	DemoModule currentTabDemo = null;

	class TabListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			SingleSelectionModel model = (SingleSelectionModel) e.getSource();
			boolean srcSelected = model.getSelectedIndex() == 1;

			if (currentTabDemo != currentDemo && srcSelected) {
				currentTabDemo = currentDemo;
			}
		}
	}
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

	/**
	 * Create menus
	 */
	public JMenuBar createMenus() {
		JMenuItem mi;
		// ***** create the menubar ****
		JMenuBar menuBar = new JMenuBar();
		menuBar.getAccessibleContext().setAccessibleName(
				getString("MenuBar.accessible_description"));

		// ***** create File menu
		JMenu fileMenu = (JMenu) menuBar.add(new JMenu(
				getString("FileMenu.file_label")));
		fileMenu.setMnemonic(getMnemonic("FileMenu.file_mnemonic"));
		fileMenu.getAccessibleContext().setAccessibleDescription(
				getString("FileMenu.accessible_description"));

		createMenuItem(fileMenu, "FileMenu.about_label",
				"FileMenu.about_mnemonic",
				"FileMenu.about_accessible_description", new AboutAction(this));

		fileMenu.addSeparator();

		createMenuItem(fileMenu, "FileMenu.open_label",
				"FileMenu.open_mnemonic",
				"FileMenu.open_accessible_description", null);

		createMenuItem(fileMenu, "FileMenu.save_label",
				"FileMenu.save_mnemonic",
				"FileMenu.save_accessible_description", null);

		createMenuItem(fileMenu, "FileMenu.save_as_label",
				"FileMenu.save_as_mnemonic",
				"FileMenu.save_as_accessible_description", null);

		fileMenu.addSeparator();

		createMenuItem(fileMenu, "FileMenu.exit_label",
				"FileMenu.exit_mnemonic",
				"FileMenu.exit_accessible_description", new ExitAction(this));

		// Create these menu items for the first SwingSet only.
		if (numSSs == 0) {
			// ***** create laf switcher menu
			lafMenu = (JMenu) menuBar.add(new JMenu(
					getString("LafMenu.laf_label")));
			lafMenu.setMnemonic(getMnemonic("LafMenu.laf_mnemonic"));
			lafMenu.getAccessibleContext().setAccessibleDescription(
					getString("LafMenu.laf_accessible_description"));

			UIManager.LookAndFeelInfo[] lafInfo = UIManager
					.getInstalledLookAndFeels();

			for (int counter = 0; counter < lafInfo.length; counter++) {
				String className = lafInfo[counter].getClassName();
				if (className == windows) {
					mi = createLafMenuItem(lafMenu, "LafMenu.windows_label",
							"LafMenu.windows_mnemonic",
							"LafMenu.windows_accessible_description", windows);
					mi.setSelected(true); // this is the default l&f
					this.updateLookAndFeel();
				}
			}

			String lafName = getLookAndFeelLabel(currentLookAndFeel);

			for (int i = 0; i < lafMenu.getItemCount(); i++) {
				JMenuItem item = lafMenu.getItem(i);
				item.setSelected(item.getText().equals(lafName));
			}

			// ***** create the options menu
			optionsMenu = (JMenu) menuBar.add(new JMenu(
					getString("OptionsMenu.options_label")));
			optionsMenu
					.setMnemonic(getMnemonic("OptionsMenu.options_mnemonic"));
			optionsMenu.getAccessibleContext().setAccessibleDescription(
					getString("OptionsMenu.options_accessible_description"));

			// ***** create tool tip submenu item.
			mi = createCheckBoxMenuItem(optionsMenu,
					"OptionsMenu.tooltip_label",
					"OptionsMenu.tooltip_mnemonic",
					"OptionsMenu.tooltip_accessible_description",
					new ToolTipAction());
			mi.setSelected(true);

			// ***** create drag support submenu item.
			createCheckBoxMenuItem(optionsMenu,
					"OptionsMenu.dragEnabled_label",
					"OptionsMenu.dragEnabled_mnemonic",
					"OptionsMenu.dragEnabled_accessible_description",
					new DragSupportAction());

		}

		GraphicsDevice[] screens = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getScreenDevices();
		if (screens.length > 1) {

			JMenu multiScreenMenu = (JMenu) menuBar.add(new JMenu(
					getString("MultiMenu.multi_label")));

			multiScreenMenu
					.setMnemonic(getMnemonic("MultiMenu.multi_mnemonic"));
			multiScreenMenu.getAccessibleContext().setAccessibleDescription(
					getString("MultiMenu.multi_accessible_description"));

			createMultiscreenMenuItem(multiScreenMenu,
					MultiScreenAction.ALL_SCREENS);
			for (int i = 0; i < screens.length; i++) {
				createMultiscreenMenuItem(multiScreenMenu, i);
			}
		}

		return menuBar;
	}

	/**
	 * Create a checkbox menu menu item
	 */
	private JMenuItem createCheckBoxMenuItem(JMenu menu, String label,
			String mnemonic, String accessibleDescription, Action action) {
		JCheckBoxMenuItem mi = (JCheckBoxMenuItem) menu
				.add(new JCheckBoxMenuItem(getString(label)));
		mi.setMnemonic(getMnemonic(mnemonic));
		mi.getAccessibleContext().setAccessibleDescription(
				getString(accessibleDescription));
		mi.addActionListener(action);
		return mi;
	}

	/**
	 * Create a radio button menu menu item for items that are part of a button
	 * group.
	 */
	private JMenuItem createButtonGroupMenuItem(JMenu menu, String label,
			String mnemonic, String accessibleDescription, Action action,
			ButtonGroup buttonGroup) {
		JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menu
				.add(new JRadioButtonMenuItem(getString(label)));
		buttonGroup.add(mi);
		mi.setMnemonic(getMnemonic(mnemonic));
		mi.getAccessibleContext().setAccessibleDescription(
				getString(accessibleDescription));
		mi.addActionListener(action);
		return mi;
	}

	/**
	 * Create the theme's audio submenu
	 */
	public JMenuItem createAudioMenuItem(JMenu menu, String label,
			String mnemonic, String accessibleDescription, Action action) {
		JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menu
				.add(new JRadioButtonMenuItem(getString(label)));
		audioMenuGroup.add(mi);
		mi.setMnemonic(getMnemonic(mnemonic));
		mi.getAccessibleContext().setAccessibleDescription(
				getString(accessibleDescription));
		mi.addActionListener(action);

		return mi;
	}

	/**
	 * Creates a generic menu item
	 */
	public JMenuItem createMenuItem(JMenu menu, String label, String mnemonic,
			String accessibleDescription, Action action) {
		JMenuItem mi = (JMenuItem) menu.add(new JMenuItem(getString(label)));
		mi.setMnemonic(getMnemonic(mnemonic));
		mi.getAccessibleContext().setAccessibleDescription(
				getString(accessibleDescription));
		mi.addActionListener(action);
		if (action == null) {
			mi.setEnabled(false);
		}
		return mi;
	}

	/**
	 * Creates a JRadioButtonMenuItem for the Themes menu
	 */
	public JMenuItem createThemesMenuItem(JMenu menu, String label,
			String mnemonic, String accessibleDescription, MetalTheme theme) {
		JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menu
				.add(new JRadioButtonMenuItem(getString(label)));
		themesMenuGroup.add(mi);
		mi.setMnemonic(getMnemonic(mnemonic));
		mi.getAccessibleContext().setAccessibleDescription(
				getString(accessibleDescription));
		mi.addActionListener(new ChangeThemeAction(this, theme));

		return mi;
	}

	/**
	 * Creates a JRadioButtonMenuItem for the Look and Feel menu
	 */
	public JMenuItem createLafMenuItem(JMenu menu, String label,
			String mnemonic, String accessibleDescription, String laf) {
		JMenuItem mi = (JRadioButtonMenuItem) menu
				.add(new JRadioButtonMenuItem(getString(label)));
		lafMenuGroup.add(mi);
		mi.setMnemonic(getMnemonic(mnemonic));
		mi.getAccessibleContext().setAccessibleDescription(
				getString(accessibleDescription));
		mi.addActionListener(new ChangeLookAndFeelAction(this, laf));

		mi.setEnabled(isAvailableLookAndFeel(laf));

		return mi;
	}

	/**
	 * Creates a multi-screen menu item
	 */
	public JMenuItem createMultiscreenMenuItem(JMenu menu, int screen) {
		JMenuItem mi = null;
		if (screen == MultiScreenAction.ALL_SCREENS) {
			mi = (JMenuItem) menu.add(new JMenuItem(
					getString("MultiMenu.all_label")));
			mi.setMnemonic(getMnemonic("MultiMenu.all_mnemonic"));
			mi.getAccessibleContext().setAccessibleDescription(
					getString("MultiMenu.all_accessible_description"));
		} else {
			mi = (JMenuItem) menu.add(new JMenuItem(
					getString("MultiMenu.single_label") + " " + screen));
			mi.setMnemonic(KeyEvent.VK_0 + screen);
			mi.getAccessibleContext().setAccessibleDescription(
					getString("MultiMenu.single_accessible_description") + " "
							+ screen);

		}
		mi.addActionListener(new MultiScreenAction(this, screen));
		return mi;
	}

	public JPopupMenu createPopupMenu() {
		JPopupMenu popup = new JPopupMenu("JPopupMenu demo");

		createPopupMenuItem(popup, "LafMenu.windows_label",
				"LafMenu.windows_mnemonic",
				"LafMenu.windows_accessible_description", windows);

		// register key binding to activate popup menu
		InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.SHIFT_MASK),
				"postMenuAction");
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTEXT_MENU, 0),
				"postMenuAction");
		getActionMap().put("postMenuAction",
				new ActivatePopupMenuAction(this, popup));

		return popup;
	}

	/**
	 * Creates a JMenuItem for the Look and Feel popup menu
	 */
	public JMenuItem createPopupMenuItem(JPopupMenu menu, String label,
			String mnemonic, String accessibleDescription, String laf) {
		JMenuItem mi = menu.add(new JMenuItem(getString(label)));
		popupMenuGroup.add(mi);
		mi.setMnemonic(getMnemonic(mnemonic));
		mi.getAccessibleContext().setAccessibleDescription(
				getString(accessibleDescription));
		mi.addActionListener(new ChangeLookAndFeelAction(this, laf));
		mi.setEnabled(isAvailableLookAndFeel(laf));

		return mi;
	}

	/**
	 * Load the first demo. This is done separately from the remaining demos so
	 * that we can get SwingSet2 up and available to the user quickly.
	 */
	public void preloadFirstDemo() {
		DemoModule demo = addDemo(new TabbedPaneDemo());
		setDemo(demo);
	}

	/**
	 * Add a demo to the toolbar
	 */
	public DemoModule addDemo(DemoModule demo) {
		demosList.add(demo);
		if (dragEnabled) {
			demo.updateDragEnabled(true);
		}
		// do the following on the gui thread
		SwingUtilities.invokeLater(new SwingSetRunnable(this, demo) {
			public void run() {
				SwitchToDemoAction action = new SwitchToDemoAction(swingset,
						(DemoModule) obj);
				JToggleButton tb = swingset.getToolBar()
						.addToggleButton(action);
				swingset.getToolBarGroup().add(tb);
				if (swingset.getToolBarGroup().getSelection() == null) {
					tb.setSelected(true);
				}
				tb.setText(null);
				tb.setToolTipText(((DemoModule) obj).getToolTip());

				if (demos[demos.length - 1].equals(obj.getClass().getName())) {
					setStatus(getString("Status.popupMenuAccessible"));
				}

			}
		});
		return demo;
	}

	/**
	 * Sets the current demo
	 */
	public void setDemo(DemoModule demo) {
		currentDemo = demo;

		// Ensure panel's UI is current before making visible
		JComponent currentDemoPanel = demo.getDemoPanel();
		SwingUtilities.updateComponentTreeUI(currentDemoPanel);

		demoPanel.removeAll();
		demoPanel.add(currentDemoPanel, BorderLayout.CENTER);

		tabbedPane.setSelectedIndex(0);
		tabbedPane.setTitleAt(0, demo.getName());
		tabbedPane.setToolTipTextAt(0, demo.getToolTip());
	}

	/**
	 * Bring up the SwingSet2 demo by showing the frame (only applicable if
	 * coming up as an application, not an applet);
	 */
	public void showSwingSet2() {
		if (getFrame() != null) {
			// put swingset in a frame and show it
			JFrame f = getFrame();
			f.setTitle(getString("Frame.title"));
			f.getContentPane().add(this, BorderLayout.CENTER);
			f.pack();

			Rectangle screenRect = f.getGraphicsConfiguration().getBounds();
			Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
					f.getGraphicsConfiguration());

			// Make sure we don't place the demo off the screen.
			int centerWidth = screenRect.width < f.getSize().width ? screenRect.x
					: screenRect.x + screenRect.width / 2 - f.getSize().width
							/ 2;
			int centerHeight = screenRect.height < f.getSize().height ? screenRect.y
					: screenRect.y + screenRect.height / 2 - f.getSize().height
							/ 2;

			centerHeight = centerHeight < screenInsets.top ? screenInsets.top
					: centerHeight;

			f.setLocation(centerWidth, centerHeight);
			f.show();
			numSSs++;
			swingSets.add(this);
		}
	}

	// *******************************************************
	// ****************** Utility Methods ********************
	// *******************************************************

	/*
	void loadDemo(String classname) {
		setStatus(getString("Status.loading") + getString(classname + ".name"));
		DemoModule demo = null;
		try {
			Class demoClass = Class.forName(classname);
			Constructor demoConstructor = demoClass
					.getConstructor();
			demo = (DemoModule) demoConstructor
					.newInstance();
			addDemo(demo);
		} catch (Exception e) {
			System.out.println("Error occurred loading demo: " + classname);
		}
	}
	*/

	/**
	 * A utility function that layers on top of the LookAndFeel's
	 * isSupportedLookAndFeel() method. Returns true if the LookAndFeel is
	 * supported. Returns false if the LookAndFeel is not supported and/or if
	 * there is any kind of error checking if the LookAndFeel is supported.
	 *
	 * The L&F menu will use this method to detemine whether the various L&F
	 * options should be active or inactive.
	 *
	 */
	protected boolean isAvailableLookAndFeel(String laf) {
		try {
			Class lnfClass = Class.forName(laf);
			LookAndFeel newLAF = (LookAndFeel) (lnfClass.newInstance());
			return newLAF.isSupportedLookAndFeel();
		} catch (Exception e) { // If ANYTHING weird happens, return false
			return false;
		}
	}

	/**
	 * Returns the frame instance
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Returns the menubar
	 */
	public JMenuBar getMenuBar() {
		return menuBar;
	}

	/**
	 * Returns the toolbar
	 */
	public ToggleButtonToolBar getToolBar() {
		return toolbar;
	}

	/**
	 * Returns the toolbar button group
	 */
	public ButtonGroup getToolBarGroup() {
		return toolbarGroup;
	}

	/**
	 * Returns the content pane wether we're in an applet or application
	 */
	public Container getContentPane() {
		if (contentPane == null) {
			if (getFrame() != null) {
				contentPane = getFrame().getContentPane();
			}
		}
		return contentPane;
	}

	/**
	 * Create a frame for SwingSet2 to reside in if brought up as an
	 * application.
	 */
	public static JFrame createFrame(GraphicsConfiguration gc) {
		JFrame frame = new JFrame(gc);
		if (numSSs == 0) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {
			WindowListener l = new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					numSSs--;
					swingSets.remove(this);
				}
			};
			frame.addWindowListener(l);
		}
		return frame;
	}

	/**
	 * Set the status
	 */
	public void setStatus(String s) {
		// do the following on the gui thread
		SwingUtilities.invokeLater(new SwingSetRunnable(this, s) {
			public void run() {
				swingset.statusField.setText((String) obj);
			}
		});
	}


	void setDragEnabled(boolean dragEnabled) {
		if (dragEnabled == this.dragEnabled) {
			return;
		}

		this.dragEnabled = dragEnabled;

		for (DemoModule dm : demosList) {
			dm.updateDragEnabled(dragEnabled);
		}

	}

	boolean isDragEnabled() {
		return dragEnabled;
	}

	/**
	 * Returns a mnemonic from the resource bundle. Typically used as keyboard
	 * shortcuts in menu items.
	 */
	public char getMnemonic(String key) {
		return (getString(key)).charAt(0);
	}

	/**
	 * Creates an icon from an image contained in the "images" directory.
	 */
	public ImageIcon createImageIcon(String filename, String description) {
		String path = "resources/images/" + filename;
		return new ImageIcon(getClass().getResource(path));
	}

	/**
	 * Creates an icon from an image contained in the "images" directory.
	 */
	public Image createImage(String filename, String description) {
		String path = "resources/images/" + filename;
		Image image = Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(path));

		return image;
	}

	/**
	 * If DEBUG is defined, prints debug information out to std ouput.
	 */
	public void debug(String s) {
		if (DEBUG) {
			System.out.println((debugCounter++) + ": " + s);
		}
	}

	/**
	 * Stores the current L&F, and calls updateLookAndFeel, below
	 */
	public void setLookAndFeel(String laf) {
		if (!currentLookAndFeel.equals(laf)) {
			currentLookAndFeel = laf;
			/*
			 * The recommended way of synchronizing state between multiple
			 * controls that represent the same command is to use Actions. The
			 * code below is a workaround and will be replaced in future version
			 * of SwingSet2 demo.
			 */
			String lafName = getLookAndFeelLabel(laf);
			updateLookAndFeel();
			for (int i = 0; i < lafMenu.getItemCount(); i++) {
				JMenuItem item = lafMenu.getItem(i);
				item.setSelected(item.getText().equals(lafName));
			}
		}
	}

	private String getLookAndFeelLabel(String laf) {
		return getString("LafMenu.windows_label");
	}

	private void updateThisSwingSet() {

		JFrame frame = getFrame();
		if (frame == null) {
			SwingUtilities.updateComponentTreeUI(this);
		} else {
			SwingUtilities.updateComponentTreeUI(frame);
		}

		SwingUtilities.updateComponentTreeUI(popupMenu);
		if (aboutBox != null) {
			SwingUtilities.updateComponentTreeUI(aboutBox);
		}
	}

	/**
	 * Sets the current L&F on each demo module
	 */
	public void updateLookAndFeel() {
		try {
			UIManager.setLookAndFeel(currentLookAndFeel);

			for (CPAPowerTool ss : swingSets) {
				ss.updateThisSwingSet();
			}
		} catch (Exception ex) {
			System.out.println("Failed loading L&F: " + currentLookAndFeel);
			System.out.println(ex);
		}
	}

	// *******************************************************
	// ************** ToggleButtonToolbar *****************
	// *******************************************************
	static Insets zeroInsets = new Insets(1, 1, 1, 1);

	protected class ToggleButtonToolBar extends JToolBar {
		public ToggleButtonToolBar() {
			super();
		}

		JToggleButton addToggleButton(Action a) {
			JToggleButton tb = new JToggleButton(
					(String) a.getValue(Action.NAME),
					(Icon) a.getValue(Action.SMALL_ICON));
			tb.setMargin(zeroInsets);
			tb.setText(null);
			tb.setEnabled(a.isEnabled());
			tb.setToolTipText((String) a.getValue(Action.SHORT_DESCRIPTION));
			tb.setAction(a);
			add(tb);
			return tb;
		}
	}

	// *******************************************************
	// ********* ToolBar Panel / Docking Listener ***********
	// *******************************************************
	class ToolBarPanel extends JPanel implements ContainerListener {

		public boolean contains(int x, int y) {
			Component c = getParent();
			if (c != null) {
				Rectangle r = c.getBounds();
				return (x >= 0) && (x < r.width) && (y >= 0) && (y < r.height);
			} else {
				return super.contains(x, y);
			}
		}

		public void componentAdded(ContainerEvent e) {
			Container c = e.getContainer().getParent();
			if (c != null) {
				c.getParent().validate();
				c.getParent().repaint();
			}
		}

		public void componentRemoved(ContainerEvent e) {
			Container c = e.getContainer().getParent();
			if (c != null) {
				c.getParent().validate();
				c.getParent().repaint();
			}
		}
	}

	// *******************************************************
	// ****************** Runnables ***********************
	// *******************************************************

	/**
	 * Generic SwingSet2 runnable. This is intended to run on the AWT gui event
	 * thread so as not to muck things up by doing gui work off the gui thread.
	 * Accepts a SwingSet2 and an Object as arguments, which gives subtypes of
	 * this class the two "must haves" needed in most runnables for this demo.
	 */
	class SwingSetRunnable implements Runnable {
		protected CPAPowerTool swingset;
		protected Object obj;

		public SwingSetRunnable(CPAPowerTool swingset, Object obj) {
			this.swingset = swingset;
			this.obj = obj;
		}

		public void run() {
		}
	}

	// *******************************************************
	// ******************** Actions ***********************
	// *******************************************************

	public class SwitchToDemoAction extends AbstractAction {
		CPAPowerTool swingset;
		DemoModule demo;

		public SwitchToDemoAction(CPAPowerTool swingset, DemoModule demo) {
			super(demo.getName(), demo.getIcon());
			this.swingset = swingset;
			this.demo = demo;
		}

		public void actionPerformed(ActionEvent e) {
			swingset.setDemo(demo);
		}
	}

	class OkAction extends AbstractAction {
		JDialog aboutBox;

		protected OkAction(JDialog aboutBox) {
			super("OkAction");
			this.aboutBox = aboutBox;
		}

		public void actionPerformed(ActionEvent e) {
			aboutBox.setVisible(false);
		}
	}

	class ChangeLookAndFeelAction extends AbstractAction {
		CPAPowerTool swingset;
		String laf;

		protected ChangeLookAndFeelAction(CPAPowerTool swingset, String laf) {
			super("ChangeTheme");
			this.swingset = swingset;
			this.laf = laf;
		}

		public void actionPerformed(ActionEvent e) {
			swingset.setLookAndFeel(laf);
		}
	}

	class ActivatePopupMenuAction extends AbstractAction {
		CPAPowerTool swingset;
		JPopupMenu popup;

		protected ActivatePopupMenuAction(CPAPowerTool swingset,
				JPopupMenu popup) {
			super("ActivatePopupMenu");
			this.swingset = swingset;
			this.popup = popup;
		}

		public void actionPerformed(ActionEvent e) {
			Dimension invokerSize = getSize();
			Dimension popupSize = popup.getPreferredSize();
			popup.show(swingset, (invokerSize.width - popupSize.width) / 2,
					(invokerSize.height - popupSize.height) / 2);
		}
	}

	// Turns on all possible auditory feedback
	class OnAudioAction extends AbstractAction {
		CPAPowerTool swingset;

		protected OnAudioAction(CPAPowerTool swingset) {
			super("Audio On");
			this.swingset = swingset;
		}

		public void actionPerformed(ActionEvent e) {
			UIManager.put("AuditoryCues.playList",
					UIManager.get("AuditoryCues.allAuditoryCues"));
			swingset.updateLookAndFeel();
		}
	}

	// Turns on the default amount of auditory feedback
	class DefaultAudioAction extends AbstractAction {
		CPAPowerTool swingset;

		protected DefaultAudioAction(CPAPowerTool swingset) {
			super("Audio Default");
			this.swingset = swingset;
		}

		public void actionPerformed(ActionEvent e) {
			UIManager.put("AuditoryCues.playList",
					UIManager.get("AuditoryCues.defaultCueList"));
			swingset.updateLookAndFeel();
		}
	}

	// Turns off all possible auditory feedback
	class OffAudioAction extends AbstractAction {
		CPAPowerTool swingset;

		protected OffAudioAction(CPAPowerTool swingset) {
			super("Audio Off");
			this.swingset = swingset;
		}

		public void actionPerformed(ActionEvent e) {
			UIManager.put("AuditoryCues.playList",
					UIManager.get("AuditoryCues.noAuditoryCues"));
			swingset.updateLookAndFeel();
		}
	}

	// Turns on or off the tool tips for the demo.
	class ToolTipAction extends AbstractAction {
		protected ToolTipAction() {
			super("ToolTip Control");
		}

		public void actionPerformed(ActionEvent e) {
			boolean status = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			ToolTipManager.sharedInstance().setEnabled(status);
		}
	}

	class DragSupportAction extends AbstractAction {
		protected DragSupportAction() {
			super("DragSupport Control");
		}

		public void actionPerformed(ActionEvent e) {
			boolean dragEnabled = ((JCheckBoxMenuItem) e.getSource())
					.isSelected();
			for (CPAPowerTool ss : swingSets) {
				ss.setDragEnabled(dragEnabled);
			}
		}
	}

	class ChangeThemeAction extends AbstractAction {
		CPAPowerTool swingset;
		MetalTheme theme;

		protected ChangeThemeAction(CPAPowerTool swingset, MetalTheme theme) {
			super("ChangeTheme");
			this.swingset = swingset;
			this.theme = theme;
		}

		public void actionPerformed(ActionEvent e) {
			MetalLookAndFeel.setCurrentTheme(theme);
			swingset.updateLookAndFeel();
		}
	}

	class ExitAction extends AbstractAction {
		CPAPowerTool swingset;

		protected ExitAction(CPAPowerTool swingset) {
			super("ExitAction");
			this.swingset = swingset;
		}

		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	class AboutAction extends AbstractAction {
		CPAPowerTool swingset;

		protected AboutAction(CPAPowerTool swingset) {
			super("AboutAction");
			this.swingset = swingset;
		}

		public void actionPerformed(ActionEvent e) {
			if (aboutBox == null) {
				// JPanel panel = new JPanel(new BorderLayout());
				JPanel panel = new AboutPanel(swingset);
				panel.setLayout(new BorderLayout());

				aboutBox = new JDialog(swingset.getFrame(),
						getString("AboutBox.title"), false);
				aboutBox.setResizable(false);
				aboutBox.getContentPane().add(panel, BorderLayout.CENTER);

				// JButton button = new
				// JButton(getString("AboutBox.ok_button_text"));
				JPanel buttonpanel = new JPanel();
				buttonpanel.setBorder(new javax.swing.border.EmptyBorder(0, 0,
						3, 0));
				buttonpanel.setOpaque(false);
				JButton button = (JButton) buttonpanel.add(new JButton(
						getString("AboutBox.ok_button_text")));
				panel.add(buttonpanel, BorderLayout.SOUTH);

				button.addActionListener(new OkAction(aboutBox));
			}
			aboutBox.pack();
			aboutBox.setLocationRelativeTo(getFrame());
			aboutBox.show();
		}
	}

	class MultiScreenAction extends AbstractAction {
		static final int ALL_SCREENS = -1;
		int screen;

		protected MultiScreenAction(CPAPowerTool swingset, int screen) {
			super("MultiScreenAction");
			this.screen = screen;
		}

		public void actionPerformed(ActionEvent e) {
			GraphicsDevice[] gds = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getScreenDevices();
			if (screen == ALL_SCREENS) {
				for (int i = 0; i < gds.length; i++) {
					CPAPowerTool swingset = new CPAPowerTool(
							gds[i].getDefaultConfiguration());
					swingset.setDragEnabled(dragEnabled);
				}
			} else {
				CPAPowerTool swingset = new CPAPowerTool(
						gds[screen].getDefaultConfiguration());
				swingset.setDragEnabled(dragEnabled);
			}
		}
	}

	// *******************************************************
	// ********************** Misc *************************
	// *******************************************************

	class DemoLoadThread extends Thread {
		CPAPowerTool swingset;

		public DemoLoadThread(CPAPowerTool swingset) {
			this.swingset = swingset;
		}

		public void run() {
			//swingset.loadDemos();
		}
	}

	class AboutPanel extends JPanel {
		ImageIcon aboutimage = null;
		CPAPowerTool swingset = null;

		public AboutPanel(CPAPowerTool swingset) {
			this.swingset = swingset;
			aboutimage = swingset.createImageIcon("About.jpg",
					"AboutBox.accessible_description");
			setOpaque(false);
		}

		public void paint(Graphics g) {
			aboutimage.paintIcon(this, g, 0, 0);
			super.paint(g);
		}

		public Dimension getPreferredSize() {
			return new Dimension(aboutimage.getIconWidth(),
					aboutimage.getIconHeight());
		}
	}

}
