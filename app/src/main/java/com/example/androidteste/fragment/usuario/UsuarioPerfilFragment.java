package com.example.androidteste.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidteste.R;
import com.example.androidteste.activity.autenticacao.CriarContaActivity;
import com.example.androidteste.activity.autenticacao.LoginActivity;
import com.example.androidteste.activity.usuario.UsuarioEnderecoActivity;
import com.example.androidteste.activity.usuario.UsuarioFavoritosActivity;
import com.example.androidteste.activity.usuario.UsuarioPerfilActivity;
import com.example.androidteste.helper.FirebaseHelper;


public class UsuarioPerfilFragment extends Fragment {

    private ConstraintLayout l_logado;
    private ConstraintLayout l_deslogado;
    private LinearLayout menu_perfil;
    private LinearLayout menu_endereco;
    private LinearLayout menu_favoritos;
    private LinearLayout menu_prestadores;
    private LinearLayout menu_ajuda;
    private LinearLayout menu_deslogar;
    private Button btn_entrar;
    private Button btn_criar_conta;
    private TextView text_usuario;
    private CardView cv_sair;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario_perfil, container, false);

    iniciaComponentes(view);

    configCliques();

    return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        verificaAcesso();

    }

    private void verificaAcesso(){
        if (FirebaseHelper.getAutenticado()){
            l_deslogado.setVisibility(View.GONE);
            l_logado.setVisibility(View.VISIBLE);
            menu_deslogar.setVisibility(View.VISIBLE);
            text_usuario.setText(FirebaseHelper.getAuth().getCurrentUser().getDisplayName());

        }else {
            l_deslogado.setVisibility(View.VISIBLE);
            l_logado.setVisibility(View.GONE);
            menu_deslogar.setVisibility(View.GONE);
            cv_sair.setVisibility(View.GONE);

        }

    }

    private void configCliques (){

        btn_entrar.setOnClickListener(view -> startActivity(new Intent(requireActivity(), LoginActivity.class)));
        btn_criar_conta.setOnClickListener(view -> startActivity(new Intent(requireActivity(), CriarContaActivity.class)));
        menu_deslogar.setOnClickListener(view -> deslogar());


        menu_perfil.setOnClickListener(view -> verificaAutenticacao(UsuarioPerfilActivity.class));
        menu_endereco.setOnClickListener(view -> verificaAutenticacao(UsuarioEnderecoActivity.class));
        menu_favoritos.setOnClickListener(view -> verificaAutenticacao(UsuarioFavoritosActivity.class));

    }

    private void deslogar(){

        FirebaseHelper.getAuth().signOut();
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.menu_home);
    }

    private void verificaAutenticacao(Class<?> clazz){
        if(FirebaseHelper.getAutenticado()){
            startActivity(new Intent(requireActivity(), clazz));
        }else{
            startActivity(new Intent(requireActivity(), LoginActivity.class));
        }

    }

    private void iniciaComponentes (View view){
        l_logado = view.findViewById(R.id.l_logado);
        l_deslogado = view.findViewById(R.id.l_deslogado);
        menu_perfil = view.findViewById(R.id.menu_perfil);
        menu_endereco = view.findViewById(R.id.menu_endereco);
        menu_favoritos = view.findViewById(R.id.menu_favoritos);
        menu_ajuda = view.findViewById(R.id.menu_ajuda);
        menu_deslogar = view.findViewById(R.id.menu_deslogar);
        btn_entrar = view.findViewById(R.id.btn_entrar);
        btn_criar_conta = view.findViewById(R.id.btn_criar_conta);
        text_usuario = view.findViewById(R.id.text_usuario);
        cv_sair = view.findViewById(R.id.cv_sair);


    }
}