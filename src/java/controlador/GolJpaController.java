/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import controlador.exceptions.NonexistentEntityException;
import controlador.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import mundo.Partido;
import mundo.EquipoJugador;
import mundo.Gol;

/**
 *
 * @author Ruben Orbes
 */
public class GolJpaController implements Serializable {

    public GolJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Gol gol) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Partido idPartido = gol.getIdPartido();
            if (idPartido != null) {
                idPartido = em.getReference(idPartido.getClass(), idPartido.getIdPartido());
                gol.setIdPartido(idPartido);
            }
            EquipoJugador idEquipoJugador = gol.getIdEquipoJugador();
            if (idEquipoJugador != null) {
                idEquipoJugador = em.getReference(idEquipoJugador.getClass(), idEquipoJugador.getIdCampeonatoEquipoJugador());
                gol.setIdEquipoJugador(idEquipoJugador);
            }
            em.persist(gol);
            if (idPartido != null) {
                idPartido.getGolList().add(gol);
                idPartido = em.merge(idPartido);
            }
            if (idEquipoJugador != null) {
                idEquipoJugador.getGolList().add(gol);
                idEquipoJugador = em.merge(idEquipoJugador);
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

    public void edit(Gol gol) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Gol persistentGol = em.find(Gol.class, gol.getIdGoles());
            Partido idPartidoOld = persistentGol.getIdPartido();
            Partido idPartidoNew = gol.getIdPartido();
            EquipoJugador idEquipoJugadorOld = persistentGol.getIdEquipoJugador();
            EquipoJugador idEquipoJugadorNew = gol.getIdEquipoJugador();
            if (idPartidoNew != null) {
                idPartidoNew = em.getReference(idPartidoNew.getClass(), idPartidoNew.getIdPartido());
                gol.setIdPartido(idPartidoNew);
            }
            if (idEquipoJugadorNew != null) {
                idEquipoJugadorNew = em.getReference(idEquipoJugadorNew.getClass(), idEquipoJugadorNew.getIdCampeonatoEquipoJugador());
                gol.setIdEquipoJugador(idEquipoJugadorNew);
            }
            gol = em.merge(gol);
            if (idPartidoOld != null && !idPartidoOld.equals(idPartidoNew)) {
                idPartidoOld.getGolList().remove(gol);
                idPartidoOld = em.merge(idPartidoOld);
            }
            if (idPartidoNew != null && !idPartidoNew.equals(idPartidoOld)) {
                idPartidoNew.getGolList().add(gol);
                idPartidoNew = em.merge(idPartidoNew);
            }
            if (idEquipoJugadorOld != null && !idEquipoJugadorOld.equals(idEquipoJugadorNew)) {
                idEquipoJugadorOld.getGolList().remove(gol);
                idEquipoJugadorOld = em.merge(idEquipoJugadorOld);
            }
            if (idEquipoJugadorNew != null && !idEquipoJugadorNew.equals(idEquipoJugadorOld)) {
                idEquipoJugadorNew.getGolList().add(gol);
                idEquipoJugadorNew = em.merge(idEquipoJugadorNew);
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
                Integer id = gol.getIdGoles();
                if (findGol(id) == null) {
                    throw new NonexistentEntityException("The gol with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Gol gol;
            try {
                gol = em.getReference(Gol.class, id);
                gol.getIdGoles();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The gol with id " + id + " no longer exists.", enfe);
            }
            Partido idPartido = gol.getIdPartido();
            if (idPartido != null) {
                idPartido.getGolList().remove(gol);
                idPartido = em.merge(idPartido);
            }
            EquipoJugador idEquipoJugador = gol.getIdEquipoJugador();
            if (idEquipoJugador != null) {
                idEquipoJugador.getGolList().remove(gol);
                idEquipoJugador = em.merge(idEquipoJugador);
            }
            em.remove(gol);
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

    public List<Gol> findGolEntities() {
        return findGolEntities(true, -1, -1);
    }

    public List<Gol> findGolEntities(int maxResults, int firstResult) {
        return findGolEntities(false, maxResults, firstResult);
    }

    private List<Gol> findGolEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Gol.class));
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

    public Gol findGol(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Gol.class, id);
        } finally {
            em.close();
        }
    }

    public int getGolCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Gol> rt = cq.from(Gol.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
