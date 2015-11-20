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
@Table(name = "gol")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Gol.findAll", query = "SELECT g FROM Gol g"),
    @NamedQuery(name = "Gol.findByIdGoles", query = "SELECT g FROM Gol g WHERE g.idGoles = :idGoles")})
public class Gol implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_goles")
    private Integer idGoles;
    @JoinColumn(name = "id_partido", referencedColumnName = "id_partido")
    @ManyToOne(optional = false)
    private Partido idPartido;
    @JoinColumn(name = "id_equipo_jugador", referencedColumnName = "id_campeonato_equipo_jugador")
    @ManyToOne(optional = false)
    private EquipoJugador idEquipoJugador;

    public Gol() {
    }

    public Gol(Integer idGoles) {
        this.idGoles = idGoles;
    }

    public Integer getIdGoles() {
        return idGoles;
    }

    public void setIdGoles(Integer idGoles) {
        this.idGoles = idGoles;
    }

    public Partido getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(Partido idPartido) {
        this.idPartido = idPartido;
    }

    public EquipoJugador getIdEquipoJugador() {
        return idEquipoJugador;
    }

    public void setIdEquipoJugador(EquipoJugador idEquipoJugador) {
        this.idEquipoJugador = idEquipoJugador;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idGoles != null ? idGoles.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Gol)) {
            return false;
        }
        Gol other = (Gol) object;
        if ((this.idGoles == null && other.idGoles != null) || (this.idGoles != null && !this.idGoles.equals(other.idGoles))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idGoles + "-";
    }
    
}
