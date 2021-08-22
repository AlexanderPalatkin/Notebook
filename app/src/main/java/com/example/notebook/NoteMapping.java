package com.example.notebook;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class NoteMapping {
    public static NoteEntity toNote(Map<String, Object> doc) {
        Timestamp timeStamp = (Timestamp) doc.get(Fields.DATE);

        NoteEntity answer = new NoteEntity(
                (String) doc.get(Fields.ID),
                (String) doc.get(Fields.TITLE),
                (String) doc.get(Fields.DESCRIPTION),
                timeStamp.toDate());
        return answer;
    }

    public static Map<String, Object> toDocument(NoteEntity note) {
        Map<String, Object> answer = new HashMap<>();
        answer.put(Fields.ID, note.getId());
        answer.put(Fields.TITLE, note.getTitle());
        answer.put(Fields.DESCRIPTION, note.getDescription());
        answer.put(Fields.DATE, note.getDate());
        return answer;
    }

    public static class Fields {
        public final static String ID = "id";
        public final static String DATE = "date";
        public final static String TITLE = "subject";
        public final static String DESCRIPTION = "description";
    }
}
