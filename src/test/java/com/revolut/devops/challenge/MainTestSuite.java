package com.revolut.devops.challenge;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.revolut.devops.challenge.exceptions.InvalidBirthdateException;
import com.revolut.devops.challenge.exceptions.InvalidUserDataDirException;
import com.revolut.devops.challenge.exceptions.InvalidUsernameException;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class MainTestSuite {

    private ServiceImpl mainServiceHub;

    @Before
    public void prepareSharedResources(){
        mainServiceHub = ServiceImpl.getInstance();
    }



    @Test
    public void validateGetInstructions(){
        String instructionResponse;
        instructionResponse = "Welcome to Our Devops Challege site.<br />";
        instructionResponse += "To Create or update a user use PUT /hello/<username> with body {\"dateOfBirth\":\"YYYY-MM-DD\"}<br />";
        instructionResponse += "To get user information: GET /hello/<username> <br />";
        Assert.assertEquals(instructionResponse,mainServiceHub.getInstruction());
    }

    @Test(expected = InvalidUsernameException.class)
    public void validateThrowExceptionOnShortUsername() throws InvalidUsernameException, IOException, InvalidBirthdateException {
       mainServiceHub.putUserInfo("ab","");
    }


    @Test(expected = InvalidUsernameException.class)
    public void validateThrowExceptionOnNullUsername() throws InvalidUsernameException, IOException, InvalidBirthdateException {
        mainServiceHub.putUserInfo(null,"");
    }


    @Test(expected = InvalidBirthdateException.class)
    public void validateThrowExceptionOnInvalidDate() throws InvalidUsernameException, IOException, InvalidBirthdateException {

        mainServiceHub.putUserInfo("abc","{\"dateOfBirth\":\"2020-01-01\"}");
    }

    @Test(expected = UnrecognizedPropertyException.class)
    public void validateThrowExceptionOnInvalidParameter() throws InvalidUsernameException, IOException, InvalidBirthdateException {
        mainServiceHub.putUserInfo("abc","{\"dateOBirth\":\"2020-01-01\"}");
    }

    @Test(expected = InvalidFormatException.class)
    public void validateThrowExceptionOnInvalidDateFormat() throws InvalidUsernameException, IOException, InvalidBirthdateException {
        mainServiceHub.putUserInfo("abc","{\"dateOfBirth\":\"2020-MAR-01\"}");
    }


    @Test
    public void testUserPersist() throws InvalidUsernameException, IOException, InvalidBirthdateException {
        mainServiceHub.putUserInfo("abc","{\"dateOfBirth\":\"2000-01-01\"}");
        Assert.assertEquals(true,new File(System.getenv("USER_DATA_DIR")+"/abc.json").exists());
    }

    @Test
    public void testCalculateBirthday()  {
        Assert.assertEquals("Hello abc  0 Day(s) to your birthday",mainServiceHub.calculateBirthdate("abc",new Date()));
    }
    @Test
    public void testCalculateBirthdayAfterCurrent()  {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 1); // <--
        Date tomorrow = cal.getTime();
        Assert.assertEquals("Hello abc  1 Day(s) to your birthday",mainServiceHub.calculateBirthdate("abc", tomorrow));
    }

}
