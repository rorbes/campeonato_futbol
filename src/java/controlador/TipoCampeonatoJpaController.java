/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import controlador.exceptions.IllegalOrphanException;
import controlador.exceptions.NonexistentEntityException;
import controlador.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mundo.Campeonato;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mundo.TipoCampeonato;

/**
 *
 * @author Ruben Orbes
 */
public class TipoCampeonatoJpaController implements Serializable {

    public TipoCampeonatoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TipoCampeonato tipoCampeonato) throws RollbackFailureException, Exception {
        if (tipoCampeonato.getCampeonatoList() == null) {
            tipoCampeonato.setCampeonatoList(new ArrayList<Campeonato>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Campeonato> attachedCampeonatoList = new ArrayList<Campeonato>();
            for (Campeonato campeonatoListCampeonatoToAttach : tipoCampeonato.getCampeonatoList()) {
                campeonatoListCampeonatoToAttach = em.getReference(campeonatoListCampeonatoToAttach.getClass(), campeonatoListCampeonatoToAttach.getIdCampeonato());
                attachedCampeonatoList.add(campeonatoListCampeonatoToAttach);
            }
            tipoCampeonato.setCampeonatoList(attachedCampeonatoList);
            em.persist(tipoCampeonato);
            for (Campeonato campeonatoListCampeonato : tipoCampeonato.getCampeonatoList()) {
                TipoCampeonato oldIdTipoCampeonatoOfCampeonatoListCampeonato = campeonatoListCampeonato.getIdTipoCampeonato();
                campeonatoListCampeonato.setIdTipoCampeonato(tipoCampeonato);
                campeonatoListCampeonato = em.merge(campeonatoListCampeonato);
                if (oldIdTipoCampeonatoOfCampeonatoListCampeonato != null) {
                    oldIdTipoCampeonatoOfCampeonatoListCampeonato.getCampeonatoList().remove(campeonatoListCampeonato);
                    oldIdTipoCampeonatoOfCampeonatoListCampeonato = em.merge(oldIdTipoCampeonatoOfCampeonatoListCampeonato);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TipoCampeonato tipoCampeonato) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            TipoCampeonato persistentTipoCampeonato = em.find(TipoCampeonato.class, tipoCampeonato.getIdTipoCampeonato());
            List<Campeonato> campeonatoListOld = persistentTipoCampeonato.getCampeonatoList();
            List<Campeonato> campeonatoListNew = tipoCampeonato.getCampeonatoList();
            List<String> illegalOrphanMessages = null;
            for (Campeonato campeonatoListOldCampeonato : campeonatoListOld) {
                if (!campeonatoListNew.contains(campeonatoListOldCampeonato)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Campeonato " + campeonatoListOldCampeonato + " since its idTipoCampeonato field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Campeonato> attachedCampeonatoListNew = new ArrayList<Campeonato>();
            for (Campeonato campeonatoListNewCampeonatoToAttach : campeonatoListNew) {
                campeonatoListNewCampeonatoToAttach = em.getReference(campeonatoListNewCampeonatoToAttach.getClass(), campeonatoListNewCampeonatoToAttach.getIdCampeonato());
                attachedCampeonatoListNew.add(campeonatoListNewCampeonatoToAttach);
            }
            campeonatoListNew = attachedCampeonatoListNew;
            tipoCampeonato.setCampeonatoList(campeonatoListNew);
            tipoCampeonato = em.merge(tipoCampeonato);
            for (Campeonato campeonatoListNewCampeonato : campeonatoListNew) {
                if (!campeonatoListOld.contains(campeonatoListNewCampeonato)) {
                    TipoCampeonato oldIdTipoCampeonatoOfCampeonatoListNewCampeonato = campeonatoListNewCampeonato.getIdTipoCampeonato();
                    campeonatoListNewCampeonato.setIdTipoCampeonato(tipoCampeonato);
                    campeonatoListNewCampeonato = em.merge(campeonatoListNewCampeonato);
                    if (oldIdTipoCampeonatoOfCampeonatoListNewCampeonato != null && !oldIdTipoCampeonatoOfCampeonatoListNewCampeonato.equals(tipoCampeonato)) {
                        oldIdTipoCampeonatoOfCampeonatoListNewCampeonato.getCampeonatoList().remove(campeonatoListNewCampeonato);
                        oldIdTipoCampeonatoOfCampeonatoListNewCampeonato = em.merge(oldIdTipoCampeonatoOfCampeonatoListNewCampeonato);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tipoCampeonato.getIdTipoCampeonato();
                if (findTipoCampeonato(id) == null) {
                    throw new NonexistentEntityException("The tipoCampeonato with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            TipoCampeonato tipoCampeonato;
            try {
                tipoCampeonato = em.getReference(TipoCampeonato.class, id);
                tipoCampeonato.getIdTipoCampeonato();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoCampeonato with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Campeonato> campeonatoListOrphanCheck = tipoCampeonato.getCampeonatoList();
            for (Campeonato campeonatoListOrphanCheckCampeonato : campeonatoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TipoCampeonato (" + tipoCampeonato + ") cannot be destroyed since the Campeonato " + campeonatoListOrphanCheckCampeonato + " in its campeonatoList field has a non-nullable idTipoCampeonato field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipoCampeonato);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TipoCampeonato> findTipoCampeonatoEntities() {
        return findTipoCampeonatoEntities(true, -1, -1);
    }

    public List<TipoCampeonato> findTipoCampeonatoEntities(int maxResults, int firstResult) {
        return findTipoCampeonatoEntities(false, maxResults, firstResult);
    }

    private List<TipoCampeonato> findTipoCampeonatoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoCampeonato.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public TipoCampeonato findTipoCampeonato(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoCampeonato.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipoCampeonatoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoCampeonato> rt = cq.from(TipoCampeonato.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
