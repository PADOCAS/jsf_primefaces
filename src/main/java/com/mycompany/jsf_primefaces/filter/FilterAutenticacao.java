/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.jsf_primefaces.filter;

import com.mycompany.jsf_primefaces.hibernate.util.JPAUtil;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lucia
 */
//Usando as dependencias do javax.servlet
//Intercepta todas as requisições que vierem do projeto ou mapeamento
//Tudo que vier de dentro da pasta principal do webApp vai ser interceptado (urlPatterns = {"/principal/*"})
@WebFilter(filterName = "FilterAutenticacao", urlPatterns = {"/*"})
public class FilterAutenticacao implements Filter {

    @Inject
    private JPAUtil jpaUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            //Roda toda vez que interceptar algo:
            HttpServletRequest req = (HttpServletRequest) request;
            HttpSession session = req.getSession();
            //Url que está sendo acessada:
            String urlParaAutenticar = req.getServletPath(); //não pega a URL toda assim, ficando errado ao direcionar

            //Validar se está logado, se não redireciona para tela de login (pega o atributo do objeto session - "usuario"):
//            if (session.getAttribute("usuario") == null
//                    || ((Pessoa) session.getAttribute("usuario")).getLogin() == null
//                    || ((Pessoa) session.getAttribute("usuario")).getLogin().isEmpty()) {
//                RequestDispatcher redirecionar = request.getRequestDispatcher("/index.xhtml");
//                request.setAttribute("msg", "Por favor realize o LOGIN!");
//                redirecionar.forward(request, response);
//                //Return para parar a execução daqui pra frente e fazer o redirecionamento certinho!
//                return;
//            }

            //Necessário para completar o filtro e executar as ações do request e response... tem que passar isso abaixo ao final.. todas as regras são feitas acima:
            chain.doFilter(request, response);
        } catch (Exception ex) {
            //Qualquer exception que der em passagens pelo filtro, vai jogar para tela de ERRO!!! Muito bom!
            ex.printStackTrace();
            //Redirecionar para uma tela de erro (erro.jsp)
            RequestDispatcher redirecionar = request.getRequestDispatcher("/erro.xhtml");
            request.setAttribute("msg", ex.getMessage());
            redirecionar.forward(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //Roda ao iniciar o Sistema:
        //Já criar o entityManager ao iniciar, agilizando o sistema!
        jpaUtil.getEntityManager();
    }

    @Override
    public void destroy() {
        //Roda ao desimplantar o sistema:
    }

}
