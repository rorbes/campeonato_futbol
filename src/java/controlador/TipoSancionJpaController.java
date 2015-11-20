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
import mundo.Sancion;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mundo.TipoSancion;

/**
 *
 * @author Ruben Orbes
 */
public class TipoSancionJpaController implements Serializable {

    public TipoSancionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TipoSancion tipoSancion) throws RollbackFailureException, Exception {
        if (tipoSancion.getSancionList() == null) {
            tipoSancion.setSancionList(new ArrayList<Sancion>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Sancion> attachedSancionList = new ArrayList<Sancion>();
            for (Sancion sancionListSancionToAttach : tipoSancion.getSancionList()) {
                sancionListSancionToAttach = em.getReference(sancionListSancionToAttach.getClass(), sancionListSancionToAttach.getIdSanciones());
                attachedSancionList.add(sancionListSancionToAttach);
            }
            tipoSancion.setSancionList(attachedSancionList);
            em.persist(tipoSancion);
            for (Sancion sancionListSancion : tipoSancion.getSancionList()) {
                TipoSancion oldIdTipoSancionOfSancionListSancion = sancionListSancion.getIdTipoSancion();
                sancionListSancion.setIdTipoSancion(tipoSancion);
                sancionListSancion = em.merge(sancionListSancion);
                if (oldIdTipoSancionOfSancionListSancion != null) {
                    oldIdTipoSancionOfSancionListSancion.getSancionList().remove(sancionListSancion);
                    oldIdTipoSancionOfSancionListSancion = em.merge(oldIdTipoSancionOfSancionListSancion);
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

    public void edit(TipoSancion tipoSancion) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            TipoSancion persistentTipoSancion = em.find(TipoSancion.class, tipoSancion.getIdTipoSancion());
            List<Sancion> sancionListOld = persistentTipoSancion.getSancionList();
            List<Sancion> sancionListNew = tipoSancion.getSancionList();
            List<String> illegalOrphanMessages = null;
            for (Sancion sancionListOldSancion : sancionListOld) {
                if (!sancionListNew.contains(sancionListOldSancion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Sancion " + sancionListOldSancion + " since its idTipoSancion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Sancion> attachedSancionListNew = new ArrayList<Sancion>();
            for (Sancion sancionListNewSancionToAttach : sancionListNew) {
                sancionListNewSancionToAttach = em.getReference(sancionListNewSancionToAttach.getClass(), sancionListNewSancionToAttach.getIdSanciones());
                attachedSancionListNew.add(sancionListNewSancionToAttach);
            }
            sancionListNew = attachedSancionListNew;
            tipoSancion.setSancionList(sancionListNew);
            tipoSancion = em.merge(tipoSancion);
            for (Sancion sancionListNewSancion : sancionListNew) {
                if (!sancionListOld.contains(sancionListNewSancion)) {
                    TipoSancion oldIdTipoSancionOfSancionListNewSancion = sancionListNewSancion.getIdTipoSancion();
                    sancionListNewSancion.setIdTipoSancion(tipoSancion);
                    sancionListNewSancion = em.merge(sancionListNewSancion);
                    if (oldIdTipoSancionOfSancionListNewSancion != null && !oldIdTipoSancionOfSancionListNewSancion.equals(tipoSancion)) {
                        oldIdTipoSancionOfSancionListNewSancion.getSancionList().remove(sancionListNewSancion);
                        oldIdTipoSancionOfSancionListNewSancion = em.merge(oldIdTipoSancionOfSancionListNewSancion);
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
                Integer id = tipoSancion.getIdTipoSancion();
                if (findTipoSancion(id) == null) {
                    throw new NonexistentEntityException("The tipoSancion with id " + id + " no longer exists.");
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
            TipoSancion tipoSancion;
            try {
                tipoSancion = em.getReference(TipoSancion.class, id);
                tipoSancion.getIdTipoSancion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoSancion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Sancion> sancionListOrphanCheck = tipoSancion.getSancionList();
            for (Sancion sancionListOrphanCheckSancion : sancionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TipoSancion (" + tipoSancion + ") cannot be destroyed since the Sancion " + sancionListOrphanCheckSancion + " in its sancionList field has a non-nullable idTipoSancion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipoSancion);
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

    public List<TipoSancion> findTipoSancionEntities() {
        return findTipoSancionEntities(true, -1, -1);
    }

    public List<TipoSancion> findTipoSancionEntities(int maxResults, int firstResult) {
        return findTipoSancionEntities(false, maxResults, firstResult);
    }

    private List<TipoSancion> findTipoSancionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoSancion.class));
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

    public TipoSancion findTipoSancion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoSancion.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipoSancionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoSancion> rt = cq.from(TipoSancion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
