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
import mundo.Arbitro;
import mundo.Cancha;
import mundo.Equipo;
import mundo.Gol;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mundo.Partido;
import mundo.Sancion;

/**
 *
 * @author Ruben Orbes
 */
public class PartidoJpaController implements Serializable {

    public PartidoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Partido partido) throws RollbackFailureException, Exception {
        if (partido.getGolList() == null) {
            partido.setGolList(new ArrayList<Gol>());
        }
        if (partido.getSancionList() == null) {
            partido.setSancionList(new ArrayList<Sancion>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Arbitro juez3 = partido.getJuez3();
            if (juez3 != null) {
                juez3 = em.getReference(juez3.getClass(), juez3.getIdArbitro());
                partido.setJuez3(juez3);
            }
            Arbitro juez2 = partido.getJuez2();
            if (juez2 != null) {
                juez2 = em.getReference(juez2.getClass(), juez2.getIdArbitro());
                partido.setJuez2(juez2);
            }
            Arbitro juez1 = partido.getJuez1();
            if (juez1 != null) {
                juez1 = em.getReference(juez1.getClass(), juez1.getIdArbitro());
                partido.setJuez1(juez1);
            }
            Arbitro central = partido.getCentral();
            if (central != null) {
                central = em.getReference(central.getClass(), central.getIdArbitro());
                partido.setCentral(central);
            }
            Cancha idCancha = partido.getIdCancha();
            if (idCancha != null) {
                idCancha = em.getReference(idCancha.getClass(), idCancha.getIdCancha());
                partido.setIdCancha(idCancha);
            }
            Equipo visitante = partido.getVisitante();
            if (visitante != null) {
                visitante = em.getReference(visitante.getClass(), visitante.getIdEquipo());
                partido.setVisitante(visitante);
            }
            Equipo local = partido.getLocal();
            if (local != null) {
                local = em.getReference(local.getClass(), local.getIdEquipo());
                partido.setLocal(local);
            }
            List<Gol> attachedGolList = new ArrayList<Gol>();
            for (Gol golListGolToAttach : partido.getGolList()) {
                golListGolToAttach = em.getReference(golListGolToAttach.getClass(), golListGolToAttach.getIdGoles());
                attachedGolList.add(golListGolToAttach);
            }
            partido.setGolList(attachedGolList);
            List<Sancion> attachedSancionList = new ArrayList<Sancion>();
            for (Sancion sancionListSancionToAttach : partido.getSancionList()) {
                sancionListSancionToAttach = em.getReference(sancionListSancionToAttach.getClass(), sancionListSancionToAttach.getIdSanciones());
                attachedSancionList.add(sancionListSancionToAttach);
            }
            partido.setSancionList(attachedSancionList);
            em.persist(partido);
            if (juez3 != null) {
                juez3.getPartidoList().add(partido);
                juez3 = em.merge(juez3);
            }
            if (juez2 != null) {
                juez2.getPartidoList().add(partido);
                juez2 = em.merge(juez2);
            }
            if (juez1 != null) {
                juez1.getPartidoList().add(partido);
                juez1 = em.merge(juez1);
            }
            if (central != null) {
                central.getPartidoList().add(partido);
                central = em.merge(central);
            }
            if (idCancha != null) {
                idCancha.getPartidoList().add(partido);
                idCancha = em.merge(idCancha);
            }
            if (visitante != null) {
                visitante.getPartidoList().add(partido);
                visitante = em.merge(visitante);
            }
            if (local != null) {
                local.getPartidoList().add(partido);
                local = em.merge(local);
            }
            for (Gol golListGol : partido.getGolList()) {
                Partido oldIdPartidoOfGolListGol = golListGol.getIdPartido();
                golListGol.setIdPartido(partido);
                golListGol = em.merge(golListGol);
                if (oldIdPartidoOfGolListGol != null) {
                    oldIdPartidoOfGolListGol.getGolList().remove(golListGol);
                    oldIdPartidoOfGolListGol = em.merge(oldIdPartidoOfGolListGol);
                }
            }
            for (Sancion sancionListSancion : partido.getSancionList()) {
                Partido oldIdPartidoOfSancionListSancion = sancionListSancion.getIdPartido();
                sancionListSancion.setIdPartido(partido);
                sancionListSancion = em.merge(sancionListSancion);
                if (oldIdPartidoOfSancionListSancion != null) {
                    oldIdPartidoOfSancionListSancion.getSancionList().remove(sancionListSancion);
                    oldIdPartidoOfSancionListSancion = em.merge(oldIdPartidoOfSancionListSancion);
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

    public void edit(Partido partido) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Partido persistentPartido = em.find(Partido.class, partido.getIdPartido());
            Arbitro juez3Old = persistentPartido.getJuez3();
            Arbitro juez3New = partido.getJuez3();
            Arbitro juez2Old = persistentPartido.getJuez2();
            Arbitro juez2New = partido.getJuez2();
            Arbitro juez1Old = persistentPartido.getJuez1();
            Arbitro juez1New = partido.getJuez1();
            Arbitro centralOld = persistentPartido.getCentral();
            Arbitro centralNew = partido.getCentral();
            Cancha idCanchaOld = persistentPartido.getIdCancha();
            Cancha idCanchaNew = partido.getIdCancha();
            Equipo visitanteOld = persistentPartido.getVisitante();
            Equipo visitanteNew = partido.getVisitante();
            Equipo localOld = persistentPartido.getLocal();
            Equipo localNew = partido.getLocal();
            List<Gol> golListOld = persistentPartido.getGolList();
            List<Gol> golListNew = partido.getGolList();
            List<Sancion> sancionListOld = persistentPartido.getSancionList();
            List<Sancion> sancionListNew = partido.getSancionList();
            List<String> illegalOrphanMessages = null;
            for (Gol golListOldGol : golListOld) {
                if (!golListNew.contains(golListOldGol)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Gol " + golListOldGol + " since its idPartido field is not nullable.");
                }
            }
            for (Sancion sancionListOldSancion : sancionListOld) {
                if (!sancionListNew.contains(sancionListOldSancion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Sancion " + sancionListOldSancion + " since its idPartido field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (juez3New != null) {
                juez3New = em.getReference(juez3New.getClass(), juez3New.getIdArbitro());
                partido.setJuez3(juez3New);
            }
            if (juez2New != null) {
                juez2New = em.getReference(juez2New.getClass(), juez2New.getIdArbitro());
                partido.setJuez2(juez2New);
            }
            if (juez1New != null) {
                juez1New = em.getReference(juez1New.getClass(), juez1New.getIdArbitro());
                partido.setJuez1(juez1New);
            }
            if (centralNew != null) {
                centralNew = em.getReference(centralNew.getClass(), centralNew.getIdArbitro());
                partido.setCentral(centralNew);
            }
            if (idCanchaNew != null) {
                idCanchaNew = em.getReference(idCanchaNew.getClass(), idCanchaNew.getIdCancha());
                partido.setIdCancha(idCanchaNew);
            }
            if (visitanteNew != null) {
                visitanteNew = em.getReference(visitanteNew.getClass(), visitanteNew.getIdEquipo());
                partido.setVisitante(visitanteNew);
            }
            if (localNew != null) {
                localNew = em.getReference(localNew.getClass(), localNew.getIdEquipo());
                partido.setLocal(localNew);
            }
            List<Gol> attachedGolListNew = new ArrayList<Gol>();
            for (Gol golListNewGolToAttach : golListNew) {
                golListNewGolToAttach = em.getReference(golListNewGolToAttach.getClass(), golListNewGolToAttach.getIdGoles());
                attachedGolListNew.add(golListNewGolToAttach);
            }
            golListNew = attachedGolListNew;
            partido.setGolList(golListNew);
            List<Sancion> attachedSancionListNew = new ArrayList<Sancion>();
            for (Sancion sancionListNewSancionToAttach : sancionListNew) {
                sancionListNewSancionToAttach = em.getReference(sancionListNewSancionToAttach.getClass(), sancionListNewSancionToAttach.getIdSanciones());
                attachedSancionListNew.add(sancionListNewSancionToAttach);
            }
            sancionListNew = attachedSancionListNew;
            partido.setSancionList(sancionListNew);
            partido = em.merge(partido);
            if (juez3Old != null && !juez3Old.equals(juez3New)) {
                juez3Old.getPartidoList().remove(partido);
                juez3Old = em.merge(juez3Old);
            }
            if (juez3New != null && !juez3New.equals(juez3Old)) {
                juez3New.getPartidoList().add(partido);
                juez3New = em.merge(juez3New);
            }
            if (juez2Old != null && !juez2Old.equals(juez2New)) {
                juez2Old.getPartidoList().remove(partido);
                juez2Old = em.merge(juez2Old);
            }
            if (juez2New != null && !juez2New.equals(juez2Old)) {
                juez2New.getPartidoList().add(partido);
                juez2New = em.merge(juez2New);
            }
            if (juez1Old != null && !juez1Old.equals(juez1New)) {
                juez1Old.getPartidoList().remove(partido);
                juez1Old = em.merge(juez1Old);
            }
            if (juez1New != null && !juez1New.equals(juez1Old)) {
                juez1New.getPartidoList().add(partido);
                juez1New = em.merge(juez1New);
            }
            if (centralOld != null && !centralOld.equals(centralNew)) {
                centralOld.getPartidoList().remove(partido);
                centralOld = em.merge(centralOld);
            }
            if (centralNew != null && !centralNew.equals(centralOld)) {
                centralNew.getPartidoList().add(partido);
                centralNew = em.merge(centralNew);
            }
            if (idCanchaOld != null && !idCanchaOld.equals(idCanchaNew)) {
                idCanchaOld.getPartidoList().remove(partido);
                idCanchaOld = em.merge(idCanchaOld);
            }
            if (idCanchaNew != null && !idCanchaNew.equals(idCanchaOld)) {
                idCanchaNew.getPartidoList().add(partido);
                idCanchaNew = em.merge(idCanchaNew);
            }
            if (visitanteOld != null && !visitanteOld.equals(visitanteNew)) {
                visitanteOld.getPartidoList().remove(partido);
                visitanteOld = em.merge(visitanteOld);
            }
            if (visitanteNew != null && !visitanteNew.equals(visitanteOld)) {
                visitanteNew.getPartidoList().add(partido);
                visitanteNew = em.merge(visitanteNew);
            }
            if (localOld != null && !localOld.equals(localNew)) {
                localOld.getPartidoList().remove(partido);
                localOld = em.merge(localOld);
            }
            if (localNew != null && !localNew.equals(localOld)) {
                localNew.getPartidoList().add(partido);
                localNew = em.merge(localNew);
            }
            for (Gol golListNewGol : golListNew) {
                if (!golListOld.contains(golListNewGol)) {
                    Partido oldIdPartidoOfGolListNewGol = golListNewGol.getIdPartido();
                    golListNewGol.setIdPartido(partido);
                    golListNewGol = em.merge(golListNewGol);
                    if (oldIdPartidoOfGolListNewGol != null && !oldIdPartidoOfGolListNewGol.equals(partido)) {
                        oldIdPartidoOfGolListNewGol.getGolList().remove(golListNewGol);
                        oldIdPartidoOfGolListNewGol = em.merge(oldIdPartidoOfGolListNewGol);
                    }
                }
            }
            for (Sancion sancionListNewSancion : sancionListNew) {
                if (!sancionListOld.contains(sancionListNewSancion)) {
                    Partido oldIdPartidoOfSancionListNewSancion = sancionListNewSancion.getIdPartido();
                    sancionListNewSancion.setIdPartido(partido);
                    sancionListNewSancion = em.merge(sancionListNewSancion);
                    if (oldIdPartidoOfSancionListNewSancion != null && !oldIdPartidoOfSancionListNewSancion.equals(partido)) {
                        oldIdPartidoOfSancionListNewSancion.getSancionList().remove(sancionListNewSancion);
                        oldIdPartidoOfSancionListNewSancion = em.merge(oldIdPartidoOfSancionListNewSancion);
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
                Integer id = partido.getIdPartido();
                if (findPartido(id) == null) {
                    throw new NonexistentEntityException("The partido with id " + id + " no longer exists.");
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
            Partido partido;
            try {
                partido = em.getReference(Partido.class, id);
                partido.getIdPartido();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The partido with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Gol> golListOrphanCheck = partido.getGolList();
            for (Gol golListOrphanCheckGol : golListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Partido (" + partido + ") cannot be destroyed since the Gol " + golListOrphanCheckGol + " in its golList field has a non-nullable idPartido field.");
            }
            List<Sancion> sancionListOrphanCheck = partido.getSancionList();
            for (Sancion sancionListOrphanCheckSancion : sancionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Partido (" + partido + ") cannot be destroyed since the Sancion " + sancionListOrphanCheckSancion + " in its sancionList field has a non-nullable idPartido field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Arbitro juez3 = partido.getJuez3();
            if (juez3 != null) {
                juez3.getPartidoList().remove(partido);
                juez3 = em.merge(juez3);
            }
            Arbitro juez2 = partido.getJuez2();
            if (juez2 != null) {
                juez2.getPartidoList().remove(partido);
                juez2 = em.merge(juez2);
            }
            Arbitro juez1 = partido.getJuez1();
            if (juez1 != null) {
                juez1.getPartidoList().remove(partido);
                juez1 = em.merge(juez1);
            }
            Arbitro central = partido.getCentral();
            if (central != null) {
                central.getPartidoList().remove(partido);
                central = em.merge(central);
            }
            Cancha idCancha = partido.getIdCancha();
            if (idCancha != null) {
                idCancha.getPartidoList().remove(partido);
                idCancha = em.merge(idCancha);
            }
            Equipo visitante = partido.getVisitante();
            if (visitante != null) {
                visitante.getPartidoList().remove(partido);
                visitante = em.merge(visitante);
            }
            Equipo local = partido.getLocal();
            if (local != null) {
                local.getPartidoList().remove(partido);
                local = em.merge(local);
            }
            em.remove(partido);
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

    public List<Partido> findPartidoEntities() {
        return findPartidoEntities(true, -1, -1);
    }

    public List<Partido> findPartidoEntities(int maxResults, int firstResult) {
        return findPartidoEntities(false, maxResults, firstResult);
    }

    private List<Partido> findPartidoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Partido.class));
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

    public Partido findPartido(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Partido.class, id);
        } finally {
            em.close();
        }
    }

    public int getPartidoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Partido> rt = cq.from(Partido.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
