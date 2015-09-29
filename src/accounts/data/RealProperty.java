package accounts.data;

import java.util.Date;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RealProperty
{

    private StringProperty             propertyName    = new SimpleStringProperty("NONE");
    private IntegerProperty            landValue       = new SimpleIntegerProperty(-1);
    private IntegerProperty            cost            = new SimpleIntegerProperty(-1);
    private IntegerProperty            renovation      = new SimpleIntegerProperty(-1);
    private IntegerProperty            loanClosingCost = new SimpleIntegerProperty(-1);
    private IntegerProperty            ownerCount      = new SimpleIntegerProperty(1);
    private SimpleObjectProperty<Date> purchaseDate    = new SimpleObjectProperty();

    public String getPropertyName()
    {
        return propertyName.get();
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName.set(propertyName);
    }

    public Integer getLandValue()
    {
        return landValue.get();
    }

    public void setLandValue(Integer landValue)
    {
        this.landValue.set(landValue);
    }

    public Integer getCost()
    {
        return cost.get();
    }

    public void setCost(Integer cost)
    {
        this.cost.set(cost);
        ;
    }

    public Integer getRenovation()
    {
        return renovation.get();
    }

    public void setRenovation(Integer renovation)
    {
        this.renovation.set(renovation);
    }

    public Integer getLoanClosingCost()
    {
        return loanClosingCost.get();
    }

    public void setLoanClosingCost(Integer loanClosingCost)
    {
        this.loanClosingCost.set(loanClosingCost);
    }

    public Integer getOwnerCount()
    {
        return ownerCount.get();
    }

    public void setOwnerCount(Integer ownerCount)
    {
        this.ownerCount.set(ownerCount);
    }

    public Date getPurchaseDate()
    {
        return purchaseDate.get();
    }

    public void setPurchaseDate(Date purchaseDate)
    {
        this.purchaseDate.set(purchaseDate);
    }

    @Override
    public String toString()
    {
        return "Prop=" + propertyName + ", land=" + landValue + ", cost=" + cost + ", renovation=" + renovation + ", pDate="
                + purchaseDate;
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
