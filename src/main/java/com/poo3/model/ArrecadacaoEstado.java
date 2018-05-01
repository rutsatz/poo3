package com.poo3.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrecadacaoEstado {

	@JsonProperty("nodes")
	List<ArrecadacaoEstadoNode> arrecadacoes;

	RestTemplate restTemplate = new RestTemplate();
	private final String URL = "http://dadosabertos.dataprev.gov.br/opendata/Arr01/formato=json";

	public ArrecadacaoEstado() {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new ByteArrayHttpMessageConverter());

		restTemplate = new RestTemplate(messageConverters);
	}

	/**
	 * Busca a lista de arrecadações por municipio.
	 * 
	 * @param value
	 * @return
	 */
	public String getLastArrecadations(Integer value) {

		try {
			// Baixa arquivo.
			String pathFile = fetchFile();
			File file = new File(pathFile);
			// Faz a leitura do arquivo.
			String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

			ObjectMapper mapper = new ObjectMapper();
			mapper.setLocale(new Locale("pt", "BR"));
			mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

			ArrecadacaoEstado estados = mapper.readValue(content, ArrecadacaoEstado.class);

			this.arrecadacoes = estados.getArrecadacoes();

			StringBuilder sb = new StringBuilder("Últimas " + value + " arrecadações registradas: ");
			sb.append("\n ano  -  valor   - cidade");

			Comparator<ArrecadacaoEstadoNode> comparator = new Comparator<ArrecadacaoEstadoNode>() {

				@Override
				public int compare(ArrecadacaoEstadoNode o1, ArrecadacaoEstadoNode o2) {
					int res = o1.getArrecadacaoEstadoNodeValores().getAno()
							.compareTo(o2.getArrecadacaoEstadoNodeValores().getAno());

					return res;
				}
			};

			arrecadacoes = arrecadacoes.stream().sorted(comparator.reversed()).limit(value)
					.collect(Collectors.toList());

			arrecadacoes.forEach(a -> sb.append("\n" + a.getArrecadacaoEstadoNodeValores().getAno() + " - R$ "
					+ a.getArrecadacaoEstadoNodeValores().getValorArrecadado() + " - "
					+ a.getArrecadacaoEstadoNodeValores().getUnidadeFederacao()));
			System.out.println(arrecadacoes);
			return sb.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Faz download do arquivo.
	 * 
	 * @throws IOException
	 * @return Caminho do arquivo baixado.
	 */
	public String fetchFile() throws IOException {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<byte[]> response = restTemplate.exchange(URL, HttpMethod.GET, entity, byte[].class, "1");

		if (response.getStatusCode() == HttpStatus.OK) {

			String property = "java.io.tmpdir";

			String tempDir = System.getProperty(property);
			String separator = System.getProperty("file.separator");
			System.out.println("Pasta temp do Sistema: " + tempDir + ", Separador: " + separator);

			String filePath = tempDir + separator + "arrecadacao.json";

			Files.write(Paths.get(filePath), response.getBody());
			return filePath;
		}
		return null;
	}

	public List<ArrecadacaoEstadoNode> getArrecadacoes() {
		return this.arrecadacoes;
	}

}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class ArrecadacaoEstadoNode {

	@JsonProperty("node")
	private ArrecadacaoEstadoNodeValores arrecadacaoEstadoNodeValores;

	public ArrecadacaoEstadoNodeValores getArrecadacaoEstadoNodeValores() {
		return this.arrecadacaoEstadoNodeValores;
	}
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class ArrecadacaoEstadoNodeValores {

	@JsonProperty("Ano")
	private Integer ano;

	@JsonProperty("M�s")
	private String mes;

	@JsonProperty("Unidade da Federa��o")
	private String unidadeFederacao;

	@JsonProperty("Valor Arrecadado (R$)")
	private String valorArrecadado;

	public Integer getAno() {
		return this.ano;
	}

	public String getMes() {
		return this.mes;
	}

	public String getUnidadeFederacao() {
		return this.unidadeFederacao;
	}

	public String getValorArrecadado() {
		return this.valorArrecadado;
	}
}
