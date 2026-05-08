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
@Table(name = "xt_diemvsat")
public class XtDiemVsat implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_vsat", nullable = false)
	private Integer idVsat;

	@Column(name = "cccd", nullable = false)
	private String cccd;

	@Column(name = "dot_thi", nullable = false)
	private String dotThi;

	@Column(name = "toan_vsat")
	private java.math.BigDecimal toanVsat;

	@Column(name = "van_vsat")
	private java.math.BigDecimal vanVsat;

	@Column(name = "anh_vsat")
	private java.math.BigDecimal anhVsat;

	@Column(name = "ly_vsat")
	private java.math.BigDecimal lyVsat;

	@Column(name = "hoa_vsat")
	private java.math.BigDecimal hoaVsat;

	@Column(name = "sinh_vsat")
	private java.math.BigDecimal sinhVsat;

	@Column(name = "su_vsat")
	private java.math.BigDecimal suVsat;

	@Column(name = "dia_vsat")
	private java.math.BigDecimal diaVsat;
}
