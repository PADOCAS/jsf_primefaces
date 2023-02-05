/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.jsf_primefaces.datatablelazy;

import com.mycompany.jsf_primefaces.hibernate.dao.DAOUsuario;
import com.mycompany.jsf_primefaces.model.Usuario;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

/**
 *
 * @author lucia
 */
public class DataTableModelLazyUsuario<T> extends LazyDataModel<Usuario> {

    //Não vou usar o Lazy por enquanto.. não está injetando o DAOUsuario no LazyDataModel... ficando nulo o daoUsuario!!!
    @Inject
    private DAOUsuario daoUsuario;

    private List<Usuario> list;

    private String sql = " from Usuario ";

    @Override
    public int count(Map<String, FilterMeta> map) {
        return getList() == null || getList().isEmpty() ? 0 : getList().size();
    }

    @Override
    public List<Usuario> load(int first, int pagesize, Map<String, SortMeta> mapSorted, Map<String, FilterMeta> mapFilter) {
        setList(daoUsuario.getEntityManager().createQuery(getSql())
                .setFirstResult(first)
                .setMaxResults(pagesize)
                .getResultList());
        //Sempre restart o sql padrão para fazer o count
        setSql(" from Usuario ");

        setPageSize(pagesize);
        setRowCount(Integer.parseInt(daoUsuario.getEntityManager().createQuery("select count(1) " + getSql()).getSingleResult().toString()));

        return getList();
    }

    public void pesquisa(String campoPesquisa) {
        if (campoPesquisa != null
                && !campoPesquisa.trim().isEmpty()) {
            setSql(getSql() + " where lower(nome) like '%" + campoPesquisa.toLowerCase() + "%'");
        }
    }

    public List<Usuario> getList() {
        return list;
    }

    public void setList(List<Usuario> list) {
        this.list = list;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

}
