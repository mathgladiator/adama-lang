package org.adamalang.extern;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

import java.util.Collections;

public class SES implements Email {
    // TODO: need to invest in how Amazon does credential management
    private final AmazonSimpleEmailServiceAsync client;

    public SES(AmazonSimpleEmailServiceAsync client) {
        this.client = client;
    }

    @Override
    public void sendCode(String email, String code) {


    }
}
