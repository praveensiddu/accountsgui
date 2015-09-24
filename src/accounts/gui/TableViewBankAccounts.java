package accounts.gui;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import accounts.data.BankAccount;
import accounts.gui.utils.EditableTableCell;
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

public class TableViewBankAccounts extends Application {

	private TableView<BankAccount> table = new TableView<BankAccount>();
	private ObservableList<BankAccount> data;
	final HBox hb = new HBox();
	private static List<BankAccount> acL;

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("requires bankaccounts.csv as argument");
			System.exit(-1);
		}
		try {
			acL = FileUtils.parseAccountFile(args[0]);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		launch(args);
	}
	

    private static void initAndShowGUI(JFXPanel jfxPanel) {


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	TableViewBankAccounts tvs = new TableViewBankAccounts();
                Scene scene = tvs.createScene(acL);
                jfxPanel.setScene(scene);
            }
        });
    }


    
	public Scene createScene(List<BankAccount> rpList)
	{
		data = FXCollections.observableArrayList(rpList);
		Scene scene = new Scene(new Group());

		final Label label = new Label("Summary:");
		label.setFont(new Font("Arial", 20));

		Callback<TableColumn, TableCell> numericFactory = new Callback<TableColumn, TableCell>() {
			@Override
			public TableCell call(TableColumn p) {
				return new NumericEditableTableCell();
			}
		};

		Callback<TableColumn, TableCell> dateCellFactory = new Callback<TableColumn, TableCell>() {
			@Override
			public TableCell call(TableColumn p) {
				return new DateEditableTableCell();
			}
		};

		Callback<TableColumn, TableCell> editableCellFactory = new Callback<TableColumn, TableCell>() {
			@Override
			public TableCell call(TableColumn p) {
				return new EditableTableCell();
			}
		};

		table.setEditable(true);
		table.setMaxWidth(950);
		table.setMinWidth(750);

		TableColumn nameCol = new TableColumn("Account Name");
		nameCol.setMinWidth(150);
		nameCol
				.setCellValueFactory(new PropertyValueFactory<BankAccount, String>(
						"name"));
		nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		nameCol
				.setOnEditCommit(new EventHandler<CellEditEvent<BankAccount, String>>() {
					@Override
					public void handle(CellEditEvent<BankAccount, String> t) {
						((BankAccount) t.getTableView().getItems()
								.get(t.getTablePosition().getRow()))
								.setName(t.getNewValue());
					}
				});


		TableColumn bankNameCol = new TableColumn("Bank Name");
		bankNameCol.setMinWidth(25);
		bankNameCol
				.setCellValueFactory(new PropertyValueFactory<BankAccount, String>(
						"bankName"));
		bankNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		bankNameCol
				.setOnEditCommit(new EventHandler<CellEditEvent<BankAccount, String>>() {
					@Override
					public void handle(CellEditEvent<BankAccount, String> t) {
						((BankAccount) t.getTableView().getItems()
								.get(t.getTablePosition().getRow()))
								.setBankName(t.getNewValue());
					}
				});


		table.setItems(data);
		table.getColumns().addAll(nameCol, bankNameCol);

		final TextField addFirstName = new TextField();
		addFirstName.setPromptText("First Name");
		addFirstName.setMaxWidth(nameCol.getPrefWidth());
		final TextField addLastName = new TextField();
		addLastName.setMaxWidth(bankNameCol.getPrefWidth());
		addLastName.setPromptText("Last Name");

		final Button addButton = new Button("Add");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				// data.add(new Person(addFirstName.getText(),
				// addLastName.getText(), addEmail.getText()));
				addFirstName.clear();
				addLastName.clear();
			}
		});

		hb.getChildren().addAll(addFirstName, addLastName, addButton);
		hb.setSpacing(3);

		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.getChildren().addAll(label, table, hb);

		((Group) scene.getRoot()).getChildren().addAll(vbox);
		return scene;

	}

	@Override
	public void start(Stage stage) {
		Scene scene = createScene(acL);
		stage.setTitle("Table View Sample");
		stage.setWidth(950);
		stage.setHeight(550);

		stage.setScene(scene);
		stage.show();
	}
}