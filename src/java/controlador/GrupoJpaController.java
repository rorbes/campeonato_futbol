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
import mundo.Equipo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mundo.Grupo;

/**
 *
 * @author Ruben Orbes
 */
public class GrupoJpaController implements Serializable {

    public GrupoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Grupo grupo) throws RollbackFailureException, Exception {
        if (grupo.getEquipoList() == null) {
            grupo.setEquipoList(new ArrayList<Equipo>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Campeonato idCampeonato = grupo.getIdCampeonato();
            if (idCampeonato != null) {
                idCampeonato = em.getReference(idCampeonato.getClass(), idCampeonato.getIdCampeonato());
                grupo.setIdCampeonato(idCampeonato);
            }
            List<Equipo> attachedEquipoList = new ArrayList<Equipo>();
            for (Equipo equipoListEquipoToAttach : grupo.getEquipoList()) {
                equipoListEquipoToAttach = em.getReference(equipoListEquipoToAttach.getClass(), equipoListEquipoToAttach.getIdEquipo());
                attachedEquipoList.add(equipoListEquipoToAttach);
            }
            grupo.setEquipoList(attachedEquipoList);
            em.persist(grupo);
            if (idCampeonato != null) {
                idCampeonato.getGrupoList().add(grupo);
                idCampeonato = em.merge(idCampeonato);
            }
            for (Equipo equipoListEquipo : grupo.getEquipoList()) {
                Grupo oldIdGrupoOfEquipoListEquipo = equipoListEquipo.getIdGrupo();
                equipoListEquipo.setIdGrupo(grupo);
                equipoListEquipo = em.merge(equipoListEquipo);
                if (oldIdGrupoOfEquipoListEquipo != null) {
                    oldIdGrupoOfEquipoListEquipo.getEquipoList().remove(equipoListEquipo);
                    oldIdGrupoOfEquipoListEquipo = em.merge(oldIdGrupoOfEquipoListEquipo);
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

    public void edit(Grupo grupo) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Grupo persistentGrupo = em.find(Grupo.class, grupo.getIdGrupo());
            Campeonato idCampeonatoOld = persistentGrupo.getIdCampeonato();
            Campeonato idCampeonatoNew = grupo.getIdCampeonato();
            List<Equipo> equipoListOld = persistentGrupo.getEquipoList();
            List<Equipo> equipoListNew = grupo.getEquipoList();
            List<String> illegalOrphanMessages = null;
            for (Equipo equipoListOldEquipo : equipoListOld) {
                if (!equipoListNew.contains(equipoListOldEquipo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Equipo " + equipoListOldEquipo + " since its idGrupo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idCampeonatoNew != null) {
                idCampeonatoNew = em.getReference(idCampeonatoNew.getClass(), idCampeonatoNew.getIdCampeonato());
                grupo.setIdCampeonato(idCampeonatoNew);
            }
            List<Equipo> attachedEquipoListNew = new ArrayList<Equipo>();
            for (Equipo equipoListNewEquipoToAttach : equipoListNew) {
                equipoListNewEquipoToAttach = em.getReference(equipoListNewEquipoToAttach.getClass(), equipoListNewEquipoToAttach.getIdEquipo());
                attachedEquipoListNew.add(equipoListNewEquipoToAttach);
            }
            equipoListNew = attachedEquipoListNew;
            grupo.setEquipoList(equipoListNew);
            grupo = em.merge(grupo);
            if (idCampeonatoOld != null && !idCampeonatoOld.equals(idCampeonatoNew)) {
                idCampeonatoOld.getGrupoList().remove(grupo);
                idCampeonatoOld = em.merge(idCampeonatoOld);
            }
            if (idCampeonatoNew != null && !idCampeonatoNew.equals(idCampeonatoOld)) {
                idCampeonatoNew.getGrupoList().add(grupo);
                idCampeonatoNew = em.merge(idCampeonatoNew);
            }
            for (Equipo equipoListNewEquipo : equipoListNew) {
                if (!equipoListOld.contains(equipoListNewEquipo)) {
                    Grupo oldIdGrupoOfEquipoListNewEquipo = equipoListNewEquipo.getIdGrupo();
                    equipoListNewEquipo.setIdGrupo(grupo);
                    equipoListNewEquipo = em.merge(equipoListNewEquipo);
                    if (oldIdGrupoOfEquipoListNewEquipo != null && !oldIdGrupoOfEquipoListNewEquipo.equals(grupo)) {
                        oldIdGrupoOfEquipoListNewEquipo.getEquipoList().remove(equipoListNewEquipo);
                        oldIdGrupoOfEquipoListNewEquipo = em.merge(oldIdGrupoOfEquipoListNewEquipo);
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
                Integer id = grupo.getIdGrupo();
                if (findGrupo(id) == null) {
                    throw new NonexistentEntityException("The grupo with id " + id + " no longer exists.");
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
            Grupo grupo;
            try {
                grupo = em.getReference(Grupo.class, id);
                grupo.getIdGrupo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The grupo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Equipo> equipoListOrphanCheck = grupo.getEquipoList();
            for (Equipo equipoListOrphanCheckEquipo : equipoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grupo (" + grupo + ") cannot be destroyed since the Equipo " + equipoListOrphanCheckEquipo + " in its equipoList field has a non-nullable idGrupo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Campeonato idCampeonato = grupo.getIdCampeonato();
            if (idCampeonato != null) {
                idCampeonato.getGrupoList().remove(grupo);
                idCampeonato = em.merge(idCampeonato);
            }
            em.remove(grupo);
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

    public List<Grupo> findGrupoEntities() {
        return findGrupoEntities(true, -1, -1);
    }

    public List<Grupo> findGrupoEntities(int maxResults, int firstResult) {
        return findGrupoEntities(false, maxResults, firstResult);
    }

    private List<Grupo> findGrupoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Grupo.class));
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

    public Grupo findGrupo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Grupo.class, id);
        } finally {
            em.close();
        }
    }

    public int getGrupoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Grupo> rt = cq.from(Grupo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
