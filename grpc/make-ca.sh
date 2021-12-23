#!/bin/sh
SUBJ="/C=US/ST=Kansas/L=KansasCity/O=Adama/OU=Adama/CN=adama.com/emailAddress=admin@adama.com"

# make ca-cert.pem for client and server to use
mkdir ca
openssl req -x509 -newkey rsa:4096 -days 365 -nodes -keyout ca/ca-key.pem -out ca/ca-cert.pem -subj "$SUBJ"
