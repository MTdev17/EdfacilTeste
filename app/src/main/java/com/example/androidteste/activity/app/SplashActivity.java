package com.example.androidteste.activity.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.androidteste.DAO.EntregaDAO;
import com.example.androidteste.DAO.ItemPedidoDAO;
import com.example.androidteste.R;
import com.example.androidteste.activity.empresas.EmpresaFinalizaCadastroActivity;
import com.example.androidteste.activity.empresas.EmpresaHomeActivity;
import com.example.androidteste.activity.usuario.UsuarioFinalizaCadastroActivity;
import com.example.androidteste.activity.usuario.UsuarioHomeActivity;
import com.example.androidteste.helper.FirebaseHelper;
import com.example.androidteste.model.Empresa;
import com.example.androidteste.model.Login;
import com.example.androidteste.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private Login login;
    private Usuario usuario;
    private Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        limparSQL();

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override public void run() {
                verificaAutenticacao();
            }
        }, 2000);

    }

    private void limparSQL(){
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO(getBaseContext());
        itemPedidoDAO.limparCarrinho();
    }

    private void verificaAutenticacao(){
        if(FirebaseHelper.getAutenticado()){

            verificaCadastro();

        }else{
            startActivity(new Intent(this, UsuarioHomeActivity.class));

        }

    }

    private void verificaCadastro() {
        DatabaseReference loginRef = FirebaseHelper.getDatabaseReference()
                .child("login")
                .child(FirebaseHelper.getIdFirebase());
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                login = snapshot.getValue(Login.class);

                verificaAcesso(login);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verificaAcesso(Login login) {
        if (login != null) {

            if (login.getTipo().equals("U")) {
                if (login.getAcesso()) {
                    finish();
                    startActivity(new Intent(getBaseContext(), UsuarioHomeActivity.class));

                } else {
                    recuperaUsuario();
                }
            } else {
                if (login.getAcesso()) {
                    finish();
                    startActivity(new Intent(getBaseContext(), EmpresaHomeActivity.class));
                } else {
                    recuperaEmpresa();
                }
            }
        }
    }

    private void recuperaUsuario(){
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(login.getId());
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if (usuario != null){
                    Intent intent = new Intent(getBaseContext(), UsuarioFinalizaCadastroActivity.class);
                    intent.putExtra("login", login);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recuperaEmpresa(){
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresa")
                .child(login.getId());
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Empresa empresa = snapshot.getValue(Empresa.class);
                if (empresa != null){
                    Intent intent = new Intent(getBaseContext(), EmpresaFinalizaCadastroActivity.class);
                    intent.putExtra("login", login);
                    intent.putExtra("empresa", empresa);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}