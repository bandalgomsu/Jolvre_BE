package com.example.jolvre.group.entity;

import com.example.jolvre.common.entity.BaseTimeEntity;
import com.example.jolvre.exhibition.entity.Exhibit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class RegisteredExhibit extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registered_exhibit_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibit_id")
    private Exhibit exhibit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_exhibit_id")
    private GroupExhibit groupExhibit;

    @Builder
    public RegisteredExhibit(Exhibit exhibit, GroupExhibit groupExhibit) {
        this.exhibit = exhibit;
        this.groupExhibit = groupExhibit;
    }
}
