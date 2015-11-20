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
import mundo.TipoCampeonato;
import mundo.Grupo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mundo.Campeonato;

/**
 *
 * @author Ruben Orbes
 */
public class CampeonatoJpaController implements Serializable {

    public CampeonatoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Campeonato campeonato) throws RollbackFailureException, Exception {
        if (campeonato.getGrupoList() == null) {
            campeonato.setGrupoList(new ArrayList<Grupo>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            TipoCampeonato idTipoCampeonato = campeonato.getIdTipoCampeonato();
            if (idTipoCampeonato != null) {
                idTipoCampeonato = em.getReference(idTipoCampeonato.getClass(), idTipoCampeonato.getIdTipoCampeonato());
                campeonato.setIdTipoCampeonato(idTipoCampeonato);
            }
            List<Grupo> attachedGrupoList = new ArrayList<Grupo>();
            for (Grupo grupoListGrupoToAttach : campeonato.getGrupoList()) {
                grupoListGrupoToAttach = em.getReference(grupoListGrupoToAttach.getClass(), grupoListGrupoToAttach.getIdGrupo());
                attachedGrupoList.add(grupoListGrupoToAttach);
            }
            campeonato.setGrupoList(attachedGrupoList);
            em.persist(campeonato);
            if (idTipoCampeonato != null) {
                idTipoCampeonato.getCampeonatoList().add(campeonato);
                idTipoCampeonato = em.merge(idTipoCampeonato);
            }
            for (Grupo grupoListGrupo : campeonato.getGrupoList()) {
                Campeonato oldIdCampeonatoOfGrupoListGrupo = grupoListGrupo.getIdCampeonato();
                grupoListGrupo.setIdCampeonato(campeonato);
                grupoListGrupo = em.merge(grupoListGrupo);
                if (oldIdCampeonatoOfGrupoListGrupo != null) {
                    oldIdCampeonatoOfGrupoListGrupo.getGrupoList().remove(grupoListGrupo);
                    oldIdCampeonatoOfGrupoListGrupo = em.merge(oldIdCampeonatoOfGrupoListGrupo);
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

    public void edit(Campeonato campeonato) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Campeonato persistentCampeonato = em.find(Campeonato.class, campeonato.getIdCampeonato());
            TipoCampeonato idTipoCampeonatoOld = persistentCampeonato.getIdTipoCampeonato();
            TipoCampeonato idTipoCampeonatoNew = campeonato.getIdTipoCampeonato();
            List<Grupo> grupoListOld = persistentCampeonato.getGrupoList();
            List<Grupo> grupoListNew = campeonato.getGrupoList();
            List<String> illegalOrphanMessages = null;
            for (Grupo grupoListOldGrupo : grupoListOld) {
                if (!grupoListNew.contains(grupoListOldGrupo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Grupo " + grupoListOldGrupo + " since its idCampeonato field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idTipoCampeonatoNew != null) {
                idTipoCampeonatoNew = em.getReference(idTipoCampeonatoNew.getClass(), idTipoCampeonatoNew.getIdTipoCampeonato());
                campeonato.setIdTipoCampeonato(idTipoCampeonatoNew);
            }
            List<Grupo> attachedGrupoListNew = new ArrayList<Grupo>();
            for (Grupo grupoListNewGrupoToAttach : grupoListNew) {
                grupoListNewGrupoToAttach = em.getReference(grupoListNewGrupoToAttach.getClass(), grupoListNewGrupoToAttach.getIdGrupo());
                attachedGrupoListNew.add(grupoListNewGrupoToAttach);
            }
            grupoListNew = attachedGrupoListNew;
            campeonato.setGrupoList(grupoListNew);
            campeonato = em.merge(campeonato);
            if (idTipoCampeonatoOld != null && !idTipoCampeonatoOld.equals(idTipoCampeonatoNew)) {
                idTipoCampeonatoOld.getCampeonatoList().remove(campeonato);
                idTipoCampeonatoOld = em.merge(idTipoCampeonatoOld);
            }
            if (idTipoCampeonatoNew != null && !idTipoCampeonatoNew.equals(idTipoCampeonatoOld)) {
                idTipoCampeonatoNew.getCampeonatoList().add(campeonato);
                idTipoCampeonatoNew = em.merge(idTipoCampeonatoNew);
            }
            for (Grupo grupoListNewGrupo : grupoListNew) {
                if (!grupoListOld.contains(grupoListNewGrupo)) {
                    Campeonato oldIdCampeonatoOfGrupoListNewGrupo = grupoListNewGrupo.getIdCampeonato();
                    grupoListNewGrupo.setIdCampeonato(campeonato);
                    grupoListNewGrupo = em.merge(grupoListNewGrupo);
                    if (oldIdCampeonatoOfGrupoListNewGrupo != null && !oldIdCampeonatoOfGrupoListNewGrupo.equals(campeonato)) {
                        oldIdCampeonatoOfGrupoListNewGrupo.getGrupoList().remove(grupoListNewGrupo);
                        oldIdCampeonatoOfGrupoListNewGrupo = em.merge(oldIdCampeonatoOfGrupoListNewGrupo);
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
                Integer id = campeonato.getIdCampeonato();
                if (findCampeonato(id) == null) {
                    throw new NonexistentEntityException("The campeonato with id " + id + " no longer exists.");
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
            Campeonato campeonato;
            try {
                campeonato = em.getReference(Campeonato.class, id);
                campeonato.getIdCampeonato();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The campeonato with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Grupo> grupoListOrphanCheck = campeonato.getGrupoList();
            for (Grupo grupoListOrphanCheckGrupo : grupoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Campeonato (" + campeonato + ") cannot be destroyed since the Grupo " + grupoListOrphanCheckGrupo + " in its grupoList field has a non-nullable idCampeonato field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            TipoCampeonato idTipoCampeonato = campeonato.getIdTipoCampeonato();
            if (idTipoCampeonato != null) {
                idTipoCampeonato.getCampeonatoList().remove(campeonato);
                idTipoCampeonato = em.merge(idTipoCampeonato);
            }
            em.remove(campeonato);
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

    public List<Campeonato> findCampeonatoEntities() {
        return findCampeonatoEntities(true, -1, -1);
    }

    public List<Campeonato> findCampeonatoEntities(int maxResults, int firstResult) {
        return findCampeonatoEntities(false, maxResults, firstResult);
    }

    private List<Campeonato> findCampeonatoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Campeonato.class));
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

    public Campeonato findCampeonato(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Campeonato.class, id);
        } finally {
            em.close();
        }
    }

    public int getCampeonatoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Campeonato> rt = cq.from(Campeonato.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
