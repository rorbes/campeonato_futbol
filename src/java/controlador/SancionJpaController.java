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
import mundo.EquipoJugador;
import mundo.TipoSancion;
import mundo.Partido;
import mundo.Sancion;

/**
 *
 * @author Ruben Orbes
 */
public class SancionJpaController implements Serializable {

    public SancionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sancion sancion) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            EquipoJugador idEquipoJugador = sancion.getIdEquipoJugador();
            if (idEquipoJugador != null) {
                idEquipoJugador = em.getReference(idEquipoJugador.getClass(), idEquipoJugador.getIdCampeonatoEquipoJugador());
                sancion.setIdEquipoJugador(idEquipoJugador);
            }
            TipoSancion idTipoSancion = sancion.getIdTipoSancion();
            if (idTipoSancion != null) {
                idTipoSancion = em.getReference(idTipoSancion.getClass(), idTipoSancion.getIdTipoSancion());
                sancion.setIdTipoSancion(idTipoSancion);
            }
            Partido idPartido = sancion.getIdPartido();
            if (idPartido != null) {
                idPartido = em.getReference(idPartido.getClass(), idPartido.getIdPartido());
                sancion.setIdPartido(idPartido);
            }
            em.persist(sancion);
            if (idEquipoJugador != null) {
                idEquipoJugador.getSancionList().add(sancion);
                idEquipoJugador = em.merge(idEquipoJugador);
            }
            if (idTipoSancion != null) {
                idTipoSancion.getSancionList().add(sancion);
                idTipoSancion = em.merge(idTipoSancion);
            }
            if (idPartido != null) {
                idPartido.getSancionList().add(sancion);
                idPartido = em.merge(idPartido);
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

    public void edit(Sancion sancion) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Sancion persistentSancion = em.find(Sancion.class, sancion.getIdSanciones());
            EquipoJugador idEquipoJugadorOld = persistentSancion.getIdEquipoJugador();
            EquipoJugador idEquipoJugadorNew = sancion.getIdEquipoJugador();
            TipoSancion idTipoSancionOld = persistentSancion.getIdTipoSancion();
            TipoSancion idTipoSancionNew = sancion.getIdTipoSancion();
            Partido idPartidoOld = persistentSancion.getIdPartido();
            Partido idPartidoNew = sancion.getIdPartido();
            if (idEquipoJugadorNew != null) {
                idEquipoJugadorNew = em.getReference(idEquipoJugadorNew.getClass(), idEquipoJugadorNew.getIdCampeonatoEquipoJugador());
                sancion.setIdEquipoJugador(idEquipoJugadorNew);
            }
            if (idTipoSancionNew != null) {
                idTipoSancionNew = em.getReference(idTipoSancionNew.getClass(), idTipoSancionNew.getIdTipoSancion());
                sancion.setIdTipoSancion(idTipoSancionNew);
            }
            if (idPartidoNew != null) {
                idPartidoNew = em.getReference(idPartidoNew.getClass(), idPartidoNew.getIdPartido());
                sancion.setIdPartido(idPartidoNew);
            }
            sancion = em.merge(sancion);
            if (idEquipoJugadorOld != null && !idEquipoJugadorOld.equals(idEquipoJugadorNew)) {
                idEquipoJugadorOld.getSancionList().remove(sancion);
                idEquipoJugadorOld = em.merge(idEquipoJugadorOld);
            }
            if (idEquipoJugadorNew != null && !idEquipoJugadorNew.equals(idEquipoJugadorOld)) {
                idEquipoJugadorNew.getSancionList().add(sancion);
                idEquipoJugadorNew = em.merge(idEquipoJugadorNew);
            }
            if (idTipoSancionOld != null && !idTipoSancionOld.equals(idTipoSancionNew)) {
                idTipoSancionOld.getSancionList().remove(sancion);
                idTipoSancionOld = em.merge(idTipoSancionOld);
            }
            if (idTipoSancionNew != null && !idTipoSancionNew.equals(idTipoSancionOld)) {
                idTipoSancionNew.getSancionList().add(sancion);
                idTipoSancionNew = em.merge(idTipoSancionNew);
            }
            if (idPartidoOld != null && !idPartidoOld.equals(idPartidoNew)) {
                idPartidoOld.getSancionList().remove(sancion);
                idPartidoOld = em.merge(idPartidoOld);
            }
            if (idPartidoNew != null && !idPartidoNew.equals(idPartidoOld)) {
                idPartidoNew.getSancionList().add(sancion);
                idPartidoNew = em.merge(idPartidoNew);
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
                Integer id = sancion.getIdSanciones();
                if (findSancion(id) == null) {
                    throw new NonexistentEntityException("The sancion with id " + id + " no longer exists.");
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
            Sancion sancion;
            try {
                sancion = em.getReference(Sancion.class, id);
                sancion.getIdSanciones();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sancion with id " + id + " no longer exists.", enfe);
            }
            EquipoJugador idEquipoJugador = sancion.getIdEquipoJugador();
            if (idEquipoJugador != null) {
                idEquipoJugador.getSancionList().remove(sancion);
                idEquipoJugador = em.merge(idEquipoJugador);
            }
            TipoSancion idTipoSancion = sancion.getIdTipoSancion();
            if (idTipoSancion != null) {
                idTipoSancion.getSancionList().remove(sancion);
                idTipoSancion = em.merge(idTipoSancion);
            }
            Partido idPartido = sancion.getIdPartido();
            if (idPartido != null) {
                idPartido.getSancionList().remove(sancion);
                idPartido = em.merge(idPartido);
            }
            em.remove(sancion);
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

    public List<Sancion> findSancionEntities() {
        return findSancionEntities(true, -1, -1);
    }

    public List<Sancion> findSancionEntities(int maxResults, int firstResult) {
        return findSancionEntities(false, maxResults, firstResult);
    }

    private List<Sancion> findSancionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sancion.class));
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

    public Sancion findSancion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sancion.class, id);
        } finally {
            em.close();
        }
    }

    public int getSancionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sancion> rt = cq.from(Sancion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
