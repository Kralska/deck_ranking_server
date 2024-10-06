package de.tschoooons.deck_ranking_server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "pod_participants")
public class PodParticipant {

    @EmbeddedId
    @JsonIgnore
    private PodParticipantId id = new PodParticipantId();

    @ManyToOne
    @MapsId("pod_id")
    @JoinColumn(name = "pod_id")
    @EqualsAndHashCode.Include
    private Pod pod;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Include
    private User user;

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private UserPodRole role;

    public PodParticipant(Pod pod, User user, UserPodRole role) {
        this.pod = pod;
        this.user = user;
        this.role = role;
    }
}
