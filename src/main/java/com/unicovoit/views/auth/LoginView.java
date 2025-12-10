package com.unicovoit.views.auth;

import com.unicovoit.dto.LoginRequestDto;
import com.unicovoit.entity.UserAccount;
import com.unicovoit.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import org.springframework.beans.factory.annotation.Autowired;

@Route("login")
@PageTitle("Connexion | UniCovoit")
public class LoginView extends VerticalLayout {

    private final UserService userService;

    private final EmailField email = new EmailField("Email universitaire");
    private final PasswordField password = new PasswordField("Mot de passe");
    private final Button loginButton = new Button("Se connecter");

    @Autowired
    public LoginView(UserService userService) {
        this.userService = userService;

        setHeightFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 title = new H2("Connexion");

        email.setWidth("320px");
        password.setWidth("320px");

        loginButton.setWidth("320px");
        loginButton.addClickListener(e -> onLogin());

        Paragraph registerText = new Paragraph("Pas encore de compte ?");
        Anchor registerLink = new Anchor("register", "Créer un compte");

        VerticalLayout form = new VerticalLayout(
                title,
                email,
                password,
                loginButton,
                registerText,
                registerLink
        );

        form.setAlignItems(Alignment.CENTER);
        form.setSpacing(true);

        add(form);
    }

    private void onLogin() {
        try {
            LoginRequestDto dto = new LoginRequestDto();
            dto.setEmail(email.getValue());
            dto.setPassword(password.getValue());

            UserAccount user = userService.authenticate(dto);

            // Save user in the session
            VaadinSession.getCurrent().setAttribute(UserAccount.class, user);

            Notification.show("Bienvenue " + user.getFirstName() + " !");
            getUI().ifPresent(ui -> ui.navigate(""));

        } catch (IllegalArgumentException ex) {
            Notification.show(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Notification.show("Erreur lors de la connexion.");
        }
    }
}
