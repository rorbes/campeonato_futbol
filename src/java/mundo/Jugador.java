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
@Table(name = "jugador")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Jugador.findAll", query = "SELECT j FROM Jugador j"),
    @NamedQuery(name = "Jugador.findByIdJugador", query = "SELECT j FROM Jugador j WHERE j.idJugador = :idJugador"),
    @NamedQuery(name = "Jugador.findByIdentificacion", query = "SELECT j FROM Jugador j WHERE j.identificacion = :identificacion"),
    @NamedQuery(name = "Jugador.findByNombres", query = "SELECT j FROM Jugador j WHERE j.nombres = :nombres"),
    @NamedQuery(name = "Jugador.findByApellidos", query = "SELECT j FROM Jugador j WHERE j.apellidos = :apellidos"),
    @NamedQuery(name = "Jugador.findByFechaNacimiento", query = "SELECT j FROM Jugador j WHERE j.fechaNacimiento = :fechaNacimiento"),
    @NamedQuery(name = "Jugador.findByPerfil", query = "SELECT j FROM Jugador j WHERE j.perfil = :perfil"),
    @NamedQuery(name = "Jugador.findByNumero", query = "SELECT j FROM Jugador j WHERE j.numero = :numero")})
public class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_jugador")
    private Integer idJugador;
    @Basic(optional = false)
    @NotNull
    @Column(name = "identificacion")
    private int identificacion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "nombres")
    private String nombres;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "apellidos")
    private String apellidos;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "perfil")
    private String perfil;
    @Basic(optional = false)
    @NotNull
    @Column(name = "numero")
    private int numero;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idJugador")
    private List<EquipoJugador> equipoJugadorList;

    public Jugador() {
    }

    public Jugador(Integer idJugador) {
        this.idJugador = idJugador;
    }

    public Jugador(Integer idJugador, int identificacion, String nombres, String apellidos, Date fechaNacimiento, String perfil, int numero) {
        this.idJugador = idJugador;
        this.identificacion = identificacion;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.perfil = perfil;
        this.numero = numero;
    }

    public Integer getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(Integer idJugador) {
        this.idJugador = idJugador;
    }

    public int getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(int identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @XmlTransient
    public List<EquipoJugador> getEquipoJugadorList() {
        return equipoJugadorList;
    }

    public void setEquipoJugadorList(List<EquipoJugador> equipoJugadorList) {
        this.equipoJugadorList = equipoJugadorList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idJugador != null ? idJugador.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Jugador)) {
            return false;
        }
        Jugador other = (Jugador) object;
        if ((this.idJugador == null && other.idJugador != null) || (this.idJugador != null && !this.idJugador.equals(other.idJugador))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idJugador + "-" + nombres + apellidos;
    }
    
}
