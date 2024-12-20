package br.com.alura.screensound.Service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.OpenAiHttpException;

import java.util.HashMap;
import java.util.Map;

public class ConsultaChatGPT {
    // Cache para armazenar traduções e evitar chamadas repetidas
    private static final Map<String, String> cacheTraducoes = new HashMap<>();

    public static String obterTraducao(String texto) {
        // Verifica se o texto já foi traduzido e retorna do cache
        if (cacheTraducoes.containsKey(texto)) {
            return cacheTraducoes.get(texto);
        }

        OpenAiService service = new OpenAiService(System.getenv("OPENAI_API_KEY"));

        try {
            CompletionRequest requisicao = CompletionRequest.builder()
                    .model("gpt-3.5-turbo-instruct") // Modelo mais econômico
                    .prompt("me fale sobre o artista, se limite a 100 palavras: " + texto)
                    .maxTokens(150) // Limite ajustado para evitar respostas longas
                    .temperature(0.5) // Mais consistência, menos uso de tokens
                    .build();

            var resposta = service.createCompletion(requisicao);
            String traducao = resposta.getChoices().get(0).getText().trim();

            // Armazena no cache para evitar chamadas repetidas
            cacheTraducoes.put(texto, traducao);

            return traducao;
        } catch (OpenAiHttpException e) {
            // Trata erros de cota excedida
            if (e.getMessage().contains("quota")) {
                return "Erro: Limite de requisições da API excedido. Verifique seu plano e tente novamente.";
            }
            throw e; // Repropaga outros erros para depuração
        }
    }
}
