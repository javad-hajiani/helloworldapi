package com.revolut.devops.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.revolut.devops.challenge.entity.User;
import com.revolut.devops.challenge.exceptions.InvalidBirthdateException;
import com.revolut.devops.challenge.exceptions.InvalidUserDataDirException;
import com.revolut.devops.challenge.exceptions.InvalidUsernameException;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ServiceImpl {
    private static  ServiceImpl SERVICE_INSTANCE;

    private static ObjectMapper objectMapper;
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("YYYY-MM-dd");
    private static Pattern userNamePattern = Pattern.compile("^[a-zA-Z]{3,15}$");

    private ServiceImpl(){}

    public static ServiceImpl getInstance(){
        if(SERVICE_INSTANCE == null)
            SERVICE_INSTANCE = new ServiceImpl();
        return SERVICE_INSTANCE;
    }

    public  String getInstruction() {
        String instructionResponse;
        instructionResponse = "Welcome to Our Devops Challege site.<br />";
        instructionResponse += "To Create or update a user use PUT /hello/<username> with body {\"dateOfBirth\":\"YYYY-MM-DD\"}<br />";
        instructionResponse += "To get user information: GET /hello/<username> <br />";
        return instructionResponse;
    }

    public  String putUserInfo(String username,String body) throws InvalidBirthdateException, IOException, InvalidUsernameException {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
            User userObject = createUserInstance(username,body);
            String formattedBirthDate = dateFormatter.format(userObject.getDateOfBirth());
            String userInfoToPersist = "{\"dateOfBirth\":\"" + formattedBirthDate + "\"}";
            Files.write(Paths.get(System.getenv("USER_DATA_DIR") + "/" + username + ".json"), userInfoToPersist.getBytes());
            return "Success!";



    }

    public User createUserInstance(String username, String body) throws InvalidBirthdateException, IOException, InvalidUsernameException {

        if(username == null){
            throw new InvalidUsernameException("Username must passed!");
        }
        Matcher matcher = userNamePattern.matcher(username);
        if (!matcher.find()) {
            throw new InvalidUsernameException("Username must only have alphabet characters and at least be 3 chars and at max be 15!");
        }
        if (body == null){
            throw new InvalidBirthdateException("No Json Content in Body to extract Birthdate from");
        }
        if (body.isEmpty()) {
            throw new InvalidBirthdateException("No Json Content in Body to extract Birthdate from");
        }
        User userObject = objectMapper.readValue(body, User.class);
        if (userObject.getDateOfBirth().after(new Date())) {
            throw new InvalidBirthdateException("Bad Request Input Format! Date can not be after current date!");
        }
        return userObject;
    }

    public  String getUserInfo(String username) throws IOException, InvalidUserDataDirException {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }
            if (System.getenv("USER_DATA_DIR")==null){
                throw new InvalidUserDataDirException("please set USER_DATA_DIR in docker-compose or kube deployment file!");
            }
            User userObject = objectMapper.readValue(new File(System.getenv("USER_DATA_DIR") + "/" + username + ".json"), User.class);
            String responseMessage = calculateBirthdate(username, userObject.getDateOfBirth());
            return responseMessage;
    }

    public  String calculateBirthdate(String username, Date birthdate) {
        LocalDate measurableBirthDate = LocalDate.parse(dateFormatter.format(birthdate));
        Calendar updatableBirthDate = Calendar.getInstance();
        updatableBirthDate.setTime(birthdate);
        LocalDate measurableCurrentDate = LocalDate.now();
        long noOfYearsBetween = ChronoUnit.YEARS.between(measurableBirthDate, measurableCurrentDate);
        updatableBirthDate.add(Calendar.YEAR, ((int) noOfYearsBetween) );
        long nextBirthday;
        if (new Date().after(updatableBirthDate.getTime())) {
            nextBirthday = ChronoUnit.DAYS.between(LocalDate.parse(dateFormatter.format(updatableBirthDate.getTime())), measurableCurrentDate);
        } else {
            nextBirthday = ChronoUnit.DAYS.between(measurableCurrentDate, LocalDate.parse(dateFormatter.format(updatableBirthDate.getTime())));
        }

        return "Hello " + username + "  " + nextBirthday + " Day(s) to your birthday";

    }
}
