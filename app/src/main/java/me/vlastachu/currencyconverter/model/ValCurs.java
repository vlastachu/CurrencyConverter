package me.vlastachu.currencyconverter.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Root
public class ValCurs implements Parcelable {
    @ElementList(entry = "Valute", inline = true)
    private List<Valute> valutes;

    @Attribute(name = "Date")
    private Date date;

    @Attribute
    private String name;

    public ValCurs() {
    }

    public ValCurs(List<Valute> valutes, Date date, String name) {
        this.valutes = valutes;
        this.date = date;
        this.name = name;
    }

    public List<Valute> getValutes() {
        return valutes;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.valutes);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.name);
    }

    protected ValCurs(Parcel in) {
        this.valutes = in.createTypedArrayList(Valute.CREATOR);
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.name = in.readString();
    }

    public static final Parcelable.Creator<ValCurs> CREATOR = new Parcelable.Creator<ValCurs>() {
        @Override
        public ValCurs createFromParcel(Parcel source) {
            return new ValCurs(source);
        }

        @Override
        public ValCurs[] newArray(int size) {
            return new ValCurs[size];
        }
    };

    public Valute findValuteById(String id) {
        for (Valute valute: valutes) {
            if (valute.getId().equals(id))
                return valute;
        }
        return null;
    }

    public Valute findValuteByCharCode(String charCode) {
        for (Valute valute: valutes) {
            if (valute.getCharCode().equals(charCode))
                return valute;
        }
        return null;
    }

    public Boolean isTooOld() {
        return TimeUnit.DAYS.convert(new Date().getTime() - date.getTime(), TimeUnit.MILLISECONDS) > 0;
    }
}
