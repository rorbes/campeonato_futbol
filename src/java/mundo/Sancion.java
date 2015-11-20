/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mundo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ruben Orbes
 */
@Entity
@Table(name = "sancion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sancion.findAll", query = "SELECT s FROM Sancion s"),
    @NamedQuery(name = "Sancion.findByIdSanciones", query = "SELECT s FROM Sancion s WHERE s.idSanciones = :idSanciones")})
public class Sancion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_sanciones")
    private Integer idSanciones;
    @JoinColumn(name = "id_equipo_jugador", referencedColumnName = "id_campeonato_equipo_jugador")
    @ManyToOne(optional = false)
    private EquipoJugador idEquipoJugador;
    @JoinColumn(name = "id_tipo_sancion", referencedColumnName = "id_tipo_sancion")
    @ManyToOne(optional = false)
    private TipoSancion idTipoSancion;
    @JoinColumn(name = "id_partido", referencedColumnName = "id_partido")
    @ManyToOne(optional = false)
    private Partido idPartido;

    public Sancion() {
    }

    public Sancion(Integer idSanciones) {
        this.idSanciones = idSanciones;
    }

    public Integer getIdSanciones() {
        return idSanciones;
    }

    public void setIdSanciones(Integer idSanciones) {
        this.idSanciones = idSanciones;
    }

    public EquipoJugador getIdEquipoJugador() {
        return idEquipoJugador;
    }

    public void setIdEquipoJugador(EquipoJugador idEquipoJugador) {
        this.idEquipoJugador = idEquipoJugador;
    }

    public TipoSancion getIdTipoSancion() {
        return idTipoSancion;
    }

    public void setIdTipoSancion(TipoSancion idTipoSancion) {
        this.idTipoSancion = idTipoSancion;
    }

    public Partido getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(Partido idPartido) {
        this.idPartido = idPartido;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idSanciones != null ? idSanciones.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sancion)) {
            return false;
        }
        Sancion other = (Sancion) object;
        if ((this.idSanciones == null && other.idSanciones != null) || (this.idSanciones != null && !this.idSanciones.equals(other.idSanciones))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idSanciones + "-";
    }
    
}
