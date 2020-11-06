package com.example.rtproduct;


import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

    public class SwarmSSLSocketFactory extends javax.net.ssl.SSLSocketFactory {

        //
        // Constants
        //

        public static final String TAG = "SwarmKeyStoreFactory";

        //
        // Fields
        //

        private javax.net.ssl.SSLSocketFactory socketFactory;
        private TrustManagerFactory trustManagerFactory;

        //
        // Constructor
        //

        public SwarmSSLSocketFactory(Context androidContext, int caRootStoreResourceId, int clientStoreResourceId, int passwordResourceId)
                throws IllegalArgumentException
        {

            try {

                KeyStore caRootStore = KeyStore.getInstance("BKS");
                InputStream caRootStoreFile = androidContext.getResources().openRawResource(caRootStoreResourceId);
                KeyStore clientStore = KeyStore.getInstance("BKS");
                InputStream clientStoreFile = androidContext.getResources().openRawResource(clientStoreResourceId);

                String password = androidContext.getResources().getString(passwordResourceId);

                try {
                    caRootStore.load(caRootStoreFile, password.toCharArray());
                    clientStore.load(clientStoreFile, password.toCharArray());
                }
                finally {
                    caRootStoreFile.close();
                    clientStoreFile.close();
                }

                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(clientStore, password.toCharArray());

                trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(caRootStore);

                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(
                        keyManagerFactory.getKeyManagers(),
                        trustManagerFactory.getTrustManagers(),
                        new SecureRandom()
                );

                this.socketFactory = sslContext.getSocketFactory();

            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Cannot build key store", ex);
            }
        }

        //
        // SSLSocketFactory Overrides
        //


        public TrustManager[] getTrustManagers() {
            return trustManagerFactory.getTrustManagers();
        }

        public String[] getDefaultCipherSuites() {
            return socketFactory.getDefaultCipherSuites();
        }

        public String[] getSupportedCipherSuites() {
            return socketFactory.getSupportedCipherSuites();
        }

        public Socket createSocket() throws IOException {
            SSLSocket r = (SSLSocket)socketFactory.createSocket();
            r.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"});
            return r;
        }

        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            SSLSocket r = (SSLSocket)socketFactory.createSocket(s, host, port, autoClose);
            r.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"});
            return r;
        }

        public Socket createSocket(String host, int port) throws IOException {

            SSLSocket r = (SSLSocket)socketFactory.createSocket(host, port);
            r.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"});
            return r;
        }

        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
            SSLSocket r = (SSLSocket)socketFactory.createSocket(host, port, localHost, localPort);
            r.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"});
            return r;
        }

        public Socket createSocket(InetAddress host, int port) throws IOException {
            SSLSocket r = (SSLSocket)socketFactory.createSocket(host, port);
            r.setEnabledProtocols(new String[]{ "TLSv1", "TLSv1.1", "TLSv1.2"});
            return r;
        }

        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            SSLSocket r = (SSLSocket)socketFactory.createSocket(address, port, localAddress,localPort);
            r.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"});
            return r;
        }



    }

