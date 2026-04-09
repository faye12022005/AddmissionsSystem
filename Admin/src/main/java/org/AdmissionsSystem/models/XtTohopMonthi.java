package org.AdmissionsSystem.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "xt_tohop_monthi")
public class XtTohopMonthi implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "idtohop", nullable = false)
    private Integer idtohop;

    @Column(name = "matohop", nullable = false)
    private String matohop;

    @Column(name = "mon1", nullable = false)
    private String mon1;

    @Column(name = "mon2", nullable = false)
    private String mon2;

    @Column(name = "mon3", nullable = false)
    private String mon3;

    @Column(name = "tentohop")
    private String tentohop;

}
