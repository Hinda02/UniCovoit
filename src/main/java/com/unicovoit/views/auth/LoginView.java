package com.unicovoit.views.auth;

import com.unicovoit.dto.LoginRequestDto;
import com.unicovoit.entity.UserAccount;
import com.unicovoit.service.UserService;
import com.unicovoit.util.NotificationHelper;
import com.unicovoit.util.SessionManager;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("login")
@PageTitle("Connexion | UniCovoit")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;

    private final EmailField emailField = new EmailField();
    private final PasswordField passwordField = new PasswordField();
    private final Button loginButton = new Button("Se connecter");

    public LoginView(UserService userService) {
        this.userService = userService;

        // Redirect if already logged in
        if (SessionManager.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate(""));
            return;
        }

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("auth-page");

        add(createLoginCard());
    }

    private Div createLoginCard() {
        Div card = new Div();
        card.addClassNames("auth-card", LumoUtility.Background.BASE, LumoUtility.BoxShadow.LARGE, LumoUtility.BorderRadius.LARGE);
        card.setWidth("420px");
        card.getStyle().set("padding", "var(--lumo-space-xl)");

        // Logo/Brand
        H1 brand = new H1("UniCovoit");
        brand.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.TextAlignment.CENTER, LumoUtility.Margin.Bottom.SMALL);

        H2 title = new H2("Connexion");
        title.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.Margin.Top.NONE);

        Paragraph subtitle = new Paragraph("Connectez-vous pour accéder à votre espace");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER, LumoUtility.Margin.Bottom.LARGE);

        // Form fields
        emailField.setLabel("Email universitaire");
        emailField.setPlaceholder("votre.email@university.fr");
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setWidthFull();
        emailField.setRequired(true);
        emailField.setErrorMessage("Veuillez saisir une adresse email valide");

        passwordField.setLabel("Mot de passe");
        passwordField.setPlaceholder("Votre mot de passe");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setWidthFull();
        passwordField.setRequired(true);

        // Login button
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        loginButton.setWidthFull();
        loginButton.addClickShortcut(Key.ENTER);
        loginButton.setIcon(VaadinIcon.SIGN_IN.create());
        loginButton.addClickListener(e -> handleLogin());

        // Register link
        Div registerSection = new Div();
        registerSection.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.Margin.Top.LARGE);

        Span registerText = new Span("Pas encore de compte ? ");
        registerText.addClassName(LumoUtility.TextColor.SECONDARY);

        Anchor registerLink = new Anchor("register", "Créer un compte");
        registerLink.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontWeight.SEMIBOLD);

        registerSection.add(registerText, registerLink);

        // Assemble card
        card.add(
                brand,
                title,
                subtitle,
                emailField,
                passwordField,
                loginButton,
                registerSection
        );

        return card;
    }

    private void handleLogin() {
        if (!validateForm()) {
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Connexion...");

        try {
            LoginRequestDto dto = new LoginRequestDto();
            dto.setEmail(emailField.getValue().trim());
            dto.setPassword(passwordField.getValue());

            UserAccount user = userService.authenticate(dto);

            SessionManager.setCurrentUser(user);
            NotificationHelper.showSuccess("Bienvenue " + user.getFirstName() + " !");

            getUI().ifPresent(ui -> ui.navigate(""));

        } catch (Exception ex) {
            NotificationHelper.showError(ex.getMessage());
            loginButton.setEnabled(true);
            loginButton.setText("Se connecter");
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        if (emailField.isEmpty()) {
            emailField.setInvalid(true);
            emailField.setErrorMessage("L'email est obligatoire");
            valid = false;
        } else {
            emailField.setInvalid(false);
        }

        if (passwordField.isEmpty()) {
            passwordField.setInvalid(true);
            passwordField.setErrorMessage("Le mot de passe est obligatoire");
            valid = false;
        } else {
            passwordField.setInvalid(false);
        }

        if (!valid) {
            NotificationHelper.showWarning("Veuillez remplir tous les champs obligatoires");
        }

        return valid;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SessionManager.isLoggedIn()) {
            event.forwardTo("");
        }
    }
}
