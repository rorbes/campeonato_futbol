/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mundo.Campeonato;

/**
 *
 * @author Ruben Orbes
 */
@Stateless
public class CampeonatoFacade extends AbstractFacade<Campeonato> {
    @PersistenceContext(unitName = "campeonatoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CampeonatoFacade() {
        super(Campeonato.class);
    }
    
}
