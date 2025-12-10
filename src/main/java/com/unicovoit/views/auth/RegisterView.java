package com.unicovoit.views.auth;

import com.unicovoit.dto.RegisterRequestDto;
import com.unicovoit.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("register")
public class RegisterView extends VerticalLayout {

    private final UserService userService;

    private final TextField firstName = new TextField("Prénom");
    private final TextField lastName = new TextField("Nom");
    private final EmailField email = new EmailField("Email universitaire");
    private final TextField university = new TextField("Université");
    private final PasswordField password = new PasswordField("Mot de passe");
    private final PasswordField confirmPassword = new PasswordField("Confirmer le mot de passe");
    private final Button registerButton = new Button("S'inscrire");

    @Autowired
    public RegisterView(UserService userService) {
        this.userService = userService;

        add(firstName, lastName, email, university, password, confirmPassword, registerButton);
        registerButton.addClickListener(e -> onRegister());
    }

    private void onRegister() {
        try {
            RegisterRequestDto dto = new RegisterRequestDto();
            dto.setFirstName(firstName.getValue());
            dto.setLastName(lastName.getValue());
            dto.setEmail(email.getValue());
            dto.setUniversity(university.getValue());
            dto.setPassword(password.getValue());
            dto.setConfirmPassword(confirmPassword.getValue());

            userService.registerStudent(dto);
            Notification.show("Compte créé avec succès !");
            // TODO: getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (IllegalArgumentException ex) {
            Notification.show(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Notification.show("Erreur inattendue lors de l'inscription");
        }
    }
}
