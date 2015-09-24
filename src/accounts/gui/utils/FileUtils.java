package accounts.gui.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import accounts.data.RealProperty;
import accounts.data.BankAccount;

public class FileUtils
{

	
	  public static List<BankAccount> parseAccountFile(String filename) throws IOException, ParseException
	    {
	        final FileReader fr = new FileReader(filename);
	        final BufferedReader br = new BufferedReader(fr);
	        List<BankAccount> aL = new ArrayList<BankAccount>();
	        try
	        {
	            for (String line; (line = br.readLine()) != null;)
	            {
	                line = line.toLowerCase().trim();
	                if (line.isEmpty())
	                {
	                    continue;
	                }
	                line = line.toLowerCase().trim();
	                if (line.isEmpty() || line.startsWith("#"))
	                {
	                    continue;
	                }

	                String[] fields = line.split(",");
	                if (fields.length != 2)
	                {
	                    throw new IOException("Invalid account line=" + line);
	                }
	                BankAccount ba = new BankAccount();
	                ba.setName(fields[0]);
	                ba.setBankName(fields[1]);
	                aL.add(ba);

	            }
	        } finally
	        {
	            if (br != null)
	            {
	                try
	                {
	                    br.close();
	                } catch (final IOException e)
	                {
	                    // Ignore
	                }
	            }
	            if (fr != null)
	            {
	                try
	                {
	                    fr.close();
	                } catch (final IOException e)
	                {
	                    // Ignore
	                }
	            }
	        }
	        return aL;
	    }

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
	
}