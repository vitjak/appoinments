package com.assignment.appointments.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "practitioners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Practitioner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String specialization;

    @ManyToMany
    @JoinTable(
            name = "practitioner_medical_services",
            joinColumns = @JoinColumn(name = "practitioner_id"),
            inverseJoinColumns = @JoinColumn(name = "medical_service_id")
    )
    @JsonIgnore
    private Set<MedicalService> medicalServices = new HashSet<>();

    @OneToMany(mappedBy = "practitioner")
    @JsonIgnore
    private List<TimeSlot> timeSlots = new ArrayList<>();
}
