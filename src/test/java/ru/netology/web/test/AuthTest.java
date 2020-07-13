package ru.netology.web.test;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;

import java.sql.SQLException;
import static com.codeborne.selenide.Selenide.open;

public class AuthTest {

    @AfterAll
    public static void cleanTables() throws SQLException {
        DataHelper.cleanData();
    }

    @Test
    void shouldEnterWhenValidData() throws SQLException {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = loginPage.validAuth(authInfo);
        String verificationCode = DataHelper.getVerificationCodeForVasya();
        verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldNotEnterWhenInvalidLogin() {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getInvalidLoginAuthInfo();
        loginPage.invalidAuth(authInfo);
    }

    @Test
    void shouldNotEnterWhenInvalidPassword() {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getInvalidPasswordAuthInfo();
        loginPage.invalidAuth(authInfo);
    }

    @Test
    void shouldNotEnterWhenInvalidCode() throws SQLException {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = loginPage.validAuth(authInfo);
        val verificationCode = DataHelper.getInvalidVerificationCode();
        verificationPage.invalidVerify(verificationCode);
    }

    @Test
    void shouldBlockWhenThreeInvalidPasswords() {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getInvalidPasswordAuthInfo();
        loginPage.invalidAuth(authInfo);
        val invalidPassword = DataHelper.invalidPassword();
        loginPage.sendInvalidPassword(invalidPassword);
        loginPage.sendInvalidPasswordThirdTime(invalidPassword);
    }
}

