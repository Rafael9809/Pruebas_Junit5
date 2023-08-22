package mx.com.vepormas.pruebas.ejemplo;

import mx.com.vepormas.pruebas.ejemplo.modelos.Banco;
import mx.com.vepormas.pruebas.ejemplo.modelos.Cuenta;
import mx.com.vepormas.pruebas.exceptions.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.Timeout;

//@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CuentaTest {
	Cuenta cuenta;
	TestInfo testInfo; 
	TestReporter testReporter;

	@BeforeAll
	static void antes(){
		System.out.println("Iniciando pruebas");
	}

	@AfterAll
	static void despues(){
		System.out.println("Finalizando pruebas");
	}

	@BeforeEach
	void initMethodTest(TestInfo testInfo, TestReporter testReporter){
		this.cuenta = new Cuenta("Rafael", new BigDecimal("1000.2112"));
		this.testInfo=testInfo;
		this.testReporter=testReporter;
		if(testInfo.getTags().size()>0){
			System.out.println("Metodo test con etiqueta: "+testInfo.getTags());
		}
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

	@Tag("jre")
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

	@ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
	@ValueSource(strings = {"100","200","900"})
	void testDebitoCuentaValueSource(String monto)
	{
		assertNotNull(cuenta.getSaldo());
		cuenta.debito(new BigDecimal(monto));
		assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
	}

	@ParameterizedTest(name = "metodo con csv en argumento, numero {index} ejecutando con valor {0} - {argumentsWithNames}")
	@CsvSource({"1,100", "2,200", "3,500", "4,900"})
	void testDebitoCuentaCsv(String index, String monto)
	{
		assertNotNull(cuenta.getSaldo());
		cuenta.debito(new BigDecimal(monto));
		assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
	}

	@ParameterizedTest(name = "metodo con csv en archivo, numero {index} ejecutando con valor {0} - {argumentsWithNames}")
	@CsvFileSource(resources = "/mx/com/vepormas/pruebas/data.csv")
	void testDebitoCuentaCsvFile(String monto)
	{
		assertNotNull(cuenta.getSaldo());
		cuenta.debito(new BigDecimal(monto));
		assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
	}

	@ParameterizedTest(name = "metodo con csv en metodo, numero {index} ejecutando con valor {0} - {argumentsWithNames}")
	@MethodSource("montoList")
	void testDebitoCuentaMethodSource(String monto)
	{
		assertNotNull(cuenta.getSaldo());
		cuenta.debito(new BigDecimal(monto));
		assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
	}

	static List<String> montoList(){
		return Arrays.asList("100", "200", "900");
	}

	@Test
	@Tag("hola")
	void testInfo(){
		System.out.println("ejecutando: "+testInfo.getDisplayName()+" "+testInfo.getTestMethod()
		+" con las etiquetas "+testInfo.getTags());
	}

	@Tag("Timeout")
	@Nested
	class timeOut{
		
		@Test
		@Timeout(5)
		void testTimeout() throws InterruptedException{
			TimeUnit.SECONDS.sleep(4);
		}

		@Test
		@Timeout(value=2000,unit=TimeUnit.MILLISECONDS)
		void testTimeout2() throws InterruptedException{
			TimeUnit.SECONDS.sleep(1);
		}

		@Test
		void testTimeout3() {
			assertTimeout(Duration.ofSeconds(5),()->TimeUnit.SECONDS.sleep(4));
		}
		}
	}

