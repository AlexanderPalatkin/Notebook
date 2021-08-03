package com.example.notebook;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class NoteEntity implements Parcelable {
    private String name;
    private String description;
    private Date date;

    public NoteEntity(String name, String description) {
        this.name = name;
        this.description = description;
        this.date = new Date();
    }

    protected NoteEntity(Parcel in) {
        name = in.readString();
        description = in.readString();
    }

    public static final Creator<NoteEntity> CREATOR = new Creator<NoteEntity>() {
        @Override
        public NoteEntity createFromParcel(Parcel in) {
            return new NoteEntity(in);
        }

        @Override
        public NoteEntity[] newArray(int size) {
            return new NoteEntity[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
    }
}
