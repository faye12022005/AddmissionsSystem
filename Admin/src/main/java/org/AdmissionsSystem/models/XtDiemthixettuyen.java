package org.AdmissionsSystem.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "xt_diemthixettuyen")
public class XtDiemthixettuyen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddiemthi", nullable = false)
    private Integer iddiemthi;

    @Column(name = "cccd", nullable = false)
    private String cccd;

    @Column(name = "sobaodanh")
    private String sobaodanh;

    @Column(name = "d_phuongthuc")
    private String dPhuongthuc;

    @Column(name = "TO")
    private java.math.BigDecimal to;

    @Column(name = "LI")
    private java.math.BigDecimal li;

    @Column(name = "HO")
    private java.math.BigDecimal ho;

    @Column(name = "SI")
    private java.math.BigDecimal si;

    @Column(name = "SU")
    private java.math.BigDecimal su;

    @Column(name = "DI")
    private java.math.BigDecimal di;

    @Column(name = "VA")
    private java.math.BigDecimal va;

    @Column(name = "GDCD")
    private java.math.BigDecimal gdcd;

    @Column(name = "N1_THI")
    private java.math.BigDecimal n1Thi;

    @Column(name = "N1_CC")
    private java.math.BigDecimal n1Cc;

    @Column(name = "CNCN")
    private java.math.BigDecimal cncn;

    @Column(name = "CNNN")
    private java.math.BigDecimal cnnn;

    @Column(name = "TI")
    private java.math.BigDecimal ti;

    @Column(name = "KTPL")
    private java.math.BigDecimal ktpl;

    @Column(name = "NL1")
    private java.math.BigDecimal nl1;

    @Column(name = "NK1")
    private java.math.BigDecimal nk1;

    @Column(name = "NK2")
    private java.math.BigDecimal nk2;

    @Column(name = "NK3")
    private java.math.BigDecimal nk3;

    @Column(name = "NK4")
    private java.math.BigDecimal nk4;

    @Column(name = "NK5")
    private java.math.BigDecimal nk5;

    @Column(name = "NK6")
    private java.math.BigDecimal nk6;

}
