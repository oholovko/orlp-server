package com.softserve.academy.spaced.repetition.controller;

import com.softserve.academy.spaced.repetition.DTO.impl.PasswordDTO;
import com.softserve.academy.spaced.repetition.domain.Person;
import com.softserve.academy.spaced.repetition.domain.User;
import com.softserve.academy.spaced.repetition.exceptions.DataFieldException;
import com.softserve.academy.spaced.repetition.exceptions.NotAuthorisedUserException;
import com.softserve.academy.spaced.repetition.exceptions.PasswordFieldException;
import com.softserve.academy.spaced.repetition.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private UserService userService;


    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController)
                .setControllerAdvice(new ExceptionHandlerController())
                .alwaysDo(print())
                .build();
    }

    private User editedUser(){
        User user = new User();
        Person person = new Person();
        person.setFirstName("Admin2");
        person.setLastName("Admin2");
        user.setPerson(person);
        return user;
    }

    private Person newPerson(){
        Person person = new Person();
        person.setFirstName("Admin2");
        person.setLastName("Admin2");
        return person;
    }

    @Test
    public void testEditPersonalData() throws Exception {
        when(userService.editPersonalData(eq(newPerson()))).thenReturn(editedUser());
        mockMvc.perform(put("/api/private/user/data")
                .content("{ \"firstName\": \"Admin2\", \"lastName\": \"Admin2\" }")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).editPersonalData(eq(newPerson()));
    }

    @Test
    public void testNotAuthorizedEditPersonalData() throws Exception {
        doThrow(NotAuthorisedUserException.class).when(userService).editPersonalData(any(Person.class));
        mockMvc.perform(put("/api/private/user/data")
                .content("{ \"firstName\": \"Admin2\", \"lastName\": \"Admin2\" }")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private Person newPersonException(){
        Person person = new Person();
        person.setFirstName("");
        person.setLastName("");
        return person;
    }

    @Test
    public void testDataFieldExceptionEditPersonalData() throws Exception {
        doThrow(DataFieldException.class).when(userService).editPersonalData(eq(newPersonException()));
        mockMvc.perform(put("/api/private/user/data")
                .content("{ \"firstName\": \"\", \"lastName\": \"\" }")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testChangePassword() throws Exception {
        mockMvc.perform(put("/api/private/user/password-change")
                .content("{\"currentPassword\":\"11111111\",\"newPassword\": \"22222222\"}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).changePassword(eq( new PasswordDTO("11111111", "22222222")));
    }

    @Test
    public void testNotAuthorizedChangePassword() throws Exception {
        doThrow(NotAuthorisedUserException.class).when(userService).changePassword(any(PasswordDTO.class));
        mockMvc.perform(put("/api/private/user/password-change")
                .content("{\"currentPassword\":\"11111111\",\"newPassword\": \"22222222\"}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPasswordFieldExceptionChangePassword() throws Exception {
        doThrow(PasswordFieldException.class).when(userService).changePassword(eq( new PasswordDTO("", "")));
        mockMvc.perform(put("/api/private/user/password-change")
                .content("{\"currentPassword\":\"\",\"newPassword\": \"\"}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteProfile() throws Exception {
        mockMvc.perform(get("/api/private/user/delete")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testNotAuthorizedDeleteProfile() throws Exception {
        doThrow(NotAuthorisedUserException.class).when(userService).deleteAccount();
        mockMvc.perform(get("/api/private/user/delete")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}