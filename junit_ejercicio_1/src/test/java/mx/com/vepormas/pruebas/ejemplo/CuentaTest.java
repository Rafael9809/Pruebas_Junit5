package mx.com.vepormas.pruebas.ejemplo;

import mx.com.vepormas.pruebas.ejemplo.modelos.Banco;
import mx.com.vepormas.pruebas.ejemplo.modelos.Cuenta;
import mx.com.vepormas.pruebas.exceptions.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

//@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CuentaTest {
	Cuenta cuenta;

	@BeforeAll
	static void antes(){
		System.out.println("Iniciando pruebas");
	}

	@AfterAll
	static void despues(){
		System.out.println("Finalizando pruebas");
	}

	@BeforeEach
	void initMethodTest(){
		this.cuenta = new Cuenta("Rafael", new BigDecimal("1000.2112"));
	}

	@AfterEach
	void outMethodTest(){
		System.out.println("Test ejecutado");
	}

	@Test
	void testNombreCuenta() {
		String esperado ="Rafael";
		String real = cuenta.getPersona();
		assertEquals(esperado, real,()->"El nombre de la cuenta no era el esperado");
	}
	
	@DisplayName("Prueba saldo cuenta")
	@RepeatedTest(value=3,name="{displayName} repetido - Repeticion numero: {currentRepetition} de {totalRepetitions}")
	void testSaldoCuenta() {
		assertEquals(1000.2112,cuenta.getSaldo().doubleValue());
	}
	
	@Test
	void testDebitoCuenta()
	{
		assertNotNull(cuenta.getSaldo());
		cuenta.debito(new BigDecimal(100));
		assertEquals(900.2112,cuenta.getSaldo().doubleValue());
	}
	
	@Test
	void testCreditoCuenta()
	{
		assertNotNull(cuenta.getSaldo());
		cuenta.credito(new BigDecimal(100));
		assertEquals(1100.2112,cuenta.getSaldo().doubleValue());
	}
	
	@Test
	void DineroInsuficienteException(){
		Exception e = assertThrows(DineroInsuficienteException.class, ()->{cuenta.debito(new BigDecimal(1001.2112));});
	}
	
	@Test
	void testTransferirDineroCuentas() {
		BigDecimal[] saldo_prueba = {new BigDecimal("1000"),new BigDecimal("2000"),new BigDecimal("500")};
		Cuenta uno = new Cuenta("Usuario_1",saldo_prueba[0]);
		Cuenta dos = new Cuenta("Usuario_2",saldo_prueba[1]);
		
		Banco banco =  new Banco();
		banco.setNombre("Banco pruebas");
		banco.transferir(dos, uno, saldo_prueba[2]);
		assertEquals(saldo_prueba[1].floatValue()-saldo_prueba[2].floatValue(), dos.getSaldo().floatValue());
		assertEquals(saldo_prueba[0].floatValue()+saldo_prueba[2].floatValue(), uno.getSaldo().floatValue());
	}
	
	@Test
	@DisplayName("Prueba de relacion banco-cliente")
	void testRelacionBancoCuentas() {
		BigDecimal[] saldo_prueba = {new BigDecimal("1000"),new BigDecimal("2000"),new BigDecimal("500")};
		Cuenta uno = new Cuenta("Usuario_1",saldo_prueba[0]);
		Cuenta dos = new Cuenta("Usuario_2",saldo_prueba[1]);
		
		Banco banco =  new Banco();
		banco.addCuenta(uno);
		banco.addCuenta(dos);
		banco.setNombre("Banco pruebas");
		assertAll(()->assertEquals(2,banco.cuentas.size()), 
				()->assertEquals("Banco pruebas",uno.getBanco().getNombre()), 
				()->assertEquals("Usuario_2",banco.getCuentas().stream().filter(c->c.getPersona().equals("Usuario_2")).findFirst().get().getPersona()), 
				()->assertTrue(banco.getCuentas().stream().anyMatch(c->c.getPersona().equals("Usuario_1"))));
	}

	@Nested
	class SistemaOperativoTest{
		@Test
		@EnabledOnOs(OS.WINDOWS)
		void testWindows(){
			System.out.println("Tests ejecutados en SO Windows");
		}

		@Test
		@EnabledOnOs(OS.LINUX)
		void testLinux(){
			System.out.println("Tests ejecutados en SO Linux");
		}
	}

	@Nested
	class SystemPropertiesTest{
		@Test
		@Disabled
		void imprimirSP(){
			Properties properties = System.getProperties();
			properties.forEach((k,v)->System.out.println(k+" = "+v));
		}

		@Test
		@EnabledIfSystemProperty(named ="os.arch", matches = ".*64.*")
		void testSP(){
			System.out.println("Tests ejecutados si arquitectura es de 64 bits");
		}
	}

	@Nested
	class EnvironmentVariablesTest{
		@Test
		@Disabled
		void imprimirEV(){
			Map<String, String> getenv = System.getenv();
			getenv.forEach((k,v)->System.out.println(k+" = "+v));
		}

		@Test
		@EnabledIfEnvironmentVariable(named="NUMBER_OF_PROCESSORS", matches="8")
		void testEV(){
			System.out.println("Tests ejecutados si el numero de procesadores es igual a 8");
		}
	}

	@Nested
	class JRETest{
		@Test
		@EnabledOnJre(JRE.JAVA_17)
		void testJRE17(){
			System.out.println("Tests ejecutados en JAVA 17");
		}

		@Test
		@EnabledOnJre(JRE.JAVA_8)
		void testJRE8(){
			System.out.println("Tests ejecutados en JAVA 8");
		}
	}

	@Test
	void testAssuption(){
		boolean var = "B03479".equals(System.getProperty("user.name"));
		boolean var2 = "Oracle Corporation".equals(System.getProperty("java.vendor"));
		assumeTrue(var);
		System.out.println("El metodo de test se ejecuto ya que b03479 es el usuario");
		assumingThat(!var, ()->{
		System.out.println("El metodo de test se ejecuto ya java.vendor es oracle");
		});
	}
	}
