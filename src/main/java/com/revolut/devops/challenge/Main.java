package com.revolut.devops.challenge;

import com.revolut.devops.challenge.exceptions.InvalidBirthdateException;
import com.revolut.devops.challenge.exceptions.InvalidUsernameException;

import java.io.IOException;

import static spark.Spark.get;
import static spark.Spark.put;

public class Main {


    public static void main(String[] args) {
        ServiceImpl mainServiceHub = ServiceImpl.getInstance();
        put("/hello/:username", (req, res) -> {
            try {
                String responseInString = mainServiceHub.putUserInfo(req.params("username"), req.body());
                res.status(204);
                return responseInString;
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
            try {
                res.status(200);
                return mainServiceHub.getUserInfo(req.params("username"));
            } catch (IOException e) {
                res.status(404);
                return e.getMessage() + ">>> no such user exists. if you are sure of the user existence, please check whether USER_DATA_DIR has a value or not.";
            }
        });
        get("/*", (req, res) -> {
            res.status(200);
            return mainServiceHub.getInstruction();
        });
    }


}
