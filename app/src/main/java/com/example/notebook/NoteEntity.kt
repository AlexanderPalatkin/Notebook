package com.example.notebook;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class NoteEntity implements Parcelable {
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
    private final String id;
    private String title;
    private String description;
    private Date date;

    NoteEntity(String id,
               String title,
               String description,
               Date date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    protected NoteEntity(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
    }

    public static String generateNewId() {
        return UUID.randomUUID().toString();
    }

    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
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

    public void update(NoteEntity newNote) {
        description = newNote.description;
        title = newNote.title;
    }
}
