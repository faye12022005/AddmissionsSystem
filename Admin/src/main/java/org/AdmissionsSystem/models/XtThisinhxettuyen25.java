package org.AdmissionsSystem.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "xt_thisinhxettuyen25")
public class XtThisinhxettuyen25 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "idthisinh", nullable = false)
    private Integer idthisinh;

    @Column(name = "cccd")
    private String cccd;

    @Column(name = "sobaodanh")
    private String sobaodanh;

    @Column(name = "ho")
    private String ho;

    @Column(name = "ten")
    private String ten;

    @Column(name = "ngay_sinh")
    private String ngaySinh;

    @Column(name = "dien_thoai")
    private String dienThoai;

    @Column(name = "password")
    private String password;

    @Column(name = "gioi_tinh")
    private String gioiTinh;

    @Column(name = "email")
    private String email;

    @Column(name = "noi_sinh")
    private String noiSinh;

    @Column(name = "updated_at")
    private java.sql.Date updatedAt;

    @Column(name = "doi_tuong")
    private String doiTuong;

    @Column(name = "khu_vuc")
    private String khuVuc;

}
