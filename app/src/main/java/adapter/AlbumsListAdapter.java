package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android10.PhotosLibrary;
import com.example.android10.R;

import java.util.ArrayList;

import activity.AlbumsListActivity;
import activity.PhotosListActivity;
import model.Album;
import model.Model;


// Create the basic adapter extending from RecyclerView.adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class AlbumsListAdapter extends RecyclerView.Adapter<AlbumsListAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView albumNameTextView;
        public Button openAlbumButton;
        public ImageButton renameOrDeleteAlbumButton;

        public ViewHolder(View itemView) {

            super(itemView);

            albumNameTextView = itemView.findViewById(R.id.albumNameTextView);
            openAlbumButton = itemView.findViewById(R.id.openAlbumButton);
            openAlbumButton.setOnClickListener(view -> open(view, albumNameTextView.getText().toString()));
            renameOrDeleteAlbumButton = itemView.findViewById(R.id.renameOrDeleteAlbumButton);
            renameOrDeleteAlbumButton.setOnClickListener(view -> showPopup(view, albumNameTextView.getText().toString()));

        }
    }

    public void open(View view, String albumName) {
        Model.initNextScene(true);
        Model.dataTransfer.add(Model.currentUser.albums.get(Model.currentUser.albums.indexOf(new Album(albumName))));
        Intent intent = new Intent(view.getContext(), PhotosListActivity.class);
        view.getContext().startActivity(intent);
    }

    public void showPopup(View view, String albumName) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.album_cardmenu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.rename) {
                SearchView searchView;
                try {
                    MenuItem menuItem = AlbumsListActivity.optionsMenu.findItem(R.id.action_create);
                    searchView = (SearchView) menuItem.getActionView();
                    AlbumsListActivity.optionsMenu.findItem(R.id.search_button).setVisible(false);
                    searchView.setQueryHint("Rename \"" + albumName + "\" to...");
                    AlbumsListActivity.isRename = true;
                    AlbumsListActivity.oldAlbumName = albumName;
                    menuItem.expandActionView();
                } catch (Exception e) {
                    PhotosLibrary.errorAlert(e, view.getContext());
                }
                return true;
            } else if (item.getItemId() == R.id.delete) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setCancelable(true);
                builder.setTitle("Are your sure you want to delete this album?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        Model.currentUser.deleteAlbum(albumName);
                        Model.persist();
                        AlbumsListActivity.refresh(view.getContext());
                    } catch (Exception e) {
                        PhotosLibrary.errorAlert(e, view.getContext());
                    }
                });
                builder.setNegativeButton("No", (dialog, which) -> {dialog.cancel();dialog.dismiss();});
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
            return false;
        });
    }

    private final ArrayList<Album> userAlbums;

    public AlbumsListAdapter(ArrayList<Album> albums) {
        userAlbums = albums;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public AlbumsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.album_card, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(AlbumsListAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Album album = userAlbums.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.albumNameTextView;
        textView.setText(album.name);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return userAlbums.size();
    }
}


