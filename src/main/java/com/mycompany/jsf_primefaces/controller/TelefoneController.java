/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.jsf_primefaces.controller;

import com.mycompany.jsf_primefaces.hibernate.dao.DAOTelefone;
import com.mycompany.jsf_primefaces.hibernate.dao.DAOUsuario;
import com.mycompany.jsf_primefaces.model.Telefone;
import com.mycompany.jsf_primefaces.model.Usuario;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author lucia
 */
@ManagedBean(name = "telefoneController")
@ViewScoped
public class TelefoneController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Telefone telefone = new Telefone();

    private Usuario usuario;

    @Inject
    private DAOUsuario daoUsuario;

    @Inject
    private DAOTelefone daoTelefone;

    /**
     * Método que é chamado após construir o managedBean
     */
    @PostConstruct
    public void initComponents() {
        try {
            String usuarioId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("usuarioId");
            if (usuarioId != null) {
                setUsuario(daoUsuario.consultar(Usuario.class, Long.parseLong(usuarioId)));
                System.out.println("\n\nCódigo Usuário: " + usuarioId + "\n\n");
            }
        } catch (Exception ex) {
            Logger.getLogger(TelefoneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getNomeSobrenomeUsuario() {
        if (usuario != null
                && usuario.getId() != null
                && usuario.getNome() != null
                && usuario.getSobrenome() != null) {
            StringBuilder str = new StringBuilder();
            str.append("(").append(usuario.getId().toString()).append(") ");
            str.append(usuario.getNome()).append(" ").append(usuario.getSobrenome());

            return str.toString();
        }

        return null;
    }

    public String salvar() {
        try {
            if (getTelefone() != null
                    && getUsuario() != null
                    && getUsuario().getId() != null) {
                getTelefone().setUsuario(getUsuario());
                setTelefone(daoTelefone.saveOrUpdate(telefone));
                mostrarMsg("Registro salvo com sucesso!", "Ok!", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception ex) {
            Logger.getLogger(TelefoneController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            mostrarMsg("Erro ao Salvar!\n" + ex.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
        }

        return "";
    }

    public String limpar() {
        setTelefone(new Telefone());
        return "";
    }

    private void mostrarMsg(String mensagem, String sumario, FacesMessage.Severity severity) {
        FacesMessage message = new FacesMessage(severity, sumario, mensagem);
        //Pode ser dado a mensagem sobre algum componente especifico ou null quando é geral:
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public String deletar() {
        try {
            if (getTelefone() != null
                    && getTelefone().getId() != null
                    && getUsuario() != null
                    && getUsuario().getId() != null) {
                daoTelefone.deletar(getTelefone());
                setTelefone(new Telefone());
                mostrarMsg("Registro removido com sucesso!", "Ok!", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception ex) {
            Logger.getLogger(TelefoneController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            mostrarMsg("Erro ao Deletar!\n" + ex.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
        }

        return "";
    }

    public List<Telefone> getChargedListTelefone() {
        List<Telefone> listTelefone = null;

        try {
            if (getUsuario() != null
                    && getUsuario().getId() != null) {
                listTelefone = daoTelefone.listarTelefones(getUsuario().getId());
            }
        } catch (Exception ex) {
            Logger.getLogger(TelefoneController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            mostrarMsg("Erro ao carregar Telefones!\n" + ex.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
        }

        return listTelefone;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public void setTelefone(Telefone telefone) {
        this.telefone = telefone;
    }

}
