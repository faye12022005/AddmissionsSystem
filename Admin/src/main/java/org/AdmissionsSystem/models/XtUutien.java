package org.AdmissionsSystem.models;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "xt_uutien")
public class XtUutien implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "cccd", nullable = false)
    private String cccd;

    @Column(name = "loai_giai", nullable = false)
    private String loaiGiai;

    @Column(name = "cap", nullable = false)
    private String cap;

    @Column(name = "ma_mon", nullable = false)
    private String maMon;

    @Column(name = "tt_nv", nullable = false)
    private Integer ttNv;

    @Column(name = "ma_nganh")
    private String maNganh;

    @Column(name = "dc_giai", nullable = false)
    private BigDecimal dcGiai;

    @Column(name = "dc_thxt", nullable = false)
    private BigDecimal dcThxt;
}

