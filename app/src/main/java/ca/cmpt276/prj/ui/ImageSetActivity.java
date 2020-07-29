package ca.cmpt276.prj.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.ImageSet;

public class ImageSetActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    List<String> images;
    TextView image_number;

    private static final int MY_READ_PERMISSION_CODE=101;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_image_set,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_photos:
                Intent intent = new Intent(ImageSetActivity.this,PhotoGalleryActivity.class);
                startActivity(intent);
                return true;
            case R.id.delete_image:
                //Need to add it.
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_set);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //image_number = findViewById(R.id.image_number);
        recyclerView = findViewById(R.id.recyclerview_gallery_images);

        //check for permission
        if(ContextCompat.checkSelfPermission(ImageSetActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ImageSetActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);

        }else{
            loadImages();
        }

    }

    private void loadImages(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        images = ImageSet.listOfImages(this);
        galleryAdapter = new GalleryAdapter(this, images, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String path) {
                //Do something with photo
                Toast.makeText(ImageSetActivity.this,""+path,Toast.LENGTH_SHORT).show();

            }
        });
        recyclerView.setAdapter(galleryAdapter);
        //image_number.setText("Image Set("+images.size()+")");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_READ_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Read external storage permission grand", Toast.LENGTH_SHORT).show();
                loadImages();
            }else{
                Toast.makeText(this, "Read external storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}