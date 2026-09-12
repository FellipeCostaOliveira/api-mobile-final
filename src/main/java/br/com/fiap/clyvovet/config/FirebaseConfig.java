package br.com.fiap.clyvovet.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Inicializa o Firebase Admin SDK a partir da variável de ambiente FIREBASE_CREDENTIALS,
 * que pode conter tanto o caminho de um arquivo JSON quanto o próprio JSON da service account.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials}")
    private String credenciais;

    @PostConstruct
    public void inicializar() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        if (credenciais == null || credenciais.isBlank()) {
            throw new IllegalStateException(
                    "FIREBASE_CREDENTIALS não foi definida. Configure essa variável de ambiente com o "
                            + "JSON completo da service account do Firebase (ou, em ambiente local, o caminho "
                            + "para o arquivo .json). A aplicação não pode iniciar sem isso, pois o login do "
                            + "app mobile depende do Firebase Admin SDK.");
        }

        GoogleCredentials credentials;
        try (InputStream stream = abrirStreamDasCredenciais()) {
            credentials = GoogleCredentials.fromStream(stream);
        } catch (IOException ex) {
            boolean pareceCaminhoDeArquivo = credenciais.length() < 300 && !credenciais.trim().startsWith("{");
            String dica = pareceCaminhoDeArquivo
                    ? "O valor parece ser um caminho de arquivo ('" + credenciais + "'), mas esse arquivo não "
                      + "existe no container. Em produção (Render), FIREBASE_CREDENTIALS deve conter o JSON "
                      + "completo da service account, não um caminho."
                    : "O valor não é um JSON válido de service account. Confirme que copiou o arquivo .json "
                      + "inteiro do Firebase Console, sem editar nada, e sem quebras de linha reais dentro do "
                      + "campo private_key (ele já vem com \\n literais).";
            throw new IllegalStateException(
                    "Falha ao carregar as credenciais do Firebase a partir de FIREBASE_CREDENTIALS. " + dica, ex);
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);
        log.info("Firebase Admin SDK inicializado com sucesso");
    }

    private InputStream abrirStreamDasCredenciais() throws IOException {
        Path caminho = Path.of(credenciais);
        if (Files.exists(caminho)) {
            return new FileInputStream(caminho.toFile());
        }
        // FIREBASE_CREDENTIALS contém o JSON diretamente (ex.: variável de ambiente no Azure)
        return new ByteArrayInputStream(credenciais.getBytes(StandardCharsets.UTF_8));
    }
}
