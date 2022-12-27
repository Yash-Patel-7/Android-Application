package adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.android10.PhotosLibrary;
import com.example.android10.R;

import java.util.ArrayList;

import activity.DisplayActivity;
import activity.PhotosListActivity;
import model.Album;
import model.Model;
import model.Photo;

public class PhotosListAdapter extends ArrayAdapter<Photo> {
    public Album album;
    public GridView gridview;

    public PhotosListAdapter(@NonNull Context context, ArrayList<Photo> photos) {
        super(context, 0, photos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View singlePhoto = convertView;

        if (singlePhoto == null) {
            singlePhoto = LayoutInflater.from(getContext()).inflate(R.layout.single_photo, parent, false);
        }

        Photo photo = getItem(position);
        ImageView imageView = singlePhoto.findViewById(R.id.imageView);

        imageView.setImageURI(Uri.parse(photo.path));

        singlePhoto.setOnClickListener(view -> displayEditPhoto(view.getContext(), photo));

        return singlePhoto;
    }

    public void displayEditPhoto(Context context, Photo selectedPhoto) {
        if (PhotosListActivity.deleteMode) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            builder.setTitle("Are your sure you want to delete this photo?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                try {
                    album.removePhoto(selectedPhoto.path);
                    Model.persist();
                    updateActivities(context);
                } catch (Exception e) {
                    PhotosLibrary.errorAlert(e, context);
                }
            });
            builder.setNegativeButton("No", (dialog, which) -> {dialog.cancel();dialog.dismiss();});
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            Model.initNextScene(true);
            Model.dataTransfer.add(album);
            Model.dataTransfer.add(selectedPhoto);
            Intent intent = new Intent(context, DisplayActivity.class);
            context.startActivity(intent);
        }
    }

    public void updateActivities(Context c) {
        PhotosListAdapter adapter = new PhotosListAdapter(c, album.photos);
        adapter.album = album;
        adapter.gridview = gridview;
        gridview.setAdapter(adapter);
    }
}
