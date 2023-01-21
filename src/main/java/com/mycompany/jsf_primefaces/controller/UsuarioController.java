/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.jsf_primefaces.controller;

import com.google.gson.Gson;
import com.mycompany.jsf_primefaces.hibernate.dao.DAOUsuario;
import com.mycompany.jsf_primefaces.model.Usuario;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

/**
 *
 * @author lucia
 */
//ManagedBean - passa o name sempre como o nome da classe com a primeira letra minuscula
@ManagedBean(name = "usuarioController")
@ViewScoped
public class UsuarioController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Usuario usuario = new Usuario();

    @Inject
    private DAOUsuario daoUsuario;

    private List<Usuario> listUsuario;

    public String salvar() {
        try {
            setUsuario(daoUsuario.saveOrUpdate(usuario));
            mostrarMsg("Registro salvo com sucesso!", "Ok!", FacesMessage.SEVERITY_INFO);
        } catch (Exception ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            mostrarMsg("Erro ao Salvar!\n" + ex.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
        }

        return ""; //Redirecionar para mesma tela após salvar
    }

    public String limpar() {
        setUsuario(new Usuario());
        return "";
    }

    public String deletar() {
        try {
            if (getUsuario() != null
                    && getUsuario().getId() != null) {
                //Remover Telefone e Usuarios em cascata! Tabela dependente deve ser excluida primeiro!!
                daoUsuario.removerUsuarioETelefonesCascata(getUsuario());
                setUsuario(new Usuario());
                mostrarMsg("Registro removido com sucesso!", "Ok!", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();

            if (ex.getMessage() != null
                    && ex.getMessage().contains("org.hibernate.exception.ConstraintViolationException")) {
                mostrarMsg("Erro ao Deletar!\nExistem telefones cadastrados para o usuário!\nNão é permitido a exclusão!", "ERRO!", FacesMessage.SEVERITY_ERROR);
            } else {
                mostrarMsg("Erro ao Deletar!\n" + ex.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
            }
        }
        return "";
    }

    private void mostrarMsg(String mensagem, String sumario, FacesMessage.Severity severity) {
        FacesMessage message = new FacesMessage(severity, sumario, mensagem);
        //Pode ser dado a mensagem sobre algum componente especifico ou null quando é geral:
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void pesquisaCep(AjaxBehaviorEvent event) {
        try {
            if (getUsuario() != null
                    && getUsuario().getCep() != null
                    && !getUsuario().getCep().trim().isEmpty()) {
                //Fazendo a requisição direta do webService:  
                //URL exemplo: https://viacep.com.br/ws/17203040/json/
                URL url = new URL("https://viacep.com.br/ws/" + getUsuario().getCep() + "/json/");
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String cep;
                StringBuilder jsonCep = new StringBuilder();

                //Atribui a variavel cep e enquanto tiver algo vai ficar no while:
                while ((cep = bufferedReader.readLine()) != null) {
                    jsonCep.append(cep);
                }

                System.out.println("\nJson Recebido do VIACEP: \n" + jsonCep.toString());

                //Como os atributos que criamos em Usuario são de mesmo nome que o Json do VIA cep vai mandar, vamos converter o json para uma instancia de Usuario:
                Usuario cepUsuarioReceptorJson = new Gson().fromJson(jsonCep.toString(), Usuario.class);
                if (cepUsuarioReceptorJson != null) {
                    getUsuario().setLogradouro(cepUsuarioReceptorJson.getLogradouro());
                    getUsuario().setComplemento(cepUsuarioReceptorJson.getComplemento());
                    getUsuario().setBairro(cepUsuarioReceptorJson.getBairro());
                    getUsuario().setLocalidade(cepUsuarioReceptorJson.getLocalidade());
                    getUsuario().setUf(cepUsuarioReceptorJson.getUf());
                    getUsuario().setIbge(cepUsuarioReceptorJson.getIbge());
                    getUsuario().setGia(cepUsuarioReceptorJson.getGia());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMsg("Erro ao consultar CEP!\n" + e.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Usuario> getListCharged() {
        try {
            setListUsuario(daoUsuario.listar(Usuario.class));
        } catch (Exception ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            mostrarMsg("Erro ao carregar Lista de Usuários!\n" + ex.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
        }

        return getListUsuario();
    }

    public List<Usuario> getListUsuario() {
        return listUsuario;
    }

    public void setListUsuario(List<Usuario> listUsuario) {
        this.listUsuario = listUsuario;
    }

}
