import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import data.RealProperty;

public class TableViewSample extends Application {

	private TableView<RealProperty> table = new TableView<RealProperty>();
	private static ObservableList<RealProperty> data;
	final HBox hb = new HBox();
	private static List<RealProperty> rpL;

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("requires properties.csv as argument");
			System.exit(-1);
		}
		try {
			rpL = RealProperties.parsePropFile(args[0]);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data = FXCollections.observableArrayList(rpL);
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(new Group());
		stage.setTitle("Table View Sample");
		stage.setWidth(950);
		stage.setHeight(550);

		final Label label = new Label("Address Book");
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

		TableColumn propNameCol = new TableColumn("Property Name");
		propNameCol.setMinWidth(150);
		propNameCol
				.setCellValueFactory(new PropertyValueFactory<RealProperty, String>(
						"propertyName"));
		propNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		propNameCol
				.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, String>>() {
					@Override
					public void handle(CellEditEvent<RealProperty, String> t) {
						((RealProperty) t.getTableView().getItems()
								.get(t.getTablePosition().getRow()))
								.setPropertyName(t.getNewValue());
					}
				});

		TableColumn landValueCol = new TableColumn("Land Value");
		landValueCol.setMinWidth(75);
		landValueCol
				.setCellValueFactory(new PropertyValueFactory<RealProperty, String>(
						"landValue"));
		landValueCol.setCellFactory(numericFactory);
		landValueCol
				.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, Integer>>() {
					@Override
					public void handle(CellEditEvent<RealProperty, Integer> t) {
						((RealProperty) t.getTableView().getItems()
								.get(t.getTablePosition().getRow()))
								.setLandValue(t.getNewValue().intValue());
					}
				});

		TableColumn costCol = new TableColumn("Cost");
		costCol.setMinWidth(75);
		costCol.setCellValueFactory(new PropertyValueFactory<RealProperty, String>(
				"cost"));
		costCol.setCellFactory(numericFactory);
		costCol.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, Integer>>() {
			@Override
			public void handle(CellEditEvent<RealProperty, Integer> t) {
				((RealProperty) t.getTableView().getItems()
						.get(t.getTablePosition().getRow())).setCost(t
						.getNewValue().intValue());
			}
		});

		TableColumn renovationCol = new TableColumn("Renovation");
		renovationCol.setMinWidth(75);
		renovationCol
				.setCellValueFactory(new PropertyValueFactory<RealProperty, String>(
						"renovation"));
		renovationCol.setCellFactory(numericFactory);
		renovationCol
				.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, Integer>>() {
					@Override
					public void handle(CellEditEvent<RealProperty, Integer> t) {
						((RealProperty) t.getTableView().getItems()
								.get(t.getTablePosition().getRow()))
								.setRenovation(t.getNewValue().intValue());
					}
				});

		TableColumn loanClosingCostCol = new TableColumn("Loan Closing");
		loanClosingCostCol.setMinWidth(75);
		loanClosingCostCol
				.setCellValueFactory(new PropertyValueFactory<RealProperty, String>(
						"loanClosingCost"));
		loanClosingCostCol.setCellFactory(numericFactory);
		loanClosingCostCol
				.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, Integer>>() {
					@Override
					public void handle(CellEditEvent<RealProperty, Integer> t) {
						((RealProperty) t.getTableView().getItems()
								.get(t.getTablePosition().getRow()))
								.setLoanClosingCost(t.getNewValue().intValue());
					}
				});

		TableColumn purchaseDateCol = new TableColumn("PurchaseDate");
		purchaseDateCol.setMinWidth(50);
		purchaseDateCol
				.setCellValueFactory(new PropertyValueFactory<RealProperty, Date>(
						"purchaseDate"));
		purchaseDateCol.setCellFactory(dateCellFactory);
		purchaseDateCol
				.setOnEditCommit(new EventHandler<CellEditEvent<RealProperty, Date>>() {
					@Override
					public void handle(CellEditEvent<RealProperty, Date> t) {
						((RealProperty) t.getTableView().getItems()
								.get(t.getTablePosition().getRow()))
								.setPurchaseDate(t.getNewValue());
					}
				});

		table.setItems(data);
		table.getColumns().addAll(propNameCol, landValueCol, costCol,
				renovationCol, loanClosingCostCol, purchaseDateCol);

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
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
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

		stage.setScene(scene);
		stage.show();
	}
}