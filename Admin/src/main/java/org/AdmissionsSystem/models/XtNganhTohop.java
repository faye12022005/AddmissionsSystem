package org.AdmissionsSystem.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "xt_nganh_tohop")
public class XtNganhTohop implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "manganh", nullable = false)
    private String manganh;

    @Column(name = "matohop", nullable = false)
    private String matohop;

    @Column(name = "th_mon1")
    private String thMon1;

    @Column(name = "hsmon1")
    private Integer hsmon1;

    @Column(name = "th_mon2")
    private String thMon2;

    @Column(name = "hsmon2")
    private Integer hsmon2;

    @Column(name = "th_mon3")
    private String thMon3;

    @Column(name = "hsmon3")
    private Integer hsmon3;

    @Column(name = "tb_keys")
    private String tbKeys;

    @Column(name = "N1")
    private Boolean n1;

    @Column(name = "TO")
    private Boolean to;

    @Column(name = "LI")
    private Boolean li;

    @Column(name = "HO")
    private Boolean ho;

    @Column(name = "SI")
    private Boolean si;

    @Column(name = "VA")
    private Boolean va;

    @Column(name = "SU")
    private Boolean su;

    @Column(name = "DI")
    private Boolean di;

    @Column(name = "TI")
    private Boolean ti;

    @Column(name = "KHAC")
    private Boolean khac;

    @Column(name = "KTPL")
    private Boolean ktpl;

    @Column(name = "dolech")
    private java.math.BigDecimal dolech;

}
