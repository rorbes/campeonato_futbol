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
import mundo.Partido;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mundo.Cancha;

/**
 *
 * @author Ruben Orbes
 */
public class CanchaJpaController implements Serializable {

    public CanchaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cancha cancha) throws RollbackFailureException, Exception {
        if (cancha.getPartidoList() == null) {
            cancha.setPartidoList(new ArrayList<Partido>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Partido> attachedPartidoList = new ArrayList<Partido>();
            for (Partido partidoListPartidoToAttach : cancha.getPartidoList()) {
                partidoListPartidoToAttach = em.getReference(partidoListPartidoToAttach.getClass(), partidoListPartidoToAttach.getIdPartido());
                attachedPartidoList.add(partidoListPartidoToAttach);
            }
            cancha.setPartidoList(attachedPartidoList);
            em.persist(cancha);
            for (Partido partidoListPartido : cancha.getPartidoList()) {
                Cancha oldIdCanchaOfPartidoListPartido = partidoListPartido.getIdCancha();
                partidoListPartido.setIdCancha(cancha);
                partidoListPartido = em.merge(partidoListPartido);
                if (oldIdCanchaOfPartidoListPartido != null) {
                    oldIdCanchaOfPartidoListPartido.getPartidoList().remove(partidoListPartido);
                    oldIdCanchaOfPartidoListPartido = em.merge(oldIdCanchaOfPartidoListPartido);
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

    public void edit(Cancha cancha) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cancha persistentCancha = em.find(Cancha.class, cancha.getIdCancha());
            List<Partido> partidoListOld = persistentCancha.getPartidoList();
            List<Partido> partidoListNew = cancha.getPartidoList();
            List<String> illegalOrphanMessages = null;
            for (Partido partidoListOldPartido : partidoListOld) {
                if (!partidoListNew.contains(partidoListOldPartido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Partido " + partidoListOldPartido + " since its idCancha field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Partido> attachedPartidoListNew = new ArrayList<Partido>();
            for (Partido partidoListNewPartidoToAttach : partidoListNew) {
                partidoListNewPartidoToAttach = em.getReference(partidoListNewPartidoToAttach.getClass(), partidoListNewPartidoToAttach.getIdPartido());
                attachedPartidoListNew.add(partidoListNewPartidoToAttach);
            }
            partidoListNew = attachedPartidoListNew;
            cancha.setPartidoList(partidoListNew);
            cancha = em.merge(cancha);
            for (Partido partidoListNewPartido : partidoListNew) {
                if (!partidoListOld.contains(partidoListNewPartido)) {
                    Cancha oldIdCanchaOfPartidoListNewPartido = partidoListNewPartido.getIdCancha();
                    partidoListNewPartido.setIdCancha(cancha);
                    partidoListNewPartido = em.merge(partidoListNewPartido);
                    if (oldIdCanchaOfPartidoListNewPartido != null && !oldIdCanchaOfPartidoListNewPartido.equals(cancha)) {
                        oldIdCanchaOfPartidoListNewPartido.getPartidoList().remove(partidoListNewPartido);
                        oldIdCanchaOfPartidoListNewPartido = em.merge(oldIdCanchaOfPartidoListNewPartido);
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
                Integer id = cancha.getIdCancha();
                if (findCancha(id) == null) {
                    throw new NonexistentEntityException("The cancha with id " + id + " no longer exists.");
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
            Cancha cancha;
            try {
                cancha = em.getReference(Cancha.class, id);
                cancha.getIdCancha();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cancha with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Partido> partidoListOrphanCheck = cancha.getPartidoList();
            for (Partido partidoListOrphanCheckPartido : partidoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cancha (" + cancha + ") cannot be destroyed since the Partido " + partidoListOrphanCheckPartido + " in its partidoList field has a non-nullable idCancha field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cancha);
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

    public List<Cancha> findCanchaEntities() {
        return findCanchaEntities(true, -1, -1);
    }

    public List<Cancha> findCanchaEntities(int maxResults, int firstResult) {
        return findCanchaEntities(false, maxResults, firstResult);
    }

    private List<Cancha> findCanchaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cancha.class));
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

    public Cancha findCancha(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cancha.class, id);
        } finally {
            em.close();
        }
    }

    public int getCanchaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cancha> rt = cq.from(Cancha.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
