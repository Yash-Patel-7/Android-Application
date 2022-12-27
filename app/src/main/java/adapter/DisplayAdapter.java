package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android10.PhotosLibrary;
import com.example.android10.R;
import java.util.ArrayList;

import activity.AlbumsListActivity;
import activity.DisplayActivity;
import model.Model;
import model.Tag;

public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.ViewHolder> {

    public String tagType;
    public String tagValue;
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tagTypeTagValue;
        public Button deleteTag;

        public ViewHolder(View itemView) {

            super(itemView);

            tagTypeTagValue = itemView.findViewById(R.id.tagTypeTagValue);
            deleteTag = itemView.findViewById(R.id.deleteTagButton);
            deleteTag.setOnClickListener(view -> deleteTag(itemView.getContext()));
        }
    }
    public void deleteTag(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Are your sure you want to delete this tag?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            try {
                DisplayActivity.currentPhoto.removeTag(tagType, tagValue);
                Model.persist();
                DisplayActivity.updateTagsList(context);

            } catch (Exception e) {
                PhotosLibrary.errorAlert(e, context);
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> {dialog.cancel();dialog.dismiss();});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private final ArrayList<Tag> photoTags;

    public DisplayAdapter(ArrayList<Tag> tags) {
        photoTags = tags;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public DisplayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.tag_card, parent, false);

        // Return a new holder instance
        return new DisplayAdapter.ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(DisplayAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Tag tag = photoTags.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.tagTypeTagValue;
        textView.setText(String.format("%s: %s", tag.type, tag.value));
        tagType = tag.type;
        tagValue = tag.value;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return photoTags.size();
    }
}
