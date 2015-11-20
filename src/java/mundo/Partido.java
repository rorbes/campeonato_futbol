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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Ruben Orbes
 */
@Entity
@Table(name = "partido")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Partido.findAll", query = "SELECT p FROM Partido p"),
    @NamedQuery(name = "Partido.findByIdPartido", query = "SELECT p FROM Partido p WHERE p.idPartido = :idPartido"),
    @NamedQuery(name = "Partido.findByFechaHora", query = "SELECT p FROM Partido p WHERE p.fechaHora = :fechaHora")})
public class Partido implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_partido")
    private Integer idPartido;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_hora")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaHora;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPartido")
    private List<Gol> golList;
    @JoinColumn(name = "juez3", referencedColumnName = "id_arbitro")
    @ManyToOne(optional = false)
    private Arbitro juez3;
    @JoinColumn(name = "juez2", referencedColumnName = "id_arbitro")
    @ManyToOne(optional = false)
    private Arbitro juez2;
    @JoinColumn(name = "juez1", referencedColumnName = "id_arbitro")
    @ManyToOne(optional = false)
    private Arbitro juez1;
    @JoinColumn(name = "central", referencedColumnName = "id_arbitro")
    @ManyToOne(optional = false)
    private Arbitro central;
    @JoinColumn(name = "id_cancha", referencedColumnName = "id_cancha")
    @ManyToOne(optional = false)
    private Cancha idCancha;
    @JoinColumn(name = "visitante", referencedColumnName = "id_equipo")
    @ManyToOne(optional = false)
    private Equipo visitante;
    @JoinColumn(name = "local", referencedColumnName = "id_equipo")
    @ManyToOne(optional = false)
    private Equipo local;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPartido")
    private List<Sancion> sancionList;

    public Partido() {
    }

    public Partido(Integer idPartido) {
        this.idPartido = idPartido;
    }

    public Partido(Integer idPartido, Date fechaHora) {
        this.idPartido = idPartido;
        this.fechaHora = fechaHora;
    }

    public Integer getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(Integer idPartido) {
        this.idPartido = idPartido;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    @XmlTransient
    public List<Gol> getGolList() {
        return golList;
    }

    public void setGolList(List<Gol> golList) {
        this.golList = golList;
    }

    public Arbitro getJuez3() {
        return juez3;
    }

    public void setJuez3(Arbitro juez3) {
        this.juez3 = juez3;
    }

    public Arbitro getJuez2() {
        return juez2;
    }

    public void setJuez2(Arbitro juez2) {
        this.juez2 = juez2;
    }

    public Arbitro getJuez1() {
        return juez1;
    }

    public void setJuez1(Arbitro juez1) {
        this.juez1 = juez1;
    }

    public Arbitro getCentral() {
        return central;
    }

    public void setCentral(Arbitro central) {
        this.central = central;
    }

    public Cancha getIdCancha() {
        return idCancha;
    }

    public void setIdCancha(Cancha idCancha) {
        this.idCancha = idCancha;
    }

    public Equipo getVisitante() {
        return visitante;
    }

    public void setVisitante(Equipo visitante) {
        this.visitante = visitante;
    }

    public Equipo getLocal() {
        return local;
    }

    public void setLocal(Equipo local) {
        this.local = local;
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
        hash += (idPartido != null ? idPartido.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Partido)) {
            return false;
        }
        Partido other = (Partido) object;
        if ((this.idPartido == null && other.idPartido != null) || (this.idPartido != null && !this.idPartido.equals(other.idPartido))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idPartido + "-" + fechaHora;
    }
    
}
