package org.AdmissionsSystem.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "xt_nguyenvongxettuyen")
public class XtNguyenvongxettuyen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "idnv", nullable = false)
    private Integer idnv;

    @Column(name = "nn_cccd", nullable = false)
    private String nnCccd;

    @Column(name = "nv_manganh", nullable = false)
    private String nvManganh;

    @Column(name = "nv_tt", nullable = false)
    private Integer nvTt;

    @Column(name = "diem_thxt")
    private java.math.BigDecimal diemThxt;

    @Column(name = "diem_utqd")
    private java.math.BigDecimal diemUtqd;

    @Column(name = "diem_cong")
    private java.math.BigDecimal diemCong;

    @Column(name = "diem_xettuyen")
    private java.math.BigDecimal diemXettuyen;

    @Column(name = "nv_ketqua")
    private String nvKetqua;

    @Column(name = "nv_keys")
    private String nvKeys;

    @Column(name = "tt_phuongthuc")
    private String ttPhuongthuc;

    @Column(name = "tt_thm")
    private String ttThm;

}
