package com.tspl.minacsaltcrm.webservices;

import android.os.Handler;
import android.os.Message;

import com.tspl.minacsaltcrm.AppConstants;
import com.tspl.minacsaltcrm.ClassObject.Ratings;
import com.tspl.minacsaltcrm.Pr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

/**
 * Created by venupai on 27/4/15.
 */
public class SoapHandler extends AppConstants {
    private Handler handler = null;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    /**
     * Used for login
     * @param username
     * @param password
     * @return
     * @throws JSONException
     */
    public String getLogin(String username, String password) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("UserName", username);
        param.put("Password", password);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        String responce = new SoapController().getStringResponse(validateUser, param);
        return responce;
    }

    public String getMessages(String employeeId, int pageNumber) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("PageNumber", pageNumber);
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        String responce = new SoapController().getStringResponse(getMessage, param);
        return responce;

    }


    public String getUnreadMessageCount(String employeeId) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(getUnReadMessageCount, param);
    }

    /**
     * getLeaveBalance(String employeeId, int queryType)
     * queryType 0 => PF
     8 queryType 1 => Leave Bal
     * @param employeeId
     * @param queryType
     * @return
     * @throws JSONException
     */
    public String getQuery(String employeeId, int queryType) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("QueryType", queryType);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        String responce = new SoapController().getStringResponse(query, param);
        return responce;
    }


    public String getProfile(String employeeId) throws JSONException {
        Pr.ln("getProfile");
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("RequestId", "00000000-0000-0000-0000-000000000000");
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        Pr.ln(param.toString());
        String responce = new SoapController().getStringResponse(getProfile, param);
        Pr.ln(responce.toString());
        return responce.toString();
    }

    public String updateMyProfile(String employeeId, String phone, String twitter, String facebook, String personalEmail, String officialEmail, String userName) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("UserName", userName);
        param.put("Mobile", phone);
        param.put("TwitterId", twitter);
        param.put("FacebookId", facebook);
        param.put("PersonalEmail", personalEmail);
        param.put("OfficeEmail", officialEmail);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        String responce = new SoapController().getStringResponse(saveProfile, param);

        return responce.toString();
    }


    public String updateFeedback(String employeeId, String subject, String content) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("Content", content);
        param.put("Subject", subject);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(setFeedBack, param);
    }

    public String updateSurvey(String employeeId, List<Ratings> listDatas) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        JSONArray jsonArr = new JSONArray();
        for (int i = 0; i < listDatas.size(); i++) {
            JSONObject jsn = new JSONObject();
            jsn.put("Image", null);
            jsn.put("Question", null);
            jsn.put("Active", false);
            jsn.put("CreatedBy", null);
            jsn.put("CreatedOn", null);
            jsn.put("ModifiedBy", null);
            jsn.put("ModifiedOn", null);
            jsn.put("ValidationErrors", null);
            jsn.put("QuestionId", listDatas.get(i).id);
            jsn.put("Rating", listDatas.get(i).rating);
            jsonArr.put(i, jsn);
        }
        param.put("Surveys", jsonArr);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        Pr.ln("param" + param);
        return new SoapController().getStringResponse("SubmitSurvey_JSON", param);
    }

    public String updateMessageRead(String employeeId, long messageId) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("MessageId", messageId);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(updateMessageRead, param);
    }

    public String setAppointment(String employeeId, String comments, int locId, String schedDate) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("Comments", comments);
        param.put("LocationId", locId);
        param.put("ScheduleDate", schedDate);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(setAppointment, param);
    }

    public String getHolidays(String employeeId) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(getHolidays, param);

    }

    public String getLeaveTypes() throws JSONException {
        JSONObject param = new JSONObject();
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(getLeaveTypes, param);

    }

    public String getMapDetails() throws JSONException {
        JSONObject param = new JSONObject();
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(getLocations, param);
    }


    public String getSurvey(String employeeId) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(getSurvey, param);
    }

    public String updateOptionalHolidays(String employeeId, String alternativeHoliday, String comment, String holiday) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("AlternativeHoliday", alternativeHoliday);
        param.put("Comment", comment);
        param.put("Holiday", holiday);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(updateMyOptionalHoliday, param);
    }

    public String requestLeave(String employeeId, String dateFrom, String dateTo, int leaveType, String reason) throws JSONException {
        JSONObject param = new JSONObject();
        JSONObject leaveJson = new JSONObject();
        leaveJson.put("DateFrom", dateFrom);
        leaveJson.put("DateTo", dateTo);
        leaveJson.put("EmployeeId", employeeId);
        leaveJson.put("LeaveType", leaveType);
        leaveJson.put("Reason", reason);
        param.put("leave", leaveJson);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(requestLeave, param);
    }

    public String requestCallback(JSONObject paramsJson) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("CallBack", paramsJson);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(requestCallback, param);
    }

    public String getPolicyList() throws JSONException {
        JSONObject param = new JSONObject();
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(getPolicyList, param);
    }

    public String requestPolicy(String employeeId, String comment, String policyId) throws JSONException {
        JSONObject param = new JSONObject();
        param.put("EmployeeId", employeeId);
        param.put("Comments", comment);
        param.put("PolicyId", policyId);
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(requestPolicy, param);
    }

    public String getCountryCode() throws JSONException {
        JSONObject param = new JSONObject();
        param.put("RequestId", UUID.randomUUID().toString());
        param.put("ServicePassword", ServicePassword);
        param.put("ServiceUserName", ServiceUserName);
        return new SoapController().getStringResponse(getCountryCode, param);
    }


}
