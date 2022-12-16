package com.noctl.ml_birds_classifier;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.noctl.ml_birds_classifier.ml.BirdsClassifier;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView myImage;
    Button upload, predict;
    TextView print, imgText;
    Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myImage = (ImageView) findViewById(R.id.myImage);
        upload = (Button) findViewById(R.id.btn_upload);
        predict = (Button) findViewById(R.id.btn_predict);
        print = (TextView) findViewById(R.id.mytext);
        imgText = (TextView) findViewById(R.id.imgText);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img = Bitmap.createScaledBitmap(img, 224,224,true);
                try {
                    BirdsClassifier model = BirdsClassifier.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorImage image = TensorImage.fromBitmap(img);

                    // Runs model inference and gets result.
                    BirdsClassifier.Outputs outputs = model.process(image);
                    List<Category> probability = outputs.getProbabilityAsCategoryList();

                    int index = 0;
                    float max = probability.get(0).getScore();

                    for (int i = 0; i < probability.size(); i++) {
                        if(max< probability.get(i).getScore())
                        {
                            max = probability.get(i).getScore();
                            index = i;
                        }
                    }

                    Category output = probability.get(index);
                    print.setText(output.getLabel());

                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100)
        {
            myImage.setImageURI(data.getData());
            Uri uri = data.getData();
            imgText.setText("Your uploaded image!");
            try {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {

            }
        }
    }
}