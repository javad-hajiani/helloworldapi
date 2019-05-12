package com.revolut.devops.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.devops.challenge.entity.User;
import com.revolut.devops.challenge.exceptions.InvalidBirthdateException;
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

import static spark.Spark.get;
import static spark.Spark.put;

public class Main {
    public static ObjectMapper objectMapper;
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("YYYY-MM-dd");
    public static Pattern userNamePattern = Pattern.compile("^[a-zA-Z]{3,15}$");


    public static void main(String[] args) {

        put("/hello/:username", (req, res) -> putUserInfo(req, res));
        get("/hello/:username", (req, res) -> getUserInfo(req, res));
        get("/*", (req, res) -> getInstruction());
    }

    public static String getInstruction() {
        String instructionResponse;
        instructionResponse = "Welcome to Our Devops Challege site.<br />";
        instructionResponse += "To Create or update a user use PUT /hello/<username> with body {\"dateOfBirth\":\"YYYY-MM-DD\"}<br />";
        instructionResponse += "To get user information: GET /hello/<username> <br />";
        return instructionResponse;
    }

    public static String putUserInfo(Request request, Response response) throws InvalidBirthdateException {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            User userObject = createUserInstance(request);
            String formattedBirthDate = dateFormatter.format(userObject.getDateOfBirth());
            String userInfoToPersist = "{\"dateOfBirth\":\"" + formattedBirthDate + "\"}";
            Files.write(Paths.get(System.getenv("USER_DATA_DIR") + "/" + request.params(":username") + ".json"), userInfoToPersist.getBytes());
            response.status(204);
            return "Success!";
        } catch (InvalidUsernameException e) {
            response.status(400);
            e.printStackTrace();
            return e.getMessage();
        } catch (InvalidBirthdateException e) {
            response.status(400);
            e.printStackTrace();
            return e.getMessage();
        } catch (IOException e) {
            response.status(400);
            e.printStackTrace();
            return e.getMessage();
        }


    }

    private static User createUserInstance(Request request) throws InvalidBirthdateException, IOException, InvalidUsernameException {
        Matcher matcher = userNamePattern.matcher(request.params(":username"));
        if (!matcher.find()) {
            throw new InvalidUsernameException("Username must only have alphabet characters and at least be 3 chars and at max be 15!");
        }
        if (request.body().isEmpty()) {
            throw new InvalidBirthdateException("No Json Content in Body to extract Birthdate from");
        }
        User userObject = objectMapper.readValue(request.body(), User.class);
        if (userObject.getDateOfBirth().after(new Date())) {
            throw new InvalidBirthdateException("Bad Request Input Format! Date can not be after current date!");
        }
        return userObject;
    }

    public static String getUserInfo(Request request, Response response) {
        try {


            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }

            User userObject = objectMapper.readValue(new File(System.getenv("USER_DATA_DIR") + "/" + request.params(":username") + ".json"), User.class);
            String responseMessage = calculateBirthdate(request.params(":username"), userObject.getDateOfBirth());
            return responseMessage;
        } catch (Exception e) {
            e.printStackTrace();
            response.status(400);
            return "Exception on Read user, Username not found";

        }
    }

    public static String calculateBirthdate(String username, Date birthdate) {
        LocalDate measurableBirthDate = LocalDate.parse(dateFormatter.format(birthdate));
        Calendar updatableBirthDate = Calendar.getInstance();
        updatableBirthDate.setTime(birthdate);
        LocalDate measurableCurrentDate = LocalDate.now();
        long noOfYearsBetween = ChronoUnit.YEARS.between(measurableBirthDate, measurableCurrentDate);
        updatableBirthDate.add(Calendar.YEAR, ((int) noOfYearsBetween) + 1);
        long nextBirthday;
        if (new Date().after(updatableBirthDate.getTime())) {
            nextBirthday = ChronoUnit.DAYS.between(LocalDate.parse(dateFormatter.format(updatableBirthDate.getTime())), measurableCurrentDate);
        } else {
            nextBirthday = ChronoUnit.DAYS.between(measurableCurrentDate, LocalDate.parse(dateFormatter.format(updatableBirthDate.getTime())));
        }

        return "Hello " + username + "  " + nextBirthday + " Day(s) to your birthday";

    }

}
