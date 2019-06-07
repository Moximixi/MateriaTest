package com.example.administrator.materiatest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LabelLab {
    private static final String TAG = "LabelLab";
    public static final String PreferenceName = "labels";

    private static LabelLab sLabelLab;
    private SharedPreferences LabelPreference;
    private Context mContext;


    public static LabelLab get(Context context) {
        if (sLabelLab == null) {
            sLabelLab = new LabelLab(context);
        }
        return sLabelLab;
    }

    public LabelLab(Context context) {
        mContext = context.getApplicationContext();
        LabelPreference = mContext.getSharedPreferences(PreferenceName, 0);
    }

    private List<Label> loadLabel() {
        List<Label> labels = new ArrayList<>();
        Type type = new TypeToken<List<Label>>() {
        }.getType();
        Gson gson = new Gson();
        String toLoad = LabelPreference.getString(PreferenceName, null);
        if (toLoad != null) {
            labels = gson.fromJson(toLoad, type);
            Log.i(TAG, "JSON to Load = " + toLoad);
        }
        return labels;

    }

    public final List<Label> getLabels() {
        return loadLabel();
    }

    public final Label getLabel(int id) {
        List<Label> labels = loadLabel();
        for (Label label : labels) {
            if (label.getId()==id) {
                return label;
            }
        }
        return null;
    }


    public void addLabel(Label label) {
        List<Label> sLabel = loadLabel();
        sLabel.add(label);
        saveLabel(sLabel);
    }

    private void saveLabel(List<Label> sLabel) {
        Gson gson = new Gson();
        String toSave = gson.toJson(sLabel);
        Log.i(TAG, "JSON to Save = " + toSave);
        LabelPreference.edit()
                .putString(PreferenceName, toSave)
                .apply();
    }

    public void deleteLabel(int id, boolean removeFromBooks) {
        List<Label> sLabel = loadLabel();
        if (removeFromBooks) {
            List<Book> books = BookLab.get(mContext).getBooks(null, id);
            for (Book book : books) {
                book.removeLabel(id);
                BookLab.get(mContext).updateBook(book);
            }
        }
        for (Label label : sLabel) {
            if (label.getId()==id) {
                sLabel.remove(label);
                break;
            }
        }
        saveLabel(sLabel);
    }

    public void renameLabel(int id, String newName) {
        List<Label> labels = loadLabel();
        for (Label label : labels) {
            if (label.getId()==id) {
                label.setTitle(newName);
                break;
            }
        }
        saveLabel(labels);

    }

    public void clearLabel(){
        List<Label> sLabel = loadLabel();
        for (Label label : sLabel) {
                sLabel.remove(label);
        }
    }

}
