package accounts.gui;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import accounts.gui.utils.AbstractEditableTableCell;

public class DateEditableTableCell<S extends Object, T extends Date> extends
		AbstractEditableTableCell<S, T> {
	private final DateFormat format;

	public DateEditableTableCell() {

		this(new SimpleDateFormat("MM/dd/yyyy"));
	}

	public DateEditableTableCell(DateFormat format) {
		this.format = format;
	}

	@Override
	protected String getString() {
		return getItem() == null ? "" : format.format(getItem());
	}

	/**
	 * Parses the value of the text field and if matches the set format commits
	 * the edit otherwise it returns the cell to it's previous value.
	 */
	@Override
	protected void commitHelper(boolean losingFocus) {
		if (textField == null) {
			return;
		}

		try {
			String input = textField.getText();

			if (input == null || input.length() == 0) {
				return;
			}

			Date purchaseDate = format.parse(input);

			commitEdit((T) purchaseDate);
		} catch (ParseException ex) {
			// Most of the time we don't mind if there is a parse exception as
			// it
			// indicates duff user data but in the case where we are losing
			// focus
			// it means the user has clicked away with bad data in the cell. In
			// that
			// situation we want to just cancel the editing and show them the
			// old
			// value.
			if (losingFocus) {
				cancelEdit();
			}
		}
	}
}