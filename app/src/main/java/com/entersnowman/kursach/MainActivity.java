package com.entersnowman.kursach;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Scheme scheme;
    RelativeLayout r;
    LinearLayout view;
    final int DIALOG = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        r = (RelativeLayout) findViewById(R.id.cont);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scheme.deleteMode){
                    scheme.deleteMode = false;
                    view.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    Toast.makeText(MainActivity.this,"Режим удаления выключен",Toast.LENGTH_SHORT).show();
                }
                else{
                    scheme.deleteMode = true;
                    view.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    Toast.makeText(MainActivity.this,"Режим удаления включен",Toast.LENGTH_SHORT).show();
                }
            }
        });
        scheme = (Scheme) findViewById(R.id.scheme);
        Element e = new Element(this,"AND",3);
        scheme.addElement(e);
        scheme.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        // создаем view из dialog.xml
        view = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.dialog, null);
        // устанавливаем ее, как содержимое тела диалога
        adb.setView(view);
        adb.setTitle("Добавить элемент (тип элементов, количество входов)");
        adb.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String type = ((Spinner)view.findViewById(R.id.typeOfElement)).getSelectedItem().toString();
                int n = Integer.valueOf(((Spinner)view.findViewById(R.id.numberOfIns)).getSelectedItem().toString());
                scheme.addElement(new Element(MainActivity.this,type,n));
            }
        }).setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return adb.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id==DIALOG){

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showDialog(DIALOG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
