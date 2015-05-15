package com.tspl.minacsaltcrm.webservices;

import android.util.Log;

import com.tspl.minacsaltcrm.AppConstants;
import com.tspl.minacsaltcrm.Pr;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by venupai on 27/4/15.
 */
public class SoapController extends AppConstants {
//    String jsn = "{\"EmployeeId\":\"047841\",\"RequestId\":\"00000000-0000-0000-0000-000000000000\",\"ServicePassword\":\"Minacs123\",\"ServiceUserName\":\"Himanshu.kaushal\"}";


    public String getStringResponse(String method, JSONObject parameters) {
        Pr.ln("parameters-- " + parameters);
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, method);
        request.addProperty(PROPERTY_NAME, parameters.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        String SOAP_ACTION = WSDL_TARGET_NAMESPACE + "IActions/" + method;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response = null;
        try {

            androidHttpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
            Pr.ln("Object response"+ response.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
