package org.AdmissionsSystem.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "xt_nganh")
public class XtNganh implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "idnganh", nullable = false)
    private Integer idnganh;

    @Column(name = "manganh", nullable = false)
    private String manganh;

    @Column(name = "tennganh", nullable = false)
    private String tennganh;

    @Column(name = "n_tohopgoc")
    private String nTohopgoc;

    @Column(name = "n_chitieu", nullable = false)
    private Integer nChitieu;

    @Column(name = "n_diemsan")
    private java.math.BigDecimal nDiemsan;

    @Column(name = "n_diemtrungtuyen")
    private java.math.BigDecimal nDiemtrungtuyen;

    @Column(name = "n_tuyenthang")
    private String nTuyenthang;

    @Column(name = "n_dgnl")
    private String nDgnl;

    @Column(name = "n_thpt")
    private String nThpt;

    @Column(name = "n_vsat")
    private String nVsat;

    @Column(name = "sl_xtt")
    private Integer slXtt;

    @Column(name = "sl_dgnl")
    private Integer slDgnl;

    @Column(name = "sl_vsat")
    private Integer slVsat;

    @Column(name = "sl_thpt")
    private String slThpt;

}
