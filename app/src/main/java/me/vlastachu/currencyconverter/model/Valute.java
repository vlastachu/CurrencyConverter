package me.vlastachu.currencyconverter.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public final class Valute implements Parcelable {
    @Attribute(name = "ID")
    private String id;

    @Element(name = "NumCode")
    private int numCode;

    @Element(name = "CharCode")
    private String charCode;

    @Element(name = "Nominal")
    private int nominal;

    @Element(name = "Name")
    private String name;

    @Element(name = "Value")
    private Float value;

    public Valute() {
    }


    public Valute(String id, int numCode, String charCode, int nominal, String name, Float value) {
        this.id = id;
        this.numCode = numCode;
        this.charCode = charCode;
        this.nominal = nominal;
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public int getNumCode() {
        return numCode;
    }

    public String getCharCode() {
        return charCode;
    }

    public int getNominal() {
        return nominal;
    }

    public String getName() {
        return name;
    }

    public float getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.numCode);
        dest.writeString(this.charCode);
        dest.writeInt(this.nominal);
        dest.writeString(this.name);
        dest.writeValue(this.value);
    }

    protected Valute(Parcel in) {
        this.id = in.readString();
        this.numCode = in.readInt();
        this.charCode = in.readString();
        this.nominal = in.readInt();
        this.name = in.readString();
        this.value = (Float) in.readValue(Float.class.getClassLoader());
    }

    public static final Parcelable.Creator<Valute> CREATOR = new Parcelable.Creator<Valute>() {
        @Override
        public Valute createFromParcel(Parcel source) {
            return new Valute(source);
        }

        @Override
        public Valute[] newArray(int size) {
            return new Valute[size];
        }
    };
}
