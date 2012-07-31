package com.areahomeschoolers.baconbits.server.paypal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.paypal.adaptive.api.requests.fnapi.SimplePay;
import com.paypal.adaptive.api.responses.PayResponse;
import com.paypal.adaptive.core.APICredential;
import com.paypal.adaptive.core.AckCode;
import com.paypal.adaptive.core.CurrencyCodes;
import com.paypal.adaptive.core.PayError;
import com.paypal.adaptive.core.PaymentType;
import com.paypal.adaptive.core.Receiver;
import com.paypal.adaptive.core.ServiceEnvironment;
import com.paypal.adaptive.exceptions.AuthorizationRequiredException;
import com.paypal.adaptive.exceptions.InvalidAPICredentialsException;
import com.paypal.adaptive.exceptions.InvalidResponseDataException;
import com.paypal.adaptive.exceptions.MissingAPICredentialsException;
import com.paypal.adaptive.exceptions.MissingParameterException;
import com.paypal.adaptive.exceptions.PayPalErrorException;
import com.paypal.adaptive.exceptions.PaymentExecException;
import com.paypal.adaptive.exceptions.PaymentInCompleteException;
import com.paypal.adaptive.exceptions.RequestAlreadyMadeException;
import com.paypal.adaptive.exceptions.RequestFailureException;

public class PayPalCredentials {
	private static APICredential credentials = new APICredential();

	public PayPalCredentials() {
		if (ServerContext.isLive()) {
			credentials.setAPIUsername("weare.home.educators_api1.gmail.com");
			credentials.setAPIPassword("BZFADA4CXJSCHYW8");
			credentials.setSignature("AFcWxV21C7fd0v3bYYYRCpSSRl31AIiJDk-qOGV.J6VfFcbztTiAOVoS");
			credentials.setAccountEmail("weare.home.educators@gmail.com");
			credentials.setAppId("b9374d996b19e1a5a34abcc3f070b04f");
		} else {
			credentials.setAPIUsername("paul.a_1343673136_biz_api1.gmail.com");
			credentials.setAPIPassword("1343673159");
			credentials.setSignature("AirQtU1053N9JvGp9xtKZtxvbZojAv39XGRp8tQ8KFG.CaL5sQWMC8F6");
			credentials.setAccountEmail("paul.a_1343673136_biz@gmail.com");
			credentials.setAppId("APP-80W284485P519543T");
		}

		try {
			StringBuilder url = new StringBuilder();
			// TODO
			url.append("http://127.0.0.1:8888/?gwt.codesvr=127.0.0.1:9997#page=Payment");
			String returnURL = url.toString() + "&return=1&action=pay&payKey=${payKey}";
			String cancelURL = url.toString() + "&action=pay&cancel=1";
			String ipnURL = url.toString() + "&action=ipn";

			SimplePay payment = new SimplePay();
			payment.setCancelUrl(cancelURL);
			payment.setReturnUrl(returnURL);
			payment.setCredentialObj(credentials);
			// TODO
			payment.setUserIp("127.0.0.1");
			payment.setApplicationName("WeAre Home Educators");
			payment.setCurrencyCode(CurrencyCodes.USD);
			// TODO
			payment.setEnv(ServiceEnvironment.SANDBOX);
			payment.setIpnURL(ipnURL);
			payment.setLanguage("en_US");
			// TODO
			payment.setMemo("A test payment");
			Receiver receiver = new Receiver();
			// TODO
			receiver.setAmount(22.14);
			// TODO
			receiver.setEmail("paul.a_1343673136_biz@gmail.com");
			receiver.setPaymentType(PaymentType.SERVICE);
			payment.setReceiver(receiver);
			// TODO
			payment.setSenderEmail("paul.a_1343673034_per@gmail.com"); // password: 343740218

			PayResponse payResponse = payment.makeRequest();
			System.out.println("PaymentExecStatus:" + payResponse.getPaymentExecStatus().toString());
		} catch (IOException e) {
			System.out.println("Payment Failed w/ IOException");
		} catch (MissingAPICredentialsException e) {
			// No API Credential Object provided - log error
			// e.printStackTrace();
			System.out.println("No APICredential object provided");
		} catch (InvalidAPICredentialsException e) {
			// invalid API Credentials provided - application error - log error
			// e.printStackTrace();
			System.out.println("Invalid API Credentials " + e.getMissingCredentials());
		} catch (MissingParameterException e) {
			// missing parameter - log error
			// e.printStackTrace();
			System.out.println("Missing Parameter error: " + e.getParameterName());
		} catch (RequestFailureException e) {
			// HTTP Error - some connection issues ?
			// e.printStackTrace();
			System.out.println("Request HTTP Error: " + e.getHTTP_RESPONSE_CODE());
		} catch (InvalidResponseDataException e) {
			// PayPal service error
			// log error
			// e.printStackTrace();
			System.out.println("Invalid Response Data from PayPal: \"" + e.getResponseData() + "\"");
		} catch (PayPalErrorException e) {
			// Request failed due to a Service/Application error
			// e.printStackTrace();
			if (e.getResponseEnvelope().getAck() == AckCode.Failure) {
				// log the error
				System.out.println("Received Failure from PayPal (ack)");
				System.out.println("ErrorData provided:");
				System.out.println(e.getPayErrorList().toString());
				for (PayError error : e.getPayErrorList()) {
					System.out.println(error.getError().getMessage());
				}
				if (e.getPaymentExecStatus() != null) {
					System.out.println("PaymentExecStatus: " + e.getPaymentExecStatus());
				}
			} else if (e.getResponseEnvelope().getAck() == AckCode.FailureWithWarning) {
				// there is a warning - log it!
				System.out.println("Received Failure with Warning from PayPal (ack)");
				System.out.println("ErrorData provided:");
				System.out.println(e.getPayErrorList().toString());
			}
		} catch (RequestAlreadyMadeException e) {
			// shouldn't occur - log the error
			// e.printStackTrace();
			System.out.println("Request to send a request that has already been sent!");
		} catch (PaymentExecException e) {
			System.out.println("Failed Payment Request w/ PaymentExecStatus: " + e.getPaymentExecStatus().toString());
			System.out.println("ErrorData provided:");

			System.out.println(e.getPayErrorList().toString());
		} catch (PaymentInCompleteException e) {
			System.out.println("Incomplete Payment w/ PaymentExecStatus: " + e.getPaymentExecStatus().toString());
			System.out.println("ErrorData provided:");

			System.out.println(e.getPayErrorList().toString());
		} catch (AuthorizationRequiredException e) {
			// redirect the user to PayPal for Authorization
			// resp.sendRedirect(e.getAuthorizationUrl(ServiceEnvironment.SANDBOX));

			try {
				System.out.println(e.getAuthorizationUrl(ServiceEnvironment.SANDBOX));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
	}

	public APICredential getCredentials() {
		return credentials;
	}
}
