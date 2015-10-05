package accounts.gui;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import accounts.data.TR;
import accounts.gui.utils.FileUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.converter.DefaultStringConverter;

public class TableViewStatement extends Application
{

    private TableView<TR>      table = new TableView<>();
    private ObservableList<TR> data;
    final HBox                 hb    = new HBox();
    private List<TR>           trL;

    public List<TR> getTrL()
    {
        return trL;
    }

    public void setTrL(List<TR> trL)
    {
        this.trL = trL;
    }

    public TableViewStatement()
    {
    }

    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("requires statement.csv as argument");
            System.exit(-1);
        }
        try
        {
            List<TR> trL = FileUtils.parseTransactions(args[0]);
            TableViewStatement tvs = new TableViewStatement();
            tvs.setTrL(trL);

        } catch (IOException | ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        launch(args);
    }

    @Override
    public void init() throws IOException, ParseException
    {
        Parameters p = getParameters();
        if (p.getRaw().isEmpty())
        {
            System.out.println("Requires statements.csv as argument");
            throw new IOException("Requires statements.csv as argument");
        }
        ;
        List<TR> trL = FileUtils.parseTransactions(p.getRaw().get(0));
        setTrL(trL);

    }

    public static void hackTooltipStartTiming(Tooltip tooltip)
    {
        try
        {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(150)));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Scene createScene()
    {
        data = FXCollections.observableArrayList(trL);
        Scene scene = new Scene(new Group());

        final Label label = new Label("Summary:");
        label.setFont(new Font("Arial", 20));

        Callback<TableColumn<TR, Integer>, TableCell<TR, Integer>> numericFactory = new Callback<TableColumn<TR, Integer>, TableCell<TR, Integer>>()
        {
            @Override
            public TableCell<TR, Integer> call(TableColumn<TR, Integer> p)
            {
                return new NumericEditableTableCell<>();
            }
        };

        Callback<TableColumn<TR, Float>, TableCell<TR, Float>> floatFactory = new Callback<TableColumn<TR, Float>, TableCell<TR, Float>>()
        {
            @Override
            public TableCell<TR, Float> call(TableColumn<TR, Float> p)
            {
                return new NumericEditableTableCell<>();
            }
        };

        Callback<TableColumn<TR, Date>, TableCell<TR, Date>> dateCellFactory = new Callback<TableColumn<TR, Date>, TableCell<TR, Date>>()
        {
            @Override
            public TableCell<TR, Date> call(TableColumn<TR, Date> p)
            {
                return new DateEditableTableCell<>();
            }
        };

        table.setEditable(true);
        table.setPrefWidth(1500);
        table.setMaxWidth(1500);
        table.setMinWidth(750);
        table.getSelectionModel().setCellSelectionEnabled(true);

        TableColumn<TR, Date> dateCol = new TableColumn<>("Date");
        dateCol.setMinWidth(50);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(dateCellFactory);
        dateCol.setOnEditCommit(new EventHandler<CellEditEvent<TR, Date>>()
        {
            @Override
            public void handle(CellEditEvent<TR, Date> t)
            {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setDate(t.getNewValue());
            }
        });

        TableColumn<TR, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setMinWidth(400);
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionCol.setOnEditCommit(new EventHandler<CellEditEvent<TR, String>>()
        {
            @Override
            public void handle(CellEditEvent<TR, String> t)
            {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setDescription(t.getNewValue());
            }
        });

        TableColumn<TR, String> commentCol = new TableColumn<>("Comment");
        commentCol.setMinWidth(150);
        commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
        commentCol.setCellFactory(TextFieldTableCell.forTableColumn());
        commentCol.setOnEditCommit(new EventHandler<CellEditEvent<TR, String>>()
        {
            @Override
            public void handle(CellEditEvent<TR, String> t)
            {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setComment(t.getNewValue());
            }
        });

        TableColumn<TR, Float> debitCol = new TableColumn<>("Debit");
        debitCol.setMinWidth(75);
        debitCol.setCellValueFactory(new PropertyValueFactory<>("debit"));
        debitCol.setCellFactory(floatFactory);
        debitCol.setOnEditCommit(new EventHandler<CellEditEvent<TR, Float>>()
        {
            @Override
            public void handle(CellEditEvent<TR, Float> t)
            {
                Object ob = t.getNewValue();
                Float newValue;
                if (ob instanceof Double)
                {
                    newValue = Float.valueOf(((Double) ob).floatValue());
                } else
                {
                    newValue = t.getNewValue().floatValue();
                }
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setDebit(newValue);
            }
        });

        TableColumn<TR, String> trTypeCol = new TableColumn<>();

        Label trTypeLabel = new Label("Transaction Type");
        Tooltip trTypeTT = new Tooltip(
                "rent/commissions/insurance/professionalfees/mortgageinterest/repairs/tax/utilities/depreciation/hoa/profit/bankfees");
        hackTooltipStartTiming(trTypeTT);
        trTypeLabel.setTooltip(trTypeTT);
        trTypeCol.setGraphic(trTypeLabel);

        trTypeCol.setMinWidth(150);
        trTypeCol.setCellValueFactory(new PropertyValueFactory<>("trType"));
        trTypeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        trTypeCol.setOnEditCommit(new EventHandler<CellEditEvent<TR, String>>()
        {
            @Override
            public void handle(CellEditEvent<TR, String> t)
            {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setTrType(t.getNewValue());
            }
        });

        TableColumn<TR, String> taxCategoryCol = new TableColumn<>("Tax Category");
        taxCategoryCol.setMinWidth(150);
        taxCategoryCol.setCellValueFactory(new PropertyValueFactory<>("taxCategory"));
        taxCategoryCol.setCellFactory(TextFieldTableCell.forTableColumn());
        taxCategoryCol.setOnEditCommit(new EventHandler<CellEditEvent<TR, String>>()
        {
            @Override
            public void handle(CellEditEvent<TR, String> t)
            {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setTaxCategory(t.getNewValue());
            }
        });

        ObservableList<String> propValues = FXCollections.observableArrayList("7963calvary", "9563batteryheights");

        TableColumn<TR, String> propertyCol = new TableColumn<>("Property");
        propertyCol.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), propValues));

        propertyCol.setMinWidth(150);
        propertyCol.setCellValueFactory(new PropertyValueFactory<>("property"));
        // propertyCol.setCellFactory(TextFieldTableCell.forTableColumn());
        propertyCol.setOnEditCommit(new EventHandler<CellEditEvent<TR, String>>()
        {
            @Override
            public void handle(CellEditEvent<TR, String> t)
            {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setProperty(t.getNewValue());
            }
        });

        table.setItems(data);
        table.getColumns().addAll(dateCol, descriptionCol, commentCol, debitCol, trTypeCol, taxCategoryCol, propertyCol);

        /*
        final TextField addFirstName = new TextField();
        addFirstName.setPromptText("First Name");
        addFirstName.setMaxWidth(propertyCol.getPrefWidth());
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
        */

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        return scene;

    }

    @Override
    public void start(Stage stage)
    {
        Scene scene = createScene();
        stage.setTitle("Table View Sample");
        stage.setWidth(1500);
        stage.setHeight(550);

        stage.setScene(scene);
        stage.show();
    }
}
