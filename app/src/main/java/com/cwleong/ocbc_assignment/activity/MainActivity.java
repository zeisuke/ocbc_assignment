package com.cwleong.ocbc_assignment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cwleong.ocbc_assignment.R;
import com.cwleong.ocbc_assignment.adapter.MessageAdapter;
import com.cwleong.ocbc_assignment.bean.MessagePayloadBean;
import com.cwleong.ocbc_assignment.bean.MessageValidatorBean;
import com.cwleong.ocbc_assignment.utils.MessageValidator;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etMsg;
    private ImageButton btnSend;
    private MessageAdapter adapter;
    private MessagePayloadBean payload;
    private String currentUser;
    private ArrayList<MessagePayloadBean.MessageBean> messages = new ArrayList<MessagePayloadBean.MessageBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        etMsg = findViewById(R.id.etMsg);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(onBtnSendClickListener);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        payload = new MessagePayloadBean(messages);

        HashMap<String, MessagePayloadBean.WalletBean> hm = new HashMap<>();
        hm.put("Alice".toUpperCase(), new MessagePayloadBean.WalletBean("Alice", 100.0));
        hm.put("Bob".toUpperCase(), new MessagePayloadBean.WalletBean("Bob", 80.0));
        payload.setHm(hm);

        adapter = new MessageAdapter(this, payload);
        recyclerView.setAdapter(adapter);
    }

    private View.OnClickListener onBtnSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (etMsg.getText().length() > 0){
                messages.add(0, new MessagePayloadBean.MessageBean("me", etMsg.getText().toString()));
                String msg = etMsg.getText().toString();
                etMsg.setText("");
                MessageValidatorBean messageValidatorBean = MessageValidator.processValidate(msg, currentUser, payload.getHm());
                messages.add(0, new MessagePayloadBean.MessageBean("", messageValidatorBean.getMsg()));

                payload.setList(messages);
                payload.setHm(messageValidatorBean.getHm());
                adapter.setItems(messages);
                if (messageValidatorBean.getCommand() != null && messageValidatorBean.getCommand().equalsIgnoreCase(MessageValidator.COMMAND_LOGIN)){
                    currentUser = messageValidatorBean.getFrom();
                }
            }
        }
    };
}