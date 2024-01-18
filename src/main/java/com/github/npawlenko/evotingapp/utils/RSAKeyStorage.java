package com.github.npawlenko.evotingapp.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Getter
@Slf4j
public class RSAKeyStorage {

    private static final String PUBLIC_KEY_PATH = "public.pem";
    private static final String PRIVATE_KEY_PATH = "private.pem";

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    @Value("${application.path}")
    private String parentPath;

    public RSAKeyStorage() {
        File publicKeyFile = new File(parentPath, PUBLIC_KEY_PATH);
        File privateKeyFile = new File(parentPath, PRIVATE_KEY_PATH);
        if (publicKeyFile.exists() && privateKeyFile.exists()) {
            log.info("Loading RSA key pair...");
            try {
                this.publicKey = loadPublicKeyFromPemFile();
                this.privateKey = loadPrivateKeyFromPemFile();
            } catch (Exception e) {
                throw new RuntimeException("Could not load RSA key from file", e);
            }
            log.info("Loaded RSA key pair from existing files");
            return;
        }

        log.info("Generating new RSA key pair...");
        KeyPair keyPair = KeyGenerationUtility.generateRsaKey();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        try {
            savePublicKeyToFile(publicKey.getEncoded());
            savePrivateKeyToFile(privateKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Could not save RSA key to file", e);
        }
        log.info("Saved RSA public key to: {}", publicKeyFile.getAbsolutePath());
        log.info("Saved RSA private key to: {}", privateKeyFile.getAbsolutePath());
    }


    private RSAPublicKey loadPublicKeyFromPemFile() throws Exception {
        String pemContents = readKeyFromFile(Paths.get(parentPath + File.separator + PUBLIC_KEY_PATH).toAbsolutePath());
        String publicKeyPEM = pemContents
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private RSAPrivateKey loadPrivateKeyFromPemFile() throws Exception {
        String pemContents = readKeyFromFile(Paths.get(parentPath + File.separator + PRIVATE_KEY_PATH).toAbsolutePath());
        String privateKeyPEM = pemContents
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private void savePublicKeyToFile(byte[] key) throws IOException {
        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" + Base64.getEncoder().encodeToString(key) + "\n-----END PUBLIC KEY-----";
        String path = parentPath + File.separator + PUBLIC_KEY_PATH;
        Files.write(Paths.get(path).toAbsolutePath(), publicKeyPem.getBytes());
    }

    private void savePrivateKeyToFile(byte[] key) throws IOException {
        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" + Base64.getEncoder().encodeToString(key) + "\n-----END PRIVATE KEY-----";
        String path = parentPath + File.separator + PRIVATE_KEY_PATH;
        Files.write(Paths.get(path).toAbsolutePath(), privateKeyPem.getBytes());
    }

    private String readKeyFromFile(Path path) throws IOException {
        return Files.readString(path);
    }

    private String getParentPath(String path) {
        return this.parentPath;
    }
}
