package com.example.colortiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {
    LinearLayout l0, l1, l2;
    TilesView tilesview;
    boolean onClick = false;
    int q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("trans", "Create в main");
        if (savedInstanceState != null) {
            onClick = savedInstanceState.getBoolean("button");
            q = savedInstanceState.getInt("dimension");
        }
        if (!onClick) {
            setContentView(R.layout.activity_main);
        }
        if (onClick) {
            setContentView(R.layout.activity_tiles);
            tilesview = findViewById(R.id.tilesView);
            tilesview.setK(q);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("trans", "Save в main");
        outState.putBoolean("button", onClick);
        outState.putInt("dimension", q);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("trans", "Rest в main");
        onClick = savedInstanceState.getBoolean("button");
        q = savedInstanceState.getInt("dimension");
    }


    public void ClickPlay(View view) {
        //диалог. выбора размерности игрового поля
        final String[] dims = new String[]{"2x2", "3x3", "4x4"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите размерность поля");
        builder.setSingleChoiceItems(dims, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                q = which + 2;
                setContentView(R.layout.activity_tiles);
                tilesview = findViewById(R.id.tilesView);
                tilesview.setK(q);
                onClick = true;
                dialog.dismiss();
            }
        });
        builder.show();
    }


    public void ClickRule(View view) {
        l0 = findViewById(R.id.main);
        l1 = findViewById(R.id.rule1);
        l1.setVisibility(View.VISIBLE);
        l0.setVisibility(View.GONE);
    }

    public void next_rule1(View view) {
        l1 = findViewById(R.id.rule1);
        l2 = findViewById(R.id.rule2);
        l2.setVisibility(View.VISIBLE);
        l1.setVisibility(View.GONE);
    }

    public void next_rule2(View view) {
        l2 = findViewById(R.id.rule2);
        l0 = findViewById(R.id.main);
        l2.setVisibility(View.INVISIBLE);
        l0.setVisibility(View.VISIBLE);
    }

}