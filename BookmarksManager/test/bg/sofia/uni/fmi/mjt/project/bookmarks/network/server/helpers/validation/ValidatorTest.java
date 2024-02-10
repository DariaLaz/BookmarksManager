package bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.helpers.validation;

import bg.sofia.uni.fmi.mjt.project.bookmarks.exceptions.AuthException;
import bg.sofia.uni.fmi.mjt.project.bookmarks.network.server.command.CommandType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidatorTest {
    @Test
    void testValidateNotNullWithNull() {
        assertThrows(IllegalArgumentException.class, () ->
                Validator.validateNotNull(null, "message"),
                "Should throw IllegalArgumentException then the object is null");
    }

    @Test
    void testValidateNotNullWithNotNull() {
        assertDoesNotThrow(() -> Validator.validateNotNull(new Object(), "message"),
                "Should not throw IllegalArgumentException then the object is not null");
    }

    @Test
    void testValidateStringWithNullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateString(null, "message"),
                "Should throw IllegalArgumentException then the string is null");
    }

    @Test
    void testValidateStringWithNotNullOrEmpty() {
        assertDoesNotThrow(() -> Validator.validateString("string", "message"),
                "Should not throw IllegalArgumentException then the string is not null");
    }

    @Test
    void testValidateListNotEmptyWithEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateListNotEmpty(List.of(), "message"),
                "Should throw IllegalArgumentException then the list is empty");
    }

    @Test
    void testValidateListNotEmptyWithNotEmpty() {
        assertDoesNotThrow(() -> Validator.validateListNotEmpty(List.of(new Object()), "message"),
                "Should not throw IllegalArgumentException then the list is not empty");
    }

    @Test
    void testValidateListSizeWithDifferentSize() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateListSize(List.of(new Object()), 2, "message"),
                "Should throw IllegalArgumentException then the list size is different");
    }

    @Test
    void testValidateListSizeWithSameSize() {
        assertDoesNotThrow(() -> Validator.validateListSize(List.of(new Object(), new Object()), 2, "message"),
                "Should not throw IllegalArgumentException then the list size is the same");
    }

    @Test
    void testValidateRegistrateWithFalse() {
        assertThrows(AuthException.class, () -> Validator.validateRegistrate(false, "message"),
                "Should throw AuthException then the boolean is false");
    }

    @Test
    void testValidateRegistrateWithTrue() {
        assertDoesNotThrow(() -> Validator.validateRegistrate(true, "message"),
                "Should not throw IllegalArgumentException then the boolean is true");
    }

    @Test
    void testValidateAuthWithRegisterOrLoginAndSessionIdNotNull() {
        assertThrows(AuthException.class, () -> Validator.validateAuth(CommandType.REGISTER, "sessionId"),
                "Should throw AuthException then the command is REGISTER and the sessionId is not null");
    }

    @Test
    void testValidateAuthWithRegisterOrLoginAndSessionIdNull() {
        assertDoesNotThrow(() -> Validator.validateAuth(CommandType.REGISTER, null),
                "Should not throw IllegalArgumentException then the command is REGISTER and the sessionId is null");
    }

}
