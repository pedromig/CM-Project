package com.example.app.ui.home.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.model.Person;
import com.example.app.ui.dashboard.MQTT;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;

public class DialogPayDebt extends DialogFragment {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final String TOPIC = "/cm/fridge-mates/" + auth.getUid();

    private final Person person;
    private final Double money;
    private int position;

    public DialogPayDebt(Person model, Double money, int position) {
        this.person = model;
        this.money = money;
        this.position = position;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_pay_debt_dialog, null);

        builder.setView(view).setMessage("Do you wish to pay this debt?")
                .setPositiveButton("Pay", (dialog, id) -> {
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Pay Debt
        Button btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setOnClickListener(v -> {
            MainActivity activity = (MainActivity) requireActivity();
            activity.getMqttService().subscribeToTopic(TOPIC);

            // DEBUG FRIENDLY
            //activity.getMqttService().subscribeToTopic("/cm/fridge-mates/" + person.getKey());

            db.getReference().child("profiles").child(auth.getUid()).child("debts")
                    .child(person.getKey()).setValue(0.0);

            publishMessage(activity.getMqttService(),
                    "Payment received from " + person.getName()
                            + " (" + money + " €)", 2, TOPIC);

            Toast.makeText(requireActivity(), "Debt Payed! User Notified",
                    Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        return dialog;
    }

    public void publishMessage(MQTT client, String msg, int qos, String topic) {
        byte[] encodedPayload;
        encodedPayload = msg.getBytes(StandardCharsets.UTF_8);
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setQos(qos);
        client.mqttAndroidClient.publish(topic, message);
    }

}
