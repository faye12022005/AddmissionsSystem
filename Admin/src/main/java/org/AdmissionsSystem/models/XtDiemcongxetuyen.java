package org.AdmissionsSystem.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "xt_diemcongxetuyen")
public class XtDiemcongxetuyen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "iddiemcong", nullable = false)
    private Integer iddiemcong;

    @Column(name = "ts_cccd", nullable = false)
    private String tsCccd;

    @Column(name = "manganh")
    private String manganh;

    @Column(name = "matohop")
    private String matohop;

    @Column(name = "phuongthuc")
    private String phuongthuc;

    @Column(name = "diemCC")
    private java.math.BigDecimal diemcc;

    @Column(name = "diemUtxt")
    private java.math.BigDecimal diemutxt;

    @Column(name = "diemTong")
    private java.math.BigDecimal diemtong;

    @Column(name = "ghichu")
    private String ghichu;

    @Column(name = "dc_keys", nullable = false)
    private String dcKeys;

}
