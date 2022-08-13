package com.example.androidteste.fragment.usuario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidteste.R;
import com.example.androidteste.activity.empresas.EmpresaCatalogoActivity;
import com.example.androidteste.adapter.ProdutoEmpresaPesquisaAdapter;
import com.example.androidteste.helper.FirebaseHelper;
import com.example.androidteste.model.Empresa;
import com.example.androidteste.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class UsuarioPesquisaFragment extends Fragment implements ProdutoEmpresaPesquisaAdapter.OnClickListener {


    private ProdutoEmpresaPesquisaAdapter produtoEmpresaPesquisaAdapter;
    private List<Empresa> empresaList = new ArrayList<>();


    private SearchView searchView;
    private RecyclerView rv_produtos_pesquisa;
    private ProgressBar progressBar;
    private TextView text_info;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usuario_pesquisa, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iniciaComponentes(view);

        configRv(empresaList);

        recuperaEmpresas();

        configSearchView();

    }

    private void configSearchView() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String pesquisa) {
                if(pesquisa.length() >= 3){
                    buscaEmpresa(pesquisa);
                }else{
                    ocultarTeclado();
                    Toast.makeText(getContext(), "Mínimo 3 caractere.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.findViewById(com.like.view.R.id.search_close_btn).setOnClickListener(view -> {
            ocultarTeclado();

            EditText edt = searchView.findViewById(com.like.view.R.id.search_src_text);
            edt.getText().clear();
        });

    }

    private void buscaEmpresa(String pesquisa) {
        List<Empresa> empresaListBusca = new ArrayList<>();

        ocultarTeclado();

        for (Empresa empresa : empresaList){
            if(empresa.getNome().toUpperCase(Locale.ROOT).contains(pesquisa.toUpperCase(Locale.ROOT))){
                empresaList.add(empresa);
            }
        }

        if(!empresaListBusca.isEmpty()){
            configRv(empresaListBusca);
        }else{
            configRv(new ArrayList<>());
            text_info.setText("Nenhum post encontrado com o nome pesquisado.");
        }

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

                configRv(empresaList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configRv(List<Empresa> empresaList) {

        rv_produtos_pesquisa.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv_produtos_pesquisa.setHasFixedSize(true);
        produtoEmpresaPesquisaAdapter = new ProdutoEmpresaPesquisaAdapter(empresaList, getContext(), this);
        rv_produtos_pesquisa.setAdapter(produtoEmpresaPesquisaAdapter);
    }

    private void iniciaComponentes(View view) {
        searchView = view.findViewById(R.id.searchView);
        rv_produtos_pesquisa = view.findViewById(R.id.rv_produtos_pesquisa);
        progressBar = view.findViewById(R.id.progressBar);
        text_info = view.findViewById(R.id.text_info);
    }

    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void OnClick(Empresa empresa) {
        Intent intent = new Intent(requireActivity(), EmpresaCatalogoActivity.class);
        intent.putExtra("empresaSelecionada", empresa);
        startActivity(intent);
    }
}