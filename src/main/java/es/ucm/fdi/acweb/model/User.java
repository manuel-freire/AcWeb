package es.ucm.fdi.acweb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name="User.byUsername",
                query="SELECT u FROM User u "
                        + "WHERE u.username = :username AND u.enabled = TRUE"),
})
@Table(name="AcUser")
public class User implements Transferable<User.Transfer> {

    public enum Role {
        USER,			// normal users
        ADMIN,          // admin users
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwd;

    private boolean enabled;
    private String roles; // split by ',' to separate roles

    @OneToMany(/*fetch=FetchType.EAGER, */cascade = CascadeType.REMOVE)
    @JoinColumn(name="owner_id")
    private List<AnalysisWeb> analysesWeb = new ArrayList<>();

    /**
     * Checks whether this user has a given role.
     * @param role to check
     * @return true iff this user has that role.
     */
    public boolean hasRole(Role role) {
        String roleName = role.name();
        return Arrays.asList(roles.split(",")).contains(roleName);
    }

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private String mail;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id,	username);
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }

    public Boolean isAdmin(){
        return hasRole(Role.ADMIN);
    }

    @Override
    public int hashCode() {
        return Math.toIntExact(this.id);
    }
}
