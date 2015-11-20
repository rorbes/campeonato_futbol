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
import mundo.EquipoJugador;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mundo.Jugador;

/**
 *
 * @author Ruben Orbes
 */
public class JugadorJpaController implements Serializable {

    public JugadorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Jugador jugador) throws RollbackFailureException, Exception {
        if (jugador.getEquipoJugadorList() == null) {
            jugador.setEquipoJugadorList(new ArrayList<EquipoJugador>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<EquipoJugador> attachedEquipoJugadorList = new ArrayList<EquipoJugador>();
            for (EquipoJugador equipoJugadorListEquipoJugadorToAttach : jugador.getEquipoJugadorList()) {
                equipoJugadorListEquipoJugadorToAttach = em.getReference(equipoJugadorListEquipoJugadorToAttach.getClass(), equipoJugadorListEquipoJugadorToAttach.getIdCampeonatoEquipoJugador());
                attachedEquipoJugadorList.add(equipoJugadorListEquipoJugadorToAttach);
            }
            jugador.setEquipoJugadorList(attachedEquipoJugadorList);
            em.persist(jugador);
            for (EquipoJugador equipoJugadorListEquipoJugador : jugador.getEquipoJugadorList()) {
                Jugador oldIdJugadorOfEquipoJugadorListEquipoJugador = equipoJugadorListEquipoJugador.getIdJugador();
                equipoJugadorListEquipoJugador.setIdJugador(jugador);
                equipoJugadorListEquipoJugador = em.merge(equipoJugadorListEquipoJugador);
                if (oldIdJugadorOfEquipoJugadorListEquipoJugador != null) {
                    oldIdJugadorOfEquipoJugadorListEquipoJugador.getEquipoJugadorList().remove(equipoJugadorListEquipoJugador);
                    oldIdJugadorOfEquipoJugadorListEquipoJugador = em.merge(oldIdJugadorOfEquipoJugadorListEquipoJugador);
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

    public void edit(Jugador jugador) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Jugador persistentJugador = em.find(Jugador.class, jugador.getIdJugador());
            List<EquipoJugador> equipoJugadorListOld = persistentJugador.getEquipoJugadorList();
            List<EquipoJugador> equipoJugadorListNew = jugador.getEquipoJugadorList();
            List<String> illegalOrphanMessages = null;
            for (EquipoJugador equipoJugadorListOldEquipoJugador : equipoJugadorListOld) {
                if (!equipoJugadorListNew.contains(equipoJugadorListOldEquipoJugador)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain EquipoJugador " + equipoJugadorListOldEquipoJugador + " since its idJugador field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<EquipoJugador> attachedEquipoJugadorListNew = new ArrayList<EquipoJugador>();
            for (EquipoJugador equipoJugadorListNewEquipoJugadorToAttach : equipoJugadorListNew) {
                equipoJugadorListNewEquipoJugadorToAttach = em.getReference(equipoJugadorListNewEquipoJugadorToAttach.getClass(), equipoJugadorListNewEquipoJugadorToAttach.getIdCampeonatoEquipoJugador());
                attachedEquipoJugadorListNew.add(equipoJugadorListNewEquipoJugadorToAttach);
            }
            equipoJugadorListNew = attachedEquipoJugadorListNew;
            jugador.setEquipoJugadorList(equipoJugadorListNew);
            jugador = em.merge(jugador);
            for (EquipoJugador equipoJugadorListNewEquipoJugador : equipoJugadorListNew) {
                if (!equipoJugadorListOld.contains(equipoJugadorListNewEquipoJugador)) {
                    Jugador oldIdJugadorOfEquipoJugadorListNewEquipoJugador = equipoJugadorListNewEquipoJugador.getIdJugador();
                    equipoJugadorListNewEquipoJugador.setIdJugador(jugador);
                    equipoJugadorListNewEquipoJugador = em.merge(equipoJugadorListNewEquipoJugador);
                    if (oldIdJugadorOfEquipoJugadorListNewEquipoJugador != null && !oldIdJugadorOfEquipoJugadorListNewEquipoJugador.equals(jugador)) {
                        oldIdJugadorOfEquipoJugadorListNewEquipoJugador.getEquipoJugadorList().remove(equipoJugadorListNewEquipoJugador);
                        oldIdJugadorOfEquipoJugadorListNewEquipoJugador = em.merge(oldIdJugadorOfEquipoJugadorListNewEquipoJugador);
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
                Integer id = jugador.getIdJugador();
                if (findJugador(id) == null) {
                    throw new NonexistentEntityException("The jugador with id " + id + " no longer exists.");
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
            Jugador jugador;
            try {
                jugador = em.getReference(Jugador.class, id);
                jugador.getIdJugador();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The jugador with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<EquipoJugador> equipoJugadorListOrphanCheck = jugador.getEquipoJugadorList();
            for (EquipoJugador equipoJugadorListOrphanCheckEquipoJugador : equipoJugadorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Jugador (" + jugador + ") cannot be destroyed since the EquipoJugador " + equipoJugadorListOrphanCheckEquipoJugador + " in its equipoJugadorList field has a non-nullable idJugador field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(jugador);
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

    public List<Jugador> findJugadorEntities() {
        return findJugadorEntities(true, -1, -1);
    }

    public List<Jugador> findJugadorEntities(int maxResults, int firstResult) {
        return findJugadorEntities(false, maxResults, firstResult);
    }

    private List<Jugador> findJugadorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Jugador.class));
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

    public Jugador findJugador(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Jugador.class, id);
        } finally {
            em.close();
        }
    }

    public int getJugadorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Jugador> rt = cq.from(Jugador.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
