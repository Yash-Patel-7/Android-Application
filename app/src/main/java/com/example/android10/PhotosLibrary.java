package com.example.android10;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import java.io.File;

import model.Model;

public final class PhotosLibrary extends Application {
    private static File filesDirectory;

    @Override
    public void onCreate() {
        super.onCreate();
        filesDirectory = this.getFilesDir();
        Model.init();
    }

    public static File getFilesDirectory() {
        return filesDirectory;
    }

    public static void errorAlert(Exception e, Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(e.getMessage());
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            dialog.dismiss();
        });
        builder.show();
    }
}

