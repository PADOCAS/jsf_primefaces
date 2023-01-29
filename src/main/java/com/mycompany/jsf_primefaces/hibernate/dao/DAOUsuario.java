/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.jsf_primefaces.hibernate.dao;

import com.mycompany.jsf_primefaces.model.Usuario;
import javax.persistence.EntityTransaction;

/**
 *
 * @author lucia
 */
public class DAOUsuario extends DAOGenerico<Usuario> {

    public void removerUsandoCascadeRemoveOrphanRemoval(Usuario usuario) throws Exception {
        //Muito mais simples e garantido.. tudo por anotações nos VOS.. não precisa se preocupar em fazer manualmente cada tabela nova..
        EntityTransaction entityTransaction = getEntityManager().getTransaction();
        entityTransaction.begin();
        
        try {
            getEntityManager().remove(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            entityTransaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
            throw new Exception(e.getMessage());
        } finally {
            if (entityTransaction.isActive()) {
                entityTransaction.commit();
            }
            //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
            //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
            //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
        }
    }

    public void removerUsuarioETelefonesCascata(Usuario usuario) throws Exception {
        StringBuilder sqlDelTelefone = new StringBuilder();
        sqlDelTelefone.append(" DELETE FROM public.telefone WHERE usuario_id = :usuarioId");

        StringBuilder sqlDelEmail = new StringBuilder();
        sqlDelEmail.append(" DELETE FROM public.email WHERE usuario_id = :usuarioId");

        if (usuario != null
                && usuario.getId() != null) {
            EntityTransaction entityTransaction = getEntityManager().getTransaction();
            entityTransaction.begin();

            try {
                Object primaryKey = getJpaUtil().getPrimaryKey(usuario);

                if (primaryKey != null) {
                    //1) Deleta Telefones do Usuario:
                    getEntityManager().createNativeQuery(sqlDelTelefone.toString())
                            .setParameter("usuarioId", usuario.getId())
                            .executeUpdate();

                    //2) Deleta Emails do Usuario:
                    getEntityManager().createNativeQuery(sqlDelEmail.toString())
                            .setParameter("usuarioId", usuario.getId())
                            .executeUpdate();

                    //3) Deletando o Usuário após matar os telefones - manualmente por SQL:
                    getEntityManager().createNativeQuery("DELETE FROM " + usuario.getClass().getSimpleName().toLowerCase() + " WHERE ID = " + primaryKey).executeUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
                entityTransaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
                throw new Exception(e.getMessage());
            } finally {
                if (entityTransaction.isActive()) {
                    entityTransaction.commit();
                }
                //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
                //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
                //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
            }
        }
    }

}
