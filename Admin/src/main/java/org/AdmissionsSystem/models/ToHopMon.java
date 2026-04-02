package org.AdmissionsSystem.models;

import javax.persistence.Id;

import lombok.Data;

@Data
public class ToHopMon {
    @Id
    private int idtohop;
    private String matohop;
    private String tentohop;
    private String mon1;
    private String mon2;
    private String mon3;
}
