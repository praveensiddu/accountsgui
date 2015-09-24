package accounts.data;

import java.util.Date;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BankAccount {

	private StringProperty name = new SimpleStringProperty("NONE");
	private StringProperty bankName = new SimpleStringProperty("NONE");

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}


	public String getBankName() {
		return bankName.get();
	}

	public void setBankName(String bankName) {
		this.bankName.set(bankName);
	}


	@Override
	public String toString() {
		return "Name=" + name + ", bank=" + bankName ;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
