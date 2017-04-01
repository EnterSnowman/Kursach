package com.entersnowman.kursach;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static com.entersnowman.kursach.R.id.list_of_signals;

public class MainActivity extends AppCompatActivity {
    Scheme scheme;
    RelativeLayout r;
    LinearLayout dialog_view;
    final static  int DIALOG = 1;
    final static  int SIGNAL_DIALOG = 2;
    final static  int RESULT_DIALOG = 3;
    EditText nameSignal;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        r = (RelativeLayout) findViewById(R.id.cont);
        nameSignal = (EditText) findViewById(R.id.nameSignal);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton inputSignalsButton = (FloatingActionButton) findViewById(R.id.inputSignals);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheme.inputMode = false;
                inputSignalsButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                if (scheme.deleteMode){
                    scheme.deleteMode = false;
                    view.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    Toast.makeText(MainActivity.this,"Режим удаления выключен",Toast.LENGTH_SHORT).show();
                    nameSignal.setVisibility(View.INVISIBLE);
                }
                else{
                    scheme.deleteMode = true;
                    view.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    Toast.makeText(MainActivity.this,"Режим удаления включен",Toast.LENGTH_SHORT).show();
                    nameSignal.setVisibility(View.INVISIBLE);
                }
            }
        });

        inputSignalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            scheme.inputMode = ! scheme.inputMode;
            scheme.deleteMode = false;
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                if (scheme.inputMode){
                    v.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                    nameSignal.setVisibility(View.VISIBLE);
                }
                else{
                    v.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    nameSignal.setVisibility(View.INVISIBLE);
                }

            }
        });
        scheme = (Scheme) findViewById(R.id.scheme);
        scheme.setActivity(this);
        /*Element e = new Element(this,"AND",2);
        Element e1 = new Element(this,"OR",2);
        Element e2 = new Element(this,"AND",2);*/
        /*Element e = new Element(this,"NAND",1);
        Element e1 = new Element(this,"OR",2);
        e1.inputPins.get(0).setTerm("a");
        e1.inputPins.get(1).setTerm("b");
        Element e2 = new Element(this,"AND",2);
        e2.inputPins.get(0).setTerm("c");
        e2.inputPins.get(1).setTerm("d");
        Element e3 = new Element(this,"AND",2);
        scheme.signals.add("a");
        scheme.signals.add("b");
        scheme.signals.add("c");
        scheme.signals.add("d");
        scheme.addElement(e);
        scheme.addElement(e1);
        scheme.addElement(e2);
        scheme.addElement(e3);*/
        scheme.setName_et(nameSignal);
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
        if (id==DIALOG) {
            // создаем view из dialog.xml
            dialog_view = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.dialog, null);
            // устанавливаем ее, как содержимое тела диалога
            adb.setView(dialog_view);
            adb.setTitle("Добавить элемент (тип элементов, количество входов)");
            adb.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String type = ((Spinner) dialog_view.findViewById(R.id.typeOfElement)).getSelectedItem().toString();
                    int n = Integer.valueOf(((Spinner) dialog_view.findViewById(R.id.numberOfIns)).getSelectedItem().toString());
                    scheme.addElement(new Element(MainActivity.this, type, n,scheme));
                }
            }).setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        if (id==SIGNAL_DIALOG){
            dialog_view = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.choose_signal_dialog, null);
            final RadioGroup radioGroup = (RadioGroup) dialog_view.findViewById(list_of_signals);
            for (String s: scheme.signals){
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(s);
                radioGroup.addView(radioButton);
            }
            adb.setView(dialog_view);
            adb.setTitle("Выбрать неполадку");
            adb.setPositiveButton("Синтезировать", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scheme.synthesis_test();
                }
            }).setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }

        if (id == RESULT_DIALOG){
            dialog_view = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.result_dialog, null);

            adb.setView(dialog_view);
            adb.setTitle("Cинтезированый тест");
            TextView enf = (TextView) dialog_view.findViewById(R.id.enf);
            TextView result = (TextView) dialog_view.findViewById(R.id.result_test);
            StringBuilder res = new StringBuilder();
            for (String s: scheme.result_test)
                res.append(s+"\n");
            result.setText(res.toString());

        }


        return adb.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id==SIGNAL_DIALOG){
            RadioGroup radioGroup  = (RadioGroup) ((AlertDialog)dialog).findViewById(R.id.list_of_signals);
            radioGroup.removeAllViews();
            for (String s: scheme.signals){
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(s);
                radioGroup.addView(radioButton);
            }
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

        if (id == R.id.clear_scheme) {
            scheme.elements.clear();
            scheme.links.clear();
            scheme.signals.clear();
            scheme.invalidate();
            scheme.result_test.clear();
            return true;
        }

        if (id== R.id.name_outputs){
            scheme.nameOutputSignals();
            return true;
        }

        if (id== R.id.synthesis){
            scheme.synthesis_test();
            //showDialog(SIGNAL_DIALOG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
