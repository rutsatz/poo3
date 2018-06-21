package com.poo3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.poo3.model.ArrecadacaoEstado;
import com.poo3.model.SalarioMinimo;
import com.poo3.rest.TwitterIntegration;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.TwitterException;

@Service
public class TwitterService {

	@Autowired
	public TwitterIntegration twitterIntegration;

	public void processRequest(DirectMessage directMessage) {
		String text = directMessage.getText();
		long user = directMessage.getSenderId();
		sendDirect(user, text);
	}

	public void processRequest(Status status) {

		String text = trataComandos(status.getText());
		// status.getText() + " em resposta a " + status.getUser().getScreenName();

		StringBuilder resposta = new StringBuilder(text);
		resposta.append("\nEm resposta a @" + status.getUser().getScreenName());
		post(resposta.toString());
	}

	public static void main(String[] args) {
		boolean value = "\\help3 @trabalhopoo".matches("\\help");
		System.out.println(value);
	}

	private String trataComandos(String comando) {
		int maxResults = 3;
		if (comando.contains("\\help")) {
			return "Lista de comandos: \n" + "\\help \n" + "salario_minimo \n arrecadacao_estado";

		} else if (comando.contains("salario_minimo")) {
			return new SalarioMinimo().getLastYears(maxResults); // Últimos 10 registros.

		} else if (comando.contains("arrecadacao_estado")) {
			return new ArrecadacaoEstado().getLastArrecadations(maxResults); // Últimos 10 registros.

		}
		return comando;
	}

	public void post(String message) {

		twitterIntegration.postTweet(message);
	}

	public void sendDirect(long recipientId, String text) {
		try {
			twitterIntegration.sendDirect(recipientId, text);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

}
