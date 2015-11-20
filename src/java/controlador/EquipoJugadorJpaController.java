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
import mundo.Jugador;
import mundo.Equipo;
import mundo.Gol;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mundo.EquipoJugador;
import mundo.Sancion;

/**
 *
 * @author Ruben Orbes
 */
public class EquipoJugadorJpaController implements Serializable {

    public EquipoJugadorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(EquipoJugador equipoJugador) throws RollbackFailureException, Exception {
        if (equipoJugador.getGolList() == null) {
            equipoJugador.setGolList(new ArrayList<Gol>());
        }
        if (equipoJugador.getSancionList() == null) {
            equipoJugador.setSancionList(new ArrayList<Sancion>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Jugador idJugador = equipoJugador.getIdJugador();
            if (idJugador != null) {
                idJugador = em.getReference(idJugador.getClass(), idJugador.getIdJugador());
                equipoJugador.setIdJugador(idJugador);
            }
            Equipo idEquipo = equipoJugador.getIdEquipo();
            if (idEquipo != null) {
                idEquipo = em.getReference(idEquipo.getClass(), idEquipo.getIdEquipo());
                equipoJugador.setIdEquipo(idEquipo);
            }
            List<Gol> attachedGolList = new ArrayList<Gol>();
            for (Gol golListGolToAttach : equipoJugador.getGolList()) {
                golListGolToAttach = em.getReference(golListGolToAttach.getClass(), golListGolToAttach.getIdGoles());
                attachedGolList.add(golListGolToAttach);
            }
            equipoJugador.setGolList(attachedGolList);
            List<Sancion> attachedSancionList = new ArrayList<Sancion>();
            for (Sancion sancionListSancionToAttach : equipoJugador.getSancionList()) {
                sancionListSancionToAttach = em.getReference(sancionListSancionToAttach.getClass(), sancionListSancionToAttach.getIdSanciones());
                attachedSancionList.add(sancionListSancionToAttach);
            }
            equipoJugador.setSancionList(attachedSancionList);
            em.persist(equipoJugador);
            if (idJugador != null) {
                idJugador.getEquipoJugadorList().add(equipoJugador);
                idJugador = em.merge(idJugador);
            }
            if (idEquipo != null) {
                idEquipo.getEquipoJugadorList().add(equipoJugador);
                idEquipo = em.merge(idEquipo);
            }
            for (Gol golListGol : equipoJugador.getGolList()) {
                EquipoJugador oldIdEquipoJugadorOfGolListGol = golListGol.getIdEquipoJugador();
                golListGol.setIdEquipoJugador(equipoJugador);
                golListGol = em.merge(golListGol);
                if (oldIdEquipoJugadorOfGolListGol != null) {
                    oldIdEquipoJugadorOfGolListGol.getGolList().remove(golListGol);
                    oldIdEquipoJugadorOfGolListGol = em.merge(oldIdEquipoJugadorOfGolListGol);
                }
            }
            for (Sancion sancionListSancion : equipoJugador.getSancionList()) {
                EquipoJugador oldIdEquipoJugadorOfSancionListSancion = sancionListSancion.getIdEquipoJugador();
                sancionListSancion.setIdEquipoJugador(equipoJugador);
                sancionListSancion = em.merge(sancionListSancion);
                if (oldIdEquipoJugadorOfSancionListSancion != null) {
                    oldIdEquipoJugadorOfSancionListSancion.getSancionList().remove(sancionListSancion);
                    oldIdEquipoJugadorOfSancionListSancion = em.merge(oldIdEquipoJugadorOfSancionListSancion);
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

    public void edit(EquipoJugador equipoJugador) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            EquipoJugador persistentEquipoJugador = em.find(EquipoJugador.class, equipoJugador.getIdCampeonatoEquipoJugador());
            Jugador idJugadorOld = persistentEquipoJugador.getIdJugador();
            Jugador idJugadorNew = equipoJugador.getIdJugador();
            Equipo idEquipoOld = persistentEquipoJugador.getIdEquipo();
            Equipo idEquipoNew = equipoJugador.getIdEquipo();
            List<Gol> golListOld = persistentEquipoJugador.getGolList();
            List<Gol> golListNew = equipoJugador.getGolList();
            List<Sancion> sancionListOld = persistentEquipoJugador.getSancionList();
            List<Sancion> sancionListNew = equipoJugador.getSancionList();
            List<String> illegalOrphanMessages = null;
            for (Gol golListOldGol : golListOld) {
                if (!golListNew.contains(golListOldGol)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Gol " + golListOldGol + " since its idEquipoJugador field is not nullable.");
                }
            }
            for (Sancion sancionListOldSancion : sancionListOld) {
                if (!sancionListNew.contains(sancionListOldSancion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Sancion " + sancionListOldSancion + " since its idEquipoJugador field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idJugadorNew != null) {
                idJugadorNew = em.getReference(idJugadorNew.getClass(), idJugadorNew.getIdJugador());
                equipoJugador.setIdJugador(idJugadorNew);
            }
            if (idEquipoNew != null) {
                idEquipoNew = em.getReference(idEquipoNew.getClass(), idEquipoNew.getIdEquipo());
                equipoJugador.setIdEquipo(idEquipoNew);
            }
            List<Gol> attachedGolListNew = new ArrayList<Gol>();
            for (Gol golListNewGolToAttach : golListNew) {
                golListNewGolToAttach = em.getReference(golListNewGolToAttach.getClass(), golListNewGolToAttach.getIdGoles());
                attachedGolListNew.add(golListNewGolToAttach);
            }
            golListNew = attachedGolListNew;
            equipoJugador.setGolList(golListNew);
            List<Sancion> attachedSancionListNew = new ArrayList<Sancion>();
            for (Sancion sancionListNewSancionToAttach : sancionListNew) {
                sancionListNewSancionToAttach = em.getReference(sancionListNewSancionToAttach.getClass(), sancionListNewSancionToAttach.getIdSanciones());
                attachedSancionListNew.add(sancionListNewSancionToAttach);
            }
            sancionListNew = attachedSancionListNew;
            equipoJugador.setSancionList(sancionListNew);
            equipoJugador = em.merge(equipoJugador);
            if (idJugadorOld != null && !idJugadorOld.equals(idJugadorNew)) {
                idJugadorOld.getEquipoJugadorList().remove(equipoJugador);
                idJugadorOld = em.merge(idJugadorOld);
            }
            if (idJugadorNew != null && !idJugadorNew.equals(idJugadorOld)) {
                idJugadorNew.getEquipoJugadorList().add(equipoJugador);
                idJugadorNew = em.merge(idJugadorNew);
            }
            if (idEquipoOld != null && !idEquipoOld.equals(idEquipoNew)) {
                idEquipoOld.getEquipoJugadorList().remove(equipoJugador);
                idEquipoOld = em.merge(idEquipoOld);
            }
            if (idEquipoNew != null && !idEquipoNew.equals(idEquipoOld)) {
                idEquipoNew.getEquipoJugadorList().add(equipoJugador);
                idEquipoNew = em.merge(idEquipoNew);
            }
            for (Gol golListNewGol : golListNew) {
                if (!golListOld.contains(golListNewGol)) {
                    EquipoJugador oldIdEquipoJugadorOfGolListNewGol = golListNewGol.getIdEquipoJugador();
                    golListNewGol.setIdEquipoJugador(equipoJugador);
                    golListNewGol = em.merge(golListNewGol);
                    if (oldIdEquipoJugadorOfGolListNewGol != null && !oldIdEquipoJugadorOfGolListNewGol.equals(equipoJugador)) {
                        oldIdEquipoJugadorOfGolListNewGol.getGolList().remove(golListNewGol);
                        oldIdEquipoJugadorOfGolListNewGol = em.merge(oldIdEquipoJugadorOfGolListNewGol);
                    }
                }
            }
            for (Sancion sancionListNewSancion : sancionListNew) {
                if (!sancionListOld.contains(sancionListNewSancion)) {
                    EquipoJugador oldIdEquipoJugadorOfSancionListNewSancion = sancionListNewSancion.getIdEquipoJugador();
                    sancionListNewSancion.setIdEquipoJugador(equipoJugador);
                    sancionListNewSancion = em.merge(sancionListNewSancion);
                    if (oldIdEquipoJugadorOfSancionListNewSancion != null && !oldIdEquipoJugadorOfSancionListNewSancion.equals(equipoJugador)) {
                        oldIdEquipoJugadorOfSancionListNewSancion.getSancionList().remove(sancionListNewSancion);
                        oldIdEquipoJugadorOfSancionListNewSancion = em.merge(oldIdEquipoJugadorOfSancionListNewSancion);
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
                Integer id = equipoJugador.getIdCampeonatoEquipoJugador();
                if (findEquipoJugador(id) == null) {
                    throw new NonexistentEntityException("The equipoJugador with id " + id + " no longer exists.");
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
            EquipoJugador equipoJugador;
            try {
                equipoJugador = em.getReference(EquipoJugador.class, id);
                equipoJugador.getIdCampeonatoEquipoJugador();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The equipoJugador with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Gol> golListOrphanCheck = equipoJugador.getGolList();
            for (Gol golListOrphanCheckGol : golListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This EquipoJugador (" + equipoJugador + ") cannot be destroyed since the Gol " + golListOrphanCheckGol + " in its golList field has a non-nullable idEquipoJugador field.");
            }
            List<Sancion> sancionListOrphanCheck = equipoJugador.getSancionList();
            for (Sancion sancionListOrphanCheckSancion : sancionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This EquipoJugador (" + equipoJugador + ") cannot be destroyed since the Sancion " + sancionListOrphanCheckSancion + " in its sancionList field has a non-nullable idEquipoJugador field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Jugador idJugador = equipoJugador.getIdJugador();
            if (idJugador != null) {
                idJugador.getEquipoJugadorList().remove(equipoJugador);
                idJugador = em.merge(idJugador);
            }
            Equipo idEquipo = equipoJugador.getIdEquipo();
            if (idEquipo != null) {
                idEquipo.getEquipoJugadorList().remove(equipoJugador);
                idEquipo = em.merge(idEquipo);
            }
            em.remove(equipoJugador);
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

    public List<EquipoJugador> findEquipoJugadorEntities() {
        return findEquipoJugadorEntities(true, -1, -1);
    }

    public List<EquipoJugador> findEquipoJugadorEntities(int maxResults, int firstResult) {
        return findEquipoJugadorEntities(false, maxResults, firstResult);
    }

    private List<EquipoJugador> findEquipoJugadorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(EquipoJugador.class));
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

    public EquipoJugador findEquipoJugador(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(EquipoJugador.class, id);
        } finally {
            em.close();
        }
    }

    public int getEquipoJugadorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<EquipoJugador> rt = cq.from(EquipoJugador.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
