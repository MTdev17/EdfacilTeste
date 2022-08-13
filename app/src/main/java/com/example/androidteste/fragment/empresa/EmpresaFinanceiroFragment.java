package com.example.androidteste.fragment.empresa;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidteste.R;
import com.example.androidteste.adapter.FinanceiroAdapter;
import com.example.androidteste.helper.FirebaseHelper;
import com.example.androidteste.helper.GetMask;
import com.example.androidteste.model.Pedido;
import com.example.androidteste.model.StatusPedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmpresaFinanceiroFragment extends Fragment {

    private FinanceiroAdapter financeiroAdapter;
    private List<Pedido> pedidoList = new ArrayList<>();

    private EditText edt_inicio, edt_final;
    private Button btn_filtrar, btn_todos;
    private ProgressBar progressBar;

    private TextView text_receita;
    private TextView text_saldo;

    private RecyclerView rv_financeiro;

    private String data_inicio;
    private String data_final;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empresa_financeiro, container, false);

        iniciaComponentes(view);

        recuperaTodosPedidos();

        configRv();

        configCliques();

        return view;
    }

    private void configRv(){
        rv_financeiro.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rv_financeiro.setHasFixedSize(true);
        financeiroAdapter = new FinanceiroAdapter(pedidoList, requireContext());
        rv_financeiro.setAdapter(financeiroAdapter);

    }

    private void configCliques(){

        btn_todos.setOnClickListener(view -> recuperaTodosPedidos());

        btn_filtrar.setOnClickListener(view -> {
            data_inicio = edt_inicio.getText().toString();
            data_final = edt_final.getText().toString();

            if(!data_inicio.isEmpty()){
                if(!data_final.isEmpty()){

                    progressBar.setVisibility(View.VISIBLE);
                    filtrarPedidosData();

                }else{
                    edt_final.requestFocus();
                    edt_final.setError("Informe a data");
                }

            }else{
                edt_inicio.requestFocus();
                edt_inicio.setError("Informe a data");
            }


        });
    }

    private void recuperaTodosPedidos() {
        DatabaseReference pedidosRef = FirebaseHelper.getDatabaseReference()
                .child("empresaPedido")
                .child(FirebaseHelper.getIdFirebase());
        pedidosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.exists()){
                    pedidoList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);

                        if(pedido.getStatusPedido() == StatusPedido.ENTREGUE){
                            pedidoList.add(pedido);
                        }else{
                            pedidoList.remove(pedido);
                        }
                    }

                    Collections.reverse(pedidoList);
                    financeiroAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    configValores();
                    ocultarTeclado();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void filtrarPedidosData() {
        DatabaseReference pedidosRef = FirebaseHelper.getDatabaseReference()
                .child("empresaPedido")
                .child(FirebaseHelper.getIdFirebase());
        pedidosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    pedidoList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);

                        verificaDataPedidos(pedido);

                    }

                    Collections.reverse(pedidoList);
                    financeiroAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verificaDataPedidos(Pedido pedido) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("PT", "br"));

        String strDataPedido = GetMask.getDate(pedido.getDataPedido(), 1);

        try {
            Date dataPedido = sdf.parse(strDataPedido);
            Date dataInicio = sdf.parse(data_inicio);
            Date dataFinal = sdf.parse(data_final);

            if((dataInicio.compareTo(dataPedido) == 0 || dataInicio.compareTo(dataPedido) < 0)
                    && (dataFinal.compareTo(dataPedido) == 0 || dataFinal.compareTo(dataPedido) > 0)){
                pedidoList.add(pedido);
            }

        }catch (Exception e){

        }

        if(pedidoList.size() > 0){
            ocultarTeclado();
        }

        configValores();

        // RESULTADO
        // 0 -> Data iguais
        // -1 -> Quando a data passada como parametro for maior que a data que invocou a funcao
        // 1 -> Quando a data passada como parametro for menor que a data que invocou a funcao

    }


    private void configValores(){
        double saldo = 0;
        for(Pedido pedido : pedidoList){
            saldo += pedido.getSubTotal()+pedido.getTaxaEntrega();

        }

        text_receita.setText(getString(R.string.text_valor, GetMask.getValor(saldo)));
        text_saldo.setText(getString(R.string.text_valor, GetMask.getValor(saldo)));
    }

    private void ocultarTeclado(){
        ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                btn_filtrar.getWindowToken(), 0
        );

    }

    private void iniciaComponentes(View view){
        edt_inicio = view.findViewById(R.id.edt_inicio);
        edt_final = view.findViewById(R.id.edt_final);
        btn_filtrar = view.findViewById(R.id.btn_filtrar);
        btn_todos = view.findViewById(R.id.btn_todos);
        progressBar = view.findViewById(R.id.progressBar);
        text_receita = view.findViewById(R.id.text_receita);
        text_saldo = view.findViewById(R.id.text_saldo);
        rv_financeiro = view.findViewById(R.id.rv_financeiro);

    }
}