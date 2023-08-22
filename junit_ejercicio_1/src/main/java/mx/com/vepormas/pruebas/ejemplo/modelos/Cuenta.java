package mx.com.vepormas.pruebas.ejemplo.modelos;

import java.math.BigDecimal;

import mx.com.vepormas.pruebas.exceptions.DineroInsuficienteException;

public class Cuenta {
	private String persona;
	private BigDecimal saldo;
	private Banco banco;
	
	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public Cuenta(String persona, BigDecimal saldo) {
		this.persona=persona;
		this.saldo=saldo;
	}
	
	public void debito(BigDecimal monto) {
		BigDecimal sd = this.saldo.subtract(monto);
		if (sd.compareTo(BigDecimal.ZERO) < 0) {
			throw new DineroInsuficienteException("Dinero insuficiente");
		}
		this.saldo=sd;
	}
	
	public void credito(BigDecimal monto) {
		this.saldo = this.saldo.add(monto);
	}
	
	public String getPersona() {
		return persona;
	}
	public void setPersona(String persona) {
		this.persona = persona;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

}
