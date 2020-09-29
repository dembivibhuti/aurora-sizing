package org.anonymous.connection;

import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;

public class GetDBCredsByIAM {

    // AWS Credentials of the IAM user with policy enabling IAM Database Authenticated access to the db by the db user.
    private static final DefaultAWSCredentialsProviderChain creds = new DefaultAWSCredentialsProviderChain();
    // private static final InstanceProfileCredentialsProvider creds =
    // InstanceProfileCredentialsProvider.createAsyncRefreshingProvider(true);

    private static final String AWS_ACCESS_KEY = creds.getCredentials().getAWSAccessKeyId();
    private static final String AWS_SECRET_KEY = creds.getCredentials().getAWSSecretKey();

    /*
     * public static String generateAuthToken(String region, String rdsInstanceHostname, int rdsInstancePort, String
     * userName) {
     * 
     * System.out.println("AWS_ACCESS_KEY " + AWS_ACCESS_KEY); System.out.println("AWS_SECRET_KEY " + AWS_SECRET_KEY);
     * BasicAWSCredentials awsCredentials = new BasicAWSCredentials("ASIARHC5AWYNXSSWI67Z",
     * "ZXikyrTZn7c/s6tEdf1vzyERN436lYATMbtHuem1");
     * 
     * RdsIamAuthTokenGenerator generator = RdsIamAuthTokenGenerator.builder() .credentials(new
     * AWSStaticCredentialsProvider(awsCredentials)).region(region).build(); return
     * generator.getAuthToken(GetIamAuthTokenRequest.builder().hostname(rdsInstanceHostname)
     * .port(rdsInstancePort).userName(userName).build()); }
     */

    public static String generateAuthToken(String region, String hostName, int port, String username) {

        RdsIamAuthTokenGenerator generator = RdsIamAuthTokenGenerator.builder()
                .credentials(new DefaultAWSCredentialsProviderChain()).region(region).build();

        String authToken = generator.getAuthToken(
                GetIamAuthTokenRequest.builder().hostname(hostName).port(port).userName(username).build());

        return authToken;
    }

}
