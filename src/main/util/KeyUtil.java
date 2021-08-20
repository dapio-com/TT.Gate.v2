package main.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.Validate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMParser;


public class KeyUtil {


    public AsymmetricKeyParameter loadPublicKey(InputStream is) {
        SubjectPublicKeyInfo spki = (SubjectPublicKeyInfo) readPemObject(is);
        try {
            return PublicKeyFactory.createKey(spki);
        } catch (IOException ex) {
            throw new RuntimeException("Cannot create public key object based on input data", ex);
        }
    }

    private Object readPemObject(InputStream is) {
        try {
            Validate.notNull(is, "Input data stream cannot be null");
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            PEMParser pemParser = new PEMParser(isr);

            Object obj = pemParser.readObject();
            if (obj == null) {
                throw new Exception("No PEM object found");
            }
            return obj;
        } catch (Throwable ex) {
            throw new RuntimeException("Cannot read PEM object from input data", ex);
        }
    }


}