package ru.netology.web.test;

import com.codeborne.selenide.Condition;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class AuthTest {

    @AfterAll
    public static void cleanData() throws SQLException {
        val runner = new QueryRunner();
        val codes = "DELETE FROM auth_codes";
        val cards = "DELETE FROM cards";
        val users = "DELETE FROM users";

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            runner.update(conn, codes);
            runner.update(conn, cards);
            runner.update(conn, users);
        }
    }

    @Test
    void shouldEnterWhenValidData() throws SQLException {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = loginPage.validAuth(authInfo);
        String verificationCode = DataHelper.getVerificationCodeForVasya();
        verificationPage.validVerify(verificationCode);
        $(byText("Личный кабинет")).waitUntil(Condition.visible, 15000);
    }

    @Test
    void shouldNotEnterWhenInvalidLogin() {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = new DataHelper.AuthInfo("kdjfdlk", "qwerty123");
        loginPage.invalidAuth(authInfo);
    }

    @Test
    void shouldNotEnterWhenInvalidPassword() {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = new DataHelper.AuthInfo("vasya", "qwerty12345");
        loginPage.invalidAuth(authInfo);
    }

    @Test
    void shouldNotEnterWhenInvalidCode() throws SQLException {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = loginPage.validAuth(authInfo);
        val verificationCode = "897987928475";
        verificationPage.validVerify(verificationCode);
        $("[data-test-id=error-notification]").shouldBe(Condition.visible);
    }

    @Test
    void shouldBlockWhenThreeInvalidPasswords() {
        open("http://localhost:9999");
        val loginPage = new LoginPage();
        val authInfo = new DataHelper.AuthInfo("petya", "qwerty12345");
        loginPage.invalidAuth(authInfo);
        loginPage.sendInvalidPassword("lkjdfkdl");
        loginPage.sendInvalidPassword("lekjrekl");
        $("[data-test-id=action-login]").shouldBe(Condition.disabled);
    }
}

