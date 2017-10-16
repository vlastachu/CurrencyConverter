package me.vlastachu.currencyconverter.main;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

import me.vlastachu.currencyconverter.model.ValCurs;
import me.vlastachu.currencyconverter.model.Valute;

public class State implements Parcelable {
    // should be immutable, but it is too hard on java (lombok can help)
    private State() {}
    public static class DataLoaded extends State implements Parcelable {
        String fromValuteId;
        String toValuteId;
        ValCurs valCurs;
        float fromValue;
        float toValue;
        boolean isReversed;
        Boolean isUpdating;
        ValuteSelectorState valuteSelectorState;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.fromValuteId);
            dest.writeString(this.toValuteId);
            dest.writeParcelable(this.valCurs, flags);
            dest.writeFloat(this.fromValue);
            dest.writeFloat(this.toValue);
            dest.writeByte(this.isReversed ? (byte) 1 : (byte) 0);
            dest.writeValue(this.isUpdating);
            dest.writeInt(this.valuteSelectorState == null ? -1 : this.valuteSelectorState.ordinal());
        }

        public DataLoaded() {
        }

        protected DataLoaded(Parcel in) {
            super(in);
            this.fromValuteId = in.readString();
            this.toValuteId = in.readString();
            this.valCurs = in.readParcelable(ValCurs.class.getClassLoader());
            this.fromValue = in.readFloat();
            this.toValue = in.readFloat();
            this.isReversed = in.readByte() != 0;
            this.isUpdating = (Boolean) in.readValue(Boolean.class.getClassLoader());
            int tmpValuteSelectorState = in.readInt();
            this.valuteSelectorState = tmpValuteSelectorState == -1 ? null : ValuteSelectorState.values()[tmpValuteSelectorState];
        }

        public static final Creator<DataLoaded> CREATOR = new Creator<DataLoaded>() {
            @Override
            public DataLoaded createFromParcel(Parcel source) {
                return new DataLoaded(source);
            }

            @Override
            public DataLoaded[] newArray(int size) {
                return new DataLoaded[size];
            }
        };
    }
    public static class Empty extends State implements Parcelable {

        public Empty() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        protected Empty(Parcel in) {
        }

        public static final Parcelable.Creator<Empty> CREATOR = new Parcelable.Creator<Empty>() {
            @Override
            public Empty createFromParcel(Parcel source) {
                return new Empty(source);
            }

            @Override
            public Empty[] newArray(int size) {
                return new Empty[size];
            }
        };
    }
    public static class LoadFailed extends State implements Parcelable {

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
        }

        public LoadFailed() {
        }

        protected LoadFailed(Parcel in) {
            super(in);
        }

        public static final Creator<LoadFailed> CREATOR = new Creator<LoadFailed>() {
            @Override
            public LoadFailed createFromParcel(Parcel source) {
                return new LoadFailed(source);
            }

            @Override
            public LoadFailed[] newArray(int size) {
                return new LoadFailed[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    protected State(Parcel in) {
    }

    public static final Creator<State> CREATOR = new Creator<State>() {
        @Override
        public State createFromParcel(Parcel source) {
            return new State(source);
        }

        @Override
        public State[] newArray(int size) {
            return new State[size];
        }
    };
}
