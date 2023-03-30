package pl.newsler.security;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public interface NLIKeyProvider {
    byte[] getKey(String alias) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException;

    char[] getCharKey(String alias) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException;
}