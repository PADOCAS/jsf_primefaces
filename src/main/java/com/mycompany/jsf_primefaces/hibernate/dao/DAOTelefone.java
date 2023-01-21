/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.jsf_primefaces.hibernate.dao;

import com.mycompany.jsf_primefaces.model.Telefone;
import java.util.List;
import javax.persistence.EntityTransaction;

/**
 *
 * @author lucia
 */
public class DAOTelefone extends DAOGenerico<Telefone> {

    public List<Telefone> listarTelefones(Long usuarioId) throws Exception {
        List<Telefone> list = null;

        if (usuarioId != null) {
            EntityTransaction transaction = getEntityManager().getTransaction();
            transaction.begin();

            try {
                list = getEntityManager().createQuery("from Telefone WHERE usuario_id = :usuarioId ORDER BY id ")
                        .setParameter("usuarioId", usuarioId)
                        .getResultList();
            } catch (Exception e) {
                e.printStackTrace();
                transaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
                throw new Exception(e.getMessage());
            } finally {
                if (transaction.isActive()) {
                    transaction.commit();
                }
                //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
                //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
                //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
            }
        }

        return list;
    }
}
