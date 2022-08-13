package com.example.androidteste.activity.empresas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidteste.R;
import com.example.androidteste.helper.FirebaseHelper;
import com.example.androidteste.model.Pagamento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmpresaRecebimentosActivity extends AppCompatActivity {

    private List<Pagamento> pagamentoList = new ArrayList<>();

    private Pagamento cartaoCreditoDebito = new Pagamento();
    private Pagamento dinheiro = new Pagamento();

    private CheckBox cb_ccd;
    private CheckBox cb_dn;

    private ImageButton ib_salvar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_recebimentos);

        iniciaComponentes();
        configCliques();
        recuperaPagamentos();
    }

    private void configCliques(){
        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
        ib_salvar.setOnClickListener(view -> salvarPagamentos());

        //Cartão de crédito e debito
        cb_ccd.setOnCheckedChangeListener((compoundButton, isCheked) -> {
            cartaoCreditoDebito.setDescricao("Pagamento no Crédito/Débito");
            cartaoCreditoDebito.setStatus(isCheked);
        });

        //Dinheiro
        cb_dn.setOnCheckedChangeListener((compoundButton, isCheked) -> {
            dinheiro.setDescricao("Pagamento em dinheiro");
            dinheiro.setStatus(isCheked);
        });

    }

    private void salvarPagamentos() {

        if(cb_ccd.isChecked()){
            if(!pagamentoList.contains(cartaoCreditoDebito)) pagamentoList.add(cartaoCreditoDebito);
        }

         if (cb_dn.isChecked()){
                if(!pagamentoList.contains(dinheiro))  pagamentoList.add(dinheiro);
         }


        Pagamento.salvar(pagamentoList);

    }

    private void validaPagamentos(){

    }

    private void recuperaPagamentos(){
        DatabaseReference pagamentosRef = FirebaseHelper.getDatabaseReference()
                .child("recebimentos")
                .child(FirebaseHelper.getIdFirebase());
        pagamentosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        Pagamento pagamento = ds.getValue(Pagamento.class);
                        pagamentoList.add(pagamento);

                    }
                    configPagamentos();
                }else {
                    configSalvar(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configSalvar(boolean progress){
        if(progress){
            progressBar.setVisibility(View.VISIBLE);
            ib_salvar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            ib_salvar.setVisibility(View.VISIBLE);
        }
    }

    private void configPagamentos() {
        for (Pagamento pagamento : pagamentoList) {
            switch (pagamento.getDescricao()){
                case "Pagamento no Crédito/Débito":
                    cartaoCreditoDebito = pagamento;
                    cb_ccd.setChecked(cartaoCreditoDebito.getStatus());
                    break;

                case "Pagamento em dinheiro":
                    dinheiro = pagamento;
                    cb_dn.setChecked(dinheiro.getStatus());
                    break;

            }

        }
        configSalvar(false);
    }

    private void iniciaComponentes(){
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Recebimentos");

        cb_ccd = findViewById(R.id.cb_ccd);
        cb_dn = findViewById(R.id.cb_dn);

        ib_salvar = findViewById(R.id.ib_salvar);
        progressBar = findViewById(R.id.progressBar);
    }
}