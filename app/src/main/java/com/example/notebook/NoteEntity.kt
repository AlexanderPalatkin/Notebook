package com.example.notebook

import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import java.util.*

class NoteEntity : Parcelable {
    val id: String?
    var title: String?
        private set
    var description: String?
        private set
    var date: Date? = null
        private set

    internal constructor(
        id: String?,
        title: String?,
        description: String?,
        date: Date?
    ) {
        this.id = id
        this.title = title
        this.description = description
        this.date = date
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readString()
        title = `in`.readString()
        description = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(title)
        dest.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun update(newNote: NoteEntity) {
        description = newNote.description
        title = newNote.title
    }

    companion object {
        @JvmField
        val CREATOR: Creator<NoteEntity?> = object : Creator<NoteEntity?> {
            override fun createFromParcel(`in`: Parcel): NoteEntity? {
                return NoteEntity(`in`)
            }

            override fun newArray(size: Int): Array<NoteEntity?> {
                return arrayOfNulls(size)
            }
        }

        @JvmStatic
        fun generateNewId(): String {
            return UUID.randomUUID().toString()
        }

        @JvmStatic
        val currentDate: Date
            get() = Calendar.getInstance().time
    }
}