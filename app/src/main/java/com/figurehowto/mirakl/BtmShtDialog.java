package com.figurehowto.mirakl;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class  BtmShtDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private BtmShtListener listener;
    TextView changeDate,deleteAlert;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet,container,false);
        changeDate= view.findViewById(R.id.change_date);
        deleteAlert = view.findViewById(R.id.delete_alert);
        changeDate.setOnClickListener(this);
        deleteAlert.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.change_date:
                //Do here
                listener.onChangeClicked();
                dismiss();
                break;
            case R.id.delete_alert:
                //Do work here
                if (getArguments() != null) {
                    //Recieve data sent to fragment using bundle
                    int alarmId = getArguments().getInt("alarmId");
                    listener.onCancelClicked(alarmId);
                    dismiss();
                }

                break;
        }
    }

    public interface BtmShtListener{
        void onChangeClicked();
        void onCancelClicked(int alarmId);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (BtmShtListener) context;
        }catch (Exception ignored){

        }

    }
}
