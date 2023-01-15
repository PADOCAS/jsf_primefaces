/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.jsf_primefaces.controller;

import com.mycompany.jsf_primefaces.hibernate.dao.DAOGenerico;
import com.mycompany.jsf_primefaces.model.Usuario;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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
    private DAOGenerico<Usuario> daoGenerico;

    private List<Usuario> listUsuario;

    public String salvar() {
        try {
            setUsuario(daoGenerico.saveOrUpdate(usuario));
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
                daoGenerico.deletar(getUsuario());
                setUsuario(new Usuario());
                mostrarMsg("Registro removido com sucesso!", "Ok!", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            mostrarMsg("Erro ao Deletar!\n" + ex.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
        }
        return "";
    }

    private void mostrarMsg(String mensagem, String sumario, FacesMessage.Severity severity) {
        FacesMessage message = new FacesMessage(severity, sumario, mensagem);
        //Pode ser dado a mensagem sobre algum componente especifico ou null quando é geral:
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Usuario> getListCharged() {
        try {
            setListUsuario(daoGenerico.listar(Usuario.class));
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
