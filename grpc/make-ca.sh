#!/bin/sh
SUBJ="/C=US/ST=Kansas/L=OverlandPark/O=Adama/OU=Adama/CN=adama.com/emailAddress=admin@adama.com"

# make ca-cert.pem for client and server to use
mkdir ca
openssl req -x509 -newkey rsa:4096 -days 365 -nodes -keyout ca/ca-key.pem -out ca/ca-cert.pem -subj "$SUBJ"
# make private key for server to use


# openssl req -newkey rsa:4096 -nodes -keyout server-key.pem -out server-req.pem -subj "$SUBJ"
# make private key for server to use
# openssl req -newkey rsa:4096 -nodes -keyout client-key.pem -out client-req.pem -subj "$SUBJ"


# stamp out a host certificate
# echo "subjectAltName=IP:127.0.0.1" > ext.cnf
# openssl x509 -req -in server-req.pem -days 60 -CA ca-cert.pem -CAkey ca-key.pem -CAcreateserial -out server-cert.pem -extfile ext.cnf
# rm ext.cnf

# echo "subjectAltName=IP:127.0.0.1" > ext.cnf
# openssl x509 -req -in client-req.pem -days 60 -CA ca-cert.pem -CAkey ca-key.pem -CAcreateserial -out client-cert.pem -extfile ext.cnf
# rm ext.cnf