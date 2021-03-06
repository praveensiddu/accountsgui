package accounts.gui;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import accounts.data.RealProperty;
import accounts.gui.utils.FileUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TableViewProperties extends Application
{

    private TableView<RealProperty>      table = new TableView<>();
    private ObservableList<RealProperty> data;
    final HBox                           hb    = new HBox();
    private static List<RealProperty>    rpL;

    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("requires properties.csv as argument");
            System.exit(-1);
        }
        try
        {
            rpL = FileUtils.parsePropFile(args[0]);
        } catch (IOException | ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        launch(args);
    }

    private static void initAndShowGUI(JFXPanel jfxPanel)
    {

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                TableViewProperties tvs = new TableViewProperties();
                Scene scene = tvs.createScene(rpL);
                jfxPanel.setScene(scene);
            }
        });
    }

    public Scene createScene(List<RealProperty> rpList)
    {
        data = FXCollections.observableArrayList(rpList);
        Scene scene = new Scene(new Group());

        final Label label = new Label("Summary:");
        label.setFont(new Font("Arial", 20));

        Callback<TableColumn<RealProperty, Integer>, TableCell<RealProperty, Integer>> numericFactory = new Callback<TableColumn<RealProperty, Integer>, TableCell<RealProperty, Integer>>()
        {
            @Override
            public TableCell<RealProperty, Integer> call(TableColumn<RealProperty, Integer> p)
            {
                return new NumericEditableTableCell<>();
            }
        };

        Callback<TableColumn<RealProperty, Date>, TableCell<RealProperty, Date>> dateCellFactory = new Callback<TableColumn<RealProperty, Date>, TableCell<RealProperty, Date>>()
        {
            @Override
            public TableCell<RealProperty, Date> call(TableColumn<RealProperty, Date> p)
            {
                return new DateEditableTableCell<>();
            }
        };

        table.setEditable(true);
        table.setMaxWidth(950);
        table.setMinWidth(750);

        TableColumn<RealProperty, String> propNameCol = new TableColumn<>("Property Name");
        propNameCol.setMinWidth(150);
        propNameCol.setCellValueFactory(new PropertyValueFactory<>("propertyName"));
        propNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        propNameCol.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, String>>()
        {
            @Override
            public void handle(CellEditEvent<RealProperty, String> t)
            {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setPropertyName(t.getNewValue());
            }
        });

        TableColumn<RealProperty, Integer> landValueCol = new TableColumn<>("Land Value");
        landValueCol.setMinWidth(75);
        landValueCol.setCellValueFactory(new PropertyValueFactory<>("landValue"));
        landValueCol.setCellFactory(numericFactory);
        landValueCol.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, Integer>>()
        {
            @Override
            public void handle(CellEditEvent<RealProperty, Integer> t)
            {
                Object ob = t.getNewValue();
                Integer newValue;
                if (ob instanceof Long)
                {
                    newValue = Integer.valueOf(((Long) ob).intValue());
                } else
                {
                    newValue = t.getNewValue().intValue();
                }
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setLandValue(newValue);
            }
        });

        TableColumn<RealProperty, Integer> costCol = new TableColumn<>("Cost");
        costCol.setMinWidth(75);
        costCol.setCellValueFactory(new PropertyValueFactory<RealProperty, Integer>("cost"));
        costCol.setCellFactory(numericFactory);
        costCol.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, Integer>>()
        {
            @Override
            public void handle(CellEditEvent<RealProperty, Integer> t)
            {
                Object ob = t.getNewValue();
                Integer newValue;
                if (ob instanceof Long)
                {
                    newValue = Integer.valueOf(((Long) ob).intValue());
                } else
                {
                    newValue = t.getNewValue().intValue();
                }
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setCost(newValue);
            }
        });

        TableColumn<RealProperty, Integer> renovationCol = new TableColumn<>("Renovation");
        renovationCol.setMinWidth(75);
        renovationCol.setCellValueFactory(new PropertyValueFactory<>("renovation"));
        renovationCol.setCellFactory(numericFactory);
        renovationCol.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, Integer>>()
        {
            @Override
            public void handle(CellEditEvent<RealProperty, Integer> t)
            {

                Object ob = t.getNewValue();
                Integer newValue;
                if (ob instanceof Long)
                {
                    newValue = Integer.valueOf(((Long) ob).intValue());
                } else
                {
                    newValue = t.getNewValue().intValue();
                }
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setRenovation(newValue);
            }
        });

        TableColumn<RealProperty, Integer> loanClosingCostCol = new TableColumn<>("Loan Closing");
        loanClosingCostCol.setMinWidth(75);
        loanClosingCostCol.setCellValueFactory(new PropertyValueFactory<>("loanClosingCost"));
        loanClosingCostCol.setCellFactory(numericFactory);
        loanClosingCostCol.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, Integer>>()
        {
            @Override
            public void handle(CellEditEvent<RealProperty, Integer> t)
            {
                Object ob = t.getNewValue();
                Integer newValue;
                if (ob instanceof Long)
                {
                    newValue = Integer.valueOf(((Long) ob).intValue());
                } else
                {
                    newValue = t.getNewValue().intValue();
                }
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setLoanClosingCost(newValue);
            }
        });

        TableColumn<RealProperty, Date> purchaseDateCol = new TableColumn<>("PurchaseDate");
        purchaseDateCol.setMinWidth(50);
        purchaseDateCol.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        purchaseDateCol.setCellFactory(dateCellFactory);
        purchaseDateCol.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, Date>>()
        {
            @Override
            public void handle(CellEditEvent<RealProperty, Date> t)
            {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setPurchaseDate(t.getNewValue());
            }
        });

        table.setItems(data);
        table.getColumns().addAll(propNameCol, landValueCol, costCol, renovationCol, loanClosingCostCol, purchaseDateCol);

        final TextField addFirstName = new TextField();
        addFirstName.setPromptText("First Name");
        addFirstName.setMaxWidth(propNameCol.getPrefWidth());
        final TextField addLastName = new TextField();
        addLastName.setMaxWidth(landValueCol.getPrefWidth());
        addLastName.setPromptText("Last Name");
        final TextField addEmail = new TextField();
        addEmail.setMaxWidth(costCol.getPrefWidth());
        addEmail.setPromptText("Email");

        final Button addButton = new Button("Add");
        addButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                // data.add(new Person(addFirstName.getText(),
                // addLastName.getText(), addEmail.getText()));
                addFirstName.clear();
                addLastName.clear();
                addEmail.clear();
            }
        });

        hb.getChildren().addAll(addFirstName, addLastName, addEmail, addButton);
        hb.setSpacing(3);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table, hb);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        return scene;

    }

    @Override
    public void start(Stage stage)
    {
        Scene scene = createScene(rpL);
        stage.setTitle("Table View Sample");
        stage.setWidth(950);
        stage.setHeight(550);

        stage.setScene(scene);
        stage.show();
    }
}
