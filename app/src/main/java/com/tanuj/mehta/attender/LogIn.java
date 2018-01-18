package com.tanuj.mehta.attender;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;


public class LogIn extends Fragment {

    EditText username;
    EditText password;
    TextView inst;
    Button button;
    Boolean verified = false;
    FragmentListener provider;
    Context thisContext;

    public interface FragmentListener{
      public Boolean getVerifState(String username,String password);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        thisContext=activity;
        try{
            provider=(FragmentListener)activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);
        button = (Button) view.findViewById(R.id.signin);
        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
        inst=(TextView)view.findViewById(R.id.inst);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDetails();
                    }
                }
        );
        inst.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInst();
                    }
                }
        );
        return view;
    }

public void sendDetails(){
        if ( (!username.getText().toString().isEmpty()) && (!password.getText().toString().isEmpty()) ){
            verified=  provider.getVerifState(username.getText().toString(),password.getText().toString());
        }
      if(verified){
          username.setEnabled(false);
          password.setEnabled(false);
          username.setText("");
          button.setEnabled(false);
      }
    password.setText("");
}
public void showInst(){
    //display instructions dialog
    AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
    builder.setMessage(R.string.inst_content)
            .setTitle(R.string.inst_title)
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(thisContext, R.string.hope_app, Toast.LENGTH_SHORT).show();
                }
            });
    AlertDialog dialog = builder.create();
    dialog.show();
}
}
