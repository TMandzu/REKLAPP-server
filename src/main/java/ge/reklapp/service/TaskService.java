package ge.reklapp.service;

import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.entity.Transaction;
import ge.reklapp.core.Constants;
import ge.reklapp.db.DBConnectionProvider;
import org.joda.money.Money;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

/**
 * Created by Tornike on 10.07.2016.
 */
@Path("/")
@Consumes( { MediaType.APPLICATION_JSON})
@Produces( { MediaType.APPLICATION_JSON})
public class TaskService {
    private final static Object lock = new Object();

    @POST
    @Path("/ads/{ad_id}/view")
    public StatusResponse increaseView(@PathParam("ad_id") int ad_id){ // increase view_left by one.
        synchronized(lock) {
            StatusResponse statusResponse = new StatusResponse("");
            try (Connection con = DBConnectionProvider.getConnection()) {
                try (PreparedStatement st =
                             con.prepareStatement("UPDATE ads SET view_left=view_left+1 WHERE ad_id=?",
                                     ResultSet.TYPE_SCROLL_SENSITIVE,
                                     ResultSet.CONCUR_UPDATABLE)) {

                    st.setInt(1, ad_id);

                    int size = st.executeUpdate();

                    if (size > 0) {
                        statusResponse.setProblem("Request completed.");
                    } else {
                        statusResponse.setProblem("მოხდა შეცდომა.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                statusResponse.setProblem("მოხდა შეცდომა.");
            }
            return statusResponse;
        }
    }


    @POST
    @Path("/pairs/{pair_id}/date")
    public StatusResponse changeDate(@PathParam("pair_id") int pair_id){ // change date to now.
        synchronized(lock) {
            StatusResponse statusResponse = new StatusResponse("");
            try (Connection con = DBConnectionProvider.getConnection()) {
                try (PreparedStatement st =
                             con.prepareStatement("UPDATE pairs SET last_seen=? WHERE pair_id=?",
                                     ResultSet.TYPE_SCROLL_SENSITIVE,
                                     ResultSet.CONCUR_UPDATABLE)) {

                    Calendar calendar = Calendar.getInstance();
                    java.util.Date currentDate = calendar.getTime();
                    java.sql.Date date = new java.sql.Date(currentDate.getTime());
                    st.setDate(1, date);
                    st.setInt(2, pair_id);

                    int size = st.executeUpdate();

                    if (size > 0) {
                        statusResponse.setProblem("Request completed.");
                    } else {
                        statusResponse.setProblem("მოხდა შეცდომა.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                statusResponse.setProblem("მოხდა შეცდომა.");
            }
            return statusResponse;
        }
    }

    @GET
    @Path("/users/{mobile_number}/{password}")
    public UserInfo getUser(@PathParam("mobile_number") String mobile_number, @PathParam("password") String password){
        synchronized(lock) {
            UserInfo userInfo = new UserInfo();
            try (Connection con = DBConnectionProvider.getConnection()) {
                try (PreparedStatement st =
                             con.prepareStatement("SELECT * FROM users WHERE mobile_number=? and password=?",
                                     ResultSet.TYPE_SCROLL_SENSITIVE,
                                     ResultSet.CONCUR_UPDATABLE)) {

                    st.setString(1, mobile_number);
                    st.setString(2, password);

                    ResultSet res = st.executeQuery();
                    res.first();

                    if (res.getRow() > 0) {
                        userInfo.setName(res.getString("name"));
                        userInfo.setSurname(res.getString("surname"));
                        userInfo.setPin(res.getString("pin"));
                        userInfo.setCountry(res.getString("country"));
                        userInfo.setCity(res.getString("city"));
                        userInfo.setStreet_address(res.getString("street_address"));
                        userInfo.setMobile_number(res.getString("mobile_number"));
                        userInfo.setBirthdate(res.getDate("birthdate"));
                        userInfo.setRelationship(res.getString("relationship"));
                        userInfo.setPassword(res.getString("password"));
                        userInfo.setNumber_of_children(res.getInt("number_of_children"));
                        userInfo.setAverage_monthly_income(res.getInt("average_monthly_income"));
                        userInfo.setEmail(res.getString("email"));
                        userInfo.setOld_mobile_number("");
                        userInfo.setMoney(res.getDouble("money"));
                        userInfo.setSex(res.getString("sex"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                userInfo = new UserInfo();
            }
            return userInfo;
        }
    }


    // TODO periodulad pairs gasuftaveba nagvisgan
    @GET
    @Path("/users/{mobile_number}/advertisments/random")
    public AdInfo getRandomAd(@PathParam("mobile_number") String mobile_number){
        synchronized(lock) {
            AdInfo adInfo = new AdInfo();
            try (Connection con = DBConnectionProvider.getConnection()) {
                try (PreparedStatement st =
                             con.prepareStatement("SELECT user_id FROM users WHERE mobile_number=?",
                                     ResultSet.TYPE_SCROLL_SENSITIVE,
                                     ResultSet.CONCUR_UPDATABLE)) {
                    st.setString(1, mobile_number);
                    ResultSet res = st.executeQuery();
                    res.first();
                    if (res.getRow() == 0) {
                        adInfo.setStatus("ვერ გპოულობთ ბაზაში.");
                    } else {
                        boolean flag = true;
                        int user_id = res.getInt("user_id");
                        try (PreparedStatement check_st =
                                     con.prepareStatement("SELECT * FROM pairs WHERE user_id=? and extract (epoch from (CURRENT_TIMESTAMP - last_seen::timestamp))::integer/60 < 24*60 ORDER BY random() LIMIT 1",
                                             ResultSet.TYPE_SCROLL_SENSITIVE,
                                             ResultSet.CONCUR_UPDATABLE)) {
                            check_st.setInt(1, user_id);
                            ResultSet check_res = check_st.executeQuery();
                            check_res.first();
                            if (check_res.getRow() > Constants.DAY_LIMIT) {
                                adInfo.setStatus("დღეს ამოწურეთ თქვენი რეკლამების ყურების ლიმიტი.");
                                flag = false;
                            }
                        }
                        if (flag) {
                            try (PreparedStatement st2 =
                                         con.prepareStatement("SELECT * FROM pairs WHERE user_id=? and extract (epoch from (CURRENT_TIMESTAMP - last_seen::timestamp))::integer/60 > 24*60 ORDER BY random() LIMIT 1",
                                                 ResultSet.TYPE_SCROLL_SENSITIVE,
                                                 ResultSet.CONCUR_UPDATABLE)) {
                                st2.setInt(1, user_id);
                                ResultSet res2 = st2.executeQuery();
                                res2.first();
                                if (res2.getRow() == 0) {
                                    adInfo.setStatus("ჯერჯერობით თქვენთვის რეკლამები არ გვაქვს.");
                                } else {
                                    int ad_id = res2.getInt("ad_id");
                                    int pair_id = res2.getInt("pair_id");
                                    try (PreparedStatement st3 =
                                                 con.prepareStatement("SELECT * FROM ads WHERE ad_id=? and view_left > 0",
                                                         ResultSet.TYPE_SCROLL_SENSITIVE,
                                                         ResultSet.CONCUR_UPDATABLE)) {
                                        st3.setInt(1, ad_id);
                                        ResultSet res3 = st3.executeQuery();
                                        res3.first();
                                        if (res3.getRow() == 0) {
                                            adInfo.setStatus("ჯერჯერობით თქვენთვის რეკლამები არ გვაქვს.");
                                        } else {
                                            int view_left = res3.getInt("view_left") - 1;
                                            try (PreparedStatement st4 =
                                                         con.prepareStatement("UPDATE ads SET view_left=? WHERE ad_id=?",
                                                                 ResultSet.TYPE_SCROLL_SENSITIVE,
                                                                 ResultSet.CONCUR_UPDATABLE)) {
                                                st4.setInt(1, view_left);
                                                st4.setInt(2, ad_id);
                                                int size = st4.executeUpdate();
                                                if (size > 0) {
                                                    adInfo.setStatus("Request completed.");
                                                    adInfo.setDescription(res3.getString("description"));
                                                    adInfo.setCompany(res3.getString("company"));
                                                    adInfo.setLink(res3.getString("link"));
                                                    adInfo.setPair_id(pair_id);
                                                    adInfo.setProduct(res3.getString("product"));
                                                    adInfo.setView_gain(res3.getDouble("view_gain"));
                                                    adInfo.setView_gain(res3.getDouble("view_gain"));
                                                    adInfo.setAd_id(ad_id);
                                                } else {
                                                    adInfo.setStatus("მოხდა შეცდომა.");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                adInfo.setStatus("მოხდა შეცდომა.");
            }
            return adInfo;
        }
    }


    @GET
    @Path("/users/{mobile_number}/money")
    public MoneyInfo getMoney(@PathParam("mobile_number") String mobile_number){
        synchronized(lock) {
            MoneyInfo moneyInfo = new MoneyInfo();
            try (Connection con = DBConnectionProvider.getConnection()) {
                try (PreparedStatement st =
                             con.prepareStatement("SELECT * FROM users WHERE mobile_number=?",
                                     ResultSet.TYPE_SCROLL_SENSITIVE,
                                     ResultSet.CONCUR_UPDATABLE)) {
                    st.setString(1, mobile_number);
                    ResultSet res = st.executeQuery();
                    res.first();
                    if (res.getRow() == 0) {
                        moneyInfo.setAmount(-1);
                    } else {
                        moneyInfo.setAmount(res.getDouble("money"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                moneyInfo.setAmount(-1);
            }
            return moneyInfo;
        }
    }

    @POST
    @Path("/users/{mobile_number}/transfer/{address}")
    public StatusResponse transferMoney(@PathParam("mobile_number") String mobile_number, @PathParam("address") String address, MoneyInfo moneyInfo){
        synchronized(lock) {
            StatusResponse statusResponse = new StatusResponse("");
            try (Connection con = DBConnectionProvider.getConnection()) {
                try (PreparedStatement st =
                             con.prepareStatement("SELECT * FROM users WHERE mobile_number=?",
                                     ResultSet.TYPE_SCROLL_SENSITIVE,
                                     ResultSet.CONCUR_UPDATABLE)) {
                    st.setString(1, mobile_number);
                    ResultSet res = st.executeQuery();
                    res.first();
                    if (res.getRow() == 0) {
                        statusResponse.setProblem("ვერ გპოულობთ ბაზაში.");
                    } else {
                        double amountNow = res.getDouble("money");
                        double delta = moneyInfo.getAmount();
                        if (address.equals("self"))
                            delta = -delta;
                        if (amountNow >= delta && transferToAddress(delta, address)) {
                            double newAmount = amountNow - delta;
                            try (PreparedStatement st2 =
                                         con.prepareStatement("UPDATE users SET money=? WHERE mobile_number=?",
                                                 ResultSet.TYPE_SCROLL_SENSITIVE,
                                                 ResultSet.CONCUR_UPDATABLE)) {
                                st2.setDouble(1, newAmount);
                                st2.setString(2, mobile_number);
                                int size = st2.executeUpdate();
                                if (size > 0) {
                                    statusResponse.setProblem("Transfer completed.");
                                } else {
                                    statusResponse.setProblem("მოხდა შეცდომა.");
                                }
                            }
                        } else {
                            statusResponse.setProblem("მოხდა შეცდომა.");
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                statusResponse.setProblem("მოხდა შეცდომა.");
            }
            return statusResponse;
        }
    }

    private boolean transferToAddress(double amount, String address){
        if (address.equals("self"))
            return true;
        Coinbase cb = new CoinbaseBuilder()
                .withApiKey("Wr716n19WKnpNbGK", "clWydFSISmZdHs9RjBAuPfPYDHkqGV0f")
                .build();
        try {
            Transaction t = new Transaction();
            t.setTo(address);
            t.setAmount(Money.parse("BTC " + String.valueOf(amount)));
            t.setNotes("Thanks for watching our ads!");
            cb.sendMoney(t);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @PUT
    @Path("/users")
    public StatusResponse updateUser(UserInfo info){
        synchronized(lock) {
            StatusResponse statusResponse = new StatusResponse("");
            checkUserInfo(info, statusResponse);

            if (statusResponse.getProblem().length() > 0)
                return statusResponse;

            try (Connection con = DBConnectionProvider.getConnection()) {
                try (PreparedStatement st_start =
                             con.prepareStatement("SELECT * FROM users WHERE mobile_number=?",
                                     ResultSet.TYPE_SCROLL_SENSITIVE,
                                     ResultSet.CONCUR_UPDATABLE)) {

                    st_start.setString(1, info.getOld_mobile_number());

                    ResultSet res = st_start.executeQuery();
                    res.first();

                    if (res.getRow() == 0) {
                        // create
                        try (PreparedStatement st =
                                     con.prepareStatement("INSERT INTO users (name, surname, pin, country, city, street_address, mobile_number, sex, birthdate, relationship, number_of_children, average_monthly_income, email, money, password)" +
                                                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                                             ResultSet.TYPE_SCROLL_SENSITIVE,
                                             ResultSet.CONCUR_UPDATABLE)) {

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
                            st.setDouble(14, info.getMoney());
                            st.setString(15, info.getPassword());

                            int size = st.executeUpdate();
                            if (size > 0) {
                                statusResponse.setProblem("Update completed.");
                            } else {
                                statusResponse.setProblem("მოხდა შეცდომა.");
                            }

                        }
                    } else {
                        // update
                        try (PreparedStatement st =
                                     con.prepareStatement("UPDATE users SET name=?,surname=?,pin=?,country=?,city=?,street_address=?,mobile_number=?,sex=?,birthdate=?,relationship=?,number_of_children=?,average_monthly_income=?,email=?,money=?,password=?  WHERE mobile_number=?",
                                             ResultSet.TYPE_SCROLL_SENSITIVE,
                                             ResultSet.CONCUR_UPDATABLE)) {

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
                            st.setDouble(14, info.getMoney());
                            st.setString(15, info.getPassword());
                            st.setString(16, info.getOld_mobile_number());

                            int size = st.executeUpdate();
                            if (size > 0) {
                                statusResponse.setProblem("Update completed.");
                            } else {
                                statusResponse.setProblem("მოხდა შეცდომა.");
                            }

                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                statusResponse.setProblem("მოხდა შეცდომა.");
            }

            return statusResponse;
        }
    }

    private void checkUserInfo(UserInfo info, StatusResponse statusResponse){
        // sheamowme shemomavali info da naxe shemosuli nomeri ukve xo ar aqvs vinmes.
        if (info.getName().length() == 0){
            statusResponse.setProblem("შეიყვანეთ სახელი");
            return;
        }
        if (info.getSurname().length() == 0){
            statusResponse.setProblem("შეიყვანეთ გვარი");
            return;
        }

        if (info.getPassword().length() < 6 || info.getPassword().length() > 20){
            statusResponse.setProblem("პაროლის სიგრძე უნდა იყოს 6-დან 20-მდე");
            return;
        }

        String pin = info.getPin();
        if (pin.length() != 11 && pin.length() != 0){
            statusResponse.setProblem("პირადი ნომერი უნდა შეიცავდეს 11 ციფრს");
            return;
        }
        for (int i = 0; i < pin.length(); i++){
            if (!(pin.charAt(i) <= '9' && pin.charAt(i) >='0')){
                statusResponse.setProblem("პირადი ნომერი უნდ აშედგებოდეს მხოლოდ ციფრებისაგან");
                return;
            }
        }
        String mobile_number = info.getMobile_number();
        if (mobile_number.length() != 9 || mobile_number.charAt(0) != '5'){
            statusResponse.setProblem("მობილურის ნორმის ფორმატი არასწორია");
            return;
        }
        String email = info.getEmail();
        if (email.length() > 0 && !isValidEmailAddress(email)){
            statusResponse.setProblem("ელ. ფოსტის ფორმატი არასწორია");
            return;
        }
        if (info.getOld_mobile_number().compareTo(info.getMobile_number()) != 0){
            try (Connection con = DBConnectionProvider.getConnection()) {
                try (PreparedStatement st =
                             con.prepareStatement("SELECT * FROM users WHERE mobile_number=?",
                                     ResultSet.TYPE_SCROLL_SENSITIVE,
                                     ResultSet.CONCUR_UPDATABLE)) {

                    st.setString(1,info.getMobile_number());

                    ResultSet res = st.executeQuery();
                    res.first();
                    if (res.getRow() > 0){
                        statusResponse.setProblem("ნომერი უკვე დაკავებულია სხვა მომხმარებლის მიერ");
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                statusResponse.setProblem("მოხდა შეცდომა. მოგვიანებით სცადეთ");
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
