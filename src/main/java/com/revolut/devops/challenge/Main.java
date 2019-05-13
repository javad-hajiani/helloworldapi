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


    public static void main(String[] args) {
        ServiceImpl mainServiceHub = ServiceImpl.getInstance();
        put("/hello/:username", (req, res) -> {
            try {
                String responseInString = mainServiceHub.putUserInfo(req.params("username"), req.body());
                res.status(204);
                return  responseInString;
            } catch (InvalidUsernameException e) {
                res.status(400);
                e.printStackTrace();
                return e.getMessage();
            } catch (InvalidBirthdateException e) {
                res.status(400);
                e.printStackTrace();
                return e.getMessage();
            } catch (IOException e) {
                res.status(400);
                e.printStackTrace();
                return e.getMessage();
            }
        });
        get("/hello/:username", (req, res) -> {
           try{
               res.status(200);
               return  mainServiceHub.getUserInfo(req.params("username"));
           } catch (IOException e){
                res.status(404);
                return  e.getMessage()+">>> no such user exists. if you are sure of the user existence, please check whether USER_DATA_DIR has a value or not.";
           }
        });
        get("/*", (req, res) -> { res.status(200);  return  mainServiceHub.getInstruction();});
    }



}
