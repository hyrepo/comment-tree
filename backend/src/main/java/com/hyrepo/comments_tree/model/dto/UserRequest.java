package com.hyrepo.comments_tree.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import static com.hyrepo.comments_tree.util.Constants.*;

public record UserRequest(@NotEmpty(message = "Username can't be empty.")
                          @Length(min = USERNAME_LENGTH_MIN, max = USERNAME_LENGTH_MAX, message = "Length of username must between 5-20")
                          @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contains numbers and alphabets.")
                          String username,
                          @NotEmpty(message = "Password can't be empty.")
                          @Length(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX, message = "Length of password must between 8-20")
                          @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).*$",
                                  message = "Password must has at least one uppercase letter, one lowercase letter, " +
                                          "one number, and one special character.")
                          String password,
                          @NotEmpty(message = "Email can't be empty.")
                          @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                                  message = "Invalid Email format")
                          String email) {
}
