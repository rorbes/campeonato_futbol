/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mundo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Ruben Orbes
 */
@Entity
@Table(name = "campeonato")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Campeonato.findAll", query = "SELECT c FROM Campeonato c"),
    @NamedQuery(name = "Campeonato.findByIdCampeonato", query = "SELECT c FROM Campeonato c WHERE c.idCampeonato = :idCampeonato"),
    @NamedQuery(name = "Campeonato.findByDescripcion", query = "SELECT c FROM Campeonato c WHERE c.descripcion = :descripcion"),
    @NamedQuery(name = "Campeonato.findByFechaInicio", query = "SELECT c FROM Campeonato c WHERE c.fechaInicio = :fechaInicio"),
    @NamedQuery(name = "Campeonato.findByFechaFin", query = "SELECT c FROM Campeonato c WHERE c.fechaFin = :fechaFin")})
public class Campeonato implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_campeonato")
    private Integer idCampeonato;
    @Size(max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCampeonato")
    private List<Grupo> grupoList;
    @JoinColumn(name = "id_tipo_campeonato", referencedColumnName = "id_tipo_campeonato")
    @ManyToOne(optional = false)
    private TipoCampeonato idTipoCampeonato;

    public Campeonato() {
    }

    public Campeonato(Integer idCampeonato) {
        this.idCampeonato = idCampeonato;
    }

    public Campeonato(Integer idCampeonato, Date fechaInicio, Date fechaFin) {
        this.idCampeonato = idCampeonato;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Integer getIdCampeonato() {
        return idCampeonato;
    }

    public void setIdCampeonato(Integer idCampeonato) {
        this.idCampeonato = idCampeonato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    @XmlTransient
    public List<Grupo> getGrupoList() {
        return grupoList;
    }

    public void setGrupoList(List<Grupo> grupoList) {
        this.grupoList = grupoList;
    }

    public TipoCampeonato getIdTipoCampeonato() {
        return idTipoCampeonato;
    }

    public void setIdTipoCampeonato(TipoCampeonato idTipoCampeonato) {
        this.idTipoCampeonato = idTipoCampeonato;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCampeonato != null ? idCampeonato.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Campeonato)) {
            return false;
        }
        Campeonato other = (Campeonato) object;
        if ((this.idCampeonato == null && other.idCampeonato != null) || (this.idCampeonato != null && !this.idCampeonato.equals(other.idCampeonato))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idCampeonato + "-" + descripcion;
    }
    
}
