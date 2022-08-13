package com.example.androidteste.fragment.empresa;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.androidteste.R;
import com.example.androidteste.activity.usuario.PedidoDetalheActivity;
import com.example.androidteste.adapter.EmpresaPedidoAdapter;
import com.example.androidteste.helper.FirebaseHelper;
import com.example.androidteste.model.Pedido;
import com.example.androidteste.model.StatusPedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EmpresaPedidoFinalizadoFragment extends Fragment implements EmpresaPedidoAdapter.OnClickListener {

    private final List<Pedido> pedidoList = new ArrayList<>();
    private EmpresaPedidoAdapter empresaPedidoAdapter;

    private RecyclerView rv_pedidos;
    private ProgressBar progressBar;
    private TextView text_info;

    private AlertDialog dialog;

    private RadioGroup rg_status;
    private RadioButton rb_pendente;
    private RadioButton rb_separacao;
    private RadioButton rb_saiu_entrega;
    private RadioButton rb_entregue;
    private RadioButton rb_cancelado;

    private Button btn_fechar;
    private Button btn_salvar;

    private int status = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empresa_pedido_andamento, container, false);


        iniciaComponentes(view);

        configRv();

        recuperaPedidos();

        return view;
    }


    private void recuperaPedidos(){
        DatabaseReference pedidoRef = FirebaseHelper.getDatabaseReference()
                .child("empresaPedido")
                .child(FirebaseHelper.getIdFirebase());
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    pedidoList.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);
                        if(pedido != null){
                            addPedidoList(pedido);
                        }
                    }
                    text_info.setText("");
                }else{
                    text_info.setText("Nenhum pedido recebido");
                }
                progressBar.setVisibility(View.GONE);
                Collections.reverse(pedidoList);
                empresaPedidoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialogStatus(Pedido pedido){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialog);
        View view = getLayoutInflater().inflate(R.layout.layout_status_pedido, null);
        builder.setView(view);

        rg_status = view.findViewById(R.id.rg_status);
        rb_pendente = view.findViewById(R.id.rb_pendente);
        rb_separacao = view.findViewById(R.id.rb_separacao);
        rb_saiu_entrega = view.findViewById(R.id.rb_saiu_entrega);
        rb_entregue = view.findViewById(R.id.rb_entregue);
        rb_cancelado = view.findViewById(R.id.rb_cancelado);
        btn_fechar = view.findViewById(R.id.btn_fechar);
        btn_salvar = view.findViewById(R.id.btn_salvar);

        configStatus(pedido);

        btn_fechar.setOnClickListener(view1 -> dialog.dismiss());


        rg_status.setOnCheckedChangeListener((radioGroup, i) -> {
            status = radioGroup.getCheckedRadioButtonId();

        });
        btn_salvar.setOnClickListener(view1 -> {
            atualizarStatus(pedido);
            dialog.dismiss();
        });

        dialog = builder.create();
        dialog.show();

    }

    private void atualizarStatus(Pedido pedido){
        StatusPedido statusPedido;

        if (status == R.id.rb_separacao){
            statusPedido = StatusPedido.SEPARAÇÃO;
        }else if (status == R.id.rb_saiu_entrega){
            statusPedido = StatusPedido.SAIU_ENTREGA;
        }else if (status == R.id.rb_entregue){
            statusPedido = StatusPedido.ENTREGUE;
        }else if (status == R.id.rb_cancelado){
            statusPedido = StatusPedido.CANCELADO_EMPRESA;
        }else{
            statusPedido = StatusPedido.PENDENTE;
        }


        if(pedido.getStatusPedido() != statusPedido){
            pedido.setStatusPedido(statusPedido);
            pedido.atualizar();
        }

        if(pedido.getStatusPedido() == StatusPedido.ENTREGUE ||
                pedido.getStatusPedido() == StatusPedido.CANCELADO_EMPRESA ||
                pedido.getStatusPedido() == StatusPedido.CANCELADO_USUÁRIO){
            pedidoList.remove(pedido);
            empresaPedidoAdapter.notifyDataSetChanged();
        }
    }


    private void configStatus(Pedido pedido){
        int id;
        switch (pedido.getStatusPedido()){
            case ENTREGUE:
                id = R.id.rb_entregue;
                break;
            case SEPARAÇÃO:
                id = R.id.rb_separacao;
                break;
            case SAIU_ENTREGA:
                id = R.id.rb_saiu_entrega;
                break;
            case CANCELADO_EMPRESA:
            case CANCELADO_USUÁRIO:
                id = R.id.rb_cancelado;
                break;
            default:
                id = R.id.rb_pendente;
                break;
        }
        rg_status.check(id);
        if(pedido.getStatusPedido() == StatusPedido.ENTREGUE ||
                pedido.getStatusPedido() == StatusPedido.CANCELADO_USUÁRIO ||
                pedido.getStatusPedido() == StatusPedido.CANCELADO_EMPRESA){
            rb_pendente.setEnabled(false);
            rb_separacao.setEnabled(false);
            rb_saiu_entrega.setEnabled(false);
            rb_entregue.setEnabled(false);
            rb_cancelado.setEnabled(false);
        }
    }


    private void addPedidoList(Pedido pedido){
        if(pedido.getStatusPedido() == StatusPedido.CANCELADO_EMPRESA ||
                pedido.getStatusPedido() == StatusPedido.CANCELADO_USUÁRIO ||
                pedido.getStatusPedido() == StatusPedido.ENTREGUE ){
            pedidoList.add(pedido);
        }
    }

    private void configRv(){
        rv_pedidos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_pedidos.setHasFixedSize(true);

        empresaPedidoAdapter = new EmpresaPedidoAdapter(pedidoList, getContext(), this);
        rv_pedidos.setAdapter(empresaPedidoAdapter);
    }

    private void iniciaComponentes(View view){

        rv_pedidos = view.findViewById(R.id.rv_pedidos);
        progressBar = view.findViewById(R.id.progressBar);
        text_info = view.findViewById(R.id.text_info);

    }


    @Override
    public void OnClick(Pedido pedido, int rota) {
        if(rota == 0){
            showDialogStatus(pedido);
        }else if(rota == 1){
            Intent intent = new Intent(getActivity(), PedidoDetalheActivity.class);
            intent.putExtra("pedidoSelecionado", pedido);
            intent.putExtra("acesso", "empresa");
            startActivity(intent);
        }
    }
}