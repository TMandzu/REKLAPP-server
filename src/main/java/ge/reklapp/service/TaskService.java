package ge.reklapp.service;

import ge.reklapp.db.DBConnectionProvider;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Tornike on 10.07.2016.
 */
@Path("/")
@Consumes( { MediaType.APPLICATION_JSON})
@Produces( { MediaType.APPLICATION_JSON})
public class TaskService {
    @POST
    @Path("/users/{mobile_number}/transfer/{address}")
    public StatusResponse transferMoney(@PathParam("mobile_number") String mobile_number, @PathParam("address") String address, Money money){
        StatusResponse statusResponse = new StatusResponse("");
        try (Connection con = DBConnectionProvider.getConnection()) {
            try (PreparedStatement st =
                         con.prepareStatement("SELECT * FROM users WHERE mobile_number=?")) {
                st.setString(1,"'" + mobile_number + "'");
                ResultSet res = st.executeQuery();
                if (res.getFetchSize() == 0){
                    statusResponse.setProblem("Can not find user.");
                }else{
                    res.first();
                    double amountNow = res.getDouble("money");
                    double delta = money.getAmount();
                    if (amountNow >= delta){
                        double newAmount = amountNow - delta;
                        try (PreparedStatement st2 =
                                     con.prepareStatement("UPDATE users SET money=? WHERE mobile_number=?")) {
                            st2.setDouble(1, newAmount);
                            st2.setString(2, mobile_number);
                            int size = st2.executeUpdate();
                            if (size > 0){
                                statusResponse.setProblem("Transfer Completed.");
                                transferToAddress(delta, address);
                            }else{
                                statusResponse.setProblem("Transfer was NOT completed. Problem occurred.");
                            }
                        }

                    }else{
                        statusResponse.setProblem("You have not enough money.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusResponse.setProblem("Transfer was NOT completed. Problem occurred.");
        }
        return statusResponse;
    }

    private void transferToAddress(double amount, String address){
        // TODO
    }

    @PUT
    @Path("/users")
    public StatusResponse updateUser(UserInfo info){
        StatusResponse statusResponse = new StatusResponse("");
        checkUserInfo(info, statusResponse);

        if (statusResponse.getProblem().length() > 0)
            return statusResponse;

        try (Connection con = DBConnectionProvider.getConnection()) {
            try (PreparedStatement st =
                         con.prepareStatement("DELETE FROM users WHERE mobile_number=?")) {

                st.setString(1, info.getOld_mobile_number());

                st.executeUpdate();
            }

            try (PreparedStatement st =
con.prepareStatement("INSERT INTO users (name, surname, pin, country, city, street_address, mobile_number, sex, birthdate, relationship, number_of_children, average_monthly_income, email, money)" +
                                 "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {

                st.setString(1, info.getName());
                st.setString(2, info.getSurname());
                st.setString(3, info.getPin());
                st.setString(4, info.getCountry());
                st.setString(5, info.getCity());
                st.setString(6, info.getStreet_address());
                st.setString(7, info.getMobile_number());
                st.setString(8, info.getSex());
                st.setDate(9, info.getBirthdate());
                st.setString(10, info.getRelationship());
                st.setInt(11, info.getNumber_of_children());
                st.setInt(12, info.getAverage_monthly_income());
                st.setString(13, info.getEmail());
                st.setDouble(14, Double.valueOf(info.getMoney()));

                int size = st.executeUpdate();
                if (size > 0){
                    statusResponse.setProblem("Update completed.");
                }else{
                    statusResponse.setProblem("Something went wrong. Look your information carefully.");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            statusResponse.setProblem("Something went wrong. Look your information carefully.");
        }

        return statusResponse;
    }

    private void checkUserInfo(UserInfo info, StatusResponse statusResponse){
        // sheamowme shemomavali info da naxe shemosuli nomeri ukve xo ar aqvs vinmes.
        if (info.getName().length() == 0){
            statusResponse.setProblem("Enter name");
            return;
        }
        if (info.getSurname().length() == 0){
            statusResponse.setProblem("Enter surname");
            return;
        }
        String pin = info.getPin();
        if (pin.length() != 11 && pin.length() != 0){
            statusResponse.setProblem("Pin must have 9 cyphers");
            return;
        }
        for (int i = 0; i < pin.length(); i++){
            if (!(pin.charAt(i) <= '9' && pin.charAt(i) >='0')){
                statusResponse.setProblem("Pin must consist of cyphers");
                return;
            }
        }
        String mobile_number = info.getMobile_number();
        if (mobile_number.length() != 9 || mobile_number.charAt(0) != '5'){
            statusResponse.setProblem("Enter real mobile number. We will check.");
            return;
        }
        String email = info.getEmail();
        if (email.length() > 0 && !isValidEmailAddress(email)){
            statusResponse.setProblem("Email address is not valid.");
            return;
        }
        if (!info.getOld_mobile_number().equals(info.getMobile_number())){
            try (Connection con = DBConnectionProvider.getConnection()) {
                try (PreparedStatement st =
                             con.prepareStatement("SELECT * FROM users WHERE mobile_number=?")) {

                    st.setString(1,info.getMobile_number());

                    int size = st.executeUpdate();
                    if (size > 0){
                        statusResponse.setProblem("This number is already taken.");
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                statusResponse.setProblem("Something went wrong. Look your information carefully.");
            }
        }
    }

    private static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

}
