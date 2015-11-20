/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mundo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Ruben Orbes
 */
@Entity
@Table(name = "tipo_campeonato")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoCampeonato.findAll", query = "SELECT t FROM TipoCampeonato t"),
    @NamedQuery(name = "TipoCampeonato.findByIdTipoCampeonato", query = "SELECT t FROM TipoCampeonato t WHERE t.idTipoCampeonato = :idTipoCampeonato"),
    @NamedQuery(name = "TipoCampeonato.findByNombre", query = "SELECT t FROM TipoCampeonato t WHERE t.nombre = :nombre")})
public class TipoCampeonato implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_tipo_campeonato")
    private Integer idTipoCampeonato;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idTipoCampeonato")
    private List<Campeonato> campeonatoList;

    public TipoCampeonato() {
    }

    public TipoCampeonato(Integer idTipoCampeonato) {
        this.idTipoCampeonato = idTipoCampeonato;
    }

    public TipoCampeonato(Integer idTipoCampeonato, String nombre) {
        this.idTipoCampeonato = idTipoCampeonato;
        this.nombre = nombre;
    }

    public Integer getIdTipoCampeonato() {
        return idTipoCampeonato;
    }

    public void setIdTipoCampeonato(Integer idTipoCampeonato) {
        this.idTipoCampeonato = idTipoCampeonato;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public List<Campeonato> getCampeonatoList() {
        return campeonatoList;
    }

    public void setCampeonatoList(List<Campeonato> campeonatoList) {
        this.campeonatoList = campeonatoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipoCampeonato != null ? idTipoCampeonato.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoCampeonato)) {
            return false;
        }
        TipoCampeonato other = (TipoCampeonato) object;
        if ((this.idTipoCampeonato == null && other.idTipoCampeonato != null) || (this.idTipoCampeonato != null && !this.idTipoCampeonato.equals(other.idTipoCampeonato))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idTipoCampeonato + "-" + nombre;
    }
    
}
