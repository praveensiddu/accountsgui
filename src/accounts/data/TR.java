package accounts.data;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import accounts.config.BankStatementFormat;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TR
{
    private SimpleObjectProperty<Date> date        = new SimpleObjectProperty<Date>();
    private StringProperty             description = new SimpleStringProperty("");
    private StringProperty             comment     = new SimpleStringProperty("");
    private FloatProperty              debit       = new SimpleFloatProperty(-1);
    private BooleanProperty            locked      = new SimpleBooleanProperty(false);
    private BooleanProperty            adjusted    = new SimpleBooleanProperty(false);
    private StringProperty             trType      = new SimpleStringProperty("");
    private StringProperty             taxCategory = new SimpleStringProperty("");
    private StringProperty             property    = new SimpleStringProperty("");

    public void copyNonPrimaryFields(TR tr)
    {
        setTrType(tr.getTrType());
        setTaxCategory(tr.getTaxCategory());
        setProperty(tr.getProperty());
        setComment(tr.getComment());
        setLocked(tr.isLocked());
    }

    public String getDescription()
    {
        return description.get();
    }

    public void setDescription(String description)
    {
        this.description.set(description);
    }

    public String getTrType()
    {
        return trType.get();
    }

    public void setTrType(final String trType)
    {
        if (trType == null)
            return;
        this.trType.set(trType.trim().toLowerCase());
    }

    public String getTaxCategory()
    {
        return taxCategory.get();
    }

    public void setTaxCategory(final String taxCategory)
    {
        if (taxCategory == null)
            return;
        this.taxCategory.set(taxCategory.trim().toLowerCase());
    }

    public Date getDate()
    {
        return date.get();
    }

    public void setDate(final Date date)
    {
        System.out.println(date);
        this.date.set(date);
    }

    public float getDebit()
    {
        return debit.floatValue();
    }

    public void setDebit(final float debit)
    {
        this.debit.set(debit);
    }

    public String getComment()
    {
        return comment.get();
    }

    public void setComment(String comment)
    {
        this.comment.set(comment);
    }

    public boolean isLocked()
    {
        return locked.get();
    }

    public void setLocked(boolean locked)
    {
        this.locked.set(locked);
    }

    public String getProperty()
    {
        return property.get();
    }

    public void setProperty(String property)
    {
        if (property == null)
            return;
        this.property.set(property);
    }

    public boolean isAdjusted()
    {
        return adjusted.get();
    }

    public void setAdjusted(boolean adjusted)
    {
        this.adjusted.set(adjusted);
    }

    private static String[] approxCsvCorrection(final String[] fields)
    {
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < fields.length;)
        {
            if (fields[i].startsWith("\""))
            {
                if (fields[i].endsWith("\""))
                {
                    list.add(fields[i]);
                    i++;
                } else
                {
                    String field = fields[i];
                    i++;
                    for (int j = i; j < fields.length; j++)
                    {
                        if (fields[j].endsWith("\""))
                        {
                            field += "," + fields[j];
                            i++;
                            break;

                        } else
                        {
                            field += "," + fields[j];
                            i++;
                        }
                    }
                    list.add(field);
                }
            } else
            {
                list.add(fields[i]);
                i++;
            }
        }
        return list.toArray(new String[0]);
    }

    public static String trimQuote(String floatStr)
    {
        if (floatStr.startsWith("\""))
        {
            floatStr = floatStr.substring(1);
        }
        if (floatStr.endsWith("\""))
        {
            floatStr = floatStr.substring(0, floatStr.length() - 1);
        }
        return floatStr;
    }

    private static float getFloatVaue(String floatStr)
    {
        boolean negative = false;

        if (floatStr.startsWith("("))
        {
            floatStr = floatStr.substring(1);
            negative = true;
        }
        if (floatStr.endsWith(")"))
        {
            floatStr = floatStr.substring(0, floatStr.length() - 1);
        }
        if (floatStr.startsWith("$"))
        {
            floatStr = floatStr.substring(1);
        }
        if (negative)
        {
            floatStr = "-" + floatStr;
        }
        return new Float(floatStr).floatValue();
    }

    public void importLine(String line) throws IOException, ParseException
    {

        line = line.toLowerCase().trim();
        if (line.isEmpty())
        {
            throw new IOException("Empty transaction");
        }
        String[] fields = line.split(",", -1);
        fields = approxCsvCorrection(fields);
        if (fields.length != 8)
        {
            throw new IOException("Invalid transaction line" + line + "\nExpected 8 fields. Found=" + fields.length);
        }
        // #DATE,DESCRIPTION,DEBIT,COMMENT,ISLOCKED,TRTYPE,TAXCATEGORY,PROPERTY
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
        System.out.println(fields[0]);
        Date date = format.parse(fields[0]);

        setDate(date);
        setDescription(fields[1]);
        Float value = getFloatVaue(fields[2].trim());
        setDebit(value);
        String tempStr = fields[3];
        if (tempStr != null)
            tempStr = tempStr.toLowerCase().trim();
        if (!"null".equals(tempStr))
        {
            setComment(tempStr);
        }
        tempStr = fields[4];
        if (tempStr != null)
            tempStr = tempStr.toLowerCase().trim();
        if ("true".equals(tempStr))
        {
            setLocked(true);
        }

        tempStr = fields[5];
        if (tempStr != null)
            tempStr = tempStr.toLowerCase().trim();
        if (!"null".equals(tempStr))
        {
            setTrType(tempStr);
        }

        tempStr = fields[6];
        if (tempStr != null)
            tempStr = tempStr.toLowerCase().trim();
        if (!"null".equals(tempStr))
        {
            setTaxCategory(tempStr);
        }

        tempStr = fields[7];
        if (tempStr != null)
            tempStr = tempStr.toLowerCase().trim();
        if (!"null".equals(tempStr))
        {
            setProperty(tempStr);
        }
    }

    public void init(String line, final BankStatementFormat bc) throws IOException, ParseException
    {
        line = line.toLowerCase().trim();
        if (line.isEmpty())
        {
            throw new IOException("Empty transaction");
        }
        String[] fields = line.split(",", -1);
        fields = approxCsvCorrection(fields);

        if (fields.length <= bc.getDateIndex())
        {
            throw new IOException("Invalid transaction line" + line);
        }
        for (int i = 0; i < fields.length; i++)
        {
            fields[i] = trimQuote(fields[i]);
        }

        DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
        Date date = format.parse(fields[bc.getDateIndex()]);

        setDate(date);

        if (fields.length > bc.getDescIndex())
        {
            setDescription(fields[bc.getDescIndex()]);
        }
        if (bc.getMemoIndex() > -1 && fields.length > bc.getMemoIndex())
        {
            final String memo = fields[bc.getMemoIndex()].trim();
            if (!memo.isEmpty())
            {
                if (getDescription() == null)
                {
                    setDescription(": memo:" + memo + ":");
                } else
                {
                    setDescription(getDescription() + ": memo:" + memo + ":");
                }

            }
        }
        if (bc.getCheckNoIndex() > -1 && fields.length > bc.getCheckNoIndex())
        {
            final String checkNoStr = fields[bc.getCheckNoIndex()].trim();
            if (!checkNoStr.isEmpty())
            {
                if (getDescription() == null)
                {
                    setDescription(": checkno:" + checkNoStr + ":");
                } else
                {
                    setDescription(getDescription() + ": checkno:" + checkNoStr + ":");
                }
            }
        }
        if (fields.length > bc.getDebitIndex() && !fields[bc.getDebitIndex()].trim().isEmpty())
        {
            Float value = getFloatVaue(fields[bc.getDebitIndex()].trim());

            if (bc.getCreditIndex() != -1)
            {
                // Credit is a separate column. that means debit should always
                // be negative
                if (value > 0)
                {
                    value = -value;
                }
            }

            setDebit(value);
        }
        if (bc.getCreditIndex() > -1 && fields.length > bc.getCreditIndex() && !fields[bc.getCreditIndex()].trim().isEmpty())
        {
            Float value = getFloatVaue(fields[bc.getCreditIndex()].trim());
            if (value < 0)
            {
                value = -value;
            }
            setDebit(value);
        }
        if (bc.getFeesIndex() > -1 && fields.length > bc.getFeesIndex() && !fields[bc.getFeesIndex()].trim().isEmpty())
        {
            Float value = getFloatVaue(fields[bc.getFeesIndex()].trim());
            if (value > 0)
            {
                value = -value;
            }
            setDebit(value);
        }
        if (getDescription() == null)
        {
            throw new IOException("Description is mandatory");
        }
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append(new SimpleDateFormat("MM/dd/yyyy").format(date));
        sb.append(", " + debit);
        sb.append(", " + trType);
        sb.append(", " + taxCategory);
        sb.append(", " + property);
        sb.append(", " + description);
        sb.append(", " + adjusted);

        return sb.toString();

    }

    public static void main(final String[] args)
    {
        // TODO Auto-generated method stub

    }
}
