package org.AdmissionsSystem.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "xt_bangquydoi")
public class XtBangquydoi implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "idqd", nullable = false)
    private Integer idqd;

    @Column(name = "d_phuongthuc")
    private String dPhuongthuc;

    @Column(name = "d_tohop")
    private String dTohop;

    @Column(name = "d_mon")
    private String dMon;

    @Column(name = "d_diema")
    private java.math.BigDecimal dDiema;

    @Column(name = "d_diemb")
    private java.math.BigDecimal dDiemb;

    @Column(name = "d_diemc")
    private java.math.BigDecimal dDiemc;

    @Column(name = "d_diemd")
    private java.math.BigDecimal dDiemd;

    @Column(name = "d_maquydoi")
    private String dMaquydoi;

    @Column(name = "d_phanvi")
    private String dPhanvi;

}
