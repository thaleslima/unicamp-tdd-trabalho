package br.com.comprefacil;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import br.com.comprefacil.frete.CalculoFrete;
import br.com.comprefacil.frete.CalculoFreteStub;
import br.com.comprefacil.frete.CodigoRetornoFrete;
import br.com.comprefacil.frete.Frete;

public class UC01Teste {

	@Test
	public void valorDofrete_Valido() {
		//Este teste j� contempla as dimens�es como testes v�lidos
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("10", new BigDecimal("11"), new BigDecimal("16"),
				new BigDecimal("11"), "40010", "14400180");
		
		Assert.assertTrue(frete.getValor() > 0);
		Assert.assertTrue(frete.getTempoEntrega() > 0);
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.PESO_CORRETO.getValue()));
	}

	@Test
	public void Sedex_pesoNegativo_testeInvalido() {
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("-1", new BigDecimal("10"), new BigDecimal("10"),
				new BigDecimal("10"), "40010", "14400180");

		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.PESO_NEGATIVO.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega() == 0);
	}
	
	@Test
	public void Sedex_pesoExcedido_testeInvalido(){
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("31", new BigDecimal("11"), new BigDecimal("16"),
				new BigDecimal("11"), "40010", "14400180");
		
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.PESO_EXCEDIDO.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega() == 0);
	}
	
	@Test
	public void Sedex10_pesoExcedido_testeInvalido(){
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("16", new BigDecimal("11"), new BigDecimal("16"),
				new BigDecimal("11"), "40215", "14400180");
		
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.PESO_EXCEDIDO.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega() == 0);
	}
	
	@Test
	public void SedexPAC_pesoExcedido_testeInvalido(){
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("50", new BigDecimal("11"), new BigDecimal("16"),
				new BigDecimal("11"), "41106", "14400180");
		
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.PESO_EXCEDIDO.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega() == 0);
	}
	
	@Test
	public void comprimentoMinIncorreto_testeInvalido(){
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("10", new BigDecimal("11"), new BigDecimal("9"),
				new BigDecimal("11"), "40010", "14400180");
		
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.COMPRIMENTO_MIN_INVALIDO.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega() == 0);
	}
	
	@Test
	public void comprimentoMaxIncorreto_testeInvalido(){
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("10", new BigDecimal("11"), new BigDecimal("106"),
				new BigDecimal("11"), "40010", "14400180");
		
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.COMPRIMENTO_MAX_EXCEDIDO.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega() == 0);
	}
	
	@Test
	public void tipoInvalido_testeInvalido(){
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("10", new BigDecimal("20"), new BigDecimal("20"), 
				new BigDecimal("20"), "11111", "14400180");
		
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.TIPOINVALIDO.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega()==0);
	}
	
	@Test
	public void CEPInvalido_testeInvalido(){
		CalculoFrete calculoFrete = new CalculoFrete();
		
		//envio de menos de 8 caracteres
		Frete frete = calculoFrete.calcularFrete("10", new BigDecimal("11"), new BigDecimal("16"),
				new BigDecimal("11"), "40010", "123456");
		
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.CEPDESTINVALIDO.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega()==0);
		
		//envio de mais de 8 caracteres
		frete = calculoFrete.calcularFrete("10", new BigDecimal("11"), new BigDecimal("16"),
				new BigDecimal("11"), "40010", "123456789");
		
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.CEPDESTINVALIDO.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega()==0);

		//envio de letra nos 8 caracteres
		frete = calculoFrete.calcularFrete("10", new BigDecimal("11"), new BigDecimal("16"),
				new BigDecimal("11"), "40010", "aabbbccc");
		
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.CEPDESTINVALIDO.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega()==0);
	}
	
	@Test
	public void larguraMaxIncorreto_testeInvalido(){
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("10", new BigDecimal("106"), new BigDecimal("16"),
				new BigDecimal("11"), "40010", "14400180");
		
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.LARGURA_MAX_EXCEDIDA.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega() == 0);
	}
	
	@Test
	public void larguraMinIncorreto_testeInvalido(){
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("10", new BigDecimal("10"), new BigDecimal("16"),
				new BigDecimal("11"), "40010", "14400180");
		
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.LARGURA_MIN_NEXCEDIDA.getValue()));
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega() == 0);
	}
	
	@Test
	public void alturaMaxIncorreto_testeInvalido() {
		//Este teste j� contempla as dimens�es como testes v�lidos
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("10", new BigDecimal("11"), new BigDecimal("16"),
				new BigDecimal("106"), "40010", "14400180");
		
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega() == 0);
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.ALTURA_MAX_EXCEDIDA.getValue()));
	}
	
	@Test
	public void alturaMinIncorreto_testeInvalido() {
		//Este teste j� contempla as dimens�es como testes v�lidos
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("10", new BigDecimal("11"), new BigDecimal("16"),
				new BigDecimal("1"), "40010", "14400180");
		
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega() == 0);
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.ALTURA_MIN_EXCEDIDA.getValue()));
	}
	
	@Test
	public void somaDimensoesMaxIncorreto_testeInvalido() {
		//Este teste j� contempla as dimens�es como testes v�lidos
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("10", new BigDecimal("105"), new BigDecimal("105"),
				new BigDecimal("2"), "40010", "14400180");
		
		Assert.assertTrue(frete.getValor() == 0);
		Assert.assertTrue(frete.getTempoEntrega() == 0);
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.SOMA_DIMENSOES_EXCEDIDA.getValue()));
	}
	
	@Test
	public void somaDimensoesLimite_testeValido() {
		//Este teste j� contempla as dimens�es como testes v�lidos
		CalculoFrete calculoFrete = new CalculoFrete();
		Frete frete = calculoFrete.calcularFrete("10", new BigDecimal("90"), new BigDecimal("90"),
				new BigDecimal("20"), "40010", "14400180");
		
		Assert.assertTrue(frete.getValor() > 0);
		Assert.assertTrue(frete.getTempoEntrega() > 0);
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.SOMA_CORRETA.getValue()));
	}
	
	@Test
	public void erroCorreiosForaAr_testeValido(){
		String nCdEmpresa = "";
		String sDsSenha = "";
		String nCdServico = "40010";
		String sCepOrigem = "13081970";
		String sCepDestino = "14400180";
		String nVlPeso = "10";
		int nCdFormato = 1;
		BigDecimal nVlComprimento = new BigDecimal("90");
		BigDecimal nVlAltura = new BigDecimal("20");
		BigDecimal nVlLargura = new BigDecimal("90");
		BigDecimal nVlDiametro = new BigDecimal("1");
		String sCdMaoPropria = "N";
		BigDecimal nVlValorDeclarado = new BigDecimal("0");
		String sCdAvisoRecebimento = "N"; 
		
		
		stubFor(get(
				urlEqualTo("/getPrecoPrazo?nCdEmpresa=" + nCdEmpresa
						+ "&sDsSenha=" + sDsSenha 
						+ "&nCdServico=" + nCdServico
						+ "&sCepOrigem=" + sCepOrigem 
						+ "&sCepDestino=" + sCepDestino 
						+ "&nVlPeso=" + nVlPeso 
						+ "&nCdFormato="+ nCdFormato 
						+ "&nVlComprimento=" + nVlComprimento
						+ "&nVlAltura=" + nVlAltura 
						+ "&nVlLargura="+ nVlLargura 
						+ "&nVlDiametro=" + nVlDiametro
						+ "&sCdMaoPropria=" + sCdMaoPropria
						+ "&nVlValorDeclarado=" + nVlValorDeclarado
						+ "&sCdAvisoRecebimento=" + sCdAvisoRecebimento))
				.willReturn(
				aResponse().withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("{ \"valor\":\"0\", \"tempoEntrega\":\"0\", \"erroCod\":\"33\","
						+ "\"erroMsg\":\"Sistema temporariamente fora do ar. Favor tentar mais tarde.\" }")));
	
		CalculoFreteStub calcularFrete = new CalculoFreteStub();
		Frete frete = calcularFrete.calcularFreteStub(nCdEmpresa, sDsSenha, nCdServico, sCepOrigem, sCepDestino, nVlPeso, nCdFormato, nVlComprimento, nVlAltura, nVlLargura, nVlDiametro, sCdMaoPropria, nVlValorDeclarado, sCdAvisoRecebimento);
	
		Assert.assertTrue(new Double(0.0).equals(frete.getValor()));
		Assert.assertTrue(new Double(0.0).equals(frete.getTempoEntrega()));
		Assert.assertTrue(frete.getErroCod().equals(CodigoRetornoFrete.SOMA_CORRETA.getValue()));
//		Assert.assertEquals("Sistema temporariamente fora do ar. Favor tentar mais tarde.",frete.getErroMsg());
				
	}
}
