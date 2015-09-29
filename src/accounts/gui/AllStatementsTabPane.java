
package accounts.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SingleSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import accounts.data.TR;
import accounts.gui.utils.FileUtils;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

public class AllStatementsTabPane extends DemoModule implements ActionListener
{
    JTabbedPane                   tabbedpane;
    private static List<List<TR>> listSt;

    public static List<List<TR>> getListSt()
    {
        return listSt;
    }

    public static void setListSt(List<List<TR>> listSt)
    {
        AllStatementsTabPane.listSt = listSt;
    }

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
        if (args.length == 0)
        {
            System.out.println("requires one or more statement.csv as argument");
            System.exit(-1);
        }
        try
        {
            listSt = new ArrayList<>();
            for (int i = 0; i < args.length; i++)
            {
                List<TR> trL = FileUtils.parseTransactions(args[i]);
                listSt.add(trL);
            }

        } catch (IOException | ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        AllStatementsTabPane demo = new AllStatementsTabPane();
        demo.mainImpl();
    }

    public AllStatementsTabPane()
    {
        // Set the title for this demo, and an icon used to represent this
        // demo inside the SwingSet2 app.
        super("AllStatementsTabPane", "toolbar/JTabbedPane.gif");
        JFrame frame = new JFrame();
        frame.setTitle(getString("Frame.title"));
        frame.getContentPane().add(this, BorderLayout.CENTER);
        // create tab
        tabbedpane = new JTabbedPane();
        getDemoPanel().add(tabbedpane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                int i = 0;
                if (listSt != null)
                {
                    for (List<TR> trL : listSt)
                    {
                        TableViewStatement tvSt = new TableViewStatement();
                        tvSt.setTrL(trL);
                        Scene sceneStatements = tvSt.createScene();
                        JFXPanel statement1JfxPanel = new JFXPanel();
                        statement1JfxPanel.setScene(sceneStatements);
                        tabbedpane.addTab(("Statement" + i++), statement1JfxPanel);

                    }
                }

                tabbedpane.getModel().addChangeListener(new ChangeListener()
                {
                    @Override
                    public void stateChanged(ChangeEvent e)
                    {
                        SingleSelectionModel model = (SingleSelectionModel) e.getSource();
                        if (model.getSelectedIndex() == tabbedpane.getTabCount() - 1)
                        {
                        }
                    }
                });
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {

    }

}
