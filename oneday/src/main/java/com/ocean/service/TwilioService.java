package com.ocean.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class TwilioService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String phoneNumber;

    public void sendReservationConfirmation(String to, String reservationDetails) {
        Twilio.init(accountSid, authToken);

        String message = "Reservation confirmed! Details: " + reservationDetails;

        Message.creator(
            new PhoneNumber(to),
            new PhoneNumber(phoneNumber),
            message
        ).create();
    }
}
