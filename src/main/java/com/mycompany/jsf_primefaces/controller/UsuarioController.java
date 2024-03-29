/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.jsf_primefaces.controller;

import com.google.gson.Gson;
import com.mycompany.jsf_primefaces.hibernate.dao.DAOEmail;
import com.mycompany.jsf_primefaces.hibernate.dao.DAOUsuario;
import com.mycompany.jsf_primefaces.model.Email;
import com.mycompany.jsf_primefaces.model.Usuario;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.animation.Animation;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;

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

    @Inject
    private DAOEmail daoEmail;

    private List<Usuario> listUsuario;

    private BarChartModel barChartModel;

    private Email email = new Email();

    private String pesquisaNome;

    @PostConstruct
    public void initComponents() {
        getListCharged();
        carregarGrafico(false);
        setPesquisaNome(null);
    }

    public String salvar() {
        try {
            if (getUsuario() != null) {
                //Iniciando as Listas (LAZY, caso estejam nulas.. se não da erro no hibernate
                if (getUsuario().getListEmail() == null) {
                    getUsuario().setListEmail(new ArrayList<>());
                }
            }
            daoUsuario.saveOrUpdate(usuario);
            setUsuario(new Usuario());
            getListCharged();
            setPesquisaNome(null);
            carregarGrafico(false);
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

    public void pesquisarUsuario() {
        try {
            setListUsuario(daoUsuario.pesquisarPorNome(getPesquisaNome()));
            carregarGrafico(true);
        } catch (Exception ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deletarEmail() {
        try {
            if (getUsuario() != null
                    && getEmail() != null) {
                daoEmail.deletar(getEmail());
                if (getUsuario().getListEmail() != null
                        && !getUsuario().getListEmail().isEmpty()) {
                    getUsuario().getListEmail().remove(getEmail());
                }
                //Após salvar, seta um novo email!
                setEmail(new Email());
                mostrarMsg("E-mail removido com sucesso!", "Ok!", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            mostrarMsg("Erro ao Remover E-mail!\n" + ex.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
        }
    }

    public void limpaEmail() {
        setEmail(new Email());
    }

    public void salvarEmail() {
        try {
            if (getUsuario() != null
                    && getEmail() != null) {
                if (getEmail().getEmailUser() != null
                        && !getEmail().getEmailUser().trim().isEmpty()) {
                    getEmail().setUsuario(getUsuario());
                    //Salva e seta o email com a chave já carregada!
                    setEmail(daoEmail.saveOrUpdate(getEmail()));

                    if (getEmail() != null
                            && getEmail().getId() != null) {
                        if (getUsuario().getListEmail() == null) {
                            getUsuario().setListEmail(new ArrayList<>());
                        }

                        if (getUsuario().getListEmail().isEmpty()) {
                            getUsuario().getListEmail().add(getEmail());
                        } else {
                            boolean existsId = false;

                            for (Email ema : getUsuario().getListEmail()) {
                                if (ema.getId() != null
                                        && ema.getId().compareTo(getEmail().getId()) == 0) {
                                    existsId = true;
                                    break;
                                }
                            }

                            if (!existsId) {
                                getUsuario().getListEmail().add(getEmail());
                            }
                        }
                        //Após salvar, seta um novo email!
                        setEmail(new Email());
                        mostrarMsg("E-mail salvo com sucesso!", "Ok!", FacesMessage.SEVERITY_INFO);
                    }
                } else {
                    mostrarMsg("E-mail deve ser informado!", "Atenção!", FacesMessage.SEVERITY_WARN);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            mostrarMsg("Erro ao Salvar E-mail!\n" + ex.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
        }
    }

    public String deletar() {
        try {
            if (getUsuario() != null
                    && getUsuario().getId() != null) {
                //Remover Telefone/Emails e Usuarios em cascata! Tabela dependente deve ser excluida primeiro!!
//                daoUsuario.removerUsuarioETelefonesCascata(getUsuario());
                //Modo para usar o framework e fazer por cascata e orphanRemoval automaticamente:
                daoUsuario.removerUsandoCascadeRemoveOrphanRemoval(getUsuario());
                setUsuario(new Usuario());
                getListCharged();
                setPesquisaNome(null);
                carregarGrafico(false);
                mostrarMsg("Registro removido com sucesso!", "Ok!", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();

            if (ex.getMessage() != null
                    && ex.getMessage().contains("org.hibernate.exception.ConstraintViolationException")) {
                mostrarMsg("Erro ao Deletar!\nExistem tabelas filhas referenciadas!\nNão é permitido a exclusão!", "ERRO!", FacesMessage.SEVERITY_ERROR);
            } else {
                mostrarMsg("Erro ao Deletar!\n" + ex.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
            }
        }
        return "";
    }

    public void uploadImagem(FileUploadEvent image) {
        if (getUsuario() != null) {
            //Converte imagem para base64:
            String imagemConvert64 = "data:image/png;base64," + DatatypeConverter.printBase64Binary(image.getFile().getContent());
            getUsuario().setImagem(imagemConvert64);
        }
    }

    public void downloadImagem() {
        try {
            if (FacesContext.getCurrentInstance() != null
                    && FacesContext.getCurrentInstance().getExternalContext() != null
                    && FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap() != null
                    && FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fileDownloadId") != null) {
                Long userId = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fileDownloadId"));
                Usuario usuarioCharged = daoUsuario.consultar(Usuario.class, userId);

                if (usuarioCharged != null
                        && usuarioCharged.getImagem() != null) {
                    byte[] imagem = new org.apache.tomcat.util.codec.binary.Base64().decode(usuarioCharged.getImagem().split("\\,")[1]);

                    //Dar a resposta para a página:
                    HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                    //Tive que usar o reset no response, pois ja estava chamando antes desse metodo.. talvez algo no filter coisa do tipo.. ai não dava certo!
                    response.reset();
                    response.setHeader("Content-Disposition", "attachment;filename=arquivo.png");
                    response.setContentType("application/octet-stream");
                    response.setContentLength(imagem.length);
                    response.getOutputStream().write(imagem);
                    response.getOutputStream().flush();
                    FacesContext.getCurrentInstance().responseComplete();
                } else {
                    mostrarMsg("Usuário sem imagem cadastrada!", "Atenção", FacesMessage.SEVERITY_WARN);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            mostrarMsg("Erro ao carregar imagem para download!\n" + ex.getMessage(), "ERRO", FacesMessage.SEVERITY_ERROR);
        }
    }

    private String getStrRgbaRandomColorGrafico(Boolean isBackground) {
        StringBuilder str = new StringBuilder();

        if (isBackground) {
            str.append("rgba(");
        } else {
            str.append("rgb(");
        }
        str.append(String.valueOf(Math.floor(Math.random() * 256)));
        str.append(", ").append(String.valueOf(Math.floor(Math.random() * 256)));
        str.append(", ").append(String.valueOf(Math.floor(Math.random() * 256)));

        if (isBackground) {
            str.append(", 0.2)");
        } else {
            str.append(")");
        }

        return str.toString();
    }

    public void carregarGrafico(Boolean carregaDaListaPesquisaNome) {
        setBarChartModel(new BarChartModel());

        try {
            List<Usuario> listUsuarioChart = null;

            if (carregaDaListaPesquisaNome != null
                    && carregaDaListaPesquisaNome) {
                listUsuarioChart = getListUsuario();
            } else {
                listUsuarioChart = daoUsuario.listar(Usuario.class);
            }

            if (listUsuarioChart != null
                    && !listUsuarioChart.isEmpty()) {
                ChartData data = new ChartData();

                BarChartDataSet barDataSet = new BarChartDataSet();
                barDataSet.setLabel("Salário dos usuários");

                List<Number> listSalarios = new ArrayList<>();
                List<String> listNomes = new ArrayList<>();
                List<String> borderColor = new ArrayList<>();
                List<String> bgColor = new ArrayList<>();

                for (Usuario usu : listUsuarioChart) {
                    listSalarios.add(usu.getSalario() == null ? Double.valueOf("0") : usu.getSalario());
                    listNomes.add(usu.getNome() + " " + usu.getSobrenome());
                    bgColor.add(getStrRgbaRandomColorGrafico(true));
                    borderColor.add("rgb(0, 0, 0)");
                }

                barDataSet.setBackgroundColor(bgColor);
                barDataSet.setBorderColor(borderColor);
                barDataSet.setBorderWidth(1);
                barDataSet.setData(listSalarios);
                data.setLabels(listNomes);
                data.addChartDataSet(barDataSet);

                getBarChartModel().setData(data);

                //Options
                BarChartOptions options = new BarChartOptions();
                CartesianScales cScales = new CartesianScales();
                CartesianLinearAxes linearAxes = new CartesianLinearAxes();
                linearAxes.setOffset(true);
                linearAxes.setBeginAtZero(true);
                CartesianLinearTicks ticks = new CartesianLinearTicks();
                linearAxes.setTicks(ticks);
                cScales.addYAxesData(linearAxes);
                options.setScales(cScales);

                Title title = new Title();
                title.setDisplay(false);
                title.setText("Salário dos Usuários");
                options.setTitle(title);

                Legend legend = new Legend();
                legend.setDisplay(true);
                legend.setPosition("top");
                LegendLabel legendLabels = new LegendLabel();
                legendLabels.setFontStyle("italic");
                legendLabels.setFontColor("#2980B9");
                legendLabels.setFontSize(24);
                legend.setLabels(legendLabels);
                options.setLegend(legend);

                // disable animation
                Animation animation = new Animation();
                animation.setDuration(0);
                options.setAnimation(animation);

                getBarChartModel().setOptions(options);
            }
        } catch (Exception ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            mostrarMsg("Erro ao carregar Gráfico!\n" + ex.getMessage(), "ERRO!", FacesMessage.SEVERITY_ERROR);
        }
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

    public String getNomeSobrenomeUsuario() {
        if (getUsuario() != null
                && getUsuario().getId() != null
                && getUsuario().getNome() != null
                && getUsuario().getSobrenome() != null) {
            StringBuilder str = new StringBuilder();
            str.append("(").append(getUsuario().getId().toString()).append(") ");
            str.append(getUsuario().getNome()).append(" ").append(getUsuario().getSobrenome());

            return str.toString();
        }

        return null;
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

    public BarChartModel getBarChartModel() {
        return barChartModel;
    }

    public void setBarChartModel(BarChartModel barChartModel) {
        this.barChartModel = barChartModel;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getPesquisaNome() {
        return pesquisaNome;
    }

    public void setPesquisaNome(String pesquisaNome) {
        this.pesquisaNome = pesquisaNome;
    }

}
