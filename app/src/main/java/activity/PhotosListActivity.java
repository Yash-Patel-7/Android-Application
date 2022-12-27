package activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android10.PhotosLibrary;
import com.example.android10.R;

import java.util.Objects;

import adapter.PhotosListAdapter;
import model.Album;
import model.Model;

public class PhotosListActivity extends AppCompatActivity {

    public static GridView albumPhotos;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch displayDeleteSwitch;
    public static Boolean deleteMode;
    public static Album currentAlbum;
    public static PhotosListAdapter adapter;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photoslist);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        currentAlbum = (Album) Model.dataTransfer.get(0);
        albumPhotos = findViewById(R.id.albumPhotos);
        adapter = new PhotosListAdapter(this, currentAlbum.photos);
        albumPhotos.setAdapter(adapter);
        adapter.album = currentAlbum;
        context = this;
        adapter.gridview = albumPhotos;
        displayDeleteSwitch = findViewById(R.id.displayDeleteSwitch);
        deleteMode = displayDeleteSwitch.isChecked();
        displayDeleteSwitch.setOnCheckedChangeListener((view, checked) -> {
            deleteMode = checked;
            displayDeleteSwitch.setText(checked ? "Delete Mode" : "Display Mode");
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photoslistmenu, menu);
        this.setTitle(currentAlbum.name);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.upload) {
            upload(this);
        } else if (id == android.R.id.home) {
            Model.initPreviousScene();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void upload(Context context) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                currentAlbum.addPhoto(data.getData().toString());
                Model.persist();
                updateActivity();
            } catch (Exception e) {
                PhotosLibrary.errorAlert(e,this);
            }
        }
    }

    public static void updateActivity() {
        adapter = new PhotosListAdapter(context, currentAlbum.photos);
        adapter.album = currentAlbum;
        adapter.gridview = albumPhotos;
        albumPhotos.setAdapter(adapter);
    }
}

