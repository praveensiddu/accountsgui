
package accounts.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class StatementPanel extends DemoModule
{
    JTable      tableView;
    JScrollPane scrollpane;
    Dimension   origin = new Dimension(0, 0);

    JCheckBox isColumnSelectionAllowedCheckBox;
    JCheckBox isRowSelectionAllowedCheckBox;

    JLabel rowHeightLabel;

    JSlider rowHeightSlider;

    JLabel headerLabel;
    JLabel footerLabel;

    JTextField headerTextField;
    JTextField footerTextField;

    JPanel      controlPanel;
    JScrollPane tableAggregate;

    String path = "ImageClub/food/";

    final int INITIAL_ROWHEIGHT = 33;

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args)
    {
        try
        {
            TextAndMnemonicUtils.createInstance();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        StatementPanel demo = new StatementPanel();
        demo.mainImpl();
    }

    public StatementPanel()
    {
        super("StatementPanel", "toolbar/JTable.gif");

        getDemoPanel().setLayout(new BorderLayout());
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        JPanel cbPanel = new JPanel(new GridLayout(3, 2));
        JPanel labelPanel = new JPanel(new GridLayout(2, 1))
        {
            @Override
            public Dimension getMaximumSize()
            {
                return new Dimension(getPreferredSize().width, super.getMaximumSize().height);
            }
        };
        JPanel sliderPanel = new JPanel(new GridLayout(2, 1))
        {
            @Override
            public Dimension getMaximumSize()
            {
                return new Dimension(getPreferredSize().width, super.getMaximumSize().height);
            }
        };
        JPanel comboPanel = new JPanel(new GridLayout(2, 1));
        JPanel printPanel = new JPanel(new ColumnLayout());

        getDemoPanel().add(controlPanel, BorderLayout.NORTH);
        Vector relatedComponents = new Vector();

        // Show that showHorizontal/Vertical controls are related
        relatedComponents.removeAllElements();
        buildAccessibleGroup(relatedComponents);

        isRowSelectionAllowedCheckBox = new JCheckBox(getString("StatementPanel.row_selection"), true);
        isRowSelectionAllowedCheckBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                boolean flag = ((JCheckBox) e.getSource()).isSelected();
                tableView.setRowSelectionAllowed(flag);
                ;
                tableView.repaint();
            }
        });

        isColumnSelectionAllowedCheckBox = new JCheckBox(getString("StatementPanel.column_selection"), false);
        isColumnSelectionAllowedCheckBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                boolean flag = ((JCheckBox) e.getSource()).isSelected();
                tableView.setColumnSelectionAllowed(flag);
                ;
                tableView.repaint();
            }
        });

        // Show that row/column selections are related
        relatedComponents.removeAllElements();
        relatedComponents.add(isColumnSelectionAllowedCheckBox);
        relatedComponents.add(isRowSelectionAllowedCheckBox);
        buildAccessibleGroup(relatedComponents);

        cbPanel.add(isRowSelectionAllowedCheckBox);
        cbPanel.add(isColumnSelectionAllowedCheckBox);

        rowHeightLabel = new JLabel(getString("StatementPanel.row_height_colon"));
        labelPanel.add(rowHeightLabel);

        rowHeightSlider = new JSlider(JSlider.HORIZONTAL, 5, 100, INITIAL_ROWHEIGHT);
        rowHeightSlider.getAccessibleContext().setAccessibleName(getString("StatementPanel.row_height"));
        rowHeightLabel.setLabelFor(rowHeightSlider);
        sliderPanel.add(rowHeightSlider);
        rowHeightSlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                int height = ((JSlider) e.getSource()).getValue();
                tableView.setRowHeight(height);
                tableView.repaint();
            }
        });

        // Show that spacing controls are related
        relatedComponents.removeAllElements();
        relatedComponents.add(rowHeightSlider);
        buildAccessibleGroup(relatedComponents);

        // Create the table.
        tableAggregate = createTable();
        getDemoPanel().add(tableAggregate, BorderLayout.CENTER);

        // print panel
        printPanel.setBorder(new TitledBorder(getString("StatementPanel.printing")));
        headerLabel = new JLabel(getString("StatementPanel.header"));
        footerLabel = new JLabel(getString("StatementPanel.footer"));
        headerTextField = new JTextField(getString("StatementPanel.headerText"), 15);
        footerTextField = new JTextField(getString("StatementPanel.footerText"), 15);

        printPanel.add(headerLabel);
        printPanel.add(headerTextField);
        printPanel.add(footerLabel);
        printPanel.add(footerTextField);

        JPanel buttons = new JPanel();

        printPanel.add(buttons);

        // Show that printing controls are related
        relatedComponents.removeAllElements();
        relatedComponents.add(headerTextField);
        relatedComponents.add(footerTextField);
        buildAccessibleGroup(relatedComponents);

        // wrap up the panels and add them
        JPanel sliderWrapper = new JPanel();
        sliderWrapper.setLayout(new BoxLayout(sliderWrapper, BoxLayout.X_AXIS));
        sliderWrapper.add(labelPanel);
        sliderWrapper.add(sliderPanel);
        sliderWrapper.add(Box.createHorizontalGlue());
        sliderWrapper.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));

        JPanel leftWrapper = new JPanel();
        leftWrapper.setLayout(new BoxLayout(leftWrapper, BoxLayout.Y_AXIS));
        leftWrapper.add(cbPanel);
        leftWrapper.add(sliderWrapper);

        // add everything
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        controlPanel.add(leftWrapper);

        setTableControllers(); // Set accessibility information

    }

    void buildAccessibleGroup(Vector components)
    {

        AccessibleContext context = null;
        int numComponents = components.size();
        Object[] group = components.toArray();
        Object object = null;
        for (int i = 0; i < numComponents; ++i)
        {
            object = components.elementAt(i);
            if (object instanceof Accessible)
            {
                context = ((Accessible) components.elementAt(i)).getAccessibleContext();
                context.getAccessibleRelationSet().add(new AccessibleRelation(AccessibleRelation.MEMBER_OF, group));
            }
        }
    } // buildAccessibleGroup()

    /**
     * This sets CONTROLLER_FOR on the controls that manipulate the table and CONTROLLED_BY relationships on the table to point
     * back to the controllers.
     */
    private void setTableControllers()
    {

        // Set up the relationships to show what controls the table
        setAccessibleController(isColumnSelectionAllowedCheckBox, tableAggregate);
        setAccessibleController(isRowSelectionAllowedCheckBox, tableAggregate);
        setAccessibleController(rowHeightSlider, tableAggregate);
    } // setTableControllers()

    /**
     * Sets up accessibility relationships to denote that one object controls another. The CONTROLLER_FOR property is set on the
     * controller object, and the CONTROLLED_BY property is set on the target object.
     */
    private void setAccessibleController(JComponent controller, JComponent target)
    {
        AccessibleRelationSet controllerRelations = controller.getAccessibleContext().getAccessibleRelationSet();
        AccessibleRelationSet targetRelations = target.getAccessibleContext().getAccessibleRelationSet();

        controllerRelations.add(new AccessibleRelation(AccessibleRelation.CONTROLLER_FOR, target));
        targetRelations.add(new AccessibleRelation(AccessibleRelation.CONTROLLED_BY, controller));
    } // setAccessibleController()

    public JScrollPane createTable()
    {

        // final
        final String[] names = { getString("TableDemo.first_name"), getString("TableDemo.last_name"),
                getString("TableDemo.favorite_color"), getString("TableDemo.favorite_movie"),
                getString("TableDemo.favorite_number"), getString("TableDemo.favorite_food") };

        ImageIcon apple = createImageIcon("ImageClub/food/apple.jpg", getString("TableDemo.apple"));
        ImageIcon asparagus = createImageIcon("ImageClub/food/asparagus.jpg", getString("TableDemo.asparagus"));
        ImageIcon banana = createImageIcon("ImageClub/food/banana.jpg", getString("TableDemo.banana"));
        ImageIcon broccoli = createImageIcon("ImageClub/food/broccoli.jpg", getString("TableDemo.broccoli"));
        ImageIcon cantaloupe = createImageIcon("ImageClub/food/cantaloupe.jpg", getString("TableDemo.cantaloupe"));
        ImageIcon carrot = createImageIcon("ImageClub/food/carrot.jpg", getString("TableDemo.carrot"));
        ImageIcon corn = createImageIcon("ImageClub/food/corn.jpg", getString("TableDemo.corn"));
        ImageIcon grapes = createImageIcon("ImageClub/food/grapes.jpg", getString("TableDemo.grapes"));
        ImageIcon grapefruit = createImageIcon("ImageClub/food/grapefruit.jpg", getString("TableDemo.grapefruit"));
        ImageIcon kiwi = createImageIcon("ImageClub/food/kiwi.jpg", getString("TableDemo.kiwi"));
        ImageIcon onion = createImageIcon("ImageClub/food/onion.jpg", getString("TableDemo.onion"));
        ImageIcon pear = createImageIcon("ImageClub/food/pear.jpg", getString("TableDemo.pear"));
        ImageIcon peach = createImageIcon("ImageClub/food/peach.jpg", getString("TableDemo.peach"));
        ImageIcon pepper = createImageIcon("ImageClub/food/pepper.jpg", getString("TableDemo.pepper"));
        ImageIcon pickle = createImageIcon("ImageClub/food/pickle.jpg", getString("TableDemo.pickle"));
        ImageIcon pineapple = createImageIcon("ImageClub/food/pineapple.jpg", getString("TableDemo.pineapple"));
        ImageIcon raspberry = createImageIcon("ImageClub/food/raspberry.jpg", getString("TableDemo.raspberry"));
        ImageIcon sparegrass = createImageIcon("ImageClub/food/asparagus.jpg", getString("TableDemo.sparegrass"));
        ImageIcon strawberry = createImageIcon("ImageClub/food/strawberry.jpg", getString("TableDemo.strawberry"));
        ImageIcon tomato = createImageIcon("ImageClub/food/tomato.jpg", getString("TableDemo.tomato"));
        ImageIcon watermelon = createImageIcon("ImageClub/food/watermelon.jpg", getString("TableDemo.watermelon"));

        NamedColor aqua = new NamedColor(new Color(127, 255, 212), getString("TableDemo.aqua"));
        NamedColor beige = new NamedColor(new Color(245, 245, 220), getString("TableDemo.beige"));
        NamedColor black = new NamedColor(Color.black, getString("TableDemo.black"));
        NamedColor blue = new NamedColor(new Color(0, 0, 222), getString("TableDemo.blue"));
        NamedColor eblue = new NamedColor(Color.blue, getString("TableDemo.eblue"));
        NamedColor jfcblue = new NamedColor(new Color(204, 204, 255), getString("TableDemo.jfcblue"));
        NamedColor jfcblue2 = new NamedColor(new Color(153, 153, 204), getString("TableDemo.jfcblue2"));
        NamedColor cybergreen = new NamedColor(Color.green.darker().brighter(), getString("TableDemo.cybergreen"));
        NamedColor darkgreen = new NamedColor(new Color(0, 100, 75), getString("TableDemo.darkgreen"));
        NamedColor forestgreen = new NamedColor(Color.green.darker(), getString("TableDemo.forestgreen"));
        NamedColor gray = new NamedColor(Color.gray, getString("TableDemo.gray"));
        NamedColor green = new NamedColor(Color.green, getString("TableDemo.green"));
        NamedColor orange = new NamedColor(new Color(255, 165, 0), getString("TableDemo.orange"));
        NamedColor purple = new NamedColor(new Color(160, 32, 240), getString("TableDemo.purple"));
        NamedColor red = new NamedColor(Color.red, getString("TableDemo.red"));
        NamedColor rustred = new NamedColor(Color.red.darker(), getString("TableDemo.rustred"));
        NamedColor sunpurple = new NamedColor(new Color(100, 100, 255), getString("TableDemo.sunpurple"));
        NamedColor suspectpink = new NamedColor(new Color(255, 105, 180), getString("TableDemo.suspectpink"));
        NamedColor turquoise = new NamedColor(new Color(0, 255, 255), getString("TableDemo.turquoise"));
        NamedColor violet = new NamedColor(new Color(238, 130, 238), getString("TableDemo.violet"));
        NamedColor yellow = new NamedColor(Color.yellow, getString("TableDemo.yellow"));

        // Create the dummy data (a few rows of names)
        final Object[][] data = { { "Mike", "Albers", green, getString("TableDemo.brazil"), new Double(44.0), strawberry },
                { "Mark", "Andrews", blue, getString("TableDemo.curse"), new Double(3), grapes },
                { "Brian", "Beck", black, getString("TableDemo.bluesbros"), new Double(2.7182818285), raspberry },
                { "Lara", "Bunni", red, getString("TableDemo.airplane"), new Double(15), strawberry },
                { "Roger", "Brinkley", blue, getString("TableDemo.man"), new Double(13), peach },
                { "Brent", "Christian", black, getString("TableDemo.bladerunner"), new Double(23), broccoli },
                { "Mark", "Davidson", darkgreen, getString("TableDemo.brazil"), new Double(27), asparagus },
                { "Jeff", "Dinkins", blue, getString("TableDemo.ladyvanishes"), new Double(8), kiwi },
                { "Ewan", "Dinkins", yellow, getString("TableDemo.bugs"), new Double(2), strawberry },
                { "Amy", "Fowler", violet, getString("TableDemo.reservoir"), new Double(3), raspberry },
                { "Hania", "Gajewska", purple, getString("TableDemo.jules"), new Double(5), raspberry },
                { "David", "Geary", blue, getString("TableDemo.pulpfiction"), new Double(3), watermelon },
                // {"James", "Gosling", pink, getString("TableDemo.tennis"), new
                // Double(21), donut},
                { "Eric", "Hawkes", blue, getString("TableDemo.bladerunner"), new Double(.693), pickle },
                { "Shannon", "Hickey", green, getString("TableDemo.shawshank"), new Double(2), grapes },
                { "Earl", "Johnson", green, getString("TableDemo.pulpfiction"), new Double(8), carrot },
                { "Robi", "Khan", green, getString("TableDemo.goodfellas"), new Double(89), apple },
                { "Robert", "Kim", blue, getString("TableDemo.mohicans"), new Double(655321), strawberry },
                { "Janet", "Koenig", turquoise, getString("TableDemo.lonestar"), new Double(7), peach },
                { "Jeff", "Kesselman", blue, getString("TableDemo.stuntman"), new Double(17), pineapple },
                { "Onno", "Kluyt", orange, getString("TableDemo.oncewest"), new Double(8), broccoli },
                { "Peter", "Korn", sunpurple, getString("TableDemo.musicman"), new Double(12), sparegrass },

                { "Rick", "Levenson", black, getString("TableDemo.harold"), new Double(1327), raspberry },
                { "Brian", "Lichtenwalter", jfcblue, getString("TableDemo.fifthelement"), new Double(22), pear },
                { "Malini", "Minasandram", beige, getString("TableDemo.joyluck"), new Double(9), corn },
                { "Michael", "Martak", green, getString("TableDemo.city"), new Double(3), strawberry },
                { "David", "Mendenhall", forestgreen, getString("TableDemo.schindlerslist"), new Double(7), peach },
                { "Phil", "Milne", suspectpink, getString("TableDemo.withnail"), new Double(3), banana },
                { "Lynn", "Monsanto", cybergreen, getString("TableDemo.dasboot"), new Double(52), peach },
                { "Hans", "Muller", rustred, getString("TableDemo.eraserhead"), new Double(0), pineapple },
                { "Joshua", "Outwater", blue, getString("TableDemo.labyrinth"), new Double(3), pineapple },
                { "Tim", "Prinzing", blue, getString("TableDemo.firstsight"), new Double(69), pepper },
                { "Raj", "Premkumar", jfcblue2, getString("TableDemo.none"), new Double(7), broccoli },
                { "Howard", "Rosen", green, getString("TableDemo.defending"), new Double(7), strawberry },
                { "Ray", "Ryan", black, getString("TableDemo.buckaroo"),
                        new Double(3.141592653589793238462643383279502884197169399375105820974944), banana },
                { "Georges", "Saab", aqua, getString("TableDemo.bicycle"), new Double(290), cantaloupe },
                { "Tom", "Santos", blue, getString("TableDemo.spinaltap"), new Double(241), pepper },
                { "Rich", "Schiavi", blue, getString("TableDemo.repoman"), new Double(0xFF), pepper },
                { "Nancy", "Schorr", green, getString("TableDemo.fifthelement"), new Double(47), watermelon },
                { "Keith", "Sprochi", darkgreen, getString("TableDemo.2001"), new Double(13), watermelon },
                { "Matt", "Tucker", eblue, getString("TableDemo.starwars"), new Double(2), broccoli },
                { "Dmitri", "Trembovetski", red, getString("TableDemo.aliens"), new Double(222), tomato },
                { "Scott", "Violet", violet, getString("TableDemo.raiders"), new Double(-97), banana },
                { "Kathy", "Walrath", darkgreen, getString("TableDemo.thinman"), new Double(8), pear },
                { "Nathan", "Walrath", black, getString("TableDemo.chusingura"), new Double(3), grapefruit },
                { "Steve", "Wilson", green, getString("TableDemo.raiders"), new Double(7), onion },
                { "Kathleen", "Zelony", gray, getString("TableDemo.dog"), new Double(13), grapes } };

        // Create a model of the data.
        TableModel dataModel = new AbstractTableModel()
        {
            @Override
            public int getColumnCount()
            {
                return names.length;
            }

            @Override
            public int getRowCount()
            {
                return data.length;
            }

            @Override
            public Object getValueAt(int row, int col)
            {
                return data[row][col];
            }

            @Override
            public String getColumnName(int column)
            {
                return names[column];
            }

            @Override
            public Class getColumnClass(int c)
            {
                return getValueAt(0, c).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int col)
            {
                return col != 5;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column)
            {
                data[row][column] = aValue;
            }
        };

        // Create the table
        tableView = new JTable(dataModel);
        TableRowSorter sorter = new TableRowSorter(dataModel);
        tableView.setRowSorter(sorter);

        // Show colors by rendering them in their own color.
        DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer()
        {
            @Override
            public void setValue(Object value)
            {
                if (value instanceof NamedColor)
                {
                    NamedColor c = (NamedColor) value;
                    setBackground(c);
                    setForeground(c.getTextColor());
                    setText(c.toString());
                } else
                {
                    super.setValue(value);
                }
            }
        };

        // Create a combo box to show that you can use one in a table.
        JComboBox comboBox = new JComboBox();
        comboBox.addItem(aqua);
        comboBox.addItem(beige);
        comboBox.addItem(black);
        comboBox.addItem(blue);
        comboBox.addItem(eblue);
        comboBox.addItem(jfcblue);
        comboBox.addItem(jfcblue2);
        comboBox.addItem(cybergreen);
        comboBox.addItem(darkgreen);
        comboBox.addItem(forestgreen);
        comboBox.addItem(gray);
        comboBox.addItem(green);
        comboBox.addItem(orange);
        comboBox.addItem(purple);
        comboBox.addItem(red);
        comboBox.addItem(rustred);
        comboBox.addItem(sunpurple);
        comboBox.addItem(suspectpink);
        comboBox.addItem(turquoise);
        comboBox.addItem(violet);
        comboBox.addItem(yellow);

        TableColumn colorColumn = tableView.getColumn(getString("TableDemo.favorite_color"));
        // Use the combo box as the editor in the "Favorite Color" column.
        colorColumn.setCellEditor(new DefaultCellEditor(comboBox));

        colorRenderer.setHorizontalAlignment(JLabel.CENTER);
        colorColumn.setCellRenderer(colorRenderer);

        tableView.setRowHeight(INITIAL_ROWHEIGHT);

        scrollpane = new JScrollPane(tableView);
        return scrollpane;
    }

    class NamedColor extends Color
    {
        String name;

        public NamedColor(Color color, String name)
        {
            super(color.getRGB());
            this.name = name;
        }

        public Color getTextColor()
        {
            int r = getRed();
            int g = getGreen();
            int b = getBlue();
            if (r > 240 || g > 240)
            {
                return Color.black;
            } else
            {
                return Color.white;
            }
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    class ColumnLayout implements LayoutManager
    {
        int xInset = 5;
        int yInset = 5;
        int yGap   = 2;

        @Override
        public void addLayoutComponent(String s, Component c)
        {
        }

        @Override
        public void layoutContainer(Container c)
        {
            Insets insets = c.getInsets();
            int height = yInset + insets.top;

            Component[] children = c.getComponents();
            Dimension compSize = null;
            for (int i = 0; i < children.length; i++)
            {
                compSize = children[i].getPreferredSize();
                children[i].setSize(compSize.width, compSize.height);
                children[i].setLocation(xInset + insets.left, height);
                height += compSize.height + yGap;
            }

        }

        @Override
        public Dimension minimumLayoutSize(Container c)
        {
            Insets insets = c.getInsets();
            int height = yInset + insets.top;
            int width = 0 + insets.left + insets.right;

            Component[] children = c.getComponents();
            Dimension compSize = null;
            for (int i = 0; i < children.length; i++)
            {
                compSize = children[i].getPreferredSize();
                height += compSize.height + yGap;
                width = Math.max(width, compSize.width + insets.left + insets.right + xInset * 2);
            }
            height += insets.bottom;
            return new Dimension(width, height);
        }

        @Override
        public Dimension preferredLayoutSize(Container c)
        {
            return minimumLayoutSize(c);
        }

        @Override
        public void removeLayoutComponent(Component c)
        {
        }
    }

}
