package com.poo3.model;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Consulta a base do governo com os últimos registros de salário mínimo.
 * 
 * @author raffa
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalarioMinimo {
	@JsonProperty("valores")
	List<SalarioMinimoValores> valores;

	RestTemplate restTemplate = new RestTemplate();
	private final String URL = "http://api.pgi.gov.br/api/1/serie/1567.json";

	public SalarioMinimo() {
	}

	/**
	 * Filtra a lista retornada para retornar os últimos registros.
	 * 
	 * @param Quantidade
	 *            de anos desejada.
	 * @return Lista com os anos e valores filtrados.
	 */
	public String getLastYears(Integer value) {

		SalarioMinimo salarios = restTemplate.getForObject(URL, SalarioMinimo.class);
		this.valores = salarios.getValores();

		StringBuilder sb = new StringBuilder("Últimos " + value + " salários registrados: ");
		sb.append("\n ano  -  valor");
		valores = valores.stream().sorted(Comparator.comparing(SalarioMinimoValores::getValor).reversed()).limit(value)
				.collect(Collectors.toList());
		valores.forEach(v -> sb.append("\n" + v.getAno() + " - R$ " + v.getValor()));
		System.out.println(valores);
		return sb.toString();
	}

	public List<SalarioMinimoValores> getValores() {
		return this.valores;
	}
}

/**
 * Classe para representar os dados retornados pelo serviço.
 * 
 * @author raffa
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class SalarioMinimoValores {
	@JsonProperty("valor")
	private Double valor;
	@JsonProperty("ano")
	private Integer ano;

	public Integer getAno() {
		return this.ano;
	}

	public Double getValor() {
		return this.valor;
	}

	@Override
	public String toString() {
		return ano + " - " + valor;
	}
}