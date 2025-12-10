package com.unicovoit.views.auth;

import com.unicovoit.dto.RegisterRequestDto;
import com.unicovoit.entity.UserAccount;
import com.unicovoit.service.UserService;
import com.unicovoit.util.NotificationHelper;
import com.unicovoit.util.SessionManager;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("register")
@PageTitle("Inscription | UniCovoit")
public class RegisterView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;

    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final EmailField emailField = new EmailField();
    private final TextField universityField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField confirmPasswordField = new PasswordField();
    private final Button registerButton = new Button("Créer mon compte");

    public RegisterView(UserService userService) {
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

        add(createRegisterCard());
    }

    private Div createRegisterCard() {
        Div card = new Div();
        card.addClassNames("auth-card", LumoUtility.Background.BASE, LumoUtility.BoxShadow.LARGE, LumoUtility.BorderRadius.LARGE);
        card.setWidth("480px");
        card.getStyle().set("padding", "var(--lumo-space-xl)");

        // Logo/Brand
        H1 brand = new H1("UniCovoit");
        brand.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.TextAlignment.CENTER, LumoUtility.Margin.Bottom.SMALL);

        H2 title = new H2("Inscription");
        title.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.Margin.Top.NONE);

        Paragraph subtitle = new Paragraph("Rejoignez la communauté de covoiturage universitaire");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER, LumoUtility.Margin.Bottom.LARGE);

        // Form fields
        firstNameField.setLabel("Prénom");
        firstNameField.setPlaceholder("Jean");
        firstNameField.setPrefixComponent(VaadinIcon.USER.create());
        firstNameField.setWidthFull();
        firstNameField.setRequired(true);

        lastNameField.setLabel("Nom");
        lastNameField.setPlaceholder("Dupont");
        lastNameField.setPrefixComponent(VaadinIcon.USER.create());
        lastNameField.setWidthFull();
        lastNameField.setRequired(true);

        emailField.setLabel("Email universitaire");
        emailField.setPlaceholder("jean.dupont@university.fr");
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setWidthFull();
        emailField.setRequired(true);
        emailField.setHelperText("Utilisez votre adresse email universitaire");

        universityField.setLabel("Université");
        universityField.setPlaceholder("Université de Paris");
        universityField.setPrefixComponent(VaadinIcon.ACADEMY_CAP.create());
        universityField.setWidthFull();
        universityField.setRequired(true);

        passwordField.setLabel("Mot de passe");
        passwordField.setPlaceholder("Minimum 8 caractères");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setWidthFull();
        passwordField.setRequired(true);
        passwordField.setHelperText("Au moins 8 caractères");

        confirmPasswordField.setLabel("Confirmer le mot de passe");
        confirmPasswordField.setPlaceholder("Ressaisissez votre mot de passe");
        confirmPasswordField.setPrefixComponent(VaadinIcon.LOCK.create());
        confirmPasswordField.setWidthFull();
        confirmPasswordField.setRequired(true);

        // Register button
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        registerButton.setWidthFull();
        registerButton.addClickShortcut(Key.ENTER);
        registerButton.setIcon(VaadinIcon.USER_CHECK.create());
        registerButton.addClickListener(e -> handleRegister());

        // Login link
        Div loginSection = new Div();
        loginSection.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.Margin.Top.LARGE);

        Span loginText = new Span("Déjà inscrit ? ");
        loginText.addClassName(LumoUtility.TextColor.SECONDARY);

        Anchor loginLink = new Anchor("login", "Se connecter");
        loginLink.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontWeight.SEMIBOLD);

        loginSection.add(loginText, loginLink);

        // Assemble card
        card.add(
                brand,
                title,
                subtitle,
                firstNameField,
                lastNameField,
                emailField,
                universityField,
                passwordField,
                confirmPasswordField,
                registerButton,
                loginSection
        );

        return card;
    }

    private void handleRegister() {
        if (!validateForm()) {
            return;
        }

        registerButton.setEnabled(false);
        registerButton.setText("Création du compte...");

        try {
            RegisterRequestDto dto = new RegisterRequestDto();
            dto.setFirstName(firstNameField.getValue().trim());
            dto.setLastName(lastNameField.getValue().trim());
            dto.setEmail(emailField.getValue().trim().toLowerCase());
            dto.setUniversity(universityField.getValue().trim());
            dto.setPassword(passwordField.getValue());
            dto.setConfirmPassword(confirmPasswordField.getValue());

            UserAccount user = userService.registerStudent(dto);

            SessionManager.setCurrentUser(user);
            NotificationHelper.showSuccess("Compte créé avec succès ! Bienvenue " + user.getFirstName() + " !");

            getUI().ifPresent(ui -> ui.navigate(""));

        } catch (Exception ex) {
            NotificationHelper.showError(ex.getMessage());
            registerButton.setEnabled(true);
            registerButton.setText("Créer mon compte");
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        if (firstNameField.isEmpty()) {
            firstNameField.setInvalid(true);
            firstNameField.setErrorMessage("Le prénom est obligatoire");
            valid = false;
        } else {
            firstNameField.setInvalid(false);
        }

        if (lastNameField.isEmpty()) {
            lastNameField.setInvalid(true);
            lastNameField.setErrorMessage("Le nom est obligatoire");
            valid = false;
        } else {
            lastNameField.setInvalid(false);
        }

        if (emailField.isEmpty()) {
            emailField.setInvalid(true);
            emailField.setErrorMessage("L'email est obligatoire");
            valid = false;
        } else if (!emailField.getValue().contains("@")) {
            emailField.setInvalid(true);
            emailField.setErrorMessage("Email invalide");
            valid = false;
        } else {
            emailField.setInvalid(false);
        }

        if (universityField.isEmpty()) {
            universityField.setInvalid(true);
            universityField.setErrorMessage("L'université est obligatoire");
            valid = false;
        } else {
            universityField.setInvalid(false);
        }

        if (passwordField.isEmpty()) {
            passwordField.setInvalid(true);
            passwordField.setErrorMessage("Le mot de passe est obligatoire");
            valid = false;
        } else if (passwordField.getValue().length() < 8) {
            passwordField.setInvalid(true);
            passwordField.setErrorMessage("Le mot de passe doit contenir au moins 8 caractères");
            valid = false;
        } else {
            passwordField.setInvalid(false);
        }

        if (confirmPasswordField.isEmpty()) {
            confirmPasswordField.setInvalid(true);
            confirmPasswordField.setErrorMessage("Veuillez confirmer votre mot de passe");
            valid = false;
        } else if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
            confirmPasswordField.setInvalid(true);
            confirmPasswordField.setErrorMessage("Les mots de passe ne correspondent pas");
            valid = false;
        } else {
            confirmPasswordField.setInvalid(false);
        }

        if (!valid) {
            NotificationHelper.showWarning("Veuillez corriger les erreurs dans le formulaire");
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
