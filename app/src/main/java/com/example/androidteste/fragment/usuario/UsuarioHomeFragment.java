package com.example.androidteste.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidteste.DAO.EmpresaDAO;
import com.example.androidteste.DAO.ItemPedidoDAO;
import com.example.androidteste.R;
import com.example.androidteste.activity.empresas.EmpresaCatalogoActivity;
import com.example.androidteste.activity.usuario.CarrinhoActivity;
import com.example.androidteste.adapter.EmpresasAdapter;
import com.example.androidteste.helper.FirebaseHelper;
import com.example.androidteste.helper.GetMask;
import com.example.androidteste.model.Empresa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioHomeFragment extends Fragment implements EmpresasAdapter.OnClickListener {

    private EmpresasAdapter empresasAdapter;
    private List<Empresa> empresaList = new ArrayList<>();
    private RecyclerView rv_empresas;
    private ProgressBar progressBar;
    private TextView text_info;

    private TextView textQtdItemSacola;
    private TextView textTotalCarrinho;
    private ConstraintLayout ll_pedido;

    private ItemPedidoDAO itemPedidoDAO;
    private EmpresaDAO empresaDAO;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario_home, container, false);

        itemPedidoDAO = new ItemPedidoDAO(getContext());
        empresaDAO = new EmpresaDAO(getContext());

        iniciaComponentes(view);

        configCliques();

        configRv();

        recuperaEmpresas();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        configSacola();
    }

    private void configCliques(){
        ll_pedido.setOnClickListener(view -> startActivity(new Intent(getActivity(), CarrinhoActivity.class)));
    }

    private void configRv(){
        rv_empresas.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv_empresas.setHasFixedSize(true);
        empresasAdapter = new EmpresasAdapter(empresaList, this, requireContext());
        rv_empresas.setAdapter(empresasAdapter);
    }

    private void iniciaComponentes(View view){
        rv_empresas = view.findViewById(R.id.rv_empresas);
        progressBar = view.findViewById(R.id.progressBar);
        text_info = view.findViewById(R.id.text_info);

        textQtdItemSacola = view.findViewById(R.id.textQtdItemSacola);
        textTotalCarrinho = view.findViewById(R.id.textTotalCarrinho);
        ll_pedido = view.findViewById(R.id.ll_pedido);



    }

    private void recuperaEmpresas(){
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresa");
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    empresaList.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        Empresa empresa = ds.getValue(Empresa.class);
                        empresaList.add(empresa);
                    }
                    text_info.setText("");

                }else{
                    text_info.setText("Nenhuma empresa cadastrada");
                }

                progressBar.setVisibility(View.GONE);
                Collections.reverse(empresaList);
                empresasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configSacola(){
        if(!itemPedidoDAO.getList().isEmpty()){

            double totalPedido = itemPedidoDAO.getTotal() + empresaDAO.getEmpresa().getTaxaEntrega();


           ll_pedido.setVisibility(View.VISIBLE);
           textQtdItemSacola.setText(String.valueOf(itemPedidoDAO.getList().size()));
           textTotalCarrinho.setText(getString(R.string.text_valor, GetMask.getValor(totalPedido)));
        }else{
            ll_pedido.setVisibility(View.GONE);
            textQtdItemSacola.setText("");
            textTotalCarrinho.setText("");

        }
    }

    @Override
    public void OnClick(Empresa empresa) {
        Intent intent = new Intent(requireActivity(), EmpresaCatalogoActivity.class);
        intent.putExtra("empresaSelecionada", empresa);
        startActivity(intent);
    }
}