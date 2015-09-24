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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRelation;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import accounts.data.RealProperty;


public class RealPropertiesSwingTable extends DemoModule {
	JTable tableView;
	JScrollPane scrollpane;
	Dimension origin = new Dimension(0, 0);

	JLabel interCellSpacingLabel;
	JLabel rowHeightLabel;

	JLabel headerLabel;
	JLabel footerLabel;

	JTextField headerTextField;
	JTextField footerTextField;

	JCheckBox fitWidth;
	JButton printButton;

	JPanel controlPanel;
	JScrollPane tableAggregate;


	final int INITIAL_ROWHEIGHT = 33;
	private static List<RealProperty> rpL;

	public static List<RealProperty> parsePropFile(String filename)
			throws IOException, ParseException {

		final FileReader fr = new FileReader(filename);
		final BufferedReader br = new BufferedReader(fr);
		List<RealProperty> rpL = new ArrayList<RealProperty>();
		try {
			for (String line; (line = br.readLine()) != null;) {
				line = line.toLowerCase().trim();
				if (line.isEmpty()) {
					continue;
				}
				line = line.toLowerCase().trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				String[] fields = line.split(",");
				if (fields.length != 7) {
					throw new IOException("Invalid property line=" + line);
				}

				RealProperty rp = new RealProperty();
				rp.setPropertyName(fields[0]);
				rp.setCost(new Integer(fields[1]));
				rp.setLandValue(new Integer(fields[2]));
				rp.setRenovation(new Integer(fields[3]));
				rp.setLoanClosingCost(new Integer(fields[4]));
				rp.setOwnerCount(new Integer(fields[5]));

				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				Date purchaseDate = formatter.parse(fields[6]);

				rp.setPurchaseDate(purchaseDate);
				rpL.add(rp);
			}
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
					// Ignore
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (final IOException e) {
					// Ignore
				}
			}
		}
		return rpL;
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("requires properties.csv as argument");
			System.exit(-1);
		}
		try {
			rpL = parsePropFile(args[0]);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RealPropertiesSwingTable demo = new RealPropertiesSwingTable();
		demo.mainImpl();
	}

	public RealPropertiesSwingTable() {
		super( "RealProperties", "toolbar/JTable.gif");

		getDemoPanel().setLayout(new BorderLayout());
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		JPanel cbPanel = new JPanel(new GridLayout(3, 2));

		JPanel comboPanel = new JPanel(new GridLayout(2, 1));
		JPanel printPanel = new JPanel(new ColumnLayout());

		getDemoPanel().add(controlPanel, BorderLayout.NORTH);
		Vector relatedComponents = new Vector();

		// Show that showHorizontal/Vertical controls are related
		relatedComponents.removeAllElements();
		buildAccessibleGroup(relatedComponents);

		// Show that row/column selections are related
		relatedComponents.removeAllElements();

		// Show that spacing controls are related
		relatedComponents.removeAllElements();
		buildAccessibleGroup(relatedComponents);

		// Create the table.
		tableAggregate = createTable();
		getDemoPanel().add(tableAggregate, BorderLayout.CENTER);

		// ComboBox for selection modes.
		JPanel selectMode = new JPanel();
		selectMode.setLayout(new BoxLayout(selectMode, BoxLayout.X_AXIS));
		selectMode.setBorder(new TitledBorder(
				getString("RealProperties.selection_mode")));

		selectMode.add(Box.createHorizontalStrut(2));
		selectMode.add(Box.createHorizontalGlue());
		comboPanel.add(selectMode);

		// Combo box for table resize mode.
		JPanel resizeMode = new JPanel();
		resizeMode.setLayout(new BoxLayout(resizeMode, BoxLayout.X_AXIS));
		resizeMode.setBorder(new TitledBorder(
				getString("RealProperties.autoresize_mode")));

		resizeMode.add(Box.createHorizontalStrut(2));
		resizeMode.add(Box.createHorizontalGlue());
		comboPanel.add(resizeMode);

		// print panel
		printPanel.setBorder(new TitledBorder(
				getString("RealProperties.printing")));
		headerLabel = new JLabel(getString("RealProperties.header"));
		footerLabel = new JLabel(getString("RealProperties.footer"));
		headerTextField = new JTextField(
				getString("RealProperties.headerText"), 15);
		footerTextField = new JTextField(
				getString("RealProperties.footerText"), 15);
		fitWidth = new JCheckBox(getString("RealProperties.fitWidth"), true);
		printButton = new JButton(getString("RealProperties.print"));
		printButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				printTable();
			}
		});

		printPanel.add(headerLabel);
		printPanel.add(headerTextField);
		printPanel.add(footerLabel);
		printPanel.add(footerTextField);

		JPanel buttons = new JPanel();
		buttons.add(fitWidth);
		buttons.add(printButton);

		printPanel.add(buttons);

		// Show that printing controls are related
		relatedComponents.removeAllElements();
		relatedComponents.add(headerTextField);
		relatedComponents.add(footerTextField);
		relatedComponents.add(printButton);
		buildAccessibleGroup(relatedComponents);

		// add everything
		controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));

		getDemoPanel().getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke("ctrl P"), "print");

		getDemoPanel().getActionMap().put("print", new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				printTable();
			}
		});

	}

	/**
	 * Sets the Accessibility MEMBER_OF property to denote that these components
	 * work together as a group. Each object is set to be a MEMBER_OF an array
	 * that contains all of the objects in the group, including itself.
	 *
	 * @param components
	 *            The list of objects that are related
	 */
	void buildAccessibleGroup(Vector components) {

		AccessibleContext context = null;
		int numComponents = components.size();
		Object[] group = components.toArray();
		Object object = null;
		for (int i = 0; i < numComponents; ++i) {
			object = components.elementAt(i);
			if (object instanceof Accessible) {
				context = ((Accessible) components.elementAt(i))
						.getAccessibleContext();
				context.getAccessibleRelationSet().add(
						new AccessibleRelation(AccessibleRelation.MEMBER_OF,
								group));
			}
		}
	} // buildAccessibleGroup()

	public JScrollPane createTable() {

		// final
		final String[] names = { getString("RealProperties.property_name"),
				getString("RealProperties.cost"),
				getString("RealProperties.land_value"),
				getString("RealProperties.renovation"),
				getString("RealProperties.loan_closing_cost"),
				getString("RealProperties.purchase_date") };

		// Create a model of the data.
		TableModel dataModel = new AbstractTableModel() {
			public int getColumnCount() {
				return names.length;
			}

			public int getRowCount() {
				if (rpL == null)
					return 0;
				return rpL.size();
			}

			public Object getValueAt(int row, int col) {
				if (rpL == null)
					return "";
				RealProperty rp = rpL.get(row);
				return rp.getPropertyName();
				// return data[row][col];
			}

			public String getColumnName(int column) {
				return names[column];
			}

			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			public boolean isCellEditable(int row, int col) {
				return true;
			}

			public void setValueAt(Object aValue, int row, int column) {
				// data[row][column] = "" + aValue;
			}
		};

		// Create the table
		tableView = new JTable(dataModel);
		TableRowSorter sorter = new TableRowSorter(dataModel);
		tableView.setRowSorter(sorter);

		tableView.setRowHeight(INITIAL_ROWHEIGHT);

		scrollpane = new JScrollPane(tableView);
		return scrollpane;
	}

	private void printTable() {
		MessageFormat headerFmt;
		MessageFormat footerFmt;
		JTable.PrintMode printMode = fitWidth.isSelected() ? JTable.PrintMode.FIT_WIDTH
				: JTable.PrintMode.NORMAL;

		String text;
		text = headerTextField.getText();
		if (text != null && text.length() > 0) {
			headerFmt = new MessageFormat(text);
		} else {
			headerFmt = null;
		}

		text = footerTextField.getText();
		if (text != null && text.length() > 0) {
			footerFmt = new MessageFormat(text);
		} else {
			footerFmt = null;
		}

		try {
			boolean status = tableView.print(printMode, headerFmt, footerFmt);

			if (status) {
				JOptionPane.showMessageDialog(tableView.getParent(),
						getString("RealProperties.printingComplete"),
						getString("RealProperties.printingResult"),
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(tableView.getParent(),
						getString("RealProperties.printingCancelled"),
						getString("RealProperties.printingResult"),
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (PrinterException pe) {
			String errorMessage = MessageFormat.format(
					getString("RealProperties.printingFailed"),
					new Object[] { pe.getMessage() });
			JOptionPane.showMessageDialog(tableView.getParent(), errorMessage,
					getString("RealProperties.printingResult"),
					JOptionPane.ERROR_MESSAGE);
		} catch (SecurityException se) {
			String errorMessage = MessageFormat.format(
					getString("RealProperties.printingFailed"),
					new Object[] { se.getMessage() });
			JOptionPane.showMessageDialog(tableView.getParent(), errorMessage,
					getString("RealProperties.printingResult"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	class ColumnLayout implements LayoutManager {
		int xInset = 5;
		int yInset = 5;
		int yGap = 2;

		public void addLayoutComponent(String s, Component c) {
		}

		public void layoutContainer(Container c) {
			Insets insets = c.getInsets();
			int height = yInset + insets.top;

			Component[] children = c.getComponents();
			Dimension compSize = null;
			for (int i = 0; i < children.length; i++) {
				compSize = children[i].getPreferredSize();
				children[i].setSize(compSize.width, compSize.height);
				children[i].setLocation(xInset + insets.left, height);
				height += compSize.height + yGap;
			}

		}

		public Dimension minimumLayoutSize(Container c) {
			Insets insets = c.getInsets();
			int height = yInset + insets.top;
			int width = 0 + insets.left + insets.right;

			Component[] children = c.getComponents();
			Dimension compSize = null;
			for (int i = 0; i < children.length; i++) {
				compSize = children[i].getPreferredSize();
				height += compSize.height + yGap;
				width = Math.max(width, compSize.width + insets.left
						+ insets.right + xInset * 2);
			}
			height += insets.bottom;
			return new Dimension(width, height);
		}

		public Dimension preferredLayoutSize(Container c) {
			return minimumLayoutSize(c);
		}

		public void removeLayoutComponent(Component c) {
		}
	}

	void updateDragEnabled(boolean dragEnabled) {
		tableView.setDragEnabled(dragEnabled);
		headerTextField.setDragEnabled(dragEnabled);
		footerTextField.setDragEnabled(dragEnabled);
	}

}
