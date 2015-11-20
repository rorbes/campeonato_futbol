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
@Table(name = "tipo_sancion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoSancion.findAll", query = "SELECT t FROM TipoSancion t"),
    @NamedQuery(name = "TipoSancion.findByIdTipoSancion", query = "SELECT t FROM TipoSancion t WHERE t.idTipoSancion = :idTipoSancion"),
    @NamedQuery(name = "TipoSancion.findByNombre", query = "SELECT t FROM TipoSancion t WHERE t.nombre = :nombre")})
public class TipoSancion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_tipo_sancion")
    private Integer idTipoSancion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idTipoSancion")
    private List<Sancion> sancionList;

    public TipoSancion() {
    }

    public TipoSancion(Integer idTipoSancion) {
        this.idTipoSancion = idTipoSancion;
    }

    public TipoSancion(Integer idTipoSancion, String nombre) {
        this.idTipoSancion = idTipoSancion;
        this.nombre = nombre;
    }

    public Integer getIdTipoSancion() {
        return idTipoSancion;
    }

    public void setIdTipoSancion(Integer idTipoSancion) {
        this.idTipoSancion = idTipoSancion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public List<Sancion> getSancionList() {
        return sancionList;
    }

    public void setSancionList(List<Sancion> sancionList) {
        this.sancionList = sancionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipoSancion != null ? idTipoSancion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoSancion)) {
            return false;
        }
        TipoSancion other = (TipoSancion) object;
        if ((this.idTipoSancion == null && other.idTipoSancion != null) || (this.idTipoSancion != null && !this.idTipoSancion.equals(other.idTipoSancion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idTipoSancion + "-" + nombre;
    }
    
}
