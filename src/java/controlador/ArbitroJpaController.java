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
import mundo.Arbitro;

/**
 *
 * @author Ruben Orbes
 */
public class ArbitroJpaController implements Serializable {

    public ArbitroJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Arbitro arbitro) throws RollbackFailureException, Exception {
        if (arbitro.getPartidoList() == null) {
            arbitro.setPartidoList(new ArrayList<Partido>());
        }
        if (arbitro.getPartidoList1() == null) {
            arbitro.setPartidoList1(new ArrayList<Partido>());
        }
        if (arbitro.getPartidoList2() == null) {
            arbitro.setPartidoList2(new ArrayList<Partido>());
        }
        if (arbitro.getPartidoList3() == null) {
            arbitro.setPartidoList3(new ArrayList<Partido>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Partido> attachedPartidoList = new ArrayList<Partido>();
            for (Partido partidoListPartidoToAttach : arbitro.getPartidoList()) {
                partidoListPartidoToAttach = em.getReference(partidoListPartidoToAttach.getClass(), partidoListPartidoToAttach.getIdPartido());
                attachedPartidoList.add(partidoListPartidoToAttach);
            }
            arbitro.setPartidoList(attachedPartidoList);
            List<Partido> attachedPartidoList1 = new ArrayList<Partido>();
            for (Partido partidoList1PartidoToAttach : arbitro.getPartidoList1()) {
                partidoList1PartidoToAttach = em.getReference(partidoList1PartidoToAttach.getClass(), partidoList1PartidoToAttach.getIdPartido());
                attachedPartidoList1.add(partidoList1PartidoToAttach);
            }
            arbitro.setPartidoList1(attachedPartidoList1);
            List<Partido> attachedPartidoList2 = new ArrayList<Partido>();
            for (Partido partidoList2PartidoToAttach : arbitro.getPartidoList2()) {
                partidoList2PartidoToAttach = em.getReference(partidoList2PartidoToAttach.getClass(), partidoList2PartidoToAttach.getIdPartido());
                attachedPartidoList2.add(partidoList2PartidoToAttach);
            }
            arbitro.setPartidoList2(attachedPartidoList2);
            List<Partido> attachedPartidoList3 = new ArrayList<Partido>();
            for (Partido partidoList3PartidoToAttach : arbitro.getPartidoList3()) {
                partidoList3PartidoToAttach = em.getReference(partidoList3PartidoToAttach.getClass(), partidoList3PartidoToAttach.getIdPartido());
                attachedPartidoList3.add(partidoList3PartidoToAttach);
            }
            arbitro.setPartidoList3(attachedPartidoList3);
            em.persist(arbitro);
            for (Partido partidoListPartido : arbitro.getPartidoList()) {
                Arbitro oldJuez3OfPartidoListPartido = partidoListPartido.getJuez3();
                partidoListPartido.setJuez3(arbitro);
                partidoListPartido = em.merge(partidoListPartido);
                if (oldJuez3OfPartidoListPartido != null) {
                    oldJuez3OfPartidoListPartido.getPartidoList().remove(partidoListPartido);
                    oldJuez3OfPartidoListPartido = em.merge(oldJuez3OfPartidoListPartido);
                }
            }
            for (Partido partidoList1Partido : arbitro.getPartidoList1()) {
                Arbitro oldJuez2OfPartidoList1Partido = partidoList1Partido.getJuez2();
                partidoList1Partido.setJuez2(arbitro);
                partidoList1Partido = em.merge(partidoList1Partido);
                if (oldJuez2OfPartidoList1Partido != null) {
                    oldJuez2OfPartidoList1Partido.getPartidoList1().remove(partidoList1Partido);
                    oldJuez2OfPartidoList1Partido = em.merge(oldJuez2OfPartidoList1Partido);
                }
            }
            for (Partido partidoList2Partido : arbitro.getPartidoList2()) {
                Arbitro oldJuez1OfPartidoList2Partido = partidoList2Partido.getJuez1();
                partidoList2Partido.setJuez1(arbitro);
                partidoList2Partido = em.merge(partidoList2Partido);
                if (oldJuez1OfPartidoList2Partido != null) {
                    oldJuez1OfPartidoList2Partido.getPartidoList2().remove(partidoList2Partido);
                    oldJuez1OfPartidoList2Partido = em.merge(oldJuez1OfPartidoList2Partido);
                }
            }
            for (Partido partidoList3Partido : arbitro.getPartidoList3()) {
                Arbitro oldCentralOfPartidoList3Partido = partidoList3Partido.getCentral();
                partidoList3Partido.setCentral(arbitro);
                partidoList3Partido = em.merge(partidoList3Partido);
                if (oldCentralOfPartidoList3Partido != null) {
                    oldCentralOfPartidoList3Partido.getPartidoList3().remove(partidoList3Partido);
                    oldCentralOfPartidoList3Partido = em.merge(oldCentralOfPartidoList3Partido);
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

    public void edit(Arbitro arbitro) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Arbitro persistentArbitro = em.find(Arbitro.class, arbitro.getIdArbitro());
            List<Partido> partidoListOld = persistentArbitro.getPartidoList();
            List<Partido> partidoListNew = arbitro.getPartidoList();
            List<Partido> partidoList1Old = persistentArbitro.getPartidoList1();
            List<Partido> partidoList1New = arbitro.getPartidoList1();
            List<Partido> partidoList2Old = persistentArbitro.getPartidoList2();
            List<Partido> partidoList2New = arbitro.getPartidoList2();
            List<Partido> partidoList3Old = persistentArbitro.getPartidoList3();
            List<Partido> partidoList3New = arbitro.getPartidoList3();
            List<String> illegalOrphanMessages = null;
            for (Partido partidoListOldPartido : partidoListOld) {
                if (!partidoListNew.contains(partidoListOldPartido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Partido " + partidoListOldPartido + " since its juez3 field is not nullable.");
                }
            }
            for (Partido partidoList1OldPartido : partidoList1Old) {
                if (!partidoList1New.contains(partidoList1OldPartido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Partido " + partidoList1OldPartido + " since its juez2 field is not nullable.");
                }
            }
            for (Partido partidoList2OldPartido : partidoList2Old) {
                if (!partidoList2New.contains(partidoList2OldPartido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Partido " + partidoList2OldPartido + " since its juez1 field is not nullable.");
                }
            }
            for (Partido partidoList3OldPartido : partidoList3Old) {
                if (!partidoList3New.contains(partidoList3OldPartido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Partido " + partidoList3OldPartido + " since its central field is not nullable.");
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
            arbitro.setPartidoList(partidoListNew);
            List<Partido> attachedPartidoList1New = new ArrayList<Partido>();
            for (Partido partidoList1NewPartidoToAttach : partidoList1New) {
                partidoList1NewPartidoToAttach = em.getReference(partidoList1NewPartidoToAttach.getClass(), partidoList1NewPartidoToAttach.getIdPartido());
                attachedPartidoList1New.add(partidoList1NewPartidoToAttach);
            }
            partidoList1New = attachedPartidoList1New;
            arbitro.setPartidoList1(partidoList1New);
            List<Partido> attachedPartidoList2New = new ArrayList<Partido>();
            for (Partido partidoList2NewPartidoToAttach : partidoList2New) {
                partidoList2NewPartidoToAttach = em.getReference(partidoList2NewPartidoToAttach.getClass(), partidoList2NewPartidoToAttach.getIdPartido());
                attachedPartidoList2New.add(partidoList2NewPartidoToAttach);
            }
            partidoList2New = attachedPartidoList2New;
            arbitro.setPartidoList2(partidoList2New);
            List<Partido> attachedPartidoList3New = new ArrayList<Partido>();
            for (Partido partidoList3NewPartidoToAttach : partidoList3New) {
                partidoList3NewPartidoToAttach = em.getReference(partidoList3NewPartidoToAttach.getClass(), partidoList3NewPartidoToAttach.getIdPartido());
                attachedPartidoList3New.add(partidoList3NewPartidoToAttach);
            }
            partidoList3New = attachedPartidoList3New;
            arbitro.setPartidoList3(partidoList3New);
            arbitro = em.merge(arbitro);
            for (Partido partidoListNewPartido : partidoListNew) {
                if (!partidoListOld.contains(partidoListNewPartido)) {
                    Arbitro oldJuez3OfPartidoListNewPartido = partidoListNewPartido.getJuez3();
                    partidoListNewPartido.setJuez3(arbitro);
                    partidoListNewPartido = em.merge(partidoListNewPartido);
                    if (oldJuez3OfPartidoListNewPartido != null && !oldJuez3OfPartidoListNewPartido.equals(arbitro)) {
                        oldJuez3OfPartidoListNewPartido.getPartidoList().remove(partidoListNewPartido);
                        oldJuez3OfPartidoListNewPartido = em.merge(oldJuez3OfPartidoListNewPartido);
                    }
                }
            }
            for (Partido partidoList1NewPartido : partidoList1New) {
                if (!partidoList1Old.contains(partidoList1NewPartido)) {
                    Arbitro oldJuez2OfPartidoList1NewPartido = partidoList1NewPartido.getJuez2();
                    partidoList1NewPartido.setJuez2(arbitro);
                    partidoList1NewPartido = em.merge(partidoList1NewPartido);
                    if (oldJuez2OfPartidoList1NewPartido != null && !oldJuez2OfPartidoList1NewPartido.equals(arbitro)) {
                        oldJuez2OfPartidoList1NewPartido.getPartidoList1().remove(partidoList1NewPartido);
                        oldJuez2OfPartidoList1NewPartido = em.merge(oldJuez2OfPartidoList1NewPartido);
                    }
                }
            }
            for (Partido partidoList2NewPartido : partidoList2New) {
                if (!partidoList2Old.contains(partidoList2NewPartido)) {
                    Arbitro oldJuez1OfPartidoList2NewPartido = partidoList2NewPartido.getJuez1();
                    partidoList2NewPartido.setJuez1(arbitro);
                    partidoList2NewPartido = em.merge(partidoList2NewPartido);
                    if (oldJuez1OfPartidoList2NewPartido != null && !oldJuez1OfPartidoList2NewPartido.equals(arbitro)) {
                        oldJuez1OfPartidoList2NewPartido.getPartidoList2().remove(partidoList2NewPartido);
                        oldJuez1OfPartidoList2NewPartido = em.merge(oldJuez1OfPartidoList2NewPartido);
                    }
                }
            }
            for (Partido partidoList3NewPartido : partidoList3New) {
                if (!partidoList3Old.contains(partidoList3NewPartido)) {
                    Arbitro oldCentralOfPartidoList3NewPartido = partidoList3NewPartido.getCentral();
                    partidoList3NewPartido.setCentral(arbitro);
                    partidoList3NewPartido = em.merge(partidoList3NewPartido);
                    if (oldCentralOfPartidoList3NewPartido != null && !oldCentralOfPartidoList3NewPartido.equals(arbitro)) {
                        oldCentralOfPartidoList3NewPartido.getPartidoList3().remove(partidoList3NewPartido);
                        oldCentralOfPartidoList3NewPartido = em.merge(oldCentralOfPartidoList3NewPartido);
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
                Integer id = arbitro.getIdArbitro();
                if (findArbitro(id) == null) {
                    throw new NonexistentEntityException("The arbitro with id " + id + " no longer exists.");
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
            Arbitro arbitro;
            try {
                arbitro = em.getReference(Arbitro.class, id);
                arbitro.getIdArbitro();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The arbitro with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Partido> partidoListOrphanCheck = arbitro.getPartidoList();
            for (Partido partidoListOrphanCheckPartido : partidoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Arbitro (" + arbitro + ") cannot be destroyed since the Partido " + partidoListOrphanCheckPartido + " in its partidoList field has a non-nullable juez3 field.");
            }
            List<Partido> partidoList1OrphanCheck = arbitro.getPartidoList1();
            for (Partido partidoList1OrphanCheckPartido : partidoList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Arbitro (" + arbitro + ") cannot be destroyed since the Partido " + partidoList1OrphanCheckPartido + " in its partidoList1 field has a non-nullable juez2 field.");
            }
            List<Partido> partidoList2OrphanCheck = arbitro.getPartidoList2();
            for (Partido partidoList2OrphanCheckPartido : partidoList2OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Arbitro (" + arbitro + ") cannot be destroyed since the Partido " + partidoList2OrphanCheckPartido + " in its partidoList2 field has a non-nullable juez1 field.");
            }
            List<Partido> partidoList3OrphanCheck = arbitro.getPartidoList3();
            for (Partido partidoList3OrphanCheckPartido : partidoList3OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Arbitro (" + arbitro + ") cannot be destroyed since the Partido " + partidoList3OrphanCheckPartido + " in its partidoList3 field has a non-nullable central field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(arbitro);
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

    public List<Arbitro> findArbitroEntities() {
        return findArbitroEntities(true, -1, -1);
    }

    public List<Arbitro> findArbitroEntities(int maxResults, int firstResult) {
        return findArbitroEntities(false, maxResults, firstResult);
    }

    private List<Arbitro> findArbitroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Arbitro.class));
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

    public Arbitro findArbitro(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Arbitro.class, id);
        } finally {
            em.close();
        }
    }

    public int getArbitroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Arbitro> rt = cq.from(Arbitro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
