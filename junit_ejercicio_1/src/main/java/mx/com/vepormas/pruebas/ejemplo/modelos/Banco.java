package mx.com.vepormas.pruebas.ejemplo.modelos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Banco {
	
	public Banco() {
		cuentas = new ArrayList<>();
		
	}
	public List<Cuenta> cuentas;
	
	public List<Cuenta> getCuentas() {
		return cuentas;
	}

	public void setCuentas(List<Cuenta> cuentas) {
		this.cuentas = cuentas;
	}
	
	public void addCuenta(Cuenta cuenta) {
		cuentas.add(cuenta);
		cuenta.setBanco(this);
	}

	private String nombre;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public void transferir(Cuenta origen, Cuenta destino, BigDecimal monto) {
		origen.debito(monto);
		destino.credito(monto);
	}

}
