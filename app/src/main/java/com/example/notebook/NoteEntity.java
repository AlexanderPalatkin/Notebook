package com.example.notebook;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class NoteEntity implements Parcelable {
    private final String id;
    private final String title;
    private final String description;
    private Date date;

    NoteEntity(String id,
               String title,
               String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = new Date();
    }

    protected NoteEntity(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public static String generateNewId() {
        return UUID.randomUUID().toString();
    }


}
